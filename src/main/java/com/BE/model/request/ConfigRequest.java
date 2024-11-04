package com.BE.model.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Duration;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfigRequest {


    @Positive(message = "Minimum number must be greater than 0")
    int minimumHours;

    @Schema(example = "PT1H || PT30M", description = "Thời gian slot, định dạng ISO-8601")
    Duration minTimeSlotDuration;


    @Positive(message = "Minimum number must be greater than 0")
    int totalTeamPoints;

    @Positive(message = "Minimum number must be greater than 0")
    int totalStudentPoints;

    @Positive(message = "Minimum number must be greater than 0")
    int pointsDeducted;
}
