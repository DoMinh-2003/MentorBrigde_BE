package com.BE.model.response;


import com.BE.enums.RoleEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class AuthenticationResponse {
     UUID id;
     String fullName;
     String username;
     String email;
     @Enumerated(value = EnumType.STRING)
     RoleEnum role;
     String token;
     String phone;
     String address;
     String avatar;
     String studentCode;
     String gender;
     String dayOfBirth;
     String teamCode;
     int points;
}

