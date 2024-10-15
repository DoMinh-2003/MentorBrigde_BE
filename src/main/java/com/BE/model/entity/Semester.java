package com.BE.model.entity;


import com.BE.enums.SemesterEnum;
import com.BE.enums.StatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
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

    LocalDateTime dateFrom;
    LocalDateTime dateTo;

    @Enumerated(EnumType.STRING)
    SemesterEnum status;

    LocalDateTime createdAt;

    @Column(name = "is_deleted")
    boolean isDeleted = false;

    @ManyToMany(mappedBy = "semesters")
    @JsonIgnore
    Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "semester")
    @JsonIgnore
    Set<Team> teams;

    @OneToMany(mappedBy = "semester",cascade = CascadeType.ALL)
    Set<Topic> topics = new HashSet<>();

    @OneToMany(mappedBy = "semester",cascade = CascadeType.ALL)
    Set<TimeFrame> timeFrames = new HashSet<>();

}
