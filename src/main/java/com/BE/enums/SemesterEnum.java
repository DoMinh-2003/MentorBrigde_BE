package com.BE.enums;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Status of the semester")
public enum SemesterEnum {
    @Schema(description = "The semester is currently active")
    ACTIVE,

    @Schema(description = "The semester is currently inactive")
    INACTIVE,

    @Schema(description = "The semester is upcoming")
    UPCOMING
}