package com.camaja.catapp.services.domain;

import com.camaja.catapp.configs.SecurityUtils;
import com.camaja.catapp.dto.OrderDTO;
import com.camaja.catapp.dto.OrderItemDTO;
import com.camaja.catapp.dto.OrderProductDTO;
import com.camaja.catapp.mappers.OrderItemMapper;
import com.camaja.catapp.mappers.OrderMapper;
import com.camaja.catapp.mappers.OrderProductMapper;
import com.camaja.catapp.models.Company;
import com.camaja.catapp.models.Order;
import com.camaja.catapp.models.OrderItem;
import com.camaja.catapp.models.OrderProduct;
import com.camaja.catapp.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock OrderRepository orderRepository;
    @Mock OrderProductRepository orderProductRepository;
    @Mock OrderItemRepository orderItemRepository;
    @Mock ProductParameterRepository productParameterRepository;
    @Mock PrintingServiceRepository printingServiceRepository;
    @Mock ContactRepository contactRepository;
    @Mock PriorityRepository priorityRepository;
    @Mock StatusRepository statusRepository;
    @Mock OrderBillRepository orderBillRepository;
    @Mock OrderMapper orderMapper;
    @Mock OrderProductMapper orderProductMapper;
    @Mock OrderItemMapper orderItemMapper;
    @Mock BillingService billingService;
    @Mock SecurityUtils securityUtils;

    @InjectMocks OrderService orderService;

    // ─── saveOrderProductDTO ──────────────────────────────────────────────────

    @Test
    void saveOrderProductDTO_withOrderId_callsRecalculateAfterSave() {
        OrderProduct item = new OrderProduct();
        when(orderProductMapper.toEntity(any(OrderProductDTO.class))).thenReturn(item);
        when(orderRepository.getReferenceById(10L)).thenReturn(new Order());

        OrderProductDTO dto = new OrderProductDTO();
        dto.setIdOrder(10L);

        orderService.saveOrderProductDTO(dto);

        verify(billingService).recalculateForOrder(10L);
    }

    @Test
    void saveOrderProductDTO_withNullOrderId_doesNotCallRecalculate() {
        OrderProduct item = new OrderProduct();
        when(orderProductMapper.toEntity(any(OrderProductDTO.class))).thenReturn(item);

        OrderProductDTO dto = new OrderProductDTO();
        dto.setIdOrder(null);

        orderService.saveOrderProductDTO(dto);

        verify(billingService, never()).recalculateForOrder(any());
    }

    // ─── saveOrderItemDTO (order contents) ────────────────────────────────────

    @Test
    void saveOrderItemDTO_savesWithoutRecalculate() {
        OrderItem item = new OrderItem();
        when(orderItemMapper.toEntity(any(OrderItemDTO.class))).thenReturn(item);
        when(orderRepository.getReferenceById(10L)).thenReturn(new Order());

        OrderItemDTO dto = new OrderItemDTO();
        dto.setIdOrder(10L);

        orderService.saveOrderItemDTO(dto);

        verify(orderItemRepository).save(item);
        verify(billingService, never()).recalculateForOrder(any());
    }

    // ─── saveOrderDTO ─────────────────────────────────────────────────────────

    @Test
    void saveOrderDTO_setsCompanyFromSecurityContext() {
        Company company = new Company();
        company.setName("ACME");
        when(securityUtils.getCurrentCompany()).thenReturn(company);

        Order order = new Order();
        order.setOrderProducts(List.of());
        when(orderMapper.toEntity(any(OrderDTO.class))).thenReturn(order);

        orderService.saveOrderDTO(new OrderDTO());

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        assertThat(captor.getValue().getCompany()).isSameAs(company);
    }
}
