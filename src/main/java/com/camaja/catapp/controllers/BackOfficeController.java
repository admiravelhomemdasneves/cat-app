package com.camaja.catapp.controllers;

import com.camaja.catapp.dto.AccessLevelRequest;
import com.camaja.catapp.dto.BackofficePageRequest;
import com.camaja.catapp.dto.CompanyRequest;
import com.camaja.catapp.dto.ContactDTO;
import com.camaja.catapp.dto.OrderDTO;
import com.camaja.catapp.dto.OrderItemDTO;
import com.camaja.catapp.dto.PermissionRequestDTO;
import com.camaja.catapp.dto.RegisterUserDTO;
import com.camaja.catapp.dto.StoreRequest;
import com.camaja.catapp.dto.UserDTO;
import com.camaja.catapp.dto.VatRequest;
import com.camaja.catapp.models.*;
import com.camaja.catapp.configs.TenantAccess;
import com.camaja.catapp.services.auth.AuthenticationService;
import com.camaja.catapp.services.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/bo")
@RestController
@CrossOrigin
public class BackOfficeController {
    private final ContactService contactService;
    private final VatService vatService;
    private final StoreService storeService;
    private final OrderService orderService;
    private final BillingService billingService;
    private final CompanyService companyService;
    private final AccessLevelService accessLevelService;
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final TenantAccess tenantAccess;
    private final BackofficePageService backofficePageService;
    private final RolePagePermissionService rolePagePermissionService;

    // ─── CONTACTS ────────────────────────────────────────────────────────────────

