package org.example.touragency.exception;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<?>> handleApiException(ApiException ex) {

        ApiResponse<?> response = new ApiResponse<>(
                ex.getMessage(),
                ex.getStatus().value()
        );

        return ResponseEntity
                .status(ex.getStatus())
                .body(response);
    }

    // 401
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthentication(AuthenticationException ex) {

        ApiResponse<?> response = new ApiResponse<>(
                "Unauthorized",
                HttpStatus.UNAUTHORIZED.value()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    // 403
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDenied(AccessDeniedException ex) {

        ApiResponse<?> response = new ApiResponse<>(
                "Forbidden",
                HttpStatus.FORBIDDEN.value()
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleOther(Exception ex) {

        ApiResponse<?> response = new ApiResponse<>(
                "Internal server error",
                500
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }



}

