package com.camaja.catapp.controllers;

import com.camaja.catapp.configs.SecurityUtils;
import com.camaja.catapp.models.Status;
import com.camaja.catapp.repository.StatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/orderStatus")
@CrossOrigin
public class StatusController {
    private final StatusRepository statusRepository;
    private final SecurityUtils securityUtils;

    @GetMapping
    List<Status> getActiveOrderStatuses() {
        return statusRepository.findVisibleByCompany(securityUtils.getCurrentCompany());
    }

    @GetMapping("/{id}")
    Optional<Status> read(@PathVariable Long id) {
        return statusRepository.findById(id);
    }

    @GetMapping("/inactivate/{id}")
    Status inactivateOrderStatus(@PathVariable Long id) {
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
        if (!securityUtils.isAdmin()) {
            status.setCompany(securityUtils.getCurrentCompany());
        }
        status.setA$user$(new Timestamp(System.currentTimeMillis()));
        statusRepository.save(status);
        return status;
    }

    @PutMapping
    Status update(@RequestBody Status status) {
        if (!securityUtils.isAdmin()) {
            status.setCompany(securityUtils.getCurrentCompany());
        }
        status.setA$user$(new Timestamp(System.currentTimeMillis()));
        statusRepository.save(status);
        return status;
    }

    @DeleteMapping("/{id}")
    Optional<Status> delete(@PathVariable Long id) {
        statusRepository.deleteById(id);
        return read(id);
    }
}
