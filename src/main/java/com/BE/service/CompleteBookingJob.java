package com.BE.service;

import com.BE.enums.TimeFrameStatus;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.model.entity.Booking;
import com.BE.model.entity.TimeFrame;
import com.BE.repository.BookingRepository;
import com.BE.repository.TimeFrameRepository;
import com.BE.service.interfaceServices.INotificationService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CompleteBookingJob implements Job {

    @Autowired
    private TimeFrameRepository timeFrameRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    INotificationService notificationService;
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String bookingIdStr = context.getJobDetail().getJobDataMap().getString("bookingId");
        UUID bookingId = UUID.fromString(bookingIdStr);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking Not Found"));

        TimeFrame timeFrame = booking.getTimeFrame();
        timeFrame.setTimeFrameStatus(TimeFrameStatus.COMPLETED);
        timeFrameRepository.save(timeFrame);

        String message = "The booking with ID " + bookingId + " has been completed.";

        try {
            notificationService.createNotification("Booking Completed", message, booking.getMentor(), true);
        } catch (Exception e) {
            System.out.println("Error while creating notification: " + e.toString());
        }
        System.out.println("Booking " + bookingId + " has been marked as completed and email sent to mentor.");
    }

}
