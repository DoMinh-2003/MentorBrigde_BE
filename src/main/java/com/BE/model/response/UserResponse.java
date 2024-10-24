package com.BE.model.response;

import com.BE.enums.RoleEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    UUID id;
    String fullName;
    String studentCode;
    String email;
    String avatar;
    RoleEnum role;
    String gender;
    String dayOfBirth;
    String phone;
    String username;
    String teamCode;
}
