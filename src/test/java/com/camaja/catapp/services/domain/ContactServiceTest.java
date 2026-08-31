package com.camaja.catapp.services.domain;

import com.camaja.catapp.configs.SecurityUtils;
import com.camaja.catapp.dto.ContactDTO;
import com.camaja.catapp.mappers.ContactMapper;
import com.camaja.catapp.models.Company;
import com.camaja.catapp.models.Contact;
import com.camaja.catapp.repository.ContactRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock ContactRepository contactRepository;
    @Mock ContactMapper contactMapper;
    @Mock SecurityUtils securityUtils;

    @InjectMocks ContactService contactService;

    @Test
    void getAllContacts_usesCurrentCompanyAndFlagActiveTrue() {
        Company company = new Company();
        when(securityUtils.getCurrentCompany()).thenReturn(company);
        when(contactRepository.findByCompanyAndFlagActive(company, true))
                .thenReturn(List.of(new Contact(), new Contact()));
        when(contactMapper.toDTO(any(Contact.class))).thenReturn(new ContactDTO());

        List<ContactDTO> result = contactService.getAllContacts();

        assertThat(result).hasSize(2);
        verify(contactRepository).findByCompanyAndFlagActive(company, true);
    }

    @Test
    void saveContactDTO_setsCompanyFromSecurityContext() {
        Company company = new Company();
        when(securityUtils.getCurrentCompany()).thenReturn(company);

        Contact contact = new Contact();
        contact.setAddresses(new ArrayList<>());
        when(contactMapper.toEntity(any(ContactDTO.class))).thenReturn(contact);

        contactService.saveContactDTO(new ContactDTO());

        ArgumentCaptor<Contact> captor = ArgumentCaptor.forClass(Contact.class);
        verify(contactRepository).save(captor.capture());
        assertThat(captor.getValue().getCompany()).isSameAs(company);
    }
}
