package com.BE.model.entity;

import com.BE.enums.BookingTypeEnum;
import com.BE.enums.RoleEnum;
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
public class UserBooking {
    @Id
    @UuidGenerator
    UUID id;

    @Enumerated(value = EnumType.STRING)
    BookingTypeEnum type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "team_id")
    Team team;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    Booking booking;

    @Enumerated(value = EnumType.STRING)
    RoleEnum role;
}
