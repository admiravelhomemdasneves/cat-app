package com.camaja.catapp.services.domain;

import com.camaja.catapp.dto.ContactDTO;
import com.camaja.catapp.dto.OrderDTO;
import com.camaja.catapp.dto.OrderItemDTO;
import com.camaja.catapp.mappers.ContactMapper;
import com.camaja.catapp.mappers.OrderItemMapper;
import com.camaja.catapp.mappers.OrderMapper;
import com.camaja.catapp.mappers.ProductParameterMapper;
import com.camaja.catapp.models.Order;
import com.camaja.catapp.repository.OrderRepository;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    @Autowired OrderRepository orderRepository;
    @Autowired OrderMapper orderMapper;

    public List<Order> getActiveOrders() {
        return orderRepository.findAll().stream().filter(order -> Boolean.TRUE.equals(order.getFlagActive())).toList();
    }

    public List<OrderDTO> getActiveOrdersDTO() {
        return getActiveOrders().stream().map(orderMapper::toDto).toList();
    }
}
