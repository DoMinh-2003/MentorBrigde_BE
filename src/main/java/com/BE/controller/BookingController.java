package com.BE.controller;



import com.BE.enums.BookingStatusEnum;
import com.BE.enums.BookingTypeEnum;
import com.BE.model.entity.Booking;
import com.BE.model.request.BookingStatusRequest;
import com.BE.service.interfaceServices.IBookingService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/booking")
@SecurityRequirement(name = "api")
public class BookingController {

    @Autowired
    IBookingService iBookingService;

    @Autowired
    ResponseHandler responseHandler;



    @GetMapping("/mentor-meeting")
    public ResponseEntity getBookingMentorAccepted(@RequestParam int month){
        return  responseHandler.response(200,"Get Booking Successfully", iBookingService.getBookingMentorAccepted(month));
    }

    @GetMapping("/student-meeting")
    public ResponseEntity getBookingStudentAccepted(@RequestParam int month){
        return  responseHandler.response(200,"Get Booking Successfully", iBookingService.getBookingStudentAccepted(month));
    }

    @GetMapping
    public ResponseEntity getBooking(@RequestParam(required = false) BookingTypeEnum type, @RequestParam BookingStatusEnum status){
        return  responseHandler.response(200,"Get Booking Successfully", iBookingService.getBooking(type, status));
    }

    @PatchMapping
    public ResponseEntity updateStatus(@Valid @RequestBody BookingStatusRequest statusRequest){
        return  responseHandler.response(200,"Change Status Booking Successfully", iBookingService.updateStatus(statusRequest));
    }

    @GetMapping("/nearest")
    public ResponseEntity getBookingsNearestToNow(){
        return  responseHandler.response(200,"Get Booking Successfully", iBookingService.getBookingsClosestToNowByUser());
    }




}
