package com.BE.model.request;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TimeFrameRequest {


     @Schema(example = "HH:mm 07:00", description = "Thời gian bắt đầu, định dạng HH:mm")
     LocalTime startTime;


     @Schema(example = "HH:mm 11:30", description = "Thời gian bắt đầu, định dạng HH:mm")
     LocalTime endTime;

}
