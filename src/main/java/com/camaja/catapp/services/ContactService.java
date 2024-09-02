package com.camaja.catapp.services;

import com.camaja.catapp.models.Contact;
import org.springframework.data.repository.CrudRepository;

public interface ContactService extends CrudRepository<Contact, Integer> {
}
