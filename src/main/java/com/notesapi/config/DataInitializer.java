package com.notesapi.config;

import com.notesapi.model.User;
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
        // Check if demo user exists
        if (!userRepository.existsByEmail("demo@example.com")) {
            User demoUser = new User();
            demoUser.setName("Demo User");
            demoUser.setEmail("demo@example.com");
            demoUser.setPassword(passwordEncoder.encode("demo123"));
            
            userRepository.save(demoUser);
            System.out.println("Demo user created successfully: demo@example.com / demo123");
        } else {
            System.out.println("Demo user already exists.");
        }
    }
}
