package com.camaja.catapp.services.domain;

import com.camaja.catapp.configs.SecurityUtils;
import com.camaja.catapp.dto.OrderDTO;
import com.camaja.catapp.dto.OrderItemDTO;
import com.camaja.catapp.mappers.OrderItemMapper;
import com.camaja.catapp.mappers.OrderMapper;
import com.camaja.catapp.models.Order;
import com.camaja.catapp.models.OrderItem;
import com.camaja.catapp.repository.*;
import com.camaja.catapp.models.Material;
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
    private final MaterialRepository materialRepository;
    private final PrintingServiceRepository printingServiceRepository;
    private final ContactRepository contactRepository;
    private final PriorityRepository priorityRepository;
    private final StatusRepository statusRepository;
    private final OrderBillRepository orderBillRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final SecurityUtils securityUtils;

    public List<Order> getActiveOrders() {
        List<Order> activeOrders = orderRepository.findByCompanyAndFlagActive(securityUtils.getCurrentCompany(), true);
        activeOrders.forEach(order -> {
            order.setOrderContents(
                    order.getOrderContents() == null ? List.of()
                    : order.getOrderContents().stream().filter(item -> Boolean.TRUE.equals(item.getFlagActive())).toList()
            );
        });
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
    public OrderItemDTO calculateOrderItemPrice(Long id) {
        OrderItem item = orderItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("OrderItem not found: " + id));

        java.math.BigDecimal price = java.math.BigDecimal.ZERO;
        java.math.BigDecimal expenses = null;

        // Material retail price × quantity
        if (item.getMaterial() != null
                && item.getMaterial().getRetailPrice() != null
                && item.getMaterial().getRetailPrice().getPriceValue() != null) {
            java.math.BigDecimal retailUnit = item.getMaterial().getRetailPrice().getPriceValue();
            java.math.BigDecimal qty = item.getQuantity() != null ? item.getQuantity() : java.math.BigDecimal.ONE;
            price = price.add(retailUnit.multiply(qty));
        }

        // Service rate component
        if (item.getService() != null && item.getServiceRate() != null) {
            String rateType = item.getService().getRateType();
            if ("HOURLY".equals(rateType) && item.getServiceHours() != null) {
                price = price.add(item.getServiceRate().multiply(item.getServiceHours()));
            } else if ("PER_UNIT".equals(rateType) && item.getQuantity() != null) {
                price = price.add(item.getServiceRate().multiply(item.getQuantity()));
            }
        }

        // Expenses = material cost price
        if (item.getMaterial() != null
                && item.getMaterial().getCostPrice() != null
                && item.getMaterial().getCostPrice().getPriceValue() != null) {
            expenses = item.getMaterial().getCostPrice().getPriceValue();
        }

        item.setPrice(price);
        item.setExpenses(expenses);
        item.setA$date$(Timestamp.from(Instant.now()));
        orderItemRepository.save(item);

        return orderItemMapper.toDto(item);
    }

    @Transactional
    public void saveOrderItemDTO(OrderItemDTO dto) {
        OrderItem orderItem = orderItemMapper.toEntity(dto);

        if (dto.getIdOrder() != null) {
            orderItem.setOrder(orderRepository.getReferenceById(dto.getIdOrder()));
        } else {
            orderItem.setOrder(null);
        }

        if (dto.getService() != null && dto.getService().getId_printing_service() != null) {
            orderItem.setService(printingServiceRepository.getReferenceById(dto.getService().getId_printing_service()));
        } else {
            orderItem.setService(null);
        }

        if (dto.getMaterial() != null && dto.getMaterial().getIdMaterial() != null) {
            orderItem.setMaterial(materialRepository.getReferenceById(dto.getMaterial().getIdMaterial()));
        } else {
            orderItem.setMaterial(null);
        }

        orderItem.setA$date$(Timestamp.from(Instant.now()));
        orderItemRepository.save(orderItem);
    }
}
