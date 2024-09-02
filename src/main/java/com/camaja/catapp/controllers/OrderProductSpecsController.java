package com.camaja.catapp.controllers;

import com.camaja.catapp.models.Order;
import com.camaja.catapp.models.OrderProduct;
import com.camaja.catapp.models.OrderProductSpecs;
import com.camaja.catapp.services.OrderProductService;
import com.camaja.catapp.services.OrderProductSpecsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/orderProductSpecs")
@CrossOrigin
public class OrderProductSpecsController {
    @Autowired
    OrderProductSpecsService orderProductSpecsService;
    @Autowired
    OrderProductService orderProductService;

    @GetMapping
    Iterable<OrderProductSpecs> read() {
        return orderProductSpecsService.findAll();
    }

    @GetMapping("/{id}")
    Optional<OrderProductSpecs> read(@PathVariable int id) {
        return orderProductSpecsService.findById(id);
    }

    @GetMapping("/orderProduct/{id}")
    Iterable<OrderProductSpecs> findByOrderProduct(@PathVariable int id) {
        Optional<OrderProduct> orderProduct = orderProductService.findById(id);
        return orderProductSpecsService.findByOrderProduct(orderProduct);
    }

    @PostMapping
    OrderProductSpecs save(@RequestBody OrderProductSpecs orderProductSpecs) {
        orderProductSpecsService.save(orderProductSpecs);
        return orderProductSpecs;
    }

    @PutMapping
    OrderProductSpecs update(@RequestBody OrderProductSpecs orderProductSpecs) {
        orderProductSpecsService.save(orderProductSpecs);
        return orderProductSpecs;
    }

    @DeleteMapping("/{id}")
    Optional<OrderProductSpecs> delete(@PathVariable int id) {
        orderProductSpecsService.deleteById(id);
        return read(id);
    }
}
