package com.camaja.catapp.controllers;

import com.camaja.catapp.models.Contact;
import com.camaja.catapp.models.OrderStatus;
import com.camaja.catapp.repository.OrderStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orderStatus")
@CrossOrigin
public class OrderStatusController {
    @Autowired
    OrderStatusRepository orderStatusRepository;

    @GetMapping
    Iterable<OrderStatus> getActiveOrderStatuses() {
        List<OrderStatus> orderStatuses = (List<OrderStatus>) orderStatusRepository.findAll();
        List<OrderStatus> activeOrderStatuses = orderStatuses.stream().filter(orderStatus -> orderStatus.getFlagActive() == null || orderStatus.getFlagActive() == true).collect(Collectors.toList());
        return activeOrderStatuses;
    }

    @GetMapping("/{id}")
    Optional<OrderStatus> read(@PathVariable int id) {
        return orderStatusRepository.findById(id);
    }

    @GetMapping("/inactivate/{id}")
    OrderStatus inactivateOrderStatus(@PathVariable int id) {
        Optional<OrderStatus> orderStatusOptional = orderStatusRepository.findById(id);

        if (orderStatusOptional.isPresent()) {
            OrderStatus orderStatus = orderStatusOptional.get();
            orderStatus.setFlagActive(false);
            orderStatus.setA$user$(new Timestamp(System.currentTimeMillis()));
            orderStatusRepository.save(orderStatus);
            return orderStatus;
        } else {
            return null;
        }
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
