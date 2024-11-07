package com.BE.model.entity;


import com.BE.enums.BookingStatusEnum;
import com.BE.enums.BookingTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
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
public class Booking {
    @Id
    @UuidGenerator
    UUID id;

    @Enumerated(EnumType.STRING)
    BookingTypeEnum type;

    @Enumerated(EnumType.STRING)
    BookingStatusEnum status;
    String meetLink;
    LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "time_frame_id")
    @JsonIgnore
    TimeFrame timeFrame;

    @ManyToOne
    @JoinColumn(name = "student_id")
    User student;

    @ManyToOne
    @JoinColumn(name = "mentor_id")
    User mentor;

    @ManyToOne
    @JoinColumn(name = "team_id")
    Team team;

    @OneToMany(mappedBy = "booking")
    Set<BookingHistory> bookingHistories = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "semester_id")
    @JsonIgnore
    Semester semester;

    @OneToMany(mappedBy = "booking")
    Set<Feedback> feedbacks = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "room_id")
    Room room;

    @OneToMany(mappedBy = "booking",cascade = CascadeType.ALL)
    @JsonIgnore
    Set<PointsHistory> pointsHistories = new HashSet<>();
}
