package com.camaja.catapp.repository;

import com.camaja.catapp.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCompanyAndFlagActive(Company company, Boolean flagActive);

    // FQN because ORDER is a JPQL keyword
    @Query("SELECT o FROM com.camaja.catapp.models.Order o WHERE o.id_order = :id AND o.company = :company")
    Optional<Order> findOwned(@Param("id") Long id, @Param("company") Company company);
}
