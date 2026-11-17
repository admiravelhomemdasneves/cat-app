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
public interface PrintingServiceRepository extends JpaRepository<PrintingService, Long> {
    @Query("SELECT ps FROM PrintingService ps WHERE (ps.company = :company OR ps.company IS NULL) AND ps.flagActive = true")
    List<PrintingService> findVisibleByCompany(@Param("company") Company company);

    @Query("SELECT ps FROM PrintingService ps WHERE ps.id_printing_service = :id AND (ps.company = :company OR ps.company IS NULL)")
    Optional<PrintingService> findVisible(@Param("id") Long id, @Param("company") Company company);
}
