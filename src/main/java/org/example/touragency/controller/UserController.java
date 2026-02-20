package org.example.touragency.controller;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.security.SecurityUtil;
import org.example.touragency.dto.response.UserResponseDto;
import org.example.touragency.dto.response.UserUpdateDto;
import org.example.touragency.security.SecurityUtils;
import org.example.touragency.service.abstractions.UserService;
import org.example.touragency.exception.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/become-agency")
    public ResponseEntity<ApiResponse<UserResponseDto>> becomeAgency() {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        UserResponseDto updatedUser = userService.addNewAgency(currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(updatedUser));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok(new ApiResponse<>(users));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUserById(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

   @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(
            @RequestBody UserUpdateDto userUpdateDto) {
       UUID currentUserId = SecurityUtils.getCurrentUserId();
       UserResponseDto updatedUser = userService.updateUser(currentUserId, userUpdateDto);
       return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(updatedUser));
   }

}
