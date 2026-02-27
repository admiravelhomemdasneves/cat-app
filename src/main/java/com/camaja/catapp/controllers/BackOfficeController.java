package com.camaja.catapp.controllers;

import com.camaja.catapp.dto.ContactDTO;
import com.camaja.catapp.services.BackOfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/bo")
@RestController
@CrossOrigin
public class BackOfficeController {
    @Autowired
    BackOfficeService backOfficeService;

    @GetMapping("/contact")
    public ResponseEntity<List<ContactDTO>> getContacts() {
        List<ContactDTO> contactDTO = backOfficeService.getAllContacts();
        return ResponseEntity.ok(contactDTO);
    }

    @GetMapping("/contact/{id}")
    public ResponseEntity<ContactDTO> getContactById(@PathVariable Long id) {
        ContactDTO contactDTO = backOfficeService.getContactById(id.intValue());
        return ResponseEntity.ok(contactDTO);
    }
}
