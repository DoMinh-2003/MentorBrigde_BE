package com.BE.controller;



import com.BE.enums.BookingStatusEnum;
import com.BE.enums.BookingTypeEnum;
import com.BE.model.entity.Booking;
import com.BE.model.request.BookingStatusRequest;
import com.BE.model.response.DataResponseDTO;
import com.BE.service.interfaceServices.IBookingService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
    public ResponseEntity getBooking(@RequestParam(required = false) BookingTypeEnum type, @RequestParam(required = false) BookingStatusEnum status){
        return  responseHandler.response(200,"Get Booking Successfully", iBookingService.getBooking(type, status));
    }

    @GetMapping("{id}")
    public ResponseEntity getBookingDetail(@PathVariable UUID id){
        return  responseHandler.response(200,"Get Booking Successfully", iBookingService.getBookingDetail(id));
    }

    @PatchMapping
    public ResponseEntity updateStatus(@Valid @RequestBody BookingStatusRequest statusRequest){
        return  responseHandler.response(200,"Change Status Booking Successfully", iBookingService.updateStatus(statusRequest));
    }

    @GetMapping("/nearest")
    public ResponseEntity getBookingsNearestToNow(){
        return  responseHandler.response(200,"Get Booking Successfully", iBookingService.getBookingsClosestToNowByUser());
    }


    @PutMapping("/{bookingId}/reschedule")
    public ResponseEntity<Booking> requestRescheduleBooking(
            @PathVariable UUID bookingId,
            @RequestParam UUID newTimeFrameId) {

        Booking rescheduledBooking = iBookingService.requestRescheduleBooking(bookingId, newTimeFrameId);
        return ResponseEntity.ok(rescheduledBooking);
    }

    @PostMapping("")
    public ResponseEntity<DataResponseDTO<Object>> createBooking(@RequestParam UUID timeFrameId,
                                                                 @RequestParam BookingTypeEnum type) {
        return responseHandler.response(200, "Create booking success!",
                iBookingService.createBooking(timeFrameId, type));
    }

    @PutMapping("/{bookingId}/confirm-reschedule")
    public ResponseEntity<Booking> confirmRescheduleBooking(
            @PathVariable UUID bookingId,
            @RequestParam UUID newTimeFrameId,
            @RequestParam boolean isConfirmed) {

        Booking updatedBooking = iBookingService.confirmRescheduleBooking(bookingId, newTimeFrameId, isConfirmed);
        return ResponseEntity.ok(updatedBooking);
    }

    @PatchMapping("/{bookingId}/finish")
    public ResponseEntity finishBooking(@PathVariable UUID bookingId) {
        return responseHandler.response(200, "Update Finish status of booking success!",
                iBookingService.updateFinishStatusBooking(bookingId));

    }


}
