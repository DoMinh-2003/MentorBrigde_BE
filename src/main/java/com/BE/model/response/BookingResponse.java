package com.BE.model.response;

import com.BE.enums.BookingStatusEnum;
import com.BE.enums.BookingTypeEnum;
import com.BE.model.entity.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BookingResponse {

    UUID id;

    @Enumerated(EnumType.STRING)
    BookingTypeEnum type;

    @Enumerated(EnumType.STRING)
    BookingStatusEnum status;

    LocalDateTime createdAt;


    TimeFrame timeFrame;


    User student;


    User mentor;


    Team team;


    Set<BookingHistory> bookingHistories = new HashSet<>();


    Semester semester;
}
