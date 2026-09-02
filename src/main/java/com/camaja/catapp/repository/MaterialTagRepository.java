package com.camaja.catapp.repository;

import com.camaja.catapp.models.MaterialTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialTagRepository extends JpaRepository<MaterialTag, Long> {
}
