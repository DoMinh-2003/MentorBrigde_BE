package com.BE.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopicRequest {

     @NotBlank(message = "Name cannot be blank")
     String name;

     @NotBlank(message = "Description cannot be blank")
     String description;

     UUID teamId;

}
