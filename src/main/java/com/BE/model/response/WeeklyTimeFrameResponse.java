package com.BE.model.response;


import com.BE.model.request.TimeFrameRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WeeklyTimeFrameResponse {
    List<TimeFrameRequest> monday = new ArrayList<>();
    List<TimeFrameRequest> tuesday = new ArrayList<>();
    List<TimeFrameRequest> wednesday = new ArrayList<>();
    List<TimeFrameRequest> thursday = new ArrayList<>();
    List<TimeFrameRequest> friday = new ArrayList<>();
    List<TimeFrameRequest> saturday = new ArrayList<>();
    List<TimeFrameRequest> sunday = new ArrayList<>();

    boolean isUpdateSchedule;

    boolean semesterUpcoming;
}
