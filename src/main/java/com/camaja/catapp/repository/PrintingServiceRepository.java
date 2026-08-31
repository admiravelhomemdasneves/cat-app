package com.camaja.catapp.repository;

import com.camaja.catapp.models.Company;
import com.camaja.catapp.models.PrintingService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrintingServiceRepository extends JpaRepository<PrintingService, Long> {
    @Query("SELECT ps FROM PrintingService ps WHERE (ps.company = :company OR ps.company IS NULL) AND ps.flagActive = true")
    List<PrintingService> findVisibleByCompany(@Param("company") Company company);
}
