package com.camaja.catapp.controllers;

import com.camaja.catapp.models.OrderStatus;
import com.camaja.catapp.services.OrderStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Optional;

@RestController
@RequestMapping("/orderStatus")
@CrossOrigin
public class OrderStatusController {
    @Autowired
    OrderStatusService orderStatusService;

    @GetMapping
    Iterable<OrderStatus> read() {
        return orderStatusService.findAll();
    }

    @GetMapping("/{id}")
    Optional<OrderStatus> read(@PathVariable int id) {
        return orderStatusService.findById(id);
    }

    @PostMapping
    OrderStatus save(@RequestBody OrderStatus orderStatus) {
        orderStatus.setA$user$(new Timestamp(System.currentTimeMillis()));
        orderStatusService.save(orderStatus);
        return orderStatus;
    }

    @PutMapping
    OrderStatus update(@RequestBody OrderStatus orderStatus) {
        orderStatus.setA$user$(new Timestamp(System.currentTimeMillis()));
        orderStatusService.save(orderStatus);
        return orderStatus;
    }

    @DeleteMapping("/{id}")
    Optional<OrderStatus> delete(@PathVariable int id) {
        orderStatusService.deleteById(id);
        return read(id);
    }
}
