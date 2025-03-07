package com.camaja.catapp.controllers;

import com.camaja.catapp.models.OrderStatus;
import com.camaja.catapp.models.PrintingDetail;
import com.camaja.catapp.repository.PrintingDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/printingDetail")
@CrossOrigin
public class PrintingDetailController {
    @Autowired
    PrintingDetailRepository printingDetailRepository;

    @GetMapping
    Iterable<PrintingDetail> getActivePrintingDetails() {
        List<PrintingDetail> printingDetails = (List<PrintingDetail>) printingDetailRepository.findAll();
        List<PrintingDetail> activePrintingDetails = printingDetails.stream().filter(printingDetail -> printingDetail.getFlagActive() == null || printingDetail.getFlagActive() == true).collect(Collectors.toList());
        return activePrintingDetails;
    }

    @GetMapping("/{id}")
    Optional<PrintingDetail> read(@PathVariable int id) {
        return printingDetailRepository.findById(id);
    }

    @PostMapping
    PrintingDetail save(@RequestBody PrintingDetail printingDetail) {
        printingDetail.setA$user$(new Timestamp(System.currentTimeMillis()));
        printingDetailRepository.save(printingDetail);
        return printingDetail;
    }

    @PutMapping
    PrintingDetail update(@RequestBody PrintingDetail printingDetail) {
        printingDetail.setA$user$(new Timestamp(System.currentTimeMillis()));
        printingDetailRepository.save(printingDetail);
        return printingDetail;
    }

    @DeleteMapping("/{id}")
    Optional<PrintingDetail> delete(@PathVariable int id) {
        printingDetailRepository.deleteById(id);
        return read(id);
    }
}
