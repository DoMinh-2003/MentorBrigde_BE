package com.BE.model.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TimeFrameRequest {


    LocalTime startTime;


    LocalTime endTime;


    @AssertTrue(message = "Start time must be before end time")
    public boolean isValidTimeFrame() {
        return startTime.isBefore(endTime);
    }

}
