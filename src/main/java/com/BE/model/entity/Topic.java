package com.BE.model.entity;

import com.BE.enums.TopicEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Topic {

    @Id
    @UuidGenerator
    UUID id;

    String name;

    String description;

    LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "team_id")
    @JsonIgnore
    Team team;

    @Enumerated(EnumType.STRING)
    TopicEnum status;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    User creator;

    @ManyToOne
    @JoinColumn(name = "semester_id")
    @JsonIgnore
    Semester semester;

    @OneToMany(mappedBy = "topic",cascade = CascadeType.ALL)
    Set<File> files = new HashSet<>();

}
