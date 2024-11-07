package com.BE.mapper;

import com.BE.model.entity.Booking;
import com.BE.model.entity.BookingHistory;
import com.BE.model.response.BookingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BookingMapper {


    @Mapping(target = "bookingHistories", expression = "java(sortBookingHistoriesByCreatedAt(booking.getBookingHistories()))")
    BookingResponse toBookingResponse(Booking booking);

    default Set<BookingHistory> sortBookingHistoriesByCreatedAt(Set<BookingHistory> bookingHistories) {
        return bookingHistories.stream()
                .sorted(Comparator.comparing(BookingHistory::getCreatedAt))
                .collect(Collectors.toCollection(LinkedHashSet::new)); // giữ thứ tự sắp xếp
    }
}
