package com.camaja.catapp.services.domain;

import com.camaja.catapp.configs.SecurityUtils;
import com.camaja.catapp.dto.UserDTO;
import com.camaja.catapp.models.AccessLevel;
import com.camaja.catapp.models.User;
import com.camaja.catapp.repository.AccessLevelRepository;
import com.camaja.catapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AccessLevelRepository accessLevelRepository;
    private final SecurityUtils securityUtils;

    public List<UserDTO> getUsersInCurrentCompany() {
        return userRepository.findByCompany(securityUtils.getCurrentCompany())
                .stream().map(UserDTO::from).toList();
    }

    public List<UserDTO> allUsersAsDTO() {
        return getUsersInCurrentCompany();
    }

    public UserDTO updateProfile(Long userId, String username, String email) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        if (username != null && !username.isBlank()) user.setUsername(username);
        if (email != null && !email.isBlank()) user.setEmail(email);
        return UserDTO.from(userRepository.save(user));
    }

    public void updateAccessLevel(Long userId, Long accessLevelId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        AccessLevel level = accessLevelRepository.findById(accessLevelId)
                .orElseThrow(() -> new IllegalArgumentException("Access level not found: " + accessLevelId));
        if (level.getCompany() != null &&
                !level.getCompany().getIdCompany().equals(securityUtils.getCurrentCompany().getIdCompany())) {
            throw new IllegalArgumentException("Access level does not belong to your company");
        }
        user.setAccessLevel(level);
        userRepository.save(user);
    }

    public void inactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        user.setEnabled(false);
        userRepository.save(user);
    }
}
