package com.BE.service.implementServices;

import com.BE.enums.BookingTypeEnum;
import com.BE.enums.BookingStatusEnum;
import com.BE.enums.SemesterEnum;
import com.BE.enums.TeamRoleEnum;
import com.BE.mapper.UserMapper;
import com.BE.model.entity.*;
import com.BE.model.response.UserResponse;
import com.BE.repository.BookingHistoryRepository;
import com.BE.repository.BookingRepository;
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
    private final ITimeFrameService timeFrameService;
    private final AccountUtils accountUtils;
    private final ITeamService teamService;
    private final BookingHistoryRepository bookingHistoryRepository;
    private final UserMapper userMapper;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              ITimeFrameService timeFrameService,
                              AccountUtils accountUtils,
                              ITeamService teamService,
                              BookingHistoryRepository bookingHistoryRepository,
                              UserMapper userMapper) {
        this.bookingRepository = bookingRepository;
        this.timeFrameService = timeFrameService;
        this.accountUtils = accountUtils;
        this.teamService = teamService;
        this.bookingHistoryRepository = bookingHistoryRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Booking createBooking(UUID timeFrameId, BookingTypeEnum type) {
        TimeFrame timeFrame = timeFrameService.getById(timeFrameId);

        validateTimeFrame(timeFrame);
        User currentUser = accountUtils.getCurrentUser();
        UserTeam userTeam = teamService.getCurrentUserTeam();
        String teamCode = userTeam.getTeam().getCode();

        User mentor = timeFrame.getMentor();

        Team team = null;
        if (type.equals(BookingTypeEnum.TEAM)) {
            team = validateTeamBooking(currentUser, teamCode);
        }

        Booking booking = createNewBooking(timeFrame, currentUser, mentor, team, type);

        return booking;
    }

    private void validateTimeFrame(TimeFrame timeFrame) {
        if (!timeFrame.getSemester().getStatus().equals(SemesterEnum.ACTIVE)) {
            throw new IllegalArgumentException("The time frame is not active.");
        }
        if (bookingRepository.existsByTimeFrameIdAndStatusNotLike(timeFrame.getId(), BookingStatusEnum.CANCELLED)) {
            throw new IllegalArgumentException("The time frame is already booked.");
        }
    }

    private Team validateTeamBooking(User currentUser, String teamCode) {
        UserTeam userTeam = teamService.getUserTeamByUserIdAndValidate(currentUser.getId(), teamCode,
                "The selected user is not part of the team.");

        if (!userTeam.getRole().equals(TeamRoleEnum.LEADER)) {
            throw new IllegalArgumentException("Only a team leader can book a time frame.");
        }

        return userTeam.getTeam();
    }

    private Booking createNewBooking(TimeFrame timeFrame, User currentUser, User mentor, Team team, BookingTypeEnum type) {
        Booking booking = new Booking();
        booking.setCreatedAt(LocalDateTime.now());
        booking.setMentor(mentor);
        booking.setTimeFrame(timeFrame);
        booking.setSemester(timeFrame.getSemester());
        booking.setStatus(BookingStatusEnum.REQUESTED);
        booking.setType(type);
        if (type.equals(BookingTypeEnum.TEAM)) {
            booking.setTeam(team);
        } else {
            booking.setStudent(currentUser);
        }
        BookingHistory bookingHistory =  logBookingHistory(booking, BookingStatusEnum.REQUESTED);
        booking.getBookingHistories().add(bookingHistory);
        booking = bookingRepository.save(booking);
        bookingHistoryRepository.save(bookingHistory);
        return booking;
    }

    private BookingHistory logBookingHistory(Booking booking, BookingStatusEnum status) {
        BookingHistory bookingHistory = new BookingHistory();
        bookingHistory.setBooking(booking);
        bookingHistory.setType(status);
        bookingHistory.setCreatedAt(LocalDateTime.now());
        return bookingHistory;
    }
}
