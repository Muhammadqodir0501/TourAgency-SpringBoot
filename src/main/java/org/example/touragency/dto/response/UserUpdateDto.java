package org.example.touragency.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserUpdateDto {
    private String fullName;
    private String email;
    private String password;
    private String phoneNumber;
}
