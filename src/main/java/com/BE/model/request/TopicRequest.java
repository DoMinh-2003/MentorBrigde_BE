package com.BE.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@ApiModel(description = "Details about the Topic")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopicRequest {

     @ApiModelProperty(notes = "Name of the topic")
     @NotBlank(message = "Name cannot be blank")
     String name;

     @ApiModelProperty(notes = "Description of the topic")
     @NotBlank(message = "Description cannot be blank")
     String description;

     @ApiModelProperty(notes = "ID of the team associated with the topic")
     UUID teamId;

}
