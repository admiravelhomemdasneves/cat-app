package com.camaja.catapp.services.domain;

import com.camaja.catapp.dto.ContactDTO;
import com.camaja.catapp.mappers.ContactMapper;
import com.camaja.catapp.models.Contact;
import com.camaja.catapp.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ContactService {
    @Autowired ContactRepository contactRepository;
    @Autowired ContactMapper contactMapper;

    public ContactDTO getContactById(Integer idContact) {
        return contactRepository.findById(idContact)
                .map(contactMapper::toDTO)
                .orElse(null); // or throw new EntityNotFoundException(...)
    }

    public List<ContactDTO> getAllContacts() {
        List<ContactDTO> contacts = new ArrayList<>();
        contactRepository.findAll().forEach(contact -> contacts.add(contactMapper.toDTO(contact)));
        return contacts;
    }

    public void saveContactDTO(ContactDTO contactDTO) {
        Contact contact = contactMapper.toEntity(contactDTO);
        contact.getAddresses().forEach(address -> address.setContact(contact));
        contactRepository.save(contact);
    }
}
