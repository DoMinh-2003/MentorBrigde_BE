package com.BE.model.request;

import com.BE.enums.RoleEnum;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    @Pattern(regexp = "(84|0[3|5|7|8|9])+(\\d{8})", message = "Invalid phone!")
    @Column(unique = true)
    String phone;

    @NotBlank(message = "Address cannot be blank")
    @Size(min = 5, message = "Address must be at least 5 characters long.")
    String address;

    @NotBlank(message = "Student code is required")
    @Pattern(regexp = "^SE\\d{6}$", message = "Student code must start with 'SE' followed by 6 digits")
    String studentCode;

    @NotBlank(message = "Avatar URL is required")
    @Pattern(regexp = "^(http|https)://.*$", message = "Avatar must be a valid URL")
    String avatar;

    @NotBlank(message = "Gender is required")
    @Size(min = 1, max = 10, message = "Gender must be between 1 and 10 characters")
    String gender;


    @NotBlank(message = "Date of birth is required")
    @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "Date of birth must be in dd/mm/yyyy format")
    String dayOfBirth;
}
