package com.camaja.catapp.controllers;

import com.camaja.catapp.models.OrderPriority;
import com.camaja.catapp.repository.OrderPriorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Optional;

@RestController
@RequestMapping("/orderPriority")
@CrossOrigin
public class OrderPriorityController {
    @Autowired
    OrderPriorityRepository orderPriorityRepository;

    @GetMapping
    Iterable<OrderPriority> read() {
        return orderPriorityRepository.findAll();
    }

    @GetMapping("/{id}")
    Optional<OrderPriority> read(@PathVariable int id) {
        return orderPriorityRepository.findById(id);
    }

    @PostMapping
    OrderPriority save(@RequestBody OrderPriority orderPriority) {
        orderPriority.setA$user$(new Timestamp(System.currentTimeMillis()));
        orderPriorityRepository.save(orderPriority);
        return orderPriority;
    }

    @PutMapping
    OrderPriority update(@RequestBody OrderPriority orderPriority) {
        orderPriority.setA$user$(new Timestamp(System.currentTimeMillis()));
        orderPriorityRepository.save(orderPriority);
        return orderPriority;
    }

    @DeleteMapping("/{id}")
    Optional<OrderPriority> delete(@PathVariable int id) {
        orderPriorityRepository.deleteById(id);
        return read(id);
    }
}
