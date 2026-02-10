package org.example.touragency.repository;

import org.example.touragency.model.entity.RefreshToken;
import org.example.touragency.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

     Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);

     void deleteByUser(User user);

     void deleteByExpiryDateBefore(Instant now);
}
