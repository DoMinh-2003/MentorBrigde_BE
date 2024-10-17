package com.BE.service.interfaceServices;

import com.BE.model.entity.TimeFrame;
import com.BE.model.request.ScheduleRequest;
import com.BE.model.response.TotalHoursResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ITimeFrameService {


    String createSchedule(ScheduleRequest scheduleRequest);

    Map<LocalDate, List<TimeFrame>>  getGroupedTimeSlots(UUID id);

    TotalHoursResponse calculateTotalHours(ScheduleRequest scheduleRequest);

    TimeFrame getById(UUID id);
}
