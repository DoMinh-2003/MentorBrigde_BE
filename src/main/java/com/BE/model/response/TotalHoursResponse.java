package com.BE.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TotalHoursResponse {

    String currentTotalHours;
    int minimumRequiredHours;
    List<String> messages;

    Boolean error;
}
