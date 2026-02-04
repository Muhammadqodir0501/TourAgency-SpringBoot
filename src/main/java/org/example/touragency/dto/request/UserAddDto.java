package org.example.touragency.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.example.touragency.model.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAddDto {

    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @JsonProperty("role")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    private Role role;
}
