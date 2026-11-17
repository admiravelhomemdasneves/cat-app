package com.camaja.catapp.repository;

import com.camaja.catapp.models.AccessLevel;
import com.camaja.catapp.models.Company;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AccessLevelRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired AccessLevelRepository repository;

    Company companyA;
    Company companyB;
    AccessLevel universalAdmin;
    AccessLevel universalManager;
    AccessLevel roleForA;
    AccessLevel roleForB;

    @BeforeEach
    void setUp() {
        companyA = em.persist(company("Company A"));
        companyB = em.persist(company("Company B"));

        universalAdmin   = em.persist(level("ADMIN",    null));
        universalManager = em.persist(level("MANAGER",  null));
        roleForA         = em.persist(level("Dev Lead",    companyA));
        roleForB         = em.persist(level("QA Lead",     companyB));

        em.flush();
    }

    @Test
    void findByCompanyIsNull_returnsOnlyUniversalRoles() {
        List<AccessLevel> result = repository.findByCompanyIsNull();
        assertThat(result).containsExactlyInAnyOrder(universalAdmin, universalManager);
        assertThat(result).doesNotContain(roleForA, roleForB);
    }

    @Test
    void findByCompany_returnsOnlyThatCompanysRoles() {
        List<AccessLevel> result = repository.findByCompany(companyA);
        assertThat(result).containsExactly(roleForA);
        assertThat(result).doesNotContain(universalAdmin, universalManager, roleForB);
    }

    @Test
    void findByCompany_differentCompany_doesNotReturnOtherCompanysRoles() {
        List<AccessLevel> resultB = repository.findByCompany(companyB);
        assertThat(resultB).containsExactly(roleForB);
        assertThat(resultB).doesNotContain(roleForA);
    }

    @Test
    void findByNameAndCompanyIsNull_whenUniversalRoleExists_returnsIt() {
        Optional<AccessLevel> result = repository.findByNameAndCompanyIsNull("ADMIN");
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("ADMIN");
    }

    @Test
    void findByNameAndCompanyIsNull_whenNameBelongsToCompanyRole_returnsEmpty() {
        Optional<AccessLevel> result = repository.findByNameAndCompanyIsNull("Dev Lead");
        assertThat(result).isEmpty();
    }

    private Company company(String name) {
        Company c = new Company();
        c.setName(name);
        c.setFlagActive(true);
        return c;
    }

    private AccessLevel level(String name, Company company) {
        AccessLevel al = new AccessLevel();
        al.setName(name);
        al.setFlagActive(true);
        al.setCompany(company);
        return al;
    }
}
