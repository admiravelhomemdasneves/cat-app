package com.camaja.catapp.controllers;

import com.camaja.catapp.models.Contact;
import com.camaja.catapp.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/contact")
@CrossOrigin
public class ContactController {
    private final ContactRepository contactRepository;

    @GetMapping
    Iterable<Contact> getActiveContacts() {
        List<Contact> contacts = (List<Contact>) contactRepository.findAll();
        List<Contact> activeContacts = contacts.stream().filter(contact -> contact.getFlagActive() != null && contact.getFlagActive() == true).collect(Collectors.toList());
        return activeContacts;
    }

    @GetMapping("/{id}")
    Optional<Contact> read(@PathVariable Long id) {
        return contactRepository.findById(id);
    }

    @GetMapping("/inactivate/{id}")
    Contact inactivateContact(@PathVariable Long id) {
        Optional<Contact> contactOptional = contactRepository.findById(id);

        if (contactOptional.isPresent()) {
            Contact contact = contactOptional.get();
            contact.setFlagActive(false);
            contact.setA$date$(new Timestamp(System.currentTimeMillis()));
            contactRepository.save(contact);
            return contact;
        } else {
            return null;
        }
    }

    @PostMapping
    Contact save(@RequestBody Contact contact) {
        contact.setA$date$(new Timestamp(System.currentTimeMillis()));
        contactRepository.save(contact);
        return contact;
    }

    @PutMapping
    Contact update(@RequestBody Contact contact) {
        contact.setA$date$(new Timestamp(System.currentTimeMillis()));
        contactRepository.save(contact);
        return contact;
    }

    @DeleteMapping("/{id}")
    Optional<Contact> delete(@PathVariable Long id) {
        try {
            contactRepository.deleteById(id);
        } catch (Exception e) {
            log.warn("Failed to delete contact {}: {}", id, e.getMessage());
        }

        return read(id);
    }
}
