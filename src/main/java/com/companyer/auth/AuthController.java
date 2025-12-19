package com.companyer.auth;

import com.companyer.entity.User;
import com.companyer.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private record LoginRequest(String username, String password) {}

    public AuthController(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest req) {
        Optional<User> userOpt = userRepository.findByUsername(req.username());
        if (userOpt.isEmpty()) {
            return Map.of("error", "Invalid username or password");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            return Map.of("error", "Invalid username or password");
        }

        String token = jwtService.generateToken(user.getUsername());
        return Map.of("token", token, "username", user.getUsername(), "role", user.getRole());
    }

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody LoginRequest req) {
        Optional<User> existingUser = userRepository.findByUsername(req.username());
        if (existingUser.isPresent()) {
            return Map.of("error", "Username already exists");
        }

        String hashedPassword = passwordEncoder.encode(req.password());
        User newUser = new User();
        newUser.setUsername(req.username());
        newUser.setPassword(hashedPassword);
        newUser.setRole("ROLE_USER");
        userRepository.save(newUser);

        String token = jwtService.generateToken(req.username());
        return Map.of("token", token, "username", req.username(), "role", "ROLE_USER");
    }
}
