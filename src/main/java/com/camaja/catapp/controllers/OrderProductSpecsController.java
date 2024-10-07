package com.camaja.catapp.controllers;

import com.camaja.catapp.models.OrderProduct;
import com.camaja.catapp.models.OrderProductSpecs;
import com.camaja.catapp.repository.OrderProductRepository;
import com.camaja.catapp.repository.OrderProductSpecsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/orderProductSpecs")
@CrossOrigin
public class OrderProductSpecsController {
    @Autowired
    OrderProductSpecsRepository orderProductSpecsRepository;
    @Autowired
    OrderProductRepository orderProductRepository;

    @GetMapping
    Iterable<OrderProductSpecs> read() {
        return orderProductSpecsRepository.findAll();
    }

    @GetMapping("/{id}")
    Optional<OrderProductSpecs> read(@PathVariable int id) {
        return orderProductSpecsRepository.findById(id);
    }

    @GetMapping("/orderProduct/{id}")
    Iterable<OrderProductSpecs> findByOrderProduct(@PathVariable int id) {
        Optional<OrderProduct> orderProduct = orderProductRepository.findById(id);
        return orderProductSpecsRepository.findByOrderProduct(orderProduct);
    }

    @PostMapping
    OrderProductSpecs save(@RequestBody OrderProductSpecs orderProductSpecs) {
        orderProductSpecsRepository.save(orderProductSpecs);
        return orderProductSpecs;
    }

    @PutMapping
    OrderProductSpecs update(@RequestBody OrderProductSpecs orderProductSpecs) {
        orderProductSpecsRepository.save(orderProductSpecs);
        return orderProductSpecs;
    }

    @DeleteMapping("/{id}")
    Optional<OrderProductSpecs> delete(@PathVariable int id) {
        orderProductSpecsRepository.deleteById(id);
        return read(id);
    }
}
