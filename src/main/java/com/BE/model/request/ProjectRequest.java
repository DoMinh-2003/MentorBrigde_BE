package com.BE.model.request;


import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectRequest {

    @NotBlank(message = "NameProject cannot be blank")
    String name;
}
