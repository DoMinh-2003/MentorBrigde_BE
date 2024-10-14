package com.BE.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TotalHoursResponse {

    String currentTotalHours;
    int minimumRequiredHours;
    Map<String, List<String>> messages;
    String overallErrorMessage;
    Boolean error;
}
