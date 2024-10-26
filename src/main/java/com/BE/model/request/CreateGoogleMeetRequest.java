package com.BE.model.request;

import com.BE.model.entity.Booking;
import com.google.api.client.util.DateTime;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateGoogleMeetRequest {
    String summary;
    String description;
    @Null
    List<String> attendees;
    @NotNull
    UUID bookingId;
}
