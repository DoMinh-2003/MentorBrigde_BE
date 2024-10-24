package com.BE.model.request;


import com.BE.enums.BookingStatusEnum;
import com.BE.exception.EnumValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class BookingStatusRequest {

    UUID id;

    @Schema(example = "REQUESTED, ACCEPTED, REJECTED, CANCELLED", description = "Status Enum")
    @EnumValidator(enumClass = BookingStatusEnum.class, message = "Invalid status value")
    @Enumerated(EnumType.STRING)
    BookingStatusEnum status;
}
