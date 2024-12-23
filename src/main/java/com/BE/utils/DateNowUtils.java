package com.BE.utils;
import com.BE.exception.exceptions.DateException;
import com.BE.model.request.SemesterRequest;
import com.google.api.client.util.DateTime;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class DateNowUtils {
    public LocalDateTime dateNow() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        return zonedDateTime.toLocalDateTime();
    }


    public LocalDateTime getCurrentDateTimeHCM() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        return zonedDateTime.toLocalDateTime();
    }


    public void validateSemesterDates(SemesterRequest semesterRequest) {
        LocalDateTime now = getCurrentDateTimeHCM();

        if (semesterRequest.getDateFrom() == null || semesterRequest.getDateTo() == null) {
            throw new DateException("Both Date From and Date To must be provided");
        }

        if (semesterRequest.getDateTo().isBefore(semesterRequest.getDateFrom())) {
            throw new DateException("Date To must be later than Date From");
        }

        if (semesterRequest.getDateFrom().isBefore(now)) {
            throw new DateException("Date From cannot be in the past");
        }

        if (semesterRequest.getDateTo().isBefore(now)) {
            throw new DateException("Date To cannot be in the past");
        }

    }

    public DateTime convertLocalDateTimeToDateTime(LocalDateTime localDateTime) {
        // Step 1: Convert LocalDateTime to ZonedDateTime
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh"));

        // Step 2: Convert ZonedDateTime to ISO 8601 String
        String isoDateTime = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        // Step 3: Create DateTime object from ISO 8601 String
        return new DateTime(isoDateTime);
    }


}
