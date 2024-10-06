package com.BE.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SemesterRequest {

    String code;
    String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    @Schema(type = "string", format = "date-time", example = "2024-10-06 08:05:07", description = "Date and time in 'yyyy-MM-dd HH:mm:ss' format")
    LocalDateTime dateFrom;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    @Schema(type = "string", format = "date-time", example = "2024-10-06T07:58:55.988Z", description = "ISO 8601 Date and time with milliseconds and timezone")
    LocalDateTime dateTo;
}
