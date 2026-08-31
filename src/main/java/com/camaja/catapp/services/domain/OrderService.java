package com.camaja.catapp.services.domain;

import com.camaja.catapp.configs.SecurityUtils;
import com.camaja.catapp.dto.OrderDTO;
import com.camaja.catapp.dto.OrderItemDTO;
import com.camaja.catapp.mappers.OrderItemMapper;
import com.camaja.catapp.mappers.OrderMapper;
import com.camaja.catapp.models.Order;
import com.camaja.catapp.models.OrderItem;
import com.camaja.catapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductParameterRepository productParameterRepository;
    private final PrintingServiceRepository printingServiceRepository;
    private final ContactRepository contactRepository;
    private final PriorityRepository priorityRepository;
    private final StatusRepository statusRepository;
    private final OrderBillRepository orderBillRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final BillingService billingService;
    private final SecurityUtils securityUtils;

    public List<Order> getActiveOrders() {
        List<Order> activeOrders = orderRepository.findByCompanyAndFlagActive(securityUtils.getCurrentCompany(), true);
        activeOrders.forEach(order -> order.setOrderItems(
                order.getOrderItems().stream().filter(item -> Boolean.TRUE.equals(item.getFlagActive())).toList()
        ));
        return activeOrders;
    }

    public List<OrderDTO> getActiveOrdersDTO() {
        return getActiveOrders().stream().map(orderMapper::toDto).toList();
    }

    public OrderDTO getOrderDTOById(Long orderId) {
        return orderRepository.findById(orderId)
                .map(orderMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
    }

    public OrderItem getOrderItemById(Long id) {
        return orderItemRepository.findById(id).orElse(null);
    }

    public void saveOrderItem(OrderItem orderItem) {
        orderItem.setA$date$(Timestamp.from(Instant.now()));
        orderItemRepository.save(orderItem);
    }

    @Transactional
    public void saveOrderDTO(OrderDTO orderDTO) {
        Order order = orderMapper.toEntity(orderDTO);

        if (orderDTO.getPriority() != null && orderDTO.getPriority().getId_priority() != null) {
            order.setPriority(priorityRepository.getReferenceById(orderDTO.getPriority().getId_priority()));
        } else {
            order.setPriority(null);
        }

        if (orderDTO.getStatus() != null && orderDTO.getStatus().getId_status() != null) {
            order.setStatus(statusRepository.getReferenceById(orderDTO.getStatus().getId_status()));
        } else {
            order.setStatus(null);
        }

        if (orderDTO.getContact() != null && orderDTO.getContact().getIdContact() != null) {
            order.setContact(contactRepository.getReferenceById(orderDTO.getContact().getIdContact()));
        } else {
            order.setContact(null);
        }

        if (order.getOrderBill() != null && order.getOrderBill().getIdOrderBill() != null) {
            order.setOrderBill(orderBillRepository.getReferenceById(order.getOrderBill().getIdOrderBill()));
        }

        if (orderDTO.getDeliveryAddress() == null) {
            if (orderDTO.getContact() != null && orderDTO.getContact().getShippingAddress() != null) {
                order.setDelivery_address(orderDTO.getContact().getShippingAddress());
            }
        }

        if (order.getDate_requested() == null) {
            order.setDate_requested(Timestamp.from(Instant.now()));
        }

        order.setCompany(securityUtils.getCurrentCompany());
        order.setDate_status(Timestamp.from(Instant.now()));
        order.setA$date$(Timestamp.from(Instant.now()));
        orderRepository.save(order);
    }

    @Transactional
    public void saveOrderItemDTO(OrderItemDTO orderItemDTO) {
        OrderItem orderItem = orderItemMapper.toEntity(orderItemDTO);

        if (orderItemDTO.getIdOrder() != null) {
            orderItem.setOrder(orderRepository.getReferenceById(orderItemDTO.getIdOrder()));
        } else {
            orderItem.setOrder(null);
        }

        if (orderItemDTO.getProduct() != null && orderItemDTO.getProduct().getIdProductParameter() != null) {
            orderItem.setProduct(productParameterRepository.getReferenceById(orderItemDTO.getProduct().getIdProductParameter()));
        } else {
            orderItem.setProduct(null);
        }

        if (orderItemDTO.getPrintingService() != null && orderItemDTO.getPrintingService().getId_printing_service() != null) {
            orderItem.setPrintingService(printingServiceRepository.getReferenceById(orderItemDTO.getPrintingService().getId_printing_service()));
        } else {
            orderItem.setPrintingService(null);
        }

        billingService.snapshotPrices(orderItem, orderItemDTO);
        orderItem.setA$date$(Timestamp.from(Instant.now()));
        orderItemRepository.save(orderItem);

        if (orderItemDTO.getIdOrder() != null) {
            billingService.recalculateForOrder(orderItemDTO.getIdOrder());
        }
    }
}
