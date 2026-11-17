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
public interface StoreRepository extends JpaRepository<Store, Long> {
    @Query("SELECT s FROM Store s WHERE (s.company = :company OR s.company IS NULL) AND (s.flagActive IS NULL OR s.flagActive = true)")
    List<Store> findVisibleByCompany(@Param("company") Company company);

    @Query("SELECT s FROM Store s WHERE s.idStore = :id AND (s.company = :company OR s.company IS NULL)")
    Optional<Store> findVisible(@Param("id") Long id, @Param("company") Company company);
}
