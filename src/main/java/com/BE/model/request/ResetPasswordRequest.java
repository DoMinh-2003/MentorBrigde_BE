package com.BE.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 5, message = "Password must be at least 5 characters long.")
    String password;
}
