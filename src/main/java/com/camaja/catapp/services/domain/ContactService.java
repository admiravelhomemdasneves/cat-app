package com.camaja.catapp.services.domain;

import com.camaja.catapp.configs.SecurityUtils;
import com.camaja.catapp.dto.ContactDTO;
import com.camaja.catapp.mappers.ContactMapper;
import com.camaja.catapp.models.Contact;
import com.camaja.catapp.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ContactService {
    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;
    private final SecurityUtils securityUtils;

    public ContactDTO getContactById(Long idContact) {
        return contactRepository.findById(idContact)
                .map(contactMapper::toDTO)
                .orElse(null);
    }

    public List<ContactDTO> getAllContacts() {
        return contactRepository.findByCompanyAndFlagActive(securityUtils.getCurrentCompany(), true)
                .stream().map(contactMapper::toDTO).toList();
    }

    @Transactional
    public void saveContactDTO(ContactDTO contactDTO) {
        Contact contact = contactMapper.toEntity(contactDTO);
        contact.setCompany(securityUtils.getCurrentCompany());
        contact.getAddresses().forEach(address -> address.setContact(contact));
        contactRepository.save(contact);
    }
}
