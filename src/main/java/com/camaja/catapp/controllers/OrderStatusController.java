package com.camaja.catapp.controllers;

import com.camaja.catapp.models.OrderStatus;
import com.camaja.catapp.repository.OrderStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Optional;

@RestController
@RequestMapping("/orderStatus")
@CrossOrigin
public class OrderStatusController {
    @Autowired
    OrderStatusRepository orderStatusRepository;

    @GetMapping
    Iterable<OrderStatus> read() {
        return orderStatusRepository.findAll();
    }

    @GetMapping("/{id}")
    Optional<OrderStatus> read(@PathVariable int id) {
        return orderStatusRepository.findById(id);
    }

    @PostMapping
    OrderStatus save(@RequestBody OrderStatus orderStatus) {
        orderStatus.setA$user$(new Timestamp(System.currentTimeMillis()));
        orderStatusRepository.save(orderStatus);
        return orderStatus;
    }

    @PutMapping
    OrderStatus update(@RequestBody OrderStatus orderStatus) {
        orderStatus.setA$user$(new Timestamp(System.currentTimeMillis()));
        orderStatusRepository.save(orderStatus);
        return orderStatus;
    }

    @DeleteMapping("/{id}")
    Optional<OrderStatus> delete(@PathVariable int id) {
        orderStatusRepository.deleteById(id);
        return read(id);
    }
}
