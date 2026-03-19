package com.camaja.catapp.services.domain;

import com.camaja.catapp.dto.OrderDTO;
import com.camaja.catapp.dto.OrderItemDTO;
import com.camaja.catapp.mappers.OrderItemMapper;
import com.camaja.catapp.mappers.OrderMapper;
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

    @Autowired OrderMapper orderMapper;
    @Autowired OrderItemMapper orderItemMapper;

    public List<Order> getActiveOrders() {
        return orderRepository.findAll().stream().filter(order -> Boolean.TRUE.equals(order.getFlagActive())).toList();
    }

    public List<OrderDTO> getActiveOrdersDTO() {
        return getActiveOrders().stream().map(orderMapper::toDto).toList();
    }

    public void saveOrderDTO(OrderDTO orderDTO) {
        Order order = orderMapper.toEntity(orderDTO);

        if (orderDTO.getContact() != null && orderDTO.getContact().getIdContact() != null) {
            order.setContact(contactRepository.getReferenceById(orderDTO.getContact().getIdContact().intValue()));
        }

        order.setA$date$(Timestamp.from(Instant.now()));
        orderRepository.save(order);
    }

    public void saveOrderItemDTO(OrderItemDTO orderItemDTO) {
        OrderItem orderItem = orderItemMapper.toEntity(orderItemDTO);

        if (orderItemDTO.getIdOrder() != null) {
            orderItem.setOrder(orderRepository.getReferenceById(orderItemDTO.getIdOrder().intValue()));
        }

        if (orderItemDTO.getProduct() != null && orderItemDTO.getProduct().getIdProductParameter() != null) {
            orderItem.setProduct(productParameterRepository.getReferenceById(orderItemDTO.getProduct().getIdProductParameter().intValue()));
        }

        if (orderItemDTO.getPrintingService() != null && orderItemDTO.getPrintingService().getId_printing_service() != null) {
            orderItem.setPrintingService(printingServiceRepository.getReferenceById(orderItemDTO.getPrintingService().getId_printing_service().intValue()));
        }

        orderItem.setA$date$(Timestamp.from(Instant.now()));

        orderItemRepository.save(orderItem);
    }
}
