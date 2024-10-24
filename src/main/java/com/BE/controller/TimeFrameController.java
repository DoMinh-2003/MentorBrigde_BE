package com.BE.controller;


import com.BE.model.request.ScheduleRequest;
import com.BE.service.interfaceServices.ITimeFrameService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/schedule")
@SecurityRequirement(name = "api")
@CrossOrigin("*")
public class TimeFrameController {

    @Autowired
    ResponseHandler responseHandler;


    @Autowired
    ITimeFrameService iTimeFrameService;



    @PostMapping
    public ResponseEntity createSchedule(@Valid @RequestBody ScheduleRequest scheduleRequest){
        return  responseHandler.response(200,"Create New Schedule Successfully", iTimeFrameService.createSchedule(scheduleRequest));
    }

    @PostMapping("validate")
    public ResponseEntity calculateTotalHours(@Valid @RequestBody ScheduleRequest scheduleRequest){
        return  responseHandler.response(400,"CalculateTotalHours Schedule Successfully", iTimeFrameService.calculateTotalHours(scheduleRequest));
    }

    @GetMapping("{id}")
    public ResponseEntity getGroupedTimeSlots(@PathVariable UUID id) {
        return  responseHandler.response(200,"Get Schedule Successfully", iTimeFrameService.getGroupedTimeSlots(id));
    }

    @GetMapping("weeklyTimeFrame")
    public ResponseEntity getGroupedTimeSlots(@RequestParam(required = false) String semesterCode) {
        return  responseHandler.response(200,"Get Weekly TimeFrame  Successfully", iTimeFrameService.getWeeklyTimeFrameForMentor(semesterCode));
    }





}
