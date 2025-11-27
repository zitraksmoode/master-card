package com.example.bank.controller;

import com.example.bank.entity.Role;
import com.example.bank.entity.User;
import com.example.bank.repository.UserRepository;
import com.example.bank.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        String token = jwtService.generateToken(request.username(),
                userRepository.findByUsername(request.username()).get().getRole().name());
        return ResponseEntity.ok(token);
    }

    // Для теста — создадим админа
    @PostMapping("/register-admin")
    public ResponseEntity<String> registerAdmin() {
        if (userRepository.findByUsername("admin").isPresent()) {
            return ResponseEntity.badRequest().body("Already exists");
        }
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
        return ResponseEntity.ok("Admin created");
    }
}

record AuthRequest(String username, String password) {}