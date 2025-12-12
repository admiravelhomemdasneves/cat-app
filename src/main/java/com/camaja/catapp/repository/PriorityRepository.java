package com.camaja.catapp.repository;

import com.camaja.catapp.models.Priority;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriorityRepository extends CrudRepository<Priority, Integer> {
}
