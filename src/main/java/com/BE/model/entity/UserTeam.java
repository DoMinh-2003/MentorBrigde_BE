package com.BE.model.entity;

import com.BE.enums.TeamRoleEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserTeam {

    @Id
    @UuidGenerator
    UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "team_code", referencedColumnName = "code")
    Team team;

    @Enumerated(value = EnumType.STRING)
    TeamRoleEnum role;
}
