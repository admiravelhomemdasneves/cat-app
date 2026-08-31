package com.camaja.catapp.controllers;

import com.camaja.catapp.dto.UserDTO;
import com.camaja.catapp.models.User;
import com.camaja.catapp.services.domain.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/users")
@RestController
@CrossOrigin
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(UserDTO.from(currentUser));
    }

    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateCurrentUser(@RequestBody Map<String, String> updates) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(
                userService.updateProfile(currentUser.getId(), updates.get("username"), updates.get("email"))
        );
    }

    @GetMapping("/")
    public ResponseEntity<List<UserDTO>> allUsers() {
        return ResponseEntity.ok(userService.allUsersAsDTO());
    }
}
