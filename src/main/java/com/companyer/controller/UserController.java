package com.companyer.controller;

import com.companyer.entity.User;
import com.companyer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/get-user")
    public Map<String, Object> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<User> userOpt = userRepository.findByUsername(username);

        Map<String, Object> response = new HashMap<>();

        User user = userOpt.get();
        response.put("username", user.getUsername());
        response.put("role", user.getRole());

        if(user.getRole().equals("ROLE_ADMIN")){
            response.put("users", userRepository.findAll());
        }

        return response;
    }

    @PostMapping("/add-user")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Map<String, Object> addUser(@RequestBody Map<String, String> payload) {
        try {
            String username = payload.get("username");
            String role = payload.getOrDefault("role", "ROLE_USER");
            String password = payload.get("password");

            if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                return Map.of("error", true, "message", "Username and password are required.");
            }

            if (userRepository.findByUsername(username).isPresent()) {
                return Map.of("error", true, "message", "Username already exists.");
            }

            User user = new User();
            user.setUsername(username);
            user.setRole(role);
            user.setPassword(passwordEncoder.encode(password));

            userRepository.save(user);

            return Map.of("error", false, "user", user);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", true, "message", "Server error.");
        }
    }

    // Edit existing user
    @PostMapping("/edit-user")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Map<String, Object> editUser(@RequestBody Map<String, String> payload) {
        try {
            if (!payload.containsKey("id")) {
                return Map.of("error", true, "message", "Missing user ID.");
            }

            int id = Integer.parseInt(payload.get("id"));
            Optional<User> userOpt = userRepository.findById(id);
            if (userOpt.isEmpty()) {
                return Map.of("error", true, "message", "User not found.");
            }

            User user = userOpt.get();

            String username = payload.get("username");
            if (username != null && !username.trim().isEmpty()) {
                user.setUsername(username);
            }

            String role = payload.get("role");
            if (role != null && !role.trim().isEmpty()) {
                user.setRole(role);
            }

            String password = payload.get("password");
            if (password != null && !password.trim().isEmpty()) {
                user.setPassword(passwordEncoder.encode(password));
            }

            userRepository.save(user);

            return Map.of("error", false, "user", user);
        } catch (NumberFormatException e) {
            return Map.of("error", true, "message", "Invalid user ID.");
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", true, "message", "Server error.");
        }
    }

    @PostMapping("/delete-user")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Map<String, Object> deleteUser(@RequestBody Map<String, String> payload) {
        try {
            if (!payload.containsKey("id")) {
                return Map.of("error", true, "message", "Missing user ID.");
            }

            int id = Integer.parseInt(payload.get("id"));
            if (!userRepository.existsById(id)) {
                return Map.of("error", true, "message", "User not found.");
            }

            userRepository.deleteById(id);

            return Map.of("error", false, "message", "User deleted successfully.");
        } catch (NumberFormatException e) {
            return Map.of("error", true, "message", "Invalid user ID.");
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", true, "message", "Server error.");
        }
    }
}
