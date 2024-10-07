package com.BE.model.request;



import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduleRequest {

     List<TimeFrameRequest> monday;
     List<TimeFrameRequest> tuesday;
     List<TimeFrameRequest> wednesday;
     List<TimeFrameRequest> thursday;
     List<TimeFrameRequest> friday;
     List<TimeFrameRequest> saturday;
     List<TimeFrameRequest> sunday;

}