    @GetMapping("/contact")
    public ResponseEntity<List<ContactDTO>> getActiveContacts() {
        List<ContactDTO> contactDTO = contactService.getAllContacts();
        List<ContactDTO> activeContacts = contactDTO.stream()
                .filter(contact -> !Boolean.FALSE.equals(contact.getFlagActive()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(activeContacts);
    }

    @GetMapping("/contact/{id}")
    public ResponseEntity<ContactDTO> getContactById(@PathVariable Long id) {
        ContactDTO contactDTO = contactService.getContactById(id);
        return ResponseEntity.ok(contactDTO);
    }

    @GetMapping("/contact/inactivate/{id}")
    public ResponseEntity<ContactDTO> inactivateContactById(@PathVariable Long id) {
        return ResponseEntity.ok(contactService.inactivateContact(id));
    }

    @PostMapping("/contact/save")
    public ResponseEntity<ContactDTO> saveContact(@RequestBody ContactDTO contactDTO) {
        contactService.saveContactDTO(contactDTO);
        return ResponseEntity.ok(contactDTO);
    }

    // ─── VATS ─────────────────────────────────────────────────────────────────────

    // Global VATs/stores (company = NULL) are listed for every company; only SUPER_ADMIN can change them.

    @GetMapping("/vat")
    public ResponseEntity<List<Vat>> getActiveVats() {
        return ResponseEntity.ok(vatService.getVisibleVats());
    }

    @PostMapping("/vat/save")
    public ResponseEntity<Vat> saveVat(@RequestBody VatRequest vat) {
        return ResponseEntity.ok(vatService.saveVat(vat));
    }

    @GetMapping("/vat/inactivate/{id}")
    public ResponseEntity<Vat> inactivateVatById(@PathVariable Long id) {
        return ResponseEntity.ok(vatService.inactivateVat(id));
    }


    // ─── STORES ───────────────────────────────────────────────────────────────────

    @GetMapping("/store")
    public ResponseEntity<List<Store>> getActiveStores() {
        return ResponseEntity.ok(storeService.getVisibleStores());
    }

    @PostMapping("/store/save")
    public ResponseEntity<Store> saveStore(@RequestBody StoreRequest store) {
        return ResponseEntity.ok(storeService.saveStore(store));
    }

    @GetMapping("/store/inactivate/{id}")
    public ResponseEntity<Store> inactivateStoreById(@PathVariable Long id) {
        return ResponseEntity.ok(storeService.inactivateStore(id));
    }

    // ─── ORDERS ───────────────────────────────────────────────────────────────────
    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> getActiveOrdersDTO() {
        List<OrderDTO> activeDTOs = orderService.getActiveOrdersDTO();
        return ResponseEntity.ok(activeDTOs);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderDTOById(id));
    }

    @GetMapping("/orders/inactivate/{id}")
    public ResponseEntity<OrderDTO> inactivateOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.inactivateOrder(id));
    }

    @GetMapping("/orders/{id}/recalculate")
    public ResponseEntity<OrderDTO> recalculateOrder(@PathVariable Long id) {
        billingService.recalculateForOrder(id);
        return ResponseEntity.ok(orderService.getOrderDTOById(id));
    }

    @PostMapping("/orders/save")
    public ResponseEntity<OrderDTO> saveOrderDTO(@RequestBody OrderDTO orderDTO) {
        orderService.saveOrderDTO(orderDTO);
        return ResponseEntity.ok(orderDTO);
    }

    // ─── ORDER ITEMS (contents) ───────────────────────────────────────────────────
    @PostMapping("/orderitems/{id}/calculate-price")
    public ResponseEntity<OrderItemDTO> calculateOrderItemPrice(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.calculateOrderItemPrice(id));
    }

    @GetMapping("/orderitems/inactivate/{id}")
    public ResponseEntity<OrderItemDTO> inactivateOrderItemById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.inactivateOrderItem(id));
    }

    @PostMapping("/orderitems/save")
    public ResponseEntity<OrderItemDTO> saveOrderItem(@RequestBody OrderItemDTO dto) {
        orderService.saveOrderItemDTO(dto);
        return ResponseEntity.ok(dto);
    }

    // ─── COMPANIES ────────────────────────────────────────────────────────────────

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @GetMapping("/companies")
    public ResponseEntity<List<Company>> getCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @GetMapping("/companies/mine")
    public ResponseEntity<Company> getMyCompany() {
        return ResponseEntity.ok(tenantAccess.company());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/companies/save")
    public ResponseEntity<Company> saveCompany(@RequestBody CompanyRequest company) {
        return ResponseEntity.ok(companyService.updateCompany(tenantAccess.company().getIdCompany(), company));
    }

    // ─── ACCESS LEVELS ────────────────────────────────────────────────────────────

    @GetMapping("/accessLevels")
    public ResponseEntity<List<AccessLevel>> getAccessLevels() {
        return ResponseEntity.ok(accessLevelService.getAllAccessLevels());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/accessLevels/save")
    public ResponseEntity<AccessLevel> saveAccessLevel(@RequestBody AccessLevelRequest level) {
        return ResponseEntity.ok(accessLevelService.saveAccessLevel(level));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/accessLevels/{id}")
    public ResponseEntity<Void> deleteAccessLevel(@PathVariable Long id) {
        accessLevelService.deleteAccessLevel(id);
        return ResponseEntity.noContent().build();
    }

    // ─── USERS ────────────────────────────────────────────────────────────────────

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getUsersInCompany() {
        return ResponseEntity.ok(userService.getUsersInCurrentCompany());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/users/save")
    public ResponseEntity<UserDTO> createUser(@RequestBody RegisterUserDTO registerUserDTO) {
        User created = authenticationService.signup(registerUserDTO);
        return ResponseEntity.ok(UserDTO.from(created));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/users/{id}/access-level")
    public ResponseEntity<Void> updateUserAccessLevel(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        userService.updateAccessLevel(id, body.get("accessLevelId"));
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users/inactivate/{id}")
    public ResponseEntity<Void> inactivateUser(@PathVariable Long id) {
        userService.inactivateUser(id);
        return ResponseEntity.ok().build();
    }

    // ─── BACKOFFICE PAGES ─────────────────────────────────────────────────────────

    @GetMapping("/pages")
    public ResponseEntity<List<BackofficePage>> getPages() {
        return ResponseEntity.ok(backofficePageService.getAllPages());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/pages/save")
    public ResponseEntity<BackofficePage> savePage(@RequestBody BackofficePageRequest page) {
        return ResponseEntity.ok(backofficePageService.savePage(page));
    }

    // ─── ROLE PAGE PERMISSIONS ────────────────────────────────────────────────────

    @GetMapping("/permissions/mine")
    public ResponseEntity<List<RolePagePermission>> getMyPermissions() {
        return ResponseEntity.ok(rolePagePermissionService.getPermissionsForCurrentRole());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/permissions")
    public ResponseEntity<List<RolePagePermission>> getAllPermissions() {
        return ResponseEntity.ok(rolePagePermissionService.getAllPermissions());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/permissions/save")
    public ResponseEntity<RolePagePermission> savePermission(@RequestBody PermissionRequestDTO dto) {
        return ResponseEntity.ok(rolePagePermissionService.savePermission(dto));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/permissions/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        rolePagePermissionService.deletePermission(id);
        return ResponseEntity.ok().build();
    }
}