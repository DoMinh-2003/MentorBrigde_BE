package com.BE.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SemesterRequest {

    String code;
    String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(type = "string", format = "date", example = "2024-10-04", description = "Date format: yyyy-MM-dd")
    LocalDate dateFrom;


    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(type = "string", format = "date",example = "2024-10-06", description = "Date format: yyyy-MM-dd")
    LocalDate dateTo;
}