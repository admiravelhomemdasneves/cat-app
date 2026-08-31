package com.camaja.catapp.repository;

import com.camaja.catapp.models.Company;
import com.camaja.catapp.models.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PriorityRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired PriorityRepository priorityRepository;

    Company companyA;
    Company companyB;
    Priority forA;
    Priority global;
    Priority forB;
    Priority inactiveForA;

    @BeforeEach
    void setUp() {
        companyA = em.persist(company("Company A"));
        companyB = em.persist(company("Company B"));

        forA = em.persist(priority("For A", companyA, true));
        global = em.persist(priority("Global", null, true));
        forB = em.persist(priority("For B", companyB, true));
        inactiveForA = em.persist(priority("Inactive A", companyA, false));

        em.flush();
    }

    @Test
    void findVisibleByCompany_returnsRowsForMatchingCompanyAndGlobalRows() {
        List<Priority> result = priorityRepository.findVisibleByCompany(companyA);
        assertThat(result).contains(forA, global);
    }

    @Test
    void findVisibleByCompany_excludesDifferentCompanyRows() {
        List<Priority> result = priorityRepository.findVisibleByCompany(companyA);
        assertThat(result).doesNotContain(forB);
    }

    @Test
    void findVisibleByCompany_excludesInactiveRows() {
        List<Priority> result = priorityRepository.findVisibleByCompany(companyA);
        assertThat(result).doesNotContain(inactiveForA);
    }

    @Test
    void findVisibleByCompany_includesGlobalRows() {
        List<Priority> result = priorityRepository.findVisibleByCompany(companyA);
        assertThat(result).contains(global);
    }

    private Company company(String name) {
        Company c = new Company();
        c.setName(name);
        c.setFlagActive(true);
        return c;
    }

    private Priority priority(String name, Company company, boolean active) {
        Priority p = new Priority();
        p.setName(name);
        p.setCompany(company);
        p.setFlagActive(active);
        return p;
    }
}
