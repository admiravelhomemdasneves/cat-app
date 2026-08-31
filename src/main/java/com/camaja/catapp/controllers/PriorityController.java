package com.camaja.catapp.controllers;

import com.camaja.catapp.configs.SecurityUtils;
import com.camaja.catapp.models.Priority;
import com.camaja.catapp.repository.PriorityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/orderPriority")
@CrossOrigin
public class PriorityController {
    private final PriorityRepository priorityRepository;
    private final SecurityUtils securityUtils;

    @GetMapping
    List<Priority> getActiveOrderPriorities() {
        return priorityRepository.findVisibleByCompany(securityUtils.getCurrentCompany());
    }

    @GetMapping("/{id}")
    Optional<Priority> read(@PathVariable Long id) {
        return priorityRepository.findById(id);
    }

    @GetMapping("/inactivate/{id}")
    Priority inactivateOrderPriority(@PathVariable Long id) {
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
        if (!securityUtils.isAdmin()) {
            priority.setCompany(securityUtils.getCurrentCompany());
        }
        priority.setA$user$(new Timestamp(System.currentTimeMillis()));
        priorityRepository.save(priority);
        return priority;
    }

    @PutMapping
    Priority update(@RequestBody Priority priority) {
        if (!securityUtils.isAdmin()) {
            priority.setCompany(securityUtils.getCurrentCompany());
        }
        priority.setA$user$(new Timestamp(System.currentTimeMillis()));
        priorityRepository.save(priority);
        return priority;
    }

    @DeleteMapping("/{id}")
    Optional<Priority> delete(@PathVariable Long id) {
        priorityRepository.deleteById(id);
        return read(id);
    }
}
