package com.camaja.catapp.mappers;

import com.camaja.catapp.dto.ContactDTO;
import com.camaja.catapp.models.Address;
import com.camaja.catapp.models.Contact;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class ContactMapper {
    public ContactDTO toDTO(Contact value) {
        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setIdContact(value.getIdContact());
        contactDTO.setFirstName(value.getFirstName());
        contactDTO.setLastName(value.getLastName());
        contactDTO.setNif(value.getNif());
        contactDTO.setEmail(value.getEmail());
        contactDTO.setPhoneNumber(value.getPhoneNumber());
        contactDTO.setFlagActive(value.getFlagActive());

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

    public Contact toEntity(ContactDTO value) {
        Contact contact = new Contact();
        contact.setIdContact(value.getIdContact());
        contact.setFirstName(value.getFirstName());
        contact.setLastName(value.getLastName());
        contact.setNif(value.getNif());
        contact.setEmail(value.getEmail());
        contact.setPhoneNumber(value.getPhoneNumber());
        contact.setA$date$(Timestamp.from(Instant.now()));
        contact.setFlagActive(value.getFlagActive());

        List<Address> addresses = new ArrayList<>();

        if (value.getShippingAddress() != null) {
            addresses.add(value.getShippingAddress());
        }

        if (value.getBillingAddress() != null) {
            addresses.add(value.getBillingAddress());
        }

        contact.setAddresses(addresses);

        return contact;
    }
}
