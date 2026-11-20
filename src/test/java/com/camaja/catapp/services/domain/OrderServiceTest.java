package com.camaja.catapp.services.domain;

import com.camaja.catapp.configs.SecurityUtils;
import com.camaja.catapp.configs.TenantAccess;
import com.camaja.catapp.dto.OrderDTO;
import com.camaja.catapp.dto.OrderItemDTO;
import com.camaja.catapp.mappers.OrderItemMapper;
import com.camaja.catapp.mappers.OrderMapper;
import com.camaja.catapp.models.Company;
import com.camaja.catapp.models.Order;
import com.camaja.catapp.models.OrderBill;
import com.camaja.catapp.models.OrderItem;
import com.camaja.catapp.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock OrderRepository orderRepository;
    @Mock OrderItemRepository orderItemRepository;
    @Mock MaterialRepository materialRepository;
    @Mock PrintingServiceRepository printingServiceRepository;
    @Mock ContactRepository contactRepository;
    @Mock PriorityRepository priorityRepository;
    @Mock StatusRepository statusRepository;
    @Mock OrderBillRepository orderBillRepository;
    @Mock AddressRepository addressRepository;
    @Mock OrderMapper orderMapper;
    @Mock OrderItemMapper orderItemMapper;
    @Mock TenantAccess tenantAccess;
    @Mock BillingService billingService;
    @Mock SecurityUtils securityUtils;

    @InjectMocks OrderService orderService;

    Company company = new Company();

    @BeforeEach
    void setUp() {
        lenient().when(tenantAccess.company()).thenReturn(company);
    }

    // ─── saveOrderItemDTO ─────────────────────────────────────────────────────

    @Test
    void saveOrderItemDTO_savesItemOnOwnedOrderAndRecalculates() {
        Order order = new Order();
        OrderItem item = new OrderItem();
        when(orderRepository.findOwned(10L, company)).thenReturn(Optional.of(order));
        when(orderItemMapper.toEntity(any(OrderItemDTO.class))).thenReturn(item);

        OrderItemDTO dto = new OrderItemDTO();
        dto.setIdOrder(10L);
        orderService.saveOrderItemDTO(dto);

        verify(orderItemRepository).save(item);
        assertThat(item.getOrder()).isSameAs(order);
        verify(billingService).recalculateForOrder(10L);
    }

    @Test
    void saveOrderItemDTO_withoutOrder_isRejected() {
        assertThatThrownBy(() -> orderService.saveOrderItemDTO(new OrderItemDTO()))
                .isInstanceOf(IllegalArgumentException.class);
        verify(orderItemRepository, never()).save(any());
    }

    // ─── saveOrderDTO ─────────────────────────────────────────────────────────

    @Test
    void saveOrderDTO_newOrder_setsCompanyAndCreatesBill() {
        Order order = new Order();
        when(orderMapper.toEntity(any(OrderDTO.class))).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(order);

        orderService.saveOrderDTO(new OrderDTO());

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getCompany()).isSameAs(company);
        assertThat(orderCaptor.getValue().getDate_requested()).isNotNull();

        ArgumentCaptor<OrderBill> billCaptor = ArgumentCaptor.forClass(OrderBill.class);
        verify(orderBillRepository).save(billCaptor.capture());
        assertThat(billCaptor.getValue().getOrder()).isSameAs(order);
    }
}
