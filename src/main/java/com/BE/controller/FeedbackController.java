package com.BE.controller;

import com.BE.model.request.CreateFeedbackRequest;
import com.BE.model.request.UpdateFeedbackRequest;
import com.BE.service.interfaceServices.IFeedbackService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api")
@SecurityRequirement(name ="api")
@CrossOrigin("*")
public class FeedbackController {
    private final IFeedbackService feedbackService;
    private final ResponseHandler responseHandler;
    public FeedbackController(IFeedbackService feedbackService,
                              ResponseHandler responseHandler) {
        this.feedbackService = feedbackService;
        this.responseHandler = responseHandler;
    }
    @PostMapping("/feedback")
    public ResponseEntity createFeedback(@RequestParam UUID bookingId,@RequestBody @Valid CreateFeedbackRequest request) {
        return responseHandler.response(200,"Create New Feedback Successfully",
                feedbackService.createFeedback(bookingId,request));
    }
    @PutMapping("/feedback")
    public ResponseEntity updateFeedback(@RequestParam UUID feedbackId,@RequestBody @Valid UpdateFeedbackRequest request) {
        return responseHandler.response(200,"Update Feedback Successfully",
                feedbackService.updateFeedback(feedbackId,request));
    }
    @DeleteMapping("/feedback")
    public ResponseEntity deleteFeedback(@RequestParam UUID feedbackId) {
        feedbackService.deleteFeedback(feedbackId);
        return responseHandler.response(200,"Delete Feedback Successfully",null);
    }
    @GetMapping("/feedback/bookingid")
    public ResponseEntity getFeedbacksByBookingId(@RequestParam UUID bookingId) {
        return responseHandler.response(200,"Get Feedbacks Successfully",
                feedbackService.getFeedbacksByBookingId(bookingId));
    }

    @GetMapping("/feedback/mentorid")
    public ResponseEntity getFeedBackByMentorId(@RequestParam UUID mentorId) {
        return responseHandler.response(200,"Get Feedbacks Successfully",
                feedbackService.getFeedBackByMentorId(mentorId));
    }
}
