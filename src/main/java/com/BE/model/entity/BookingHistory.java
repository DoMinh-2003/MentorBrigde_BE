package com.BE.model.entity;

import com.BE.enums.BookingStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingHistory {
    @Id
    @UuidGenerator
    UUID id;

    @Enumerated(EnumType.STRING)
    BookingStatusEnum type;
    LocalDateTime createdAt;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "booking_id")
    Booking booking;
}
