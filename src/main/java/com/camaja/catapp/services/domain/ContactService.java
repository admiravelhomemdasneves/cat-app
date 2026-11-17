package com.camaja.catapp.services.domain;

import com.camaja.catapp.configs.TenantAccess;
import com.camaja.catapp.dto.ContactDTO;
import com.camaja.catapp.mappers.ContactMapper;
import com.camaja.catapp.models.Address;
import com.camaja.catapp.models.Contact;
import com.camaja.catapp.repository.AddressRepository;
import com.camaja.catapp.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ContactService {
    private final ContactRepository contactRepository;
    private final AddressRepository addressRepository;
    private final ContactMapper contactMapper;
    private final TenantAccess tenantAccess;

    /** The contact if it belongs to the current company, otherwise 404. */
    public Contact getOwnedContact(Long idContact) {
        return contactRepository.findByIdContactAndCompany(idContact, tenantAccess.company())
                .orElseThrow(() -> TenantAccess.notFound("Contact", idContact));
    }

    @Transactional(readOnly = true)
    public ContactDTO getContactById(Long idContact) {
        return contactMapper.toDTO(getOwnedContact(idContact));
    }

    @Transactional(readOnly = true)
    public List<ContactDTO> getAllContacts() {
        return contactRepository.findByCompanyAndFlagActive(tenantAccess.company(), true)
                .stream().map(contactMapper::toDTO).toList();
    }

    @Transactional
    public ContactDTO inactivateContact(Long idContact) {
        Contact contact = getOwnedContact(idContact);
        contact.setFlagActive(false);
        contact.setA$date$(Timestamp.from(Instant.now()));
        contactRepository.save(contact);
        return contactMapper.toDTO(contact);
    }

    @Transactional
    public void saveContactDTO(ContactDTO contactDTO) {
        if (contactDTO.getIdContact() != null) {
            getOwnedContact(contactDTO.getIdContact());
        }
        assertAddressBelongsToContact(contactDTO.getShippingAddress(), contactDTO.getIdContact());
        assertAddressBelongsToContact(contactDTO.getBillingAddress(), contactDTO.getIdContact());

        Contact contact = contactMapper.toEntity(contactDTO);
        contact.setCompany(tenantAccess.company());
        contact.getAddresses().forEach(address -> address.setContact(contact));
        contactRepository.save(contact);
    }

    /** An existing address (with an ID) can only be edited through the contact that owns it. */
    private void assertAddressBelongsToContact(Address address, Long idContact) {
        if (address == null || address.getId_address() == null) {
            return;
        }
        Address existing = addressRepository.findOwned(address.getId_address(), tenantAccess.company())
                .orElseThrow(() -> TenantAccess.notFound("Address", address.getId_address()));
        if (idContact == null || !idContact.equals(existing.getContact().getIdContact())) {
            throw TenantAccess.notFound("Address", address.getId_address());
        }
    }
}
