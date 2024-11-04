package com.BE.service.interfaceServices;

import com.BE.enums.BookingStatusEnum;
import com.BE.enums.BookingTypeEnum;
import com.BE.model.entity.Booking;
import com.BE.model.entity.User;
import com.BE.model.request.BookingRequestFilter;
import com.BE.model.request.BookingStatusRequest;
import com.BE.model.response.BookingResponse;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IBookingService {
    Booking createBooking(UUID timeFrameId, BookingTypeEnum type);

    Map<String, List<BookingResponse>> getBookingMentorAccepted(int month);

    Map<String, List<BookingResponse>> getBookingStudentAccepted(int month);

    List<BookingResponse> getBooking(BookingTypeEnum type, BookingStatusEnum status);

    BookingResponse updateStatus(BookingStatusRequest statusRequest);
    Booking getBookingById(UUID id);
    List<Booking> getBookingsByMentorId(UUID mentorId);
    Booking saveBooking(Booking booking);
    List<BookingResponse> getBookingsClosestToNowByUser();

    Booking requestRescheduleBooking(UUID bookingId, UUID newTimeFrameId);

    Booking confirmRescheduleBooking(UUID bookingId, UUID newTimeFrameId, boolean isConfirmed);

    BookingResponse updateFinishStatusBooking(UUID bookingId);

    BookingResponse getBookingDetail(UUID id);
}
