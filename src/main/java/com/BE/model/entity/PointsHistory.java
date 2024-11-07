package com.BE.model.entity;


import com.BE.enums.BookingTypeEnum;
import com.BE.enums.PointChangeType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PointsHistory {
    @Id
    @UuidGenerator
    UUID id;

    @Enumerated(EnumType.STRING)
    BookingTypeEnum bookingTypeEnum;

    @Enumerated(EnumType.STRING)
    PointChangeType pointChangeType;

    int changePoints; // số điểm thay đổi trong sự kiện

    int previousScore;  // điểm trước khi thay đổi

    int newScore; // điểm sau khi thay đổi

    @ManyToOne
    @JoinColumn(name = "user_id")
    User student;

    @ManyToOne
    @JoinColumn(name = "team_id")
    Team team;

    LocalDateTime changeTime;
}
