package com.BE.model.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
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

    @ManyToOne
    @JoinColumn(name = "semester_id")
    @JsonIgnore
    Semester semester;

    @OneToMany(mappedBy = "timeFrame")
    @JsonIgnore
    Set<Booking> userBookings = new HashSet<>();
}
