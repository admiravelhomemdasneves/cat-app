package com.camaja.catapp.services.domain;

import com.camaja.catapp.models.User;
import com.camaja.catapp.repository.UserRepository;
import com.camaja.catapp.services.infrastructure.EmailService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
    }

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }
}
