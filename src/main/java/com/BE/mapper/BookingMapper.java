package com.BE.mapper;

import com.BE.model.entity.Booking;
import com.BE.model.response.BookingResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    BookingResponse toBookingResponse(Booking booking);
}
