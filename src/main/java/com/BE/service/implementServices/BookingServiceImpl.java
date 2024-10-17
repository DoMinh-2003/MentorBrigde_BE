package com.BE.service.implementServices;

import com.BE.enums.BookingTypeEnum;
import com.BE.enums.BookingStatusEnum;
import com.BE.enums.SemesterEnum;
import com.BE.model.entity.*;
import com.BE.repository.BookingHistoryRepository;
import com.BE.repository.BookingRepository;
import com.BE.repository.UserBookingRepository;
import com.BE.service.interfaceServices.IBookingService;
import com.BE.service.interfaceServices.ITeamService;
import com.BE.service.interfaceServices.ITimeFrameService;
import com.BE.utils.AccountUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class BookingServiceImpl implements IBookingService {

    private final BookingRepository bookingRepository;
    private final UserBookingRepository userBookingRepository;
    private final ITimeFrameService timeFrameService;
    private final AccountUtils accountUtils;
    private final ITeamService teamService;
    private final BookingHistoryRepository bookingHistoryRepository;
    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserBookingRepository userBookingRepository,
                              ITimeFrameService timeFrameService,
                              AccountUtils accountUtils,
                              ITeamService teamService,
                              BookingHistoryRepository bookingHistoryRepository) {
        this.bookingRepository = bookingRepository;
        this.userBookingRepository = userBookingRepository;
        this.timeFrameService = timeFrameService;
        this.accountUtils = accountUtils;
        this.teamService = teamService;
        this.bookingHistoryRepository = bookingHistoryRepository;
    }

    @Override
    public Booking createBooking(UUID timeFrameId, String teamCode, BookingTypeEnum type) {
        TimeFrame timeFrame = timeFrameService.getById(timeFrameId);
        // validate
        validateTimeFrame(timeFrame);
        User currentUser = accountUtils.getCurrentUser();
        User mentor = timeFrame.getMentor();

        // Create new booking
        Booking booking = initializeBooking(timeFrame, currentUser,type);

        if(type.equals(BookingTypeEnum.TEAM)) {
            Team team = teamService.getTeamByCode(teamCode);
            addTeamBooking(booking, team, type);
            return booking;
        }
        // Create user bookings (for both student and mentor)
        addUserBooking(booking, currentUser, BookingTypeEnum.INDIVIDUAL);
        addUserBooking(booking, mentor, BookingTypeEnum.INDIVIDUAL);
        // Create booking history
        addBookingHistory(booking, BookingStatusEnum.REQUESTED);
        return booking;
    }
    private void validateTimeFrame(TimeFrame timeFrame) {
        if(!timeFrame.getSemester().getStatus().equals(SemesterEnum.ACTIVE)) {
            throw new IllegalArgumentException("Time frame is not active");
        }
        if(bookingRepository.existsByTimeFrameIdAndStatusNotLike(timeFrame.getId(), BookingStatusEnum.CANCELLED)) {
            throw new IllegalArgumentException("Time frame is already booked");
        }

    }
    private Booking initializeBooking(TimeFrame timeFrame, User currentUser, BookingTypeEnum type) {
        Booking booking = new Booking();
        booking.setCreatedAt(LocalDateTime.now());
        booking.setCreatedBy(currentUser.getFullName());
        booking.setTimeFrame(timeFrame);
        booking.setStatus(BookingStatusEnum.REQUESTED);
        booking.setType(type);
        bookingRepository.save(booking);
        return booking;
    }

    private void addUserBooking(Booking booking, User user, BookingTypeEnum type) {
        UserBooking userBooking = new UserBooking();
        userBooking.setUser(user);
        userBooking.setBooking(booking);
        userBooking.setRole(user.getRole());
        userBooking.setType(type);
        booking.getUserBookings().add(userBooking);
        userBookingRepository.save(userBooking);
    }
    private void addTeamBooking(Booking booking, Team team, BookingTypeEnum type) {
        UserBooking userBooking = new UserBooking();
        userBooking.setTeam(team);
        userBooking.setBooking(booking);
        userBooking.setType(type);
        booking.getUserBookings().add(userBooking);
        userBookingRepository.save(userBooking);
    }
    private void addBookingHistory(Booking booking, BookingStatusEnum type) {
        BookingHistory bookingHistory = new BookingHistory();
        bookingHistory.setBooking(booking);
        bookingHistory.setType(type);
        bookingHistory.setCreatedAt(LocalDateTime.now());
        bookingHistoryRepository.save(bookingHistory);
    }

}
