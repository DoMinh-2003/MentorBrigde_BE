package com.BE.model.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class SemesterResponse {
    UUID id;
    String code;
    String name;
    LocalDate dateFrom;
    LocalDate dateTo;
}
