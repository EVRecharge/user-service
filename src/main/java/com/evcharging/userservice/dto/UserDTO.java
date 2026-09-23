package com.evcharging.userservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long userId;
    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    /** Only used on registration; validated in UserService, not here, since this DTO is reused for updates. */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String vehicleType;
    private String vehicleNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
