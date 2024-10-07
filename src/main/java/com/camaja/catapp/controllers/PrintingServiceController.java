package com.camaja.catapp.controllers;

import com.camaja.catapp.models.PrintingService;
import com.camaja.catapp.repository.PrintingServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Optional;

@RestController
@RequestMapping("/printingService")
@CrossOrigin
public class PrintingServiceController {
    @Autowired
    PrintingServiceRepository printingServiceRepository;

    @GetMapping
    Iterable<PrintingService> read() {
        return printingServiceRepository.findAll();
    }

    @GetMapping("/{id}")
    Optional<PrintingService> read(@PathVariable int id) {
        return printingServiceRepository.findById(id);
    }

    @PostMapping
    PrintingService save(@RequestBody PrintingService printingService) {
        printingService.setA$user$(new Timestamp(System.currentTimeMillis()));
        printingServiceRepository.save(printingService);
        return printingService;
    }

    @PutMapping
    PrintingService update(@RequestBody PrintingService printingService) {
        printingService.setA$user$(new Timestamp(System.currentTimeMillis()));
        printingServiceRepository.save(printingService);
        return printingService;
    }

    @DeleteMapping("/{id}")
    Optional<PrintingService> delete(@PathVariable int id) {
        printingServiceRepository.deleteById(id);
        return read(id);
    }
}
