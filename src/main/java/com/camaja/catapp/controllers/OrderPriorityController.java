package com.camaja.catapp.controllers;

import com.camaja.catapp.models.Contact;
import com.camaja.catapp.models.Order;
import com.camaja.catapp.models.OrderPriority;
import com.camaja.catapp.repository.OrderPriorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orderPriority")
@CrossOrigin
public class OrderPriorityController {
    @Autowired
    OrderPriorityRepository orderPriorityRepository;

    @GetMapping
    Iterable<OrderPriority> getActiveOrderPriorities() {
        List<OrderPriority> orderPriorities = (List<OrderPriority>) orderPriorityRepository.findAll();
        List<OrderPriority> activeOrderPriorities = orderPriorities.stream().filter(orderPriority -> orderPriority.getFlagActive() == null || orderPriority.getFlagActive() == true).collect(Collectors.toList());
        return activeOrderPriorities;
    }

    @GetMapping("/{id}")
    Optional<OrderPriority> read(@PathVariable int id) {
        return orderPriorityRepository.findById(id);
    }

    @GetMapping("/inactivate/{id}")
    OrderPriority inactivateOrderPriority(@PathVariable int id) {
        Optional<OrderPriority> orderPriorityOptional = orderPriorityRepository.findById(id);

        if (orderPriorityOptional.isPresent()) {
            OrderPriority orderPriority = orderPriorityOptional.get();
            orderPriority.setFlagActive(false);
            orderPriority.setA$user$(new Timestamp(System.currentTimeMillis()));
            orderPriorityRepository.save(orderPriority);
            return orderPriority;
        } else {
            return null;
        }
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
