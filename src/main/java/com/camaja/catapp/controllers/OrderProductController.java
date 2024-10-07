package com.camaja.catapp.controllers;

import com.camaja.catapp.dto.CompleteOrderProductDTO;
import com.camaja.catapp.models.Order;
import com.camaja.catapp.models.OrderProduct;
import com.camaja.catapp.models.OrderProductSpecs;
import com.camaja.catapp.repository.OrderProductRepository;
import com.camaja.catapp.repository.OrderProductSpecsRepository;
import com.camaja.catapp.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Optional;

@RestController
@RequestMapping("/orderProduct")
@CrossOrigin
public class OrderProductController {
    @Autowired
    OrderProductRepository orderProductRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderProductSpecsRepository orderProductSpecsRepository;

    @GetMapping
    Iterable<OrderProduct> read() {
        return orderProductRepository.findAll();
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

    @GetMapping("/complete/{id}")
    CompleteOrderProductDTO readCompleteOrderProduct(@PathVariable int id) {
        Optional<OrderProduct> orderProduct = orderProductRepository.findById(id);

        if (orderProduct.isPresent()) {
            Iterable<OrderProductSpecs> orderProductSpecs = orderProductSpecsRepository.findByOrderProduct(orderProduct);

            CompleteOrderProductDTO completeOrderProductDTO = new CompleteOrderProductDTO(orderProduct.get(), orderProductSpecs);

            return completeOrderProductDTO;
        } else {
            return null;
        }
    }

    @PostMapping
    OrderProduct save(@RequestBody OrderProduct orderProduct) {
        orderProduct.setA$date$(new Timestamp(System.currentTimeMillis()));
        orderProductRepository.save(orderProduct);
        return orderProduct;
    }

    @PutMapping
    OrderProduct update(@RequestBody OrderProduct orderProduct) {
        orderProduct.setA$date$(new Timestamp(System.currentTimeMillis()));
        orderProductRepository.save(orderProduct);
        return orderProduct;
    }

    @DeleteMapping("/{id}")
    Optional<OrderProduct> delete(@PathVariable int id) {
        try {
            orderProductRepository.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return read(id);
    }
}
