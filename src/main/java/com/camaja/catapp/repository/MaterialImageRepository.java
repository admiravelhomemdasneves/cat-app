package com.camaja.catapp.repository;

import com.camaja.catapp.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialImageRepository extends JpaRepository<MaterialImage, Long> {
    @Query("SELECT i FROM MaterialImage i WHERE i.idMaterialImage = :id AND i.material.company = :company")
    Optional<MaterialImage> findOwned(@Param("id") Long id, @Param("company") Company company);
}
