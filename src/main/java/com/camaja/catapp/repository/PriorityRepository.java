package com.camaja.catapp.repository;

import com.camaja.catapp.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Rows with company = NULL are global (shared by every company, read-only for non-admins).
@Repository
public interface PriorityRepository extends JpaRepository<Priority, Long> {
    @Query("SELECT p FROM Priority p WHERE (p.company = :company OR p.company IS NULL) AND p.flagActive = true")
    List<Priority> findVisibleByCompany(@Param("company") Company company);

    @Query("SELECT p FROM Priority p WHERE p.id_priority = :id AND (p.company = :company OR p.company IS NULL)")
    Optional<Priority> findVisible(@Param("id") Long id, @Param("company") Company company);
}
