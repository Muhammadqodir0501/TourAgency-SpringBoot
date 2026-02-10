package org.example.touragency.service.abstractions;

import org.example.touragency.dto.request.RegisterRequest;
import org.example.touragency.dto.response.UserResponseDto;
import org.example.touragency.dto.response.UserUpdateDto;
import org.example.touragency.model.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User register(RegisterRequest request);

    UserResponseDto addNewAgency(UUID agencyId);

    void deleteUser(UUID userId);

    UserResponseDto updateUser(UUID userId, UserUpdateDto userUpdateDto);

    List<UserResponseDto> getAllUsers();

    User getUserById(UUID userId);
}
