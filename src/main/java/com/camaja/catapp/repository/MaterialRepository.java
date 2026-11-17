package com.camaja.catapp.repository;

import com.camaja.catapp.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findByCompanyAndFlagActive(Company company, Boolean flagActive);

    Optional<Material> findByIdMaterialAndCompany(Long idMaterial, Company company);
}
