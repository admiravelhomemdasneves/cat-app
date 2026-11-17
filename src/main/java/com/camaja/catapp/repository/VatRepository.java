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
public interface VatRepository extends JpaRepository<Vat, Long> {
    @Query("SELECT v FROM Vat v WHERE (v.company = :company OR v.company IS NULL) AND (v.flagActive IS NULL OR v.flagActive = true)")
    List<Vat> findVisibleByCompany(@Param("company") Company company);

    @Query("SELECT v FROM Vat v WHERE v.idVat = :id AND (v.company = :company OR v.company IS NULL)")
    Optional<Vat> findVisible(@Param("id") Long id, @Param("company") Company company);
}
