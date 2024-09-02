package com.camaja.catapp.controllers;

import com.camaja.catapp.models.PrintingDetail;
import com.camaja.catapp.services.PrintingDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Optional;

@RestController
@RequestMapping("/printingDetail")
@CrossOrigin
public class PrintingDetailController {
    @Autowired
    PrintingDetailService printingDetailService;

    @GetMapping
    Iterable<PrintingDetail> read() {
        return printingDetailService.findAll();
    }

    @GetMapping("/{id}")
    Optional<PrintingDetail> read(@PathVariable int id) {
        return printingDetailService.findById(id);
    }

    @PostMapping
    PrintingDetail save(@RequestBody PrintingDetail printingDetail) {
        printingDetail.setA$user$(new Timestamp(System.currentTimeMillis()));
        printingDetailService.save(printingDetail);
        return printingDetail;
    }

    @PutMapping
    PrintingDetail update(@RequestBody PrintingDetail printingDetail) {
        printingDetail.setA$user$(new Timestamp(System.currentTimeMillis()));
        printingDetailService.save(printingDetail);
        return printingDetail;
    }

    @DeleteMapping("/{id}")
    Optional<PrintingDetail> delete(@PathVariable int id) {
        printingDetailService.deleteById(id);
        return read(id);
    }
}
