package com.BE.model.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class PointsResponse {

    int totalTeamPoints;
    int totalStudentPoints;

    int studentPoints;
    int teamPoints;
}
