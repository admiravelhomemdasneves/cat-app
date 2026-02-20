package com.camaja.catapp.controllers;

import com.camaja.catapp.models.Order;
import com.camaja.catapp.models.OrderItem;
import com.camaja.catapp.repository.OrderItemRepository;
import com.camaja.catapp.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orderProduct")
@CrossOrigin
public class OrderItemController {
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    OrderRepository orderRepository;

    @GetMapping
    Iterable<OrderItem> getActiveOrderProduct() {
        List<OrderItem> orderItems = (List<OrderItem>) orderItemRepository.findAll();
        List<OrderItem> activeOrderItems = orderItems.stream().filter(orderItem -> orderItem.getFlagActive() == null || orderItem.getFlagActive() == true).collect(Collectors.toList());
        return activeOrderItems;
    }

    @GetMapping("/{id}")
    Optional<OrderItem> read(@PathVariable int id) {
        return orderItemRepository.findById(id);
    }

    @GetMapping("/order/{id}")
    Iterable<OrderItem> findByOrder(@PathVariable int id) {
        Optional<Order> order = orderRepository.findById(id);
        return orderItemRepository.findByOrder(order);
    }

    @PostMapping
    OrderItem save(@RequestBody OrderItem orderItem) {
        orderItem.setA$date$(new Timestamp(System.currentTimeMillis()));

        if (orderItem.getQuantity() == null) {
            orderItem.setQuantity(0);
        }

        orderItemRepository.save(orderItem);
        return orderItem;
    }

    @PutMapping
    OrderItem update(@RequestBody OrderItem orderItem) {
        orderItem.setA$date$(new Timestamp(System.currentTimeMillis()));

        if (orderItem.getQuantity() == null) {
            orderItem.setQuantity(0);
        }

        orderItemRepository.save(orderItem);
        return orderItem;
    }

    @GetMapping("/inactivate/{id}")
    OrderItem inactivateOrderProduct(@PathVariable int id) {
        Optional<OrderItem> orderProductOptional = orderItemRepository.findById(id);

        if (orderProductOptional.isPresent()) {
            OrderItem orderItem = orderProductOptional.get();
            orderItem.setFlagActive(false);
            orderItem.setOrder(null);
            orderItem.setA$date$(new Timestamp(System.currentTimeMillis()));
            orderItemRepository.save(orderItem);
            return orderItem;
        } else {
            return null;
        }
    }
}
