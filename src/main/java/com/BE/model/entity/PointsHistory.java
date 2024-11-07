package com.BE.model.entity;


import com.BE.enums.BookingTypeEnum;
import com.BE.enums.PointChangeType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class PointsHistory {
    @Id
    @UuidGenerator
    UUID id;

    @Enumerated(EnumType.STRING)
    BookingTypeEnum bookingTypeEnum;
    @Enumerated(EnumType.STRING)
    PointChangeType pointChangeType;

    int changePoints; // số điểm thay đổi trong sự kiện

    int previousPoints;  // điểm trước khi thay đổi

    int newPoints; // điểm sau khi thay đổi

    @ManyToOne
    @JoinColumn(name = "user_id")
    User student;

    @ManyToOne
    @JoinColumn(name = "team_id")
    Team team;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    Booking booking;


    LocalDateTime changeTime;
}
