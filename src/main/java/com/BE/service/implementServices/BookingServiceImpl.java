package com.BE.service.implementServices;


import com.BE.enums.*;
import com.BE.exception.exceptions.BadRequestException;
import com.BE.mapper.BookingMapper;
import com.BE.mapper.UserMapper;
import com.BE.model.EmailDetail;
import com.BE.model.entity.*;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.model.request.BookingRequestFilter;
import com.BE.model.request.BookingStatusRequest;
import com.BE.model.request.RoomRequest;
import com.BE.model.response.BookingResponse;
import com.BE.repository.BookingHistoryRepository;
import com.BE.repository.BookingRepository;
import com.BE.repository.SemesterRepository;
import com.BE.repository.TimeFrameRepository;
import com.BE.service.GoogleMeetService;
import com.BE.service.JWTService;
import com.BE.service.interfaceServices.*;
import com.BE.utils.AccountUtils;
import com.BE.utils.PageUtil;

import com.BE.utils.SendMailUtils;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements IBookingService {

    private final BookingRepository bookingRepository;
    private final ITimeFrameService timeFrameService;
    private final AccountUtils accountUtils;
    private final ITeamService teamService;
    private final BookingHistoryRepository bookingHistoryRepository;
    private final UserMapper userMapper;
    private final GoogleMeetService googleMeetService;
    private final INotificationService notificationService;
    @Autowired
    PageUtil pageUtil;

    @Autowired
    SemesterRepository semesterRepository;

    @Autowired
    TimeFrameRepository timeFrameRepository;

    @Autowired
    BookingMapper bookingMapper;

    @Autowired
    SendMailUtils sendMailUtils;

    @Autowired
    JWTService jwtService;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              ITimeFrameService timeFrameService,
                              AccountUtils accountUtils,
                              ITeamService teamService,
                              BookingHistoryRepository bookingHistoryRepository,
                              UserMapper userMapper,
                              GoogleMeetService googleMeetService,
                              INotificationService notificationService) {
        this.bookingRepository = bookingRepository;
        this.timeFrameService = timeFrameService;
        this.accountUtils = accountUtils;
        this.teamService = teamService;
        this.bookingHistoryRepository = bookingHistoryRepository;
        this.userMapper = userMapper;
        this.googleMeetService = googleMeetService;
        this.notificationService = notificationService;
    }


    @Autowired
    IChatService iChatService;

    @Override
    public Booking createBooking(UUID timeFrameId, BookingTypeEnum type) {
        TimeFrame timeFrame = timeFrameService.getById(timeFrameId);
        validateTimeFrame(timeFrame);
        User currentUser = accountUtils.getCurrentUser();
        User mentor = timeFrame.getMentor();
        String name = currentUser.getFullName();
        Team team = null;
        if (type.equals(BookingTypeEnum.TEAM)) {
            UserTeam userTeam = teamService.getCurrentUserTeam();
            String teamCode = userTeam.getTeam().getCode();
            team = validateTeamBooking(currentUser, teamCode);
            name = userTeam.getTeam().getCode();
        }

        Booking booking = createNewBooking(timeFrame, currentUser, mentor, team, type);
        // Gửi thông báo cho mentor
        notificationService.createNotification("Đặt lịch mentor",
                name + " đặt lịch mentor " +
                        timeFrame.getTimeFrameFrom() +
                        " - " + timeFrame.getTimeFrameTo()
                , mentor, true);
        return booking;
    }


    @Override
    public Map<String, List<BookingResponse>> getBookingMentorAccepted(int month) {
        User user = accountUtils.getCurrentUser();
        Semester semester = semesterRepository.findByStatus(SemesterEnum.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Active semester not found"));

        // Tìm các booking dựa trên mentor, status, semester và month từ TimeFrame
        List<Booking> bookings = bookingRepository.findByMentorAndStatusAndSemesterAndTimeFrameMonth(user, BookingStatusEnum.ACCEPTED, semester, month);

        // Sắp xếp danh sách theo timeFrameFrom (từ ngày sớm nhất đến muộn nhất)
        bookings.sort(Comparator.comparing(booking -> booking.getTimeFrame().getTimeFrameFrom()));

        // Tạo Map để nhóm theo ngày của TimeFrame
        Map<String, List<BookingResponse>> bookingsByDay = new LinkedHashMap<>();

        for (Booking booking : bookings) {
            if (booking.getTimeFrame() != null) {
                // Chuyển đổi LocalDateTime của timeFrameFrom sang String (dạng yyyy-MM-dd)
                String day = booking.getTimeFrame().getTimeFrameFrom().toLocalDate().toString();

                // Thêm booking vào nhóm theo ngày
                bookingsByDay.computeIfAbsent(day, k -> new ArrayList<>())
                        .add(bookingMapper.toBookingResponse(booking));  // Giả sử bạn có phương thức convertToBookingResponse
            }
        }

        return bookingsByDay;
    }

    @Override
    public Map<String, List<BookingResponse>> getBookingStudentAccepted(int month) {
        User user = accountUtils.getCurrentUser();
        Semester semester = semesterRepository.findByStatus(SemesterEnum.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Active semester not found"));

        // Tìm các booking dựa trên student hoặc member team, status, semester và month từ TimeFrame
        List<Booking> bookings = bookingRepository.findByStudentOrTeamMemberAndStatusAndSemesterAndTimeFrameMonth(user, BookingStatusEnum.ACCEPTED, semester, month);

        // Sắp xếp danh sách theo timeFrameFrom (từ ngày sớm nhất đến muộn nhất)
        bookings.sort(Comparator.comparing(booking -> booking.getTimeFrame().getTimeFrameFrom()));

        // Tạo Map để nhóm theo ngày của TimeFrame
        Map<String, List<BookingResponse>> bookingsByDay = new LinkedHashMap<>();

        for (Booking booking : bookings) {
            if (booking.getTimeFrame() != null) {
                // Chuyển đổi LocalDateTime của timeFrameFrom sang String (dạng yyyy-MM-dd)
                String day = booking.getTimeFrame().getTimeFrameFrom().toLocalDate().toString();

                // Thêm booking vào nhóm theo ngày
                bookingsByDay.computeIfAbsent(day, k -> new ArrayList<>())
                        .add(bookingMapper.toBookingResponse(booking)); // Giả sử bạn có phương thức convertToBookingResponse
            }
        }

        return bookingsByDay;
    }


    private Specification<Booking> filterBookings(User user, BookingTypeEnum type, BookingStatusEnum status, Semester semester) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Add conditions based on whether user is a mentor or student
            if (user.getRole().equals(RoleEnum.MENTOR)) {
                predicates.add(cb.equal(root.get("mentor"), user));
            } else {
                predicates.add(cb.equal(root.get("student"), user));
            }

            // Add status condition
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            // Add semester condition
            if (semester != null) {
                predicates.add(cb.equal(root.get("semester"), semester));
            }

            // Add type condition if provided
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public List<BookingResponse> getBooking(BookingTypeEnum type, BookingStatusEnum status) {
        User user = accountUtils.getCurrentUser();
        Semester semester = semesterRepository.findByStatus(SemesterEnum.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Active semester not found"));

        // Sử dụng Specification để lọc booking
        Specification<Booking> spec = filterBookings(user, type, status, semester);
        List<Booking> bookings = bookingRepository.findAll(spec);

        // Sắp xếp theo vai trò người dùng
        if (user.getRole().equals(RoleEnum.MENTOR)) {
            bookings.sort(Comparator.comparing((Booking booking) ->
                            booking.getTeam() != null &&
                                    booking.getTeam().getUserTeams().stream()
                                            .anyMatch(userTeam -> userTeam.getUser().equals(user) && userTeam.getRole() == TeamRoleEnum.MENTOR) ? 0 : 1)
                    .thenComparing(Booking::getCreatedAt));
        } else {
            bookings.sort(Comparator.comparing(Booking::getCreatedAt).reversed());
        }


        List<BookingResponse> bookingResponses = bookings.stream()
                .map(bookingMapper::toBookingResponse)
                .collect(Collectors.toList());

        return bookingResponses;
    }

    @Override
    public BookingResponse updateStatus(BookingStatusRequest statusRequest) {
        User user = accountUtils.getCurrentUser();
        Booking booking = bookingRepository.findByIdAndStatus(statusRequest.getId(), BookingStatusEnum.REQUESTED).orElseThrow(() -> new NotFoundException("Booking này không tồn tại"));

        if (statusRequest.getStatus().equals(BookingStatusEnum.ACCEPTED) || statusRequest.getStatus().equals(BookingStatusEnum.REJECTED)) {
            if (booking.getMentor().getId().equals(user.getId())) {
                booking.setStatus(statusRequest.getStatus());
                //send notification
                String message = "Mentor đã chấp nhận booking của bạn";
                String title = "Phản hồi của Mentor";
                if (statusRequest.getStatus().equals(BookingStatusEnum.REJECTED)) {
                    message = "Mentor đã từ chối booking của bạn";
                }
                sendNotificationForTeam(booking, message,title);
            } else {
                throw new BadRequestException("Booking này không phải của bạn");
            }


            if (statusRequest.getStatus().equals(BookingStatusEnum.REJECTED)){
                BookingHistory bookingHistory = logBookingHistory(booking, BookingStatusEnum.REJECTED);
                bookingHistoryRepository.save(bookingHistory);
            }else if(statusRequest.getStatus().equals(BookingStatusEnum.ACCEPTED)){
                BookingHistory bookingHistory = logBookingHistory(booking, BookingStatusEnum.ACCEPTED);
                bookingHistoryRepository.save(bookingHistory);
            }
        } else if (statusRequest.getStatus().equals(BookingStatusEnum.CANCELLED)) {

            if (booking.getType().equals(BookingTypeEnum.INDIVIDUAL)) {
                if (booking.getStudent().getId().equals(user.getId())) {
                    booking.setStatus(statusRequest.getStatus());
                    //send notification
                    String title = "Hủy Booking ";
                    String message = "Đã Hủy Booking của " + booking.getStudent().getFullName();
                    notificationService.createNotification(title, message, booking.getMentor(),true);
                } else {
                    throw new BadRequestException("Booking này không phải của bạn");
                }
            } else {
                boolean isLeader = booking.getTeam().getUserTeams().stream()
                        .anyMatch(userTeam -> userTeam.getUser().getId().equals(user.getId()) && userTeam.getRole().equals(TeamRoleEnum.LEADER));

                if (isLeader) {
                    booking.setStatus(statusRequest.getStatus());
                    //
                    String title = "Hủy Booking";
                    String message = "Đã hủy Booking của nhóm " + booking.getTeam().getCode();
                    notificationService.createNotification(title, message, booking.getMentor(),true);
                } else {
                    throw new BadRequestException("Chỉ leader mới có thể huỷ booking này.");
                }
            }
            BookingHistory bookingHistory = logBookingHistory(booking, BookingStatusEnum.CANCELLED);
            bookingHistoryRepository.save(bookingHistory);

        } else {
            throw new BadRequestException("Không thể chuyển qua status REQUESTED");
        }

        return bookingMapper.toBookingResponse(bookingRepository.save(booking));
    }

    private void sendNotificationForTeam(Booking booking, String message, String title) {
        if (booking.getTeam() != null) {
            Set<UserTeam> userTeams = booking.getTeam().getUserTeams();
            for (UserTeam userTeam : userTeams) {
                notificationService.createNotification(title, message , userTeam.getUser(),true);
            }
        } else {
            notificationService.createNotification(title, message, booking.getStudent(),true);
        }
    }


    private void validateTimeFrame(TimeFrame timeFrame) {
        if (!timeFrame.getSemester().getStatus().equals(SemesterEnum.ACTIVE)) {
            throw new IllegalArgumentException("The time frame is not active.");
        }
        if (timeFrame.getTimeFrameStatus().equals(TimeFrameStatus.BOOKED)) {
            throw new IllegalArgumentException("SLot này đã được booking");
        }
        if (timeFrame.getTimeFrameStatus().equals(TimeFrameStatus.EXPIRED)) {
            throw new IllegalArgumentException("SLot này đã qua thời gian hiện tại");
        }
        if (bookingRepository.existsByTimeFrameIdAndStatus(timeFrame.getId(), BookingStatusEnum.ACCEPTED)) {
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
        try {
            String meetLink = googleMeetService.createGoogleMeetLink(booking);
            booking.setMeetLink(meetLink);
        } catch (Exception e) {
            throw new BadRequestException("Cannot create meet link");
        }
        if (type.equals(BookingTypeEnum.TEAM)) {
            booking.setTeam(team);
            Optional<Topic> topic = team.getTopics().stream().filter((t)-> t.getStatus().equals(TopicEnum.ACTIVE)).findFirst();
            RoomRequest roomRequest = new RoomRequest();
            if (topic.isPresent()) {
                roomRequest.setName(team.getCode() + " - " + topic.get().getName());
            } else {
                // Xử lý trường hợp không có topic nào ACTIVE
                roomRequest.setName(team.getCode() + " - No Active Topic");
            }
            List<UUID> members =
                    team.getUserTeams().stream()
                            .map(userTeam -> userTeam.getUser().getId()) // Giả sử User có phương thức getId() trả về UUID
                            .collect(Collectors.toList());

            User leader = team.getUserTeams().stream().filter(userTeam -> userTeam.getRole().equals(TeamRoleEnum.LEADER)).findFirst().get().getUser();
            members.add(mentor.getId());
            roomRequest.setMembers(members);
            roomRequest.setLeaderId(leader.getId());
            iChatService.createNewRoom(roomRequest);
        } else {
            RoomRequest roomRequest = new RoomRequest();
            List<UUID> members = new ArrayList<>();
            members.add(mentor.getId());
            members.add(currentUser.getId());
            roomRequest.setMembers(members);
            iChatService.createNewRoom(roomRequest);
            booking.setStudent(currentUser);
        }
        BookingHistory bookingHistory = logBookingHistory(booking, BookingStatusEnum.REQUESTED);
        booking.getBookingHistories().add(bookingHistory);
        booking = bookingRepository.save(booking);
        bookingHistoryRepository.save(bookingHistory);
        timeFrame.setTimeFrameStatus(TimeFrameStatus.BOOKED);
        timeFrameRepository.save(timeFrame);
        return booking;
    }

    private BookingHistory logBookingHistory(Booking booking, BookingStatusEnum status) {
        BookingHistory bookingHistory = new BookingHistory();
        bookingHistory.setBooking(booking);
        bookingHistory.setType(status);
        bookingHistory.setCreatedAt(LocalDateTime.now());
        return bookingHistory;
    }

    @Override
    public Booking getBookingById(UUID id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking Not Found"));
    }

    @Override
    public Booking saveBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    @Override
    public List<BookingResponse> getBookingsClosestToNowByUser(){
        User user = accountUtils.getCurrentUser();
        LocalDateTime nowInHoChiMinh = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        return bookingRepository.findBookingsClosestToDateByUser(nowInHoChiMinh, user).stream().map(bookingMapper::toBookingResponse).collect(Collectors.toList());
    }

    @Override
    public Booking requestRescheduleBooking(UUID bookingId, UUID newTimeFrameId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BadRequestException("Booking không tồn tại."));

        TimeFrame newTimeFrame = timeFrameRepository.findById(newTimeFrameId)
                .orElseThrow(() -> new BadRequestException("TimeFrame mới không tồn tại."));

        if (newTimeFrame.getTimeFrameStatus() != TimeFrameStatus.AVAILABLE) {
            throw new BadRequestException("TimeFrame mới không khả dụng.");
        }

        // Đặt trạng thái của booking thành PENDING_RESCHEDULE để chờ xác nhận
        booking.setStatus(BookingStatusEnum.PENDING_RESCHEDULE);
        bookingRepository.save(booking);

        BookingHistory bookingHistory = logBookingHistory(booking, BookingStatusEnum.PENDING_RESCHEDULE);

        bookingHistoryRepository.save(bookingHistory);


        // Gửi thông báo cho team hoặc student để xác nhận
        sendRescheduleNotification(booking, newTimeFrame);

        return booking;
    }

    @Override
    public Booking confirmRescheduleBooking(UUID bookingId, UUID newTimeFrameId, boolean isConfirmed) {
        Booking booking = bookingRepository.findByIdAndStatus(bookingId, BookingStatusEnum.PENDING_RESCHEDULE)
                .orElseThrow(() -> new BadRequestException("Booking không tồn tại."));

        if (!isConfirmed) {
            // Nếu từ chối, đưa booking về trạng thái ban đầu và không dời lịch
            BookingHistory bookingHistory = logBookingHistory(booking, BookingStatusEnum.RESCHEDULE_REJECTED);
            bookingHistoryRepository.save(bookingHistory);

            String title = "Dời lịch Booking ";
            String message =  "Nhóm " + booking.getTeam().getCode() + " đã từ chối dời lịch Booking";
            notificationService.createNotification(title, message, booking.getMentor(),true);

            return booking;
        }

//        // Nếu đồng ý, tiến hành dời lịch
//        TimeFrame currentTimeFrame = booking.getTimeFrame();
        TimeFrame newTimeFrame = timeFrameRepository.findById(newTimeFrameId)
                .orElseThrow(() -> new BadRequestException("TimeFrame mới không tồn tại."));

        if (newTimeFrame.getTimeFrameStatus() != TimeFrameStatus.AVAILABLE) {
            throw new BadRequestException("TimeFrame mới không khả dụng.");
        }

//        currentTimeFrame.setTimeFrameStatus(TimeFrameStatus.AVAILABLE);
//        timeFrameRepository.save(currentTimeFrame);

        booking.setTimeFrame(newTimeFrame);
        booking.setStatus(BookingStatusEnum.RESCHEDULED);
        bookingRepository.save(booking);

        newTimeFrame.setTimeFrameStatus(TimeFrameStatus.BOOKED);
        timeFrameRepository.save(newTimeFrame);

        // Ghi lịch sử dời lịch

        BookingHistory bookingHistory = logBookingHistory(booking, BookingStatusEnum.RESCHEDULED);
        bookingHistoryRepository.save(bookingHistory);

        String title = "Dời lịch Booking ";
        String message =  "Nhóm " + booking.getTeam().getCode() + " đã đồng ý dời lịch Booking ";
        notificationService.createNotification(title, message, booking.getMentor(),true);

        return booking;
    }

    private void sendRescheduleNotification(Booking booking, TimeFrame newTimeFrame) {
        List<User> recipients = new ArrayList<>();

        if (booking.getType() == BookingTypeEnum.TEAM && booking.getTeam() != null) {
            recipients.addAll(booking.getTeam().getUserTeams().stream()
                    .map(UserTeam::getUser)
                    .collect(Collectors.toList()));
        } else if (booking.getStudent() != null) {
            recipients.add(booking.getStudent());
        }

        for (User recipient : recipients) {
            EmailDetail emailDetail = new EmailDetail();
            emailDetail.setSubject("Yêu cầu dời lịch");
            emailDetail.setFullName(recipient.getFullName());
            emailDetail.setRecipient(recipient.getEmail());
            emailDetail.setButtonValue("Xem lịch booking mới");
            emailDetail.setLink("http://localhost5173/booking/reschedule?bookingId="+booking.getId()+"&newTimeFrameId="+newTimeFrame.getId()+"&token="+jwtService.generateToken(recipient));
            sendMailUtils.threadSendMailTemplate(emailDetail);
            String title = "Dời lịch Booking";
            String message = "Đã dời lịch Booking của nhóm " + booking.getTeam().getCode();
            notificationService.createNotification(title, message, recipient,false);
        }
    }

    @Override
    public List<Booking> getBookingsByMentorId(UUID mentorId) {
        List<Booking> bookings = bookingRepository.findByMentorId(mentorId);
        return bookings;
    }



}
