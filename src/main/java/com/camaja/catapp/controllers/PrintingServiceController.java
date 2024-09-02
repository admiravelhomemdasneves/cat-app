package com.camaja.catapp.controllers;

import com.camaja.catapp.models.PrintingService;
import com.camaja.catapp.services.PrintingServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Optional;

@RestController
@RequestMapping("/printingService")
@CrossOrigin
public class PrintingServiceController {
    @Autowired
    PrintingServiceService printingServiceService;

    @GetMapping
    Iterable<PrintingService> read() {
        return printingServiceService.findAll();
    }

    @GetMapping("/{id}")
    Optional<PrintingService> read(@PathVariable int id) {
        return printingServiceService.findById(id);
    }

    @PostMapping
    PrintingService save(@RequestBody PrintingService printingService) {
        printingService.setA$user$(new Timestamp(System.currentTimeMillis()));
        printingServiceService.save(printingService);
        return printingService;
    }

    @PutMapping
    PrintingService update(@RequestBody PrintingService printingService) {
        printingService.setA$user$(new Timestamp(System.currentTimeMillis()));
        printingServiceService.save(printingService);
        return printingService;
    }

    @DeleteMapping("/{id}")
    Optional<PrintingService> delete(@PathVariable int id) {
        printingServiceService.deleteById(id);
        return read(id);
    }
}
