package com.BE.service.interfaceServices;

import com.BE.enums.BookingTypeEnum;
import com.BE.model.entity.Booking;

import java.util.UUID;

public interface IBookingService {
    Booking createBooking(UUID timeFrameId, String teamCode, BookingTypeEnum type);
}
