package org.example.touragency.config;

import lombok.RequiredArgsConstructor;
import org.example.touragency.model.Role;
import org.example.touragency.model.entity.User;
import org.example.touragency.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        String adminEmail = "admin@tour.com";
        String encodedPassword = passwordEncoder.encode("password");

        Optional<User> existingUser = userRepository.findByEmail(adminEmail);

        if (existingUser.isPresent()) {
            // Если админ есть - просто обновляем ему пароль и роль
            User admin = existingUser.get();
            admin.setPassword(encodedPassword);
            admin.setRole(Role.AGENCY); // Убедись, что роль правильная
            userRepository.save(admin);
            System.out.println("✅ Админ уже существует. Пароль и роль обновлены.");
        } else {
            // Если нет - создаем нового
            User admin = User.builder()
                    .fullName("Super Admin")
                    .email(adminEmail)
                    .password(encodedPassword)
                    .phoneNumber("+998901234567")
                    .role(Role.AGENCY)
                    .build();
            userRepository.save(admin);
            System.out.println("✅ Новый админ создан успешно.");
        }
    }
}