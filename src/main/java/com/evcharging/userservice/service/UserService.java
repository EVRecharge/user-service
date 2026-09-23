package com.evcharging.userservice.service;

import com.evcharging.userservice.dto.UserDTO;
import com.evcharging.userservice.entity.User;
import com.evcharging.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDTO registerUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new RuntimeException("User with email already exists");
        }
        if (userDTO.getPassword() == null || userDTO.getPassword().isBlank()) {
            throw new RuntimeException("Password is required");
        }
        if (userDTO.getPassword().length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters");
        }

        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        user.setAddress(userDTO.getAddress());
        user.setCity(userDTO.getCity());
        user.setState(userDTO.getState());
        user.setZipCode(userDTO.getZipCode());
        user.setVehicleType(userDTO.getVehicleType());
        user.setVehicleNumber(userDTO.getVehicleNumber());
        user.setIsActive(true);

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    /** Throws the same generic error for "no such account" and "wrong password" to avoid account enumeration. */
    public UserDTO login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (user.getPasswordHash() == null || !passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new RuntimeException("This account has been deactivated");
        }
        return convertToDTO(user);
    }

    public UserDTO registerOAuthUser(String email, String firstName, String lastName,
                                     String oauthProvider, String oauthId) {
        var existingUser = userRepository.findByEmail(email);

        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
            user.setOauthProvider(oauthProvider);
            user.setOauthId(oauthId);
        } else {
            user = new User();
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setOauthProvider(oauthProvider);
            user.setOauthId(oauthId);
            user.setIsActive(true);
        }

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return convertToDTO(user);
    }

    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return convertToDTO(user);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setAddress(userDTO.getAddress());
        user.setCity(userDTO.getCity());
        user.setState(userDTO.getState());
        user.setZipCode(userDTO.getZipCode());
        user.setVehicleType(userDTO.getVehicleType());
        user.setVehicleNumber(userDTO.getVehicleNumber());

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setIsActive(false);
        userRepository.save(user);
    }

    public boolean userExists(Long userId) {
        return userRepository.existsById(userId);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        dto.setCity(user.getCity());
        dto.setState(user.getState());
        dto.setZipCode(user.getZipCode());
        dto.setVehicleType(user.getVehicleType());
        dto.setVehicleNumber(user.getVehicleNumber());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
