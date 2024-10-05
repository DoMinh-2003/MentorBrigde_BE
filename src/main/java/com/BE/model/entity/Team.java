package com.BE.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Team {
    @Id
    @UuidGenerator
    UUID id;
    @Column(unique = true, nullable = false)
    String code;
    LocalDate createdAt;
    String createdBy;

    @ManyToOne
    @JoinColumn(name = "semester_id")
    Semester semester;

    @OneToMany(mappedBy = "team")
    @JsonIgnore
    Set<UserTeam> userTeams = new HashSet<>();
}
