package com.BE.model.response;


import com.BE.enums.StatusEnum;
import com.BE.model.entity.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectResponse {
    UUID id;
    String name;
    @Enumerated(EnumType.STRING)
    StatusEnum statusEnum;
    Set<User> users;
}
