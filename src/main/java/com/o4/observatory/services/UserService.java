package com.o4.observatory.services;

import com.o4.observatory.models.User;
import com.o4.observatory.repositories.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Transactional
    public User registerNewUser(String username, String password, String nickname) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname);
        
        // Save with flush to force immediate persistence
        userRepository.saveAndFlush(user);
        System.out.println("User saved with ID: " + user.getId());
        
        // Double-check persistence
        User savedUser = userRepository.findById(user.getId()).orElse(null);
        System.out.println("User retrieved after save: " + (savedUser != null ? savedUser.getId() : "null"));
        
        return user;
    }
}