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
    LocalDateTime createdAt;
    String createdBy;

    @ManyToOne
    @JoinColumn(name = "time_frame_id")
    @JsonIgnore
    TimeFrame timeFrame;

    @OneToMany(mappedBy = "booking")
    @JsonIgnore
    Set<UserBooking> userBookings = new HashSet<>();

    @OneToMany(mappedBy = "booking")
    Set<BookingHistory> bookingHistories = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "semester_id")
    @JsonIgnore
    Semester semester;
}
