package com.clavis.init;

import com.clavis.model.User;
import com.clavis.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN"); // ROLE_ADMIN
            userRepository.save(admin);

            User player = new User();
            player.setUsername("player");
            player.setPassword(passwordEncoder.encode("player123"));
            player.setRole("PLAYER");
            userRepository.save(player);

            System.out.println("Seeded admin/admin123 and player/player123");
        }
    }
}
