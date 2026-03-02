package com.camaja.catapp.services;

import com.camaja.catapp.dto.ContactDTO;
import com.camaja.catapp.models.Address;
import com.camaja.catapp.models.Contact;
import com.camaja.catapp.repository.AddressRepository;
import com.camaja.catapp.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BackOfficeService {
    @Autowired
    ContactRepository contactRepository;

    @Autowired
    AddressRepository addressRepository;

    private ContactDTO mapToContactDTO(Contact value) {
        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setIdContact(value.getIdContact());
        contactDTO.setFirstName(value.getFirstName());
        contactDTO.setLastName(value.getLastName());
        contactDTO.setNif(value.getNif());
        contactDTO.setEmail(value.getEmail());
        contactDTO.setPhoneNumber(value.getPhoneNumber());

        value.getAddresses().stream()
                .filter(a -> a.getFlagActive() && "Shipping".equals(a.getAddressType()))
                .min(Comparator.comparingLong(Address::getId_address))
                .ifPresent(contactDTO::setShippingAddress);
        value.getAddresses().stream()
                .filter(a -> a.getFlagActive() && "Billing".equals(a.getAddressType()))
                .min(Comparator.comparingLong(Address::getId_address))
                .ifPresent(contactDTO::setBillingAddress);

        return contactDTO;
    }

    public ContactDTO getContactById(Integer idContact) {
        return contactRepository.findById(idContact)
                .map(this::mapToContactDTO)
                .orElse(null); // or throw new EntityNotFoundException(...)
    }

    public List<ContactDTO> getAllContacts() {
        List<ContactDTO> contacts = new ArrayList<>();
        contactRepository.findAll().forEach(contact -> contacts.add(mapToContactDTO(contact)));
        return contacts;
    }
}
