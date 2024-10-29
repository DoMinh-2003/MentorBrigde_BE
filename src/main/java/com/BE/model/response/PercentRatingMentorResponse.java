package com.BE.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PercentRatingMentorResponse {
    String totalRating;
    String positive;
    String negative;
    String fiveStar;
    String fourStar;
    String threeStar;
    String twoStar;
    String oneStar;
}
