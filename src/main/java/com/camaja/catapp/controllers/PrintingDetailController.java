package com.camaja.catapp.controllers;

import com.camaja.catapp.models.PrintingDetail;
import com.camaja.catapp.repository.PrintingDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Optional;

@RestController
@RequestMapping("/printingDetail")
@CrossOrigin
public class PrintingDetailController {
    @Autowired
    PrintingDetailRepository printingDetailRepository;

    @GetMapping
    Iterable<PrintingDetail> read() {
        return printingDetailRepository.findAll();
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
