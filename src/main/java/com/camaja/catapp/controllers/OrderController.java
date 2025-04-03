package com.camaja.catapp.controllers;

import com.camaja.catapp.models.*;
import com.camaja.catapp.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
@CrossOrigin
public class OrderController {
    @Autowired
    OrderRepository orderRepository;

    @GetMapping
    Iterable<Order> getActiveOrders() {
        List<Order> orders = (List<Order>) orderRepository.findAll();
        List<Order> activeOrders = orders.stream().filter(order -> order.getFlagActive() == null || order.getFlagActive() == true).collect(Collectors.toList());
        return activeOrders;
    }

    @GetMapping("/{id}")
    Optional<Order> read(@PathVariable int id) {
        return orderRepository.findById(id);
    }

    @GetMapping("/inactivate/{id}")
    Order inactivateOrder(@PathVariable int id) {
        Optional<Order> orderOptional = orderRepository.findById(id);

        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setFlagActive(false);
            order.setA$date$(new Timestamp(System.currentTimeMillis()));
            orderRepository.save(order);
            return order;
        } else {
            return null;
        }
    }

    @PostMapping("/new")
    Order createNewOrder(@RequestBody Order order) {
        order.setDate_requested(new Timestamp(System.currentTimeMillis()));
        order.setA$date$(new Timestamp(System.currentTimeMillis()));
        order.setId_order(null);
        orderRepository.save(order);
        return order;
    }

    @PostMapping
    Order save(@RequestBody Order order) {
        if (order.getDate_requested() == null) {order.setDate_requested(new Timestamp(System.currentTimeMillis()));}
        order.setA$date$(new Timestamp(System.currentTimeMillis()));
        orderRepository.save(order);
        return order;
    }

    @PutMapping
    Order update(@RequestBody Order order) {
        if (order.getDate_requested() == null) {order.setDate_requested(new Timestamp(System.currentTimeMillis()));}
        order.setA$date$(new Timestamp(System.currentTimeMillis()));
        orderRepository.save(order);
        return order;
    }

    @DeleteMapping("/{id}")
    Optional<Order> delete(@PathVariable int id) {
        orderRepository.deleteById(id);
        return read(id);
    }
}
