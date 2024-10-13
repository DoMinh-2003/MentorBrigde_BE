package com.BE.model.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TimeFrame {


    @Id
    @UuidGenerator
    UUID id;

    LocalDateTime timeFrameFrom;
    LocalDateTime timeFrameTo;

    @ManyToOne
    @JoinColumn(name = "mentor_id")
    @JsonIgnore
    User mentor;

}
