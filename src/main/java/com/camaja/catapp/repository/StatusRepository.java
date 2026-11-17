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
public interface StatusRepository extends JpaRepository<Status, Long> {
    @Query("SELECT s FROM Status s WHERE (s.company = :company OR s.company IS NULL) AND s.flagActive = true")
    List<Status> findVisibleByCompany(@Param("company") Company company);

    @Query("SELECT s FROM Status s WHERE s.id_status = :id AND (s.company = :company OR s.company IS NULL)")
    Optional<Status> findVisible(@Param("id") Long id, @Param("company") Company company);
}
