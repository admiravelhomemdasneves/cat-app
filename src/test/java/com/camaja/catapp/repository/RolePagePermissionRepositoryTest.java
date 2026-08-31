package com.camaja.catapp.repository;

import com.camaja.catapp.models.AccessLevel;
import com.camaja.catapp.models.BackofficePage;
import com.camaja.catapp.models.RolePagePermission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RolePagePermissionRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired RolePagePermissionRepository repository;

    AccessLevel al1;
    BackofficePage bp1;
    BackofficePage bp2;
    RolePagePermission permission;

    @BeforeEach
    void setUp() {
        al1 = em.persist(accessLevel("ADMIN"));

        bp1 = em.persist(page("Orders", "/orders"));
        bp2 = em.persist(page("Contacts", "/contacts"));

        permission = new RolePagePermission();
        permission.setAccessLevel(al1);
        permission.setBackofficePage(bp1);
        permission.setCanView(true);
        permission.setCanCreate(false);
        permission.setCanEdit(false);
        permission.setCanDelete(false);
        permission = em.persist(permission);

        em.flush();
    }

    @Test
    void findByAccessLevel_returnsCorrectRows() {
        List<RolePagePermission> result = repository.findByAccessLevel(al1);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCanView()).isTrue();
    }

    @Test
    void findByAccessLevelAndBackofficePage_returnsTheUniqueRow() {
        Optional<RolePagePermission> result = repository.findByAccessLevelAndBackofficePage(al1, bp1);
        assertThat(result).isPresent();
        assertThat(result.get().getBackofficePage().getPath()).isEqualTo("/orders");
    }

    @Test
    void findByAccessLevelAndBackofficePage_whenNotFound_returnsEmpty() {
        Optional<RolePagePermission> result = repository.findByAccessLevelAndBackofficePage(al1, bp2);
        assertThat(result).isEmpty();
    }

    private AccessLevel accessLevel(String name) {
        AccessLevel al = new AccessLevel();
        al.setName(name);
        al.setFlagActive(true);
        return al;
    }

    private BackofficePage page(String name, String path) {
        BackofficePage p = new BackofficePage();
        p.setName(name);
        p.setPath(path);
        return p;
    }
}
