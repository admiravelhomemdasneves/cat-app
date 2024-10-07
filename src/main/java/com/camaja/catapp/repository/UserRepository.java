package com.camaja.catapp.repository;

import com.camaja.catapp.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String Email);
    Optional<User> findByVerificationCode(String verificationCode);
}
