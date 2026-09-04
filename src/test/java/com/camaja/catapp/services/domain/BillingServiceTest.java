package com.camaja.catapp.services.domain;

import com.camaja.catapp.dto.OrderProductDTO;
import com.camaja.catapp.dto.ProductParameterDTO;
import com.camaja.catapp.models.*;
import com.camaja.catapp.repository.OrderBillRepository;
import com.camaja.catapp.repository.OrderRepository;
import com.camaja.catapp.repository.PrintingServiceRepository;
import com.camaja.catapp.repository.ProductParameterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock OrderRepository orderRepository;
    @Mock OrderBillRepository orderBillRepository;
    @Mock ProductParameterRepository productParameterRepository;
    @Mock PrintingServiceRepository printingServiceRepository;

    @InjectMocks BillingService billingService;

    // ─── recalculate ──────────────────────────────────────────────────────────

    @Test
    void recalculate_withNoActiveItems_setsBillTotalsToZero() {
        Order order = new Order();
        order.setOrderProducts(List.of());
        OrderBill existingBill = new OrderBill();
        existingBill.setVatRate(new BigDecimal("0.23"));
        order.setOrderBill(existingBill);

        billingService.recalculate(order);

        ArgumentCaptor<OrderBill> captor = ArgumentCaptor.forClass(OrderBill.class);
        verify(orderBillRepository).save(captor.capture());
        OrderBill saved = captor.getValue();
        assertThat(saved.getExpensesAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(saved.getServiceAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(saved.getVatAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(saved.getTotalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void recalculate_withInactiveItems_treatsThemAsEmpty() {
        OrderProduct inactive = new OrderProduct();
        inactive.setFlagActive(false);
        inactive.setUnitPrice(new BigDecimal("100.00"));
        inactive.setQuantity(1);

        Order order = new Order();
        order.setOrderProducts(List.of(inactive));
        order.setOrderBill(billWithVat("0.00"));

        billingService.recalculate(order);

        ArgumentCaptor<OrderBill> captor = ArgumentCaptor.forClass(OrderBill.class);
        verify(orderBillRepository).save(captor.capture());
        assertThat(captor.getValue().getExpensesAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void recalculate_withExpensesAndServiceItems_computesTotalsCorrectly() {
        OrderProduct expenseItem = new OrderProduct();
        expenseItem.setFlagActive(true);
        expenseItem.setUnitPrice(new BigDecimal("10.00"));
        expenseItem.setQuantity(2);

        OrderProduct serviceItem = new OrderProduct();
        serviceItem.setFlagActive(true);
        serviceItem.setServiceHourlyRate(new BigDecimal("25.00"));
        serviceItem.setServiceHours(3);

        Order order = new Order();
        order.setOrderProducts(List.of(expenseItem, serviceItem));
        order.setOrderBill(billWithVat("0.23"));

        billingService.recalculate(order);

        ArgumentCaptor<OrderBill> captor = ArgumentCaptor.forClass(OrderBill.class);
        verify(orderBillRepository).save(captor.capture());
        OrderBill saved = captor.getValue();
        assertThat(saved.getExpensesAmount()).isEqualByComparingTo("20.0000");
        assertThat(saved.getServiceAmount()).isEqualByComparingTo("75.0000");
        assertThat(saved.getVatAmount()).isEqualByComparingTo("21.8500");
        assertThat(saved.getTotalAmount()).isEqualByComparingTo("116.8500");
    }

    @Test
    void recalculate_withNullOrderBill_createsNewBill() {
        Order order = new Order();
        order.setOrderProducts(List.of());
        order.setOrderBill(null);

        billingService.recalculate(order);

        ArgumentCaptor<OrderBill> captor = ArgumentCaptor.forClass(OrderBill.class);
        verify(orderBillRepository).save(captor.capture());
        assertThat(captor.getValue()).isNotNull();
    }

    // ─── snapshotPrices ───────────────────────────────────────────────────────

    @Test
    void snapshotPrices_whenUnitPriceNull_copiesPriceFromProductParameter() {
        OrderProduct item = new OrderProduct();
        item.setUnitPrice(null);

        ProductParameterDTO productDTO = new ProductParameterDTO();
        productDTO.setIdProductParameter(5L);

        OrderProductDTO dto = new OrderProductDTO();
        dto.setProduct(productDTO);

        ProductParameters pp = new ProductParameters();
        pp.setPrice(new BigDecimal("19.99"));
        when(productParameterRepository.findById(5L)).thenReturn(Optional.of(pp));

        billingService.snapshotPrices(item, dto);

        assertThat(item.getUnitPrice()).isEqualByComparingTo("19.99");
    }

    @Test
    void snapshotPrices_whenUnitPriceAlreadySet_doesNotOverwrite() {
        OrderProduct item = new OrderProduct();
        item.setUnitPrice(new BigDecimal("9.99"));

        ProductParameterDTO productDTO = new ProductParameterDTO();
        productDTO.setIdProductParameter(5L);

        OrderProductDTO dto = new OrderProductDTO();
        dto.setProduct(productDTO);

        billingService.snapshotPrices(item, dto);

        verify(productParameterRepository, never()).findById(any());
        assertThat(item.getUnitPrice()).isEqualByComparingTo("9.99");
    }

    @Test
    void snapshotPrices_whenServiceHourlyRateNull_copiesRateFromPrintingService() {
        OrderProduct item = new OrderProduct();
        item.setServiceHourlyRate(null);

        PrintingService ps = new PrintingService();
        ps.setId_printing_service(3L);
        ps.setHourlyRate(new BigDecimal("50.00"));

        OrderProductDTO dto = new OrderProductDTO();
        dto.setPrintingService(ps);

        when(printingServiceRepository.findById(3L)).thenReturn(Optional.of(ps));

        billingService.snapshotPrices(item, dto);

        assertThat(item.getServiceHourlyRate()).isEqualByComparingTo("50.00");
    }

    @Test
    void snapshotPrices_whenServiceHourlyRateAlreadySet_doesNotOverwrite() {
        OrderProduct item = new OrderProduct();
        item.setServiceHourlyRate(new BigDecimal("30.00"));

        PrintingService ps = new PrintingService();
        ps.setId_printing_service(3L);

        OrderProductDTO dto = new OrderProductDTO();
        dto.setPrintingService(ps);

        billingService.snapshotPrices(item, dto);

        verify(printingServiceRepository, never()).findById(any());
        assertThat(item.getServiceHourlyRate()).isEqualByComparingTo("30.00");
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    private OrderBill billWithVat(String rate) {
        OrderBill bill = new OrderBill();
        bill.setVatRate(new BigDecimal(rate));
        return bill;
    }
}
