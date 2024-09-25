package com.BE.utils;


import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateNowUtils {
    public String dateNow() {
        LocalDateTime localDateTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDateTime();
        return localDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

}
