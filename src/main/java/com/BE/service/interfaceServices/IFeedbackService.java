package com.BE.service.interfaceServices;

import com.BE.model.entity.Feedback;
import com.BE.model.request.CreateFeedbackRequest;
import com.BE.model.request.UpdateFeedbackRequest;
import com.BE.model.response.PercentRatingMentorResponse;

import java.util.List;
import java.util.UUID;

public interface IFeedbackService {
    Feedback createFeedback(UUID bookingId,CreateFeedbackRequest request);
    Feedback updateFeedback(UUID feedbackId,UpdateFeedbackRequest request);
    void deleteFeedback(UUID feedbackId);
    List<Feedback> getFeedbacksByBookingId(UUID bookingId);
    PercentRatingMentorResponse getFeedBackByMentorId(UUID mentorId);
}
