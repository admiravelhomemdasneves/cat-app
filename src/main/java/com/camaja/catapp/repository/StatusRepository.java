package com.camaja.catapp.repository;

import com.camaja.catapp.models.Company;
import com.camaja.catapp.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {
    @Query("SELECT s FROM Status s WHERE (s.company = :company OR s.company IS NULL) AND s.flagActive = true")
    List<Status> findVisibleByCompany(@Param("company") Company company);
}
