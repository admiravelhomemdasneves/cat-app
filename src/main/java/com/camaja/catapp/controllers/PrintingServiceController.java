package com.camaja.catapp.controllers;

import com.camaja.catapp.configs.SecurityUtils;
import com.camaja.catapp.models.PrintingService;
import com.camaja.catapp.repository.PrintingServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/printingService")
@CrossOrigin
public class PrintingServiceController {
    private final PrintingServiceRepository printingServiceRepository;
    private final SecurityUtils securityUtils;

    @GetMapping
    List<PrintingService> getActivePrintingServices() {
        return printingServiceRepository.findVisibleByCompany(securityUtils.getCurrentCompany());
    }

    @GetMapping("/{id}")
    Optional<PrintingService> read(@PathVariable Long id) {
        return printingServiceRepository.findById(id);
    }

    @GetMapping("/inactivate/{id}")
    PrintingService inactivatePrintingService(@PathVariable Long id) {
        Optional<PrintingService> printingServiceOptional = printingServiceRepository.findById(id);

        if (printingServiceOptional.isPresent()) {
            PrintingService printingService = printingServiceOptional.get();
            printingService.setFlagActive(false);
            printingService.setA$user$(new Timestamp(System.currentTimeMillis()));
            printingServiceRepository.save(printingService);
            return printingService;
        } else {
            return null;
        }
    }

    @PostMapping
    PrintingService save(@RequestBody PrintingService printingService) {
        if (!securityUtils.isAdmin()) {
            printingService.setCompany(securityUtils.getCurrentCompany());
        }
        printingService.setA$user$(new Timestamp(System.currentTimeMillis()));
        printingServiceRepository.save(printingService);
        return printingService;
    }

    @PutMapping
    PrintingService update(@RequestBody PrintingService printingService) {
        if (!securityUtils.isAdmin()) {
            printingService.setCompany(securityUtils.getCurrentCompany());
        }
        printingService.setA$user$(new Timestamp(System.currentTimeMillis()));
        printingServiceRepository.save(printingService);
        return printingService;
    }

    @DeleteMapping("/{id}")
    Optional<PrintingService> delete(@PathVariable Long id) {
        printingServiceRepository.deleteById(id);
        return read(id);
    }
}
