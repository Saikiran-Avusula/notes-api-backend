package com.notesapi.config;

import com.notesapi.entity.User;
import com.notesapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create demo user if it doesn't exist
        if (!userRepository.existsByEmail("demo@example.com")) {
            User demoUser = new User();
            demoUser.setName("Demo User");
            demoUser.setEmail("demo@example.com");
            demoUser.setPassword(passwordEncoder.encode("demo123"));
            
            userRepository.save(demoUser);
            System.out.println("Demo user created successfully!");
        } else {
            System.out.println("Demo user already exists.");
        }
    }
}