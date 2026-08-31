package com.camaja.catapp.repository;

import com.camaja.catapp.models.Company;
import com.camaja.catapp.models.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriorityRepository extends JpaRepository<Priority, Long> {
    @Query("SELECT p FROM Priority p WHERE (p.company = :company OR p.company IS NULL) AND p.flagActive = true")
    List<Priority> findVisibleByCompany(@Param("company") Company company);
}
