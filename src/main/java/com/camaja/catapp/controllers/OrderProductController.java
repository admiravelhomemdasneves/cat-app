package com.camaja.catapp.controllers;

import com.camaja.catapp.dto.CompleteOrderProductDTO;
import com.camaja.catapp.models.Order;
import com.camaja.catapp.models.OrderProduct;
import com.camaja.catapp.models.OrderProductSpecs;
import com.camaja.catapp.services.OrderProductService;
import com.camaja.catapp.services.OrderProductSpecsService;
import com.camaja.catapp.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Optional;

@RestController
@RequestMapping("/orderProduct")
@CrossOrigin
public class OrderProductController {
    @Autowired
    OrderProductService orderProductService;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderProductSpecsService orderProductSpecsService;

    @GetMapping
    Iterable<OrderProduct> read() {
        return orderProductService.findAll();
    }

    @GetMapping("/{id}")
    Optional<OrderProduct> read(@PathVariable int id) {
        return orderProductService.findById(id);
    }

    @GetMapping("/order/{id}")
    Iterable<OrderProduct> findByOrder(@PathVariable int id) {
        Optional<Order> order = orderService.findById(id);
        return orderProductService.findByOrder(order);
    }

    @GetMapping("/complete/{id}")
    CompleteOrderProductDTO readCompleteOrderProduct(@PathVariable int id) {
        Optional<OrderProduct> orderProduct = orderProductService.findById(id);

        if (orderProduct.isPresent()) {
            Iterable<OrderProductSpecs> orderProductSpecs = orderProductSpecsService.findByOrderProduct(orderProduct);

            CompleteOrderProductDTO completeOrderProductDTO = new CompleteOrderProductDTO(orderProduct.get(), orderProductSpecs);

            return completeOrderProductDTO;
        } else {
            return null;
        }
    }

    @PostMapping
    OrderProduct save(@RequestBody OrderProduct orderProduct) {
        orderProduct.setA$date$(new Timestamp(System.currentTimeMillis()));
        orderProductService.save(orderProduct);
        return orderProduct;
    }

    @PutMapping
    OrderProduct update(@RequestBody OrderProduct orderProduct) {
        orderProduct.setA$date$(new Timestamp(System.currentTimeMillis()));
        orderProductService.save(orderProduct);
        return orderProduct;
    }

    @DeleteMapping("/{id}")
    Optional<OrderProduct> delete(@PathVariable int id) {
        orderProductService.deleteById(id);
        return read(id);
    }
}
