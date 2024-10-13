package com.BE.model.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

import java.time.Duration;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduleRequest {

     @Schema(example = "PT1H || PT30M", description = "Thời gian slot, định dạng ISO-8601")
     Duration slotDuration;

      List<TimeFrameRequest> monday;
      List<TimeFrameRequest> tuesday;
      List<TimeFrameRequest> wednesday;
      List<TimeFrameRequest> thursday;
      List<TimeFrameRequest> friday;
      List<TimeFrameRequest> saturday;
      List<TimeFrameRequest> sunday;

}

