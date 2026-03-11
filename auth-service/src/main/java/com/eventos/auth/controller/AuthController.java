package com.eventos.auth.controller;

import com.eventos.auth.config.JwtUtil;
import com.eventos.auth.dto.AuthResponse;
import com.eventos.auth.dto.LoginRequest;
import com.eventos.auth.dto.RegisterRequest;
import com.eventos.auth.model.User;
import com.eventos.auth.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Email already exists", 400));
        }
        if (userRepository.existsByUsername(request.getUsername())) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Username already exists", 400));
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .build();

        userRepository.save(user);

        // Auto login after registration to send token immediately
        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole());

        AuthResponse response = AuthResponse.builder()
                .message("User registered successfully")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .token(token)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid credentials", 401));
        }

        User user = userOpt.get();
        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole());

        AuthResponse response = AuthResponse.builder()
                .message("Login successful")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .token(token)
                .build();

        return ResponseEntity.ok(response);
    }

    // Helper class for error responses
    record ErrorResponse(String message, int status) {
        public long getTimestamp() {
            return System.currentTimeMillis();
        }
    }
}
