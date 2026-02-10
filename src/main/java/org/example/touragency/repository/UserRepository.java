package org.example.touragency.repository;

import org.example.touragency.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface UserRepository extends JpaRepository<User,UUID> {

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    List<User> findAll();

    void deleteById(UUID id);
}