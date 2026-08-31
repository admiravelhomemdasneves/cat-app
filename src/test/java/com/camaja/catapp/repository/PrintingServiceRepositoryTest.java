package com.camaja.catapp.repository;

import com.camaja.catapp.models.Company;
import com.camaja.catapp.models.PrintingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PrintingServiceRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired PrintingServiceRepository printingServiceRepository;

    Company companyA;
    Company companyB;
    PrintingService forA;
    PrintingService global;
    PrintingService forB;
    PrintingService inactiveForA;

    @BeforeEach
    void setUp() {
        companyA = em.persist(company("Company A"));
        companyB = em.persist(company("Company B"));

        forA = em.persist(ps("For A", companyA, true));
        global = em.persist(ps("Global", null, true));
        forB = em.persist(ps("For B", companyB, true));
        inactiveForA = em.persist(ps("Inactive A", companyA, false));

        em.flush();
    }

    @Test
    void findVisibleByCompany_returnsRowsForMatchingCompanyAndGlobalRows() {
        List<PrintingService> result = printingServiceRepository.findVisibleByCompany(companyA);
        assertThat(result).contains(forA, global);
    }

    @Test
    void findVisibleByCompany_excludesDifferentCompanyRows() {
        List<PrintingService> result = printingServiceRepository.findVisibleByCompany(companyA);
        assertThat(result).doesNotContain(forB);
    }

    @Test
    void findVisibleByCompany_excludesInactiveRows() {
        List<PrintingService> result = printingServiceRepository.findVisibleByCompany(companyA);
        assertThat(result).doesNotContain(inactiveForA);
    }

    @Test
    void findVisibleByCompany_includesGlobalRows() {
        List<PrintingService> result = printingServiceRepository.findVisibleByCompany(companyA);
        assertThat(result).contains(global);
    }

    private Company company(String name) {
        Company c = new Company();
        c.setName(name);
        c.setFlagActive(true);
        return c;
    }

    private PrintingService ps(String name, Company company, boolean active) {
        PrintingService s = new PrintingService();
        s.setName(name);
        s.setCompany(company);
        s.setFlagActive(active);
        return s;
    }
}
