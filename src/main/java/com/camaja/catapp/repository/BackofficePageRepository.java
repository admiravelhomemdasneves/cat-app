package com.camaja.catapp.repository;

import com.camaja.catapp.models.BackofficePage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BackofficePageRepository extends JpaRepository<BackofficePage, Long> {
    Optional<BackofficePage> findByPath(String path);
}
