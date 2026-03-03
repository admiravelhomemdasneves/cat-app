package com.camaja.catapp.controllers;

import com.camaja.catapp.dto.ContactDTO;
import com.camaja.catapp.services.BackOfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/bo")
@RestController
@CrossOrigin
public class BackOfficeController {
    @Autowired
    BackOfficeService backOfficeService;

    @GetMapping("/contact")
    public ResponseEntity<List<ContactDTO>> getActiveContacts() {
        List<ContactDTO> contactDTO = backOfficeService.getAllContacts();
        List<ContactDTO> activeContacts = contactDTO.stream()
                .filter(contact -> !Boolean.FALSE.equals(contact.getFlagActive()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(activeContacts);
    }

    @GetMapping("/contact/{id}")
    public ResponseEntity<ContactDTO> getContactById(@PathVariable Long id) {
        ContactDTO contactDTO = backOfficeService.getContactById(id.intValue());
        return ResponseEntity.ok(contactDTO);
    }

    @GetMapping("/contact/inactivate/{id}")
    public ResponseEntity<ContactDTO> inactivateContactById(@PathVariable Long id) {
        ContactDTO contactDTO = backOfficeService.getContactById(id.intValue());
        contactDTO.setFlagActive(false);
        backOfficeService.saveContactDTO(contactDTO);
        return ResponseEntity.ok(contactDTO);
    }

    @PostMapping("/contact")
    public ResponseEntity<ContactDTO> saveContact(@RequestBody ContactDTO contactDTO) {
        backOfficeService.saveContactDTO(contactDTO);
        return ResponseEntity.ok(contactDTO);
    }


}
