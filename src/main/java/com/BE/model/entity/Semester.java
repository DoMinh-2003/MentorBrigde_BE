package com.BE.model.entity;


import com.BE.enums.StatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Semester {
    @Id
    @UuidGenerator
    UUID id;

    String code;
    String name;
    LocalDate dateFrom;
    LocalDate dateTo;

    @Enumerated(EnumType.STRING)
    StatusEnum status = StatusEnum.ACTIVE;

    @ManyToMany(mappedBy = "semesters")
    Set<User> users;

    @OneToMany(mappedBy = "semester")
    @JsonIgnore
    Set<Team> teams;


}
