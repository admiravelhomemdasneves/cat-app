package com.camaja.catapp.controllers;

import com.camaja.catapp.models.OrderPriority;
import com.camaja.catapp.services.OrderPriorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Optional;

@RestController
@RequestMapping("/orderPriority")
@CrossOrigin
public class OrderPriorityController {
    @Autowired
    OrderPriorityService orderPriorityService;

    @GetMapping
    Iterable<OrderPriority> read() {
        return orderPriorityService.findAll();
    }

    @GetMapping("/{id}")
    Optional<OrderPriority> read(@PathVariable int id) {
        return orderPriorityService.findById(id);
    }

    @PostMapping
    OrderPriority save(@RequestBody OrderPriority orderPriority) {
        orderPriority.setA$user$(new Timestamp(System.currentTimeMillis()));
        orderPriorityService.save(orderPriority);
        return orderPriority;
    }

    @PutMapping
    OrderPriority update(@RequestBody OrderPriority orderPriority) {
        orderPriority.setA$user$(new Timestamp(System.currentTimeMillis()));
        orderPriorityService.save(orderPriority);
        return orderPriority;
    }

    @DeleteMapping("/{id}")
    Optional<OrderPriority> delete(@PathVariable int id) {
        orderPriorityService.deleteById(id);
        return read(id);
    }
}
