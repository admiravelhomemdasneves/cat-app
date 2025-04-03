package com.camaja.catapp.controllers;

import com.camaja.catapp.models.Order;
import com.camaja.catapp.models.OrderProduct;
import com.camaja.catapp.repository.OrderProductRepository;
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
public class OrderProductController {
    @Autowired
    OrderProductRepository orderProductRepository;
    @Autowired
    OrderRepository orderRepository;

    @GetMapping
    Iterable<OrderProduct> getActiveOrderProduct() {
        List<OrderProduct> orderProducts = (List<OrderProduct>) orderProductRepository.findAll();
        List<OrderProduct> activeOrderProducts = orderProducts.stream().filter(orderProduct -> orderProduct.getFlagActive() == null || orderProduct.getFlagActive() == true).collect(Collectors.toList());
        return activeOrderProducts;
    }

    @GetMapping("/{id}")
    Optional<OrderProduct> read(@PathVariable int id) {
        return orderProductRepository.findById(id);
    }

    @GetMapping("/order/{id}")
    Iterable<OrderProduct> findByOrder(@PathVariable int id) {
        Optional<Order> order = orderRepository.findById(id);
        return orderProductRepository.findByOrder(order);
    }

    @PostMapping
    OrderProduct save(@RequestBody OrderProduct orderProduct) {
        orderProduct.setA$date$(new Timestamp(System.currentTimeMillis()));

        if (orderProduct.getQuantity() == null) {
            orderProduct.setQuantity(0);
        }

        orderProductRepository.save(orderProduct);
        return orderProduct;
    }

    @PutMapping
    OrderProduct update(@RequestBody OrderProduct orderProduct) {
        orderProduct.setA$date$(new Timestamp(System.currentTimeMillis()));

        if (orderProduct.getQuantity() == null) {
            orderProduct.setQuantity(0);
        }

        orderProductRepository.save(orderProduct);
        return orderProduct;
    }

    @GetMapping("/inactivate/{id}")
    OrderProduct inactivateOrderProduct(@PathVariable int id) {
        Optional<OrderProduct> orderProductOptional = orderProductRepository.findById(id);

        if (orderProductOptional.isPresent()) {
            OrderProduct orderProduct = orderProductOptional.get();
            orderProduct.setFlagActive(false);
            orderProduct.setOrder(null);
            orderProduct.setA$date$(new Timestamp(System.currentTimeMillis()));
            orderProductRepository.save(orderProduct);
            return orderProduct;
        } else {
            return null;
        }
    }
}
