package com.camaja.catapp.repository;

import com.camaja.catapp.models.MaterialImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialImageRepository extends JpaRepository<MaterialImage, Long> {
}
