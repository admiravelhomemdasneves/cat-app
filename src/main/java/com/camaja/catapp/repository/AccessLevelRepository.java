package com.camaja.catapp.repository;

import com.camaja.catapp.models.AccessLevel;
import com.camaja.catapp.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccessLevelRepository extends JpaRepository<AccessLevel, Long> {
    Optional<AccessLevel> findByName(String name);
    List<AccessLevel> findByCompanyIsNull();
    List<AccessLevel> findByCompany(Company company);
    Optional<AccessLevel> findByNameAndCompanyIsNull(String name);
}
