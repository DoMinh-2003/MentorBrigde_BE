package com.BE.model.response;


import com.BE.enums.BookingTypeEnum;
import com.BE.enums.PointChangeType;
import com.BE.model.entity.Team;
import com.BE.model.entity.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class PointsHistoryResponse {

    UUID id;

    @Enumerated(EnumType.STRING)
    BookingTypeEnum bookingTypeEnum;

    @Enumerated(EnumType.STRING)
    PointChangeType pointChangeType;

    int changePoints; // số điểm thay đổi trong sự kiện

    int previousPoints;  // điểm trước khi thay đổi

    int newPoints; // điểm sau khi thay đổi


    User student;

    Team team;

    LocalDateTime changeTime;
}
