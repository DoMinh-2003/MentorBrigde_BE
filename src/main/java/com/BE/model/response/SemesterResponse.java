package com.BE.model.response;


import com.BE.enums.SemesterEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class SemesterResponse {
    UUID id;
    String code;
    String name;
    LocalDateTime dateFrom;
    LocalDateTime dateTo;
    SemesterEnum status;
}
