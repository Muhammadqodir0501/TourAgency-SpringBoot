package org.example.touragency.service.abstractions;

import org.example.touragency.model.entity.RefreshToken;
import org.example.touragency.model.entity.User;

public interface RefreshTokenService {
    RefreshToken create(User user);

    RefreshToken verify(String token);

    void logout(String token);
}
