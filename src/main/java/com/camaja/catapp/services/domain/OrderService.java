package com.camaja.catapp.services.domain;

import com.camaja.catapp.dto.OrderDTO;
import com.camaja.catapp.dto.OrderItemDTO;
import com.camaja.catapp.mappers.OrderItemMapper;
import com.camaja.catapp.mappers.OrderMapper;
import com.camaja.catapp.models.Color;
import com.camaja.catapp.models.Order;
import com.camaja.catapp.models.OrderItem;
import com.camaja.catapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class OrderService {
    @Autowired OrderRepository orderRepository;
    @Autowired OrderItemRepository orderItemRepository;
    @Autowired ProductParameterRepository productParameterRepository;
    @Autowired PrintingServiceRepository printingServiceRepository;
    @Autowired ContactRepository contactRepository;
    @Autowired PriorityRepository priorityRepository;
    @Autowired StatusRepository statusRepository;

    @Autowired OrderMapper orderMapper;
    @Autowired OrderItemMapper orderItemMapper;

    public List<Order> getActiveOrders() {
        List<Order> activeOrders = orderRepository.findAll().stream().filter(order -> Boolean.TRUE.equals(order.getFlagActive())).toList();
        activeOrders.forEach(order -> {
            order.setOrderItems(
                    order.getOrderItems().stream().filter(orderItem -> Boolean.TRUE.equals(orderItem.getFlagActive())).toList()
            );
        });

        return activeOrders;
    }

    public List<OrderDTO> getActiveOrdersDTO() {
        return getActiveOrders().stream().map(orderMapper::toDto).toList();
    }

    public OrderItem getOrderItemById(Long id) {
        return orderItemRepository.findById(id.intValue()).orElse(null);
    }

    public void saveOrderItem(OrderItem orderItem) {
        orderItem.setA$date$(Timestamp.from(Instant.now()));
        orderItemRepository.save(orderItem);
    }

    public void saveOrderDTO(OrderDTO orderDTO) {
        Order order = orderMapper.toEntity(orderDTO);

        if (orderDTO.getPriority() != null && orderDTO.getPriority().getId_priority() != null) {
            order.setPriority(priorityRepository.getReferenceById(orderDTO.getPriority().getId_priority().intValue()));
        } else {
            order.setPriority(null);
        }

        if (orderDTO.getStatus() != null && orderDTO.getStatus().getId_status() != null) {
            order.setStatus(statusRepository.getReferenceById(orderDTO.getStatus().getId_status().intValue()));
        } else {
            order.setStatus(null);
        }

        if (orderDTO.getContact() != null && orderDTO.getContact().getIdContact() != null) {
            order.setContact(contactRepository.getReferenceById(orderDTO.getContact().getIdContact().intValue()));
        } else {
            order.setContact(null);
        }

        if (order.getDate_requested() == null) {
            order.setDate_requested(Timestamp.from(Instant.now()));
        }

        order.setDate_status(Timestamp.from(Instant.now()));
        order.setA$date$(Timestamp.from(Instant.now()));
        orderRepository.save(order);
    }

    public void saveOrderItemDTO(OrderItemDTO orderItemDTO) {
        OrderItem orderItem = orderItemMapper.toEntity(orderItemDTO);

        if (orderItemDTO.getIdOrder() != null) {
            orderItem.setOrder(orderRepository.getReferenceById(orderItemDTO.getIdOrder().intValue()));
        } else {
            orderItem.setOrder(null);
        }

        if (orderItemDTO.getProduct() != null && orderItemDTO.getProduct().getIdProductParameter() != null) {
            orderItem.setProduct(productParameterRepository.getReferenceById(orderItemDTO.getProduct().getIdProductParameter().intValue()));
        } else {
            orderItem.setProduct(null);
        }

        if (orderItemDTO.getPrintingService() != null && orderItemDTO.getPrintingService().getId_printing_service() != null) {
            orderItem.setPrintingService(printingServiceRepository.getReferenceById(orderItemDTO.getPrintingService().getId_printing_service().intValue()));
        } else {
            orderItem.setPrintingService(null);
        }

        orderItem.setA$date$(Timestamp.from(Instant.now()));
        orderItemRepository.save(orderItem);
    }
}
