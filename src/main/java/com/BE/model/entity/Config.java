package com.BE.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.time.Duration;
import java.util.UUID;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Config {

    @Id
    @UuidGenerator
    UUID id;

    int minimumHours;

    Duration minTimeSlotDuration;

    int totalTeamPoints;

    int totalStudentPoints;

    int pointsDeducted;

}
