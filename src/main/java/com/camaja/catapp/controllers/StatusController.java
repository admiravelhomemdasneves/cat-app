package com.camaja.catapp.controllers;

import com.camaja.catapp.models.Status;
import com.camaja.catapp.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orderStatus")
@CrossOrigin
public class StatusController {
    @Autowired
    StatusRepository statusRepository;

    @GetMapping
    Iterable<Status> getActiveOrderStatuses() {
        List<Status> statuses = (List<Status>) statusRepository.findAll();
        List<Status> activeStatuses = statuses.stream().filter(status -> status.getFlagActive() == null || status.getFlagActive() == true).collect(Collectors.toList());
        return activeStatuses;
    }

    @GetMapping("/{id}")
    Optional<Status> read(@PathVariable int id) {
        return statusRepository.findById(id);
    }

    @GetMapping("/inactivate/{id}")
    Status inactivateOrderStatus(@PathVariable int id) {
        Optional<Status> orderStatusOptional = statusRepository.findById(id);

        if (orderStatusOptional.isPresent()) {
            Status status = orderStatusOptional.get();
            status.setFlagActive(false);
            status.setA$user$(new Timestamp(System.currentTimeMillis()));
            statusRepository.save(status);
            return status;
        } else {
            return null;
        }
    }

    @PostMapping
    Status save(@RequestBody Status status) {
        status.setA$user$(new Timestamp(System.currentTimeMillis()));
        statusRepository.save(status);
        return status;
    }

    @PutMapping
    Status update(@RequestBody Status status) {
        status.setA$user$(new Timestamp(System.currentTimeMillis()));
        statusRepository.save(status);
        return status;
    }

    @DeleteMapping("/{id}")
    Optional<Status> delete(@PathVariable int id) {
        statusRepository.deleteById(id);
        return read(id);
    }
}
