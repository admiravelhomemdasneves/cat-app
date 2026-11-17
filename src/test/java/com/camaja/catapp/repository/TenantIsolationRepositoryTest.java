package com.camaja.catapp.repository;

import com.camaja.catapp.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Company A must never reach company B's records through the company-scoped repository methods,
 * while global catalog rows (company = NULL) stay visible to everyone.
 */
@DataJpaTest
class TenantIsolationRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired OrderRepository orderRepository;
    @Autowired OrderItemRepository orderItemRepository;
    @Autowired ContactRepository contactRepository;
    @Autowired AddressRepository addressRepository;
    @Autowired MaterialRepository materialRepository;
    @Autowired MaterialImageRepository materialImageRepository;
    @Autowired VatRepository vatRepository;
    @Autowired StoreRepository storeRepository;
    @Autowired StatusRepository statusRepository;

    Company companyA;
    Company companyB;
    Order orderB;
    OrderItem itemB;
    Contact contactB;
    Address addressB;
    Material materialB;
    MaterialImage imageB;
    Vat vatA;
    Vat vatB;
    Vat vatGlobal;
    Store storeB;
    Store storeGlobal;
    Status statusB;

    @BeforeEach
    void setUp() {
        companyA = em.persist(company("Company A"));
        companyB = em.persist(company("Company B"));

        contactB = new Contact();
        contactB.setFirstName("Bob");
        contactB.setCompany(companyB);
        contactB.setFlagActive(true);
        contactB.setAddresses(new ArrayList<>());
        contactB = em.persist(contactB);

        addressB = new Address();
        addressB.setContact(contactB);
        addressB.setStreet("B street");
        addressB.setFlagActive(true);
        addressB = em.persist(addressB);

        orderB = new Order();
        orderB.setDescription("B order");
        orderB.setCompany(companyB);
        orderB.setFlagActive(true);
        orderB = em.persist(orderB);

        itemB = new OrderItem();
        itemB.setOrder(orderB);
        itemB.setFlagActive(true);
        itemB = em.persist(itemB);

        materialB = new Material();
        materialB.setName("B paper");
        materialB.setCompany(companyB);
        materialB.setFlagActive(true);
        materialB = em.persist(materialB);

        imageB = new MaterialImage();
        imageB.setMaterial(materialB);
        imageB.setImageUrl("https://example.test/b.png");
        imageB.setFlagActive(true);
        imageB = em.persist(imageB);

        vatA = em.persist(vat("PT", companyA));
        vatB = em.persist(vat("ES", companyB));
        vatGlobal = em.persist(vat("EU", null));

        storeB = em.persist(store("B store", companyB));
        storeGlobal = em.persist(store("Global store", null));

        statusB = new Status();
        statusB.setName("B status");
        statusB.setCompany(companyB);
        statusB.setFlagActive(true);
        statusB = em.persist(statusB);

        em.flush();
        em.clear();
    }

    @Test
    void companyOwnedRecords_areOnlyFoundByTheirOwnCompany() {
        assertThat(orderRepository.findOwned(orderB.getId_order(), companyB)).isPresent();
        assertThat(orderRepository.findOwned(orderB.getId_order(), companyA)).isEmpty();

        assertThat(orderItemRepository.findOwned(itemB.getIdOrderItem(), companyB)).isPresent();
        assertThat(orderItemRepository.findOwned(itemB.getIdOrderItem(), companyA)).isEmpty();

        assertThat(contactRepository.findByIdContactAndCompany(contactB.getIdContact(), companyB)).isPresent();
        assertThat(contactRepository.findByIdContactAndCompany(contactB.getIdContact(), companyA)).isEmpty();

        assertThat(addressRepository.findOwned(addressB.getId_address(), companyB)).isPresent();
        assertThat(addressRepository.findOwned(addressB.getId_address(), companyA)).isEmpty();

        assertThat(materialRepository.findByIdMaterialAndCompany(materialB.getIdMaterial(), companyB)).isPresent();
        assertThat(materialRepository.findByIdMaterialAndCompany(materialB.getIdMaterial(), companyA)).isEmpty();

        assertThat(materialImageRepository.findOwned(imageB.getIdMaterialImage(), companyB)).isPresent();
        assertThat(materialImageRepository.findOwned(imageB.getIdMaterialImage(), companyA)).isEmpty();
    }

    @Test
    void lists_onlyContainTheCompanysOwnRecords() {
        assertThat(orderRepository.findByCompanyAndFlagActive(companyA, true)).isEmpty();
        assertThat(contactRepository.findByCompanyAndFlagActive(companyA, true)).isEmpty();
        assertThat(materialRepository.findByCompanyAndFlagActive(companyA, true)).isEmpty();
    }

    @Test
    void vats_ownAndGlobalAreVisible_otherCompanysAreNot() {
        assertThat(vatRepository.findVisibleByCompany(companyA))
                .extracting(Vat::getIdVat)
                .containsExactlyInAnyOrder(vatA.getIdVat(), vatGlobal.getIdVat());

        assertThat(vatRepository.findVisible(vatGlobal.getIdVat(), companyA)).isPresent();
        assertThat(vatRepository.findVisible(vatA.getIdVat(), companyA)).isPresent();
        assertThat(vatRepository.findVisible(vatB.getIdVat(), companyA)).isEmpty();
    }

    @Test
    void stores_ownAndGlobalAreVisible_otherCompanysAreNot() {
        assertThat(storeRepository.findVisibleByCompany(companyA))
                .extracting(Store::getIdStore)
                .containsExactly(storeGlobal.getIdStore());

        assertThat(storeRepository.findVisible(storeGlobal.getIdStore(), companyA)).isPresent();
        assertThat(storeRepository.findVisible(storeB.getIdStore(), companyA)).isEmpty();
    }

    @Test
    void statuses_otherCompanysAreNotVisibleById() {
        assertThat(statusRepository.findVisible(statusB.getId_status(), companyB)).isPresent();
        assertThat(statusRepository.findVisible(statusB.getId_status(), companyA)).isEmpty();
    }

    private static Company company(String name) {
        Company c = new Company();
        c.setName(name);
        c.setFlagActive(true);
        return c;
    }

    private static Vat vat(String country, Company company) {
        Vat v = new Vat();
        v.setCountry(country);
        v.setVatRate(new BigDecimal("0.23"));
        v.setCompany(company);
        v.setFlagActive(true);
        return v;
    }

    private static Store store(String name, Company company) {
        Store s = new Store();
        s.setStoreName(name);
        s.setCompany(company);
        s.setFlagActive(true);
        return s;
    }
}
