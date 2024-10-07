package com.BE.controller;


import com.BE.model.request.ScheduleRequest;
import com.BE.model.request.SemesterRequest;
import com.BE.model.response.SemesterResponse;
import com.BE.service.interfaceServices.ITimeFrameService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/schedule")
@SecurityRequirement(name = "api")
public class TimeFrameController {

    @Autowired
    ResponseHandler responseHandler;


    @Autowired
    ITimeFrameService iTimeFrameService;



    @PostMapping
    public ResponseEntity<SemesterResponse> createSchedule(@Valid @RequestBody ScheduleRequest scheduleRequest){
        return  responseHandler.response(200,"Create New Schedule Successfully",iTimeFrameService.createSchedule(scheduleRequest));
    }


}
