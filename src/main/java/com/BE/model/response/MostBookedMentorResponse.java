package com.BE.model.response;


import com.BE.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MostBookedMentorResponse {
    User userResponse;
    private long bookingCount;
}
