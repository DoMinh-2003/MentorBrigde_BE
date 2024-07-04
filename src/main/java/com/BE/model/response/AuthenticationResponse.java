package com.BE.model.response;


import com.BE.enums.RoleEnum;
import com.BE.model.entity.Project;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
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
public class AuthenticationResponse {
     UUID id;
     String firstName;
     String lastName;
     String username;
     @Enumerated(value = EnumType.STRING)
     RoleEnum role;
     String token;
     Set<Project> projects;
}

