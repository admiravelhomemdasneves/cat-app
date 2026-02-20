package com.camaja.catapp.controllers;

import com.camaja.catapp.models.Priority;
import com.camaja.catapp.repository.PriorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orderPriority")
@CrossOrigin
public class PriorityController {
    @Autowired
    PriorityRepository priorityRepository;

    @GetMapping
    Iterable<Priority> getActiveOrderPriorities() {
        List<Priority> orderPriorities = (List<Priority>) priorityRepository.findAll();
        List<Priority> activeOrderPriorities = orderPriorities.stream().filter(priority -> priority.getFlagActive() == null || priority.getFlagActive() == true).collect(Collectors.toList());
        return activeOrderPriorities;
    }

    @GetMapping("/{id}")
    Optional<Priority> read(@PathVariable int id) {
        return priorityRepository.findById(id);
    }

    @GetMapping("/inactivate/{id}")
    Priority inactivateOrderPriority(@PathVariable int id) {
        Optional<Priority> orderPriorityOptional = priorityRepository.findById(id);

        if (orderPriorityOptional.isPresent()) {
            Priority priority = orderPriorityOptional.get();
            priority.setFlagActive(false);
            priority.setA$user$(new Timestamp(System.currentTimeMillis()));
            priorityRepository.save(priority);
            return priority;
        } else {
            return null;
        }
    }

    @PostMapping
    Priority save(@RequestBody Priority priority) {
        priority.setA$user$(new Timestamp(System.currentTimeMillis()));
        priorityRepository.save(priority);
        return priority;
    }

    @PutMapping
    Priority update(@RequestBody Priority priority) {
        priority.setA$user$(new Timestamp(System.currentTimeMillis()));
        priorityRepository.save(priority);
        return priority;
    }

    @DeleteMapping("/{id}")
    Optional<Priority> delete(@PathVariable int id) {
        priorityRepository.deleteById(id);
        return read(id);
    }
}
