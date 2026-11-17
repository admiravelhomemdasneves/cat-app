package com.camaja.catapp.services.domain;

import com.camaja.catapp.configs.TenantAccess;
import com.camaja.catapp.models.Company;
import com.camaja.catapp.models.Order;
import com.camaja.catapp.models.OrderBill;
import com.camaja.catapp.models.OrderItem;
import com.camaja.catapp.repository.OrderBillRepository;
import com.camaja.catapp.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock OrderRepository orderRepository;
    @Mock OrderBillRepository orderBillRepository;
    @Mock TenantAccess tenantAccess;

    @InjectMocks BillingService billingService;

    // ─── recalculate ──────────────────────────────────────────────────────────

    @Test
    void recalculate_withNoItems_setsTotalToZero() {
        Order order = new Order();
        order.setOrderContents(List.of());
        order.setOrderBill(billWithVat("0.23"));

        billingService.recalculate(order);

        assertThat(savedBill().getTotalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void recalculate_ignoresInactiveItemsAndItemsWithoutPrice() {
        Order order = new Order();
        order.setOrderContents(List.of(item("100.00", false), item(null, true), item("10.00", true)));
        order.setOrderBill(billWithVat("0.00"));

        billingService.recalculate(order);

        assertThat(savedBill().getTotalAmount()).isEqualByComparingTo("10.0000");
    }

    @Test
    void recalculate_appliesVatToTheSumOfActivePrices() {
        Order order = new Order();
        order.setOrderContents(List.of(item("20.00", true), item("75.00", true)));
        order.setOrderBill(billWithVat("0.23"));

        billingService.recalculate(order);

        assertThat(savedBill().getTotalAmount()).isEqualByComparingTo("116.8500");
    }

    @Test
    void recalculate_withNullOrderBill_createsNewBillForTheOrder() {
        Order order = new Order();
        order.setOrderContents(List.of(item("5.00", true)));

        billingService.recalculate(order);

        OrderBill saved = savedBill();
        assertThat(saved.getOrder()).isSameAs(order);
        assertThat(saved.getTotalAmount()).isEqualByComparingTo("5.0000");
    }

    // ─── recalculateForOrder ──────────────────────────────────────────────────

    @Test
    void recalculateForOrder_ofAnotherCompany_isNotFound() {
        Company company = new Company();
        when(tenantAccess.company()).thenReturn(company);
        when(orderRepository.findOwned(9L, company)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> billingService.recalculateForOrder(9L))
                .isInstanceOf(ResponseStatusException.class);
        verify(orderBillRepository, never()).save(any());
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    private OrderBill savedBill() {
        ArgumentCaptor<OrderBill> captor = ArgumentCaptor.forClass(OrderBill.class);
        verify(orderBillRepository).save(captor.capture());
        return captor.getValue();
    }

    private static OrderItem item(String price, boolean active) {
        OrderItem item = new OrderItem();
        item.setPrice(price == null ? null : new BigDecimal(price));
        item.setFlagActive(active);
        return item;
    }

    private static OrderBill billWithVat(String rate) {
        OrderBill bill = new OrderBill();
        bill.setVatRate(new BigDecimal(rate));
        return bill;
    }
}
