package com.camaja.catapp.repository;

import com.camaja.catapp.models.Company;
import com.camaja.catapp.models.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StatusRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired StatusRepository statusRepository;

    Company companyA;
    Company companyB;
    Status forA;
    Status global;
    Status forB;
    Status inactiveForA;

    @BeforeEach
    void setUp() {
        companyA = em.persist(company("Company A"));
        companyB = em.persist(company("Company B"));

        forA = em.persist(status("For A", companyA, true));
        global = em.persist(status("Global", null, true));
        forB = em.persist(status("For B", companyB, true));
        inactiveForA = em.persist(status("Inactive A", companyA, false));

        em.flush();
    }

    @Test
    void findVisibleByCompany_returnsRowsForMatchingCompanyAndGlobalRows() {
        List<Status> result = statusRepository.findVisibleByCompany(companyA);
        assertThat(result).contains(forA, global);
    }

    @Test
    void findVisibleByCompany_excludesDifferentCompanyRows() {
        List<Status> result = statusRepository.findVisibleByCompany(companyA);
        assertThat(result).doesNotContain(forB);
    }

    @Test
    void findVisibleByCompany_excludesInactiveRows() {
        List<Status> result = statusRepository.findVisibleByCompany(companyA);
        assertThat(result).doesNotContain(inactiveForA);
    }

    @Test
    void findVisibleByCompany_includesGlobalRows() {
        List<Status> result = statusRepository.findVisibleByCompany(companyA);
        assertThat(result).contains(global);
    }

    private Company company(String name) {
        Company c = new Company();
        c.setName(name);
        c.setFlagActive(true);
        return c;
    }

    private Status status(String name, Company company, boolean active) {
        Status s = new Status();
        s.setName(name);
        s.setCompany(company);
        s.setFlagActive(active);
        return s;
    }
}
