package com.BE.model.request;

import com.BE.enums.RoleEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

    @NotBlank(message = "FullName cannot be blank")
    String fullName;


    @Email(message = "Email is not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @NotBlank(message = "Email cannot be blank")
    String email;


    @Size(min = 5, message = "Username must be at least 5 characters long")
    @NotBlank(message = "Username cannot be blank")
    String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 5, message = "Password must be at least 5 characters long.")
    String password;
}
