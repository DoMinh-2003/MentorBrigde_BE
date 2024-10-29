package com.BE.service.implementServices;

import com.BE.exception.exceptions.BadRequestException;
import com.BE.mapper.FeedBackMapper;
import com.BE.model.entity.Booking;
import com.BE.model.entity.Feedback;
import com.BE.model.entity.User;
import com.BE.model.request.CreateFeedbackRequest;
import com.BE.model.request.UpdateFeedbackRequest;
import com.BE.model.response.PercentRatingMentorResponse;
import com.BE.repository.FeedbackRepository;
import com.BE.service.interfaceServices.IBookingService;
import com.BE.service.interfaceServices.IFeedbackService;
import com.BE.utils.AccountUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class FeedbackServiceImpl implements IFeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final AccountUtils accountUtils;
    private final IBookingService bookingService;
    private final FeedBackMapper feedbackMapper;
    public FeedbackServiceImpl(FeedbackRepository feedbackRepository,
                               AccountUtils accountUtils,
                               IBookingService bookingService,
                               FeedBackMapper feedbackMapper) {
        this.feedbackRepository = feedbackRepository;
        this.accountUtils = accountUtils;
        this.bookingService = bookingService;
        this.feedbackMapper = feedbackMapper;
    }

    @Override
    public Feedback createFeedback(UUID bookingId,CreateFeedbackRequest request) {
        if(feedbackRepository.existsByBookingIdAndUserId(bookingId, accountUtils.getCurrentUser().getId())) {
            throw new BadRequestException("Người dùng đã feedback cho Booking này");
        }
        User user = accountUtils.getCurrentUser();
        Booking booking = bookingService.getBookingById(bookingId);
        Feedback feedback = new Feedback();
        feedback.setRating(request.getRating());
        feedback.setContent(request.getContent());
        LocalDateTime nowInHoChiMinh = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        feedback.setCreatedAt(nowInHoChiMinh);
        feedback.setUser(user);
        feedback.setBooking(booking);
        return feedbackRepository.save(feedback);
    }

    @Override
    public Feedback updateFeedback(UUID feedbackId,UpdateFeedbackRequest request) {
        Feedback feedback = feedbackRepository.findById(feedbackId).orElseThrow(() ->
                new BadRequestException("Feedback không tìm thấy"));
        feedbackMapper.updateFeedback(feedback,request);
        LocalDateTime nowInHoChiMinh = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        feedback.setUpdatedAt(nowInHoChiMinh);
        return feedbackRepository.save(feedback);
    }

    @Override
    public void deleteFeedback(UUID feedbackId) {
        feedbackRepository.deleteById(feedbackId);
    }

    @Override
    public List<Feedback> getFeedbacksByBookingId(UUID bookingId){
        return feedbackRepository.findByBookingId(bookingId);
    }

    @Override
    public PercentRatingMentorResponse getFeedBackByMentorId(UUID mentorId) {
        List<Booking> bookings = bookingService.getBookingsByMentorId(mentorId);
        List<UUID> bookingIds = bookings.stream().map(Booking::getId).toList();
        List<Feedback> feedbacks = feedbackRepository.findByBookingIdIn(bookingIds);

        int total = feedbacks.size();
        if (total == 0) {
            return new PercentRatingMentorResponse("0", "0.0", "0.0", "0.0",
                    "0.0", "0.0", "0.0", "0.0");
        }

        int[] starCounts = new int[5];  // Index 0 for 1-star, index 4 for 5-star
        for (Feedback feedback : feedbacks) {
            int rating = feedback.getRating();
            if (rating >= 1 && rating <= 5) {
                starCounts[rating - 1]++;
            }
        }
        float[] starPercentages = new float[5];
        for (int i = 0; i < 5; i++) {
            starPercentages[i] = (float) starCounts[i] / total * 100;
        }

        float percentPositive = starPercentages[4] + starPercentages[3] + starPercentages[2];
        float percentNegative = starPercentages[1] + starPercentages[0];

        return new PercentRatingMentorResponse(
                String.valueOf(total),
                String.format("%.1f", percentPositive),
                String.format("%.1f", percentNegative),
                String.format("%.1f", starPercentages[4]),
                String.format("%.1f", starPercentages[3]),
                String.format("%.1f", starPercentages[2]),
                String.format("%.1f", starPercentages[1]),
                String.format("%.1f", starPercentages[0])
        );
    }

}
