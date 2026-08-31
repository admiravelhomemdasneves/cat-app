package com.camaja.catapp.dto;

import com.camaja.catapp.models.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private boolean enabled;
    private Long companyId;
    private String companyName;
    private Long accessLevelId;
    private String accessLevelName;

    public static UserDTO from(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setEnabled(user.isEnabled());
        if (user.getCompany() != null) {
            dto.setCompanyId(user.getCompany().getIdCompany());
            dto.setCompanyName(user.getCompany().getName());
        }
        if (user.getAccessLevel() != null) {
            dto.setAccessLevelId(user.getAccessLevel().getIdAccessLevel());
            dto.setAccessLevelName(user.getAccessLevel().getName());
        }
        return dto;
    }
}
