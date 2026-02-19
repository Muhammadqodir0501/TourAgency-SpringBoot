package org.example.touragency.dto.response;

import lombok.*;
import org.example.touragency.enums.Role;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private UUID userId;
    private String fullName;
    private String email;
    private String password;
    private String phoneNumber;
    private Role role;
}
