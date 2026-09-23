package com.evcharging.userservice.controller;

import com.evcharging.userservice.dto.LoginRequest;
import com.evcharging.userservice.dto.UserDTO;
import com.evcharging.userservice.security.JwtTokenProvider;
import com.evcharging.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO userDTO) {
        try {
            UserDTO registeredUser = userService.registerUser(userDTO);
            String token = jwtTokenProvider.generateToken(registeredUser.getEmail(), registeredUser.getUserId());
            String refreshToken = jwtTokenProvider.generateRefreshToken(registeredUser.getEmail(), registeredUser.getUserId());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("user", registeredUser);
            response.put("token", token);
            response.put("refreshToken", refreshToken);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            UserDTO user = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
            String token = jwtTokenProvider.generateToken(user.getEmail(), user.getUserId());
            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail(), user.getUserId());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("user", user);
            response.put("token", token);
            response.put("refreshToken", refreshToken);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/oauth/login")
    public ResponseEntity<?> oauthLogin(@RequestParam String email,
                                        @RequestParam String firstName,
                                        @RequestParam String lastName,
                                        @RequestParam String oauthProvider,
                                        @RequestParam String oauthId) {
        try {
            UserDTO user = userService.registerOAuthUser(email, firstName, lastName, oauthProvider, oauthId);
            String token = jwtTokenProvider.generateToken(user.getEmail(), user.getUserId());
            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail(), user.getUserId());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("user", user);
            response.put("token", token);
            response.put("refreshToken", refreshToken);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        try {
            UserDTO user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        try {
            UserDTO user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId,
                                        @Valid @RequestBody UserDTO userDTO) {
        try {
            UserDTO updatedUser = userService.updateUser(userId, userDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/exists/{userId}")
    public ResponseEntity<?> userExists(@PathVariable Long userId) {
        boolean exists = userService.userExists(userId);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            boolean isValid = jwtTokenProvider.validateToken(jwtToken);
            if (isValid) {
                Long userId = jwtTokenProvider.getUserIdFromToken(jwtToken);
                String email = jwtTokenProvider.getEmailFromToken(jwtToken);
                return ResponseEntity.ok(Map.of("valid", true, "userId", userId, "email", email));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("valid", false, "error", "Token expired or invalid"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "error", e.getMessage()));
        }
    }
}
