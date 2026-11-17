package com.camaja.catapp.services.domain;

import com.camaja.catapp.configs.SecurityUtils;
import com.camaja.catapp.configs.TenantAccess;
import com.camaja.catapp.dto.ContactDTO;
import com.camaja.catapp.dto.OrderDTO;
import com.camaja.catapp.dto.OrderItemDTO;
import com.camaja.catapp.dto.VatRequest;
import com.camaja.catapp.mappers.ContactMapper;
import com.camaja.catapp.mappers.OrderItemMapper;
import com.camaja.catapp.mappers.OrderMapper;
import com.camaja.catapp.models.*;
import com.camaja.catapp.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Requests for another company's records must fail with 404 and never write anything. */
class TenantIsolationServiceTest {

    SecurityUtils securityUtils = mock(SecurityUtils.class);
    TenantAccess tenantAccess = new TenantAccess(securityUtils);

    OrderRepository orderRepository = mock(OrderRepository.class);
    OrderItemRepository orderItemRepository = mock(OrderItemRepository.class);
    MaterialRepository materialRepository = mock(MaterialRepository.class);
    PrintingServiceRepository printingServiceRepository = mock(PrintingServiceRepository.class);
    ContactRepository contactRepository = mock(ContactRepository.class);
    PriorityRepository priorityRepository = mock(PriorityRepository.class);
    StatusRepository statusRepository = mock(StatusRepository.class);
    OrderBillRepository orderBillRepository = mock(OrderBillRepository.class);
    AddressRepository addressRepository = mock(AddressRepository.class);
    VatRepository vatRepository = mock(VatRepository.class);
    BillingService billingService = mock(BillingService.class);

    OrderItemMapper orderItemMapper = new OrderItemMapper();
    ContactMapper contactMapper = new ContactMapper();
    OrderMapper orderMapper = new OrderMapper(contactMapper, orderItemMapper);

    OrderService orderService = new OrderService(orderRepository, orderItemRepository, materialRepository,
            printingServiceRepository, contactRepository, priorityRepository, statusRepository,
            orderBillRepository, addressRepository, orderMapper, orderItemMapper, tenantAccess, billingService);
    ContactService contactService = new ContactService(contactRepository, addressRepository, contactMapper, tenantAccess);
    VatService vatService = new VatService(vatRepository, tenantAccess);

    Company companyA = company(1L);

    @BeforeEach
    void loggedInAsCompanyA() {
        when(securityUtils.getCurrentCompany()).thenReturn(companyA);
        // A company ADMIN, but not the platform SUPER_ADMIN.
        when(securityUtils.isAdmin()).thenReturn(true);
        when(securityUtils.isSuperAdmin()).thenReturn(false);
        // Nothing of another company is found by the scoped queries.
        when(orderRepository.findOwned(any(), any())).thenReturn(Optional.empty());
        when(orderItemRepository.findOwned(any(), any())).thenReturn(Optional.empty());
        when(contactRepository.findByIdContactAndCompany(any(), any())).thenReturn(Optional.empty());
        when(addressRepository.findOwned(any(), any())).thenReturn(Optional.empty());
        when(vatRepository.findVisible(any(), any())).thenReturn(Optional.empty());
    }

    @Test
    void readingAnotherCompanysOrder_is404() {
        assertNotFound(() -> orderService.getOrderDTOById(99L));
        assertNotFound(() -> orderService.inactivateOrder(99L));
        assertNotFound(() -> orderService.getOrderItemById(99L));
        assertNotFound(() -> orderService.inactivateOrderItem(99L));
        assertNotFound(() -> orderService.calculateOrderItemPrice(99L));
        verify(orderRepository, never()).save(any());
        verify(orderItemRepository, never()).save(any());
    }

    @Test
    void updatingAnotherCompanysOrder_is404() {
        OrderDTO dto = new OrderDTO();
        dto.setIdOrder(99L);
        assertNotFound(() -> orderService.saveOrderDTO(dto));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void linkingAnotherCompanysContactToAnOrder_is404() {
        OrderDTO dto = new OrderDTO();
        ContactDTO foreignContact = new ContactDTO();
        foreignContact.setIdContact(42L);
        dto.setContact(foreignContact);
        assertNotFound(() -> orderService.saveOrderDTO(dto));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void addingAnItemToAnotherCompanysOrder_is404() {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setIdOrder(99L);
        assertNotFound(() -> orderService.saveOrderItemDTO(dto));
        verify(orderItemRepository, never()).save(any());
    }

    @Test
    void readingOrEditingAnotherCompanysContact_is404() {
        assertNotFound(() -> contactService.getContactById(7L));
        assertNotFound(() -> contactService.inactivateContact(7L));

        ContactDTO dto = new ContactDTO();
        dto.setIdContact(7L);
        assertNotFound(() -> contactService.saveContactDTO(dto));
        verify(contactRepository, never()).save(any());
    }

    @Test
    void attachingAnotherCompanysAddressToANewContact_is404() {
        ContactDTO dto = new ContactDTO();
        Address foreign = new Address();
        foreign.setId_address(5L);
        dto.setShippingAddress(foreign);
        assertNotFound(() -> contactService.saveContactDTO(dto));
        verify(contactRepository, never()).save(any());
    }

    @Test
    void newVat_belongsToTheCurrentCompany_evenIfClientAsksForGlobal() {
        when(vatRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        VatRequest request = new VatRequest();
        request.setCountry("PT");

        Vat saved = vatService.saveVat(request);
        assertThat(saved.getCompany()).isSameAs(companyA);

        request.setGlobal(true);
        assertThatThrownBy(() -> vatService.saveVat(request))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void globalVat_isReadOnlyForCompanyAdmins_andEditableBySuperAdmin() {
        Vat global = new Vat();
        global.setIdVat(3L);
        when(vatRepository.findVisible(eq(3L), any())).thenReturn(Optional.of(global));
        when(vatRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThatThrownBy(() -> vatService.inactivateVat(3L))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN));

        when(securityUtils.isSuperAdmin()).thenReturn(true);
        assertThat(vatService.inactivateVat(3L).getFlagActive()).isFalse();
        assertThat(global.getCompany()).isNull();
    }

    @Test
    void superAdmin_canCreateGlobalVat() {
        when(securityUtils.isSuperAdmin()).thenReturn(true);
        when(vatRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        VatRequest request = new VatRequest();
        request.setCountry("EU");
        request.setGlobal(true);

        assertThat(vatService.saveVat(request).getCompany()).isNull();
    }

    @Test
    void superAdminFlag_grantsSuperAdminAuthority_andIsOffByDefault() {
        User user = new User("u", "u@e.com", "pwd");
        assertThat(user.isSuperAdmin()).isFalse();
        assertThat(user.getAuthorities()).isEmpty();

        user.setSuperAdmin(true);
        assertThat(user.getAuthorities()).extracting(Object::toString).containsExactly(User.SUPER_ADMIN);
    }

    @Test
    void anotherCompanysVat_is404() {
        assertNotFound(() -> vatService.getVatById(8L));
        assertNotFound(() -> vatService.inactivateVat(8L));
        verify(vatRepository, never()).save(any());
    }

    private static void assertNotFound(org.assertj.core.api.ThrowableAssert.ThrowingCallable call) {
        assertThatThrownBy(call)
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    private static Company company(Long id) {
        Company c = new Company();
        c.setIdCompany(id);
        c.setName("Company " + id);
        return c;
    }
}
