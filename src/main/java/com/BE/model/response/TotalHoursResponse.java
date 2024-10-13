package com.BE.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TotalHoursResponse {

    String currentTotalHours;
    int minimumRequiredHours;
    String message;

    Boolean error;
}
