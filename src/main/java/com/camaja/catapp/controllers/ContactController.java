package com.camaja.catapp.controllers;

import com.camaja.catapp.models.Contact;
import com.camaja.catapp.services.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Optional;

@RestController
@RequestMapping("/contact")
@CrossOrigin
public class ContactController {
    @Autowired
    ContactService contactService;

    @GetMapping
    Iterable<Contact> read() {
        return contactService.findAll();
    }

    @GetMapping("/{id}")
    Optional<Contact> read(@PathVariable int id) {
        return contactService.findById(id);
    }

    @PostMapping
    Contact save(@RequestBody Contact contact) {
        contact.setA$user$(new Timestamp(System.currentTimeMillis()));
        contactService.save(contact);
        return contact;
    }

    @PutMapping
    Contact update(@RequestBody Contact contact) {
        contact.setA$user$(new Timestamp(System.currentTimeMillis()));
        contactService.save(contact);
        return contact;
    }

    @DeleteMapping("/{id}")
    Optional<Contact> delete(@PathVariable int id) {
        try {
            contactService.deleteById(id);
        } catch (Exception e) {
            System.out.println(e);
        }

        return read(id);
    }
}
