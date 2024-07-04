package com.BE.model.entity;


import com.BE.enums.RoleEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    @Id
    @UuidGenerator
    UUID id;

    String firstName;

    String lastName;


    @Column(unique = true)
    String email;

    @Column(unique = true)
    String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String password;

    @Enumerated(value = EnumType.STRING)
    RoleEnum role;

    @ManyToMany(mappedBy = "users",cascade = CascadeType.ALL)
    @JsonBackReference
    Set<Project> projects = new HashSet<>();


}
