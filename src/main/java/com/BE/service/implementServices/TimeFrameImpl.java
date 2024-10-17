package com.BE.service.implementServices;

import com.BE.enums.SemesterEnum;
import com.BE.exception.exceptions.BadRequestException;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.model.entity.Config;
import com.BE.model.entity.Semester;
import com.BE.model.entity.TimeFrame;
import com.BE.model.entity.User;
import com.BE.model.request.ScheduleRequest;
import com.BE.model.request.TimeFrameRequest;
import com.BE.model.response.TotalHoursResponse;
import com.BE.repository.ConfigRepository;
import com.BE.repository.SemesterRepository;
import com.BE.repository.TimeFrameRepository;
import com.BE.service.interfaceServices.ITimeFrameService;
import com.BE.utils.AccountUtils;
import com.BE.utils.DateNowUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;


@Service
public class TimeFrameImpl implements ITimeFrameService {

    @Autowired
    TimeFrameRepository timeFrameRepository;

    @Autowired
    SemesterRepository semesterRepository;

    @Autowired
    ConfigRepository configRepository;

    @Autowired
    AccountUtils accountUtils;

    @Autowired
    DateNowUtils dateNowUtils;


    @Override
    public String createSchedule(ScheduleRequest scheduleRequest) {


        // Lấy thông tin kỳ học từ cơ sở dữ liệu
        Semester semester = semesterRepository.findByStatus(SemesterEnum.UPCOMING)
                .orElseThrow(() -> new NotFoundException("Semester not found"));

        TotalHoursResponse  totalHoursResponse = calculateTotalHours(scheduleRequest);

        if(totalHoursResponse.getError()) {
            throw new NotFoundException("Fail Create Semester");
        }

        LocalDateTime semesterStartDate = semester.getDateFrom();
        LocalDateTime semesterEndDate = semester.getDateTo();

        // Lặp qua từng ngày của kỳ học
        for (LocalDateTime date = semesterStartDate; !date.isAfter(semesterEndDate); date = date.plusDays(1)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();

            // Lấy danh sách TimeFrameRequest cho ngày hiện tại
            List<TimeFrameRequest> timeFramesForDay = getTimeFramesForDay(scheduleRequest, dayOfWeek);



            // Chia các khung giờ thành các slot dựa trên Duration
            Config config = configRepository.findFirstBy();

            for (TimeFrameRequest timeFrameRequest : timeFramesForDay) {
                List<TimeFrame> slots = splitTimeFrame(accountUtils.getCurrentUser(),semester , timeFrameRequest, scheduleRequest.getSlotDuration(), config.getMinTimeSlotDuration(), date);

                // Lưu các slot vào CSDL
                timeFrameRepository.saveAll(slots);
            }
        }
        return "Create Schedule Successfully";

    }

    private boolean isValidTimeFrame(TimeFrameRequest timeFrameRequest) {
        return timeFrameRequest.getStartTime().isBefore(timeFrameRequest.getEndTime());
    }

    // Lấy khung giờ cho mỗi ngày trong tuần
    private List<TimeFrameRequest> getTimeFramesForDay(ScheduleRequest scheduleRequest, DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return scheduleRequest.getMonday();
            case TUESDAY:
                return scheduleRequest.getTuesday();
            case WEDNESDAY:
                return scheduleRequest.getWednesday();
            case THURSDAY:
                return scheduleRequest.getThursday();
            case FRIDAY:
                return scheduleRequest.getFriday();
            case SATURDAY:
                return scheduleRequest.getSaturday();
            case SUNDAY:
                return scheduleRequest.getSunday();
            default:
                return Collections.emptyList();
        }
    }

    private List<TimeFrame> splitTimeFrame(User user, Semester semester, TimeFrameRequest timeFrameRequest, Duration slotDuration, Duration minTimeSlotDuration, LocalDateTime date) {

        List<TimeFrame> slots = new ArrayList<>();

        LocalTime startTime = timeFrameRequest.getStartTime();
        LocalTime endTime = timeFrameRequest.getEndTime();
        LocalDateTime slotStart = date.with(startTime);
        LocalDateTime slotEnd = date.with(endTime);

        while (!slotStart.plus(slotDuration).isAfter(slotEnd)) {
            LocalDateTime nextSlotEnd = slotStart.plus(slotDuration);

            TimeFrame timeFrame = new TimeFrame();
            timeFrame.setTimeFrameFrom(slotStart);
            timeFrame.setTimeFrameTo(nextSlotEnd);
            timeFrame.setMentor(user);
            timeFrame.setSemester(semester);

            slots.add(timeFrame);

            slotStart = nextSlotEnd;
        }

        // Kiểm tra phần thừa, chỉ tạo slot nếu lớn hơn minTimeSlotDuration
        if (slotStart.isBefore(slotEnd) && java.time.Duration.between(slotStart, slotEnd).compareTo(minTimeSlotDuration) >= 0) {
            TimeFrame remainderSlot = new TimeFrame();
            remainderSlot.setTimeFrameFrom(slotStart);
            remainderSlot.setTimeFrameTo(slotEnd);
            remainderSlot.setSemester(semester);
            slots.add(remainderSlot);
        }

        return slots;
    }
    @Override
    public Map<LocalDate, List<TimeFrame>> getGroupedTimeSlots(UUID id) {

        Semester semester = semesterRepository.findByStatus(SemesterEnum.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Semester not found"));

        List<TimeFrame> timeSlots = timeFrameRepository.findByMentorIdAndSemesterIdOrderByTimeFrameFromAsc(id,semester.getId());

        Map<LocalDate, List<TimeFrame>> groupedTimeSlots = new TreeMap<>();

        for (TimeFrame slot : timeSlots) {
            LocalDate dateKey = slot.getTimeFrameFrom().toLocalDate();
            groupedTimeSlots
                    .computeIfAbsent(dateKey, k -> new ArrayList<>())
                    .add(slot);
        }

        return groupedTimeSlots;
    }

    @Override
    public TotalHoursResponse calculateTotalHours(ScheduleRequest scheduleRequest) {
        Duration totalDuration = Duration.ZERO; // Khởi tạo biến tổng
        Map<String, List<String>> messages = new LinkedHashMap<>();
        boolean error = false;
        String overallErrorMessage = null;

        Config config = configRepository.findFirstBy();
        Duration minTimeSlotDuration = config.getMinTimeSlotDuration();

        // Kiểm tra thời gian slot
        if (minTimeSlotDuration.compareTo(scheduleRequest.getSlotDuration()) > 0) {
            overallErrorMessage = "Error: Không đủ thời gian để tạo slot";
            error = true;
        }

        // Kiểm tra các khung thời gian cho từng ngày trong tuần
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (String day : daysOfWeek) {
            List<TimeFrameRequest> dailySlot = getTimeFramesForDay(scheduleRequest, DayOfWeek.valueOf(day.toUpperCase()));
            error |= checkForOverlappingAndIdenticalFrames(dailySlot, messages, day);
            error |= checkDayDuration(dailySlot, error, messages, day, minTimeSlotDuration, scheduleRequest.getSlotDuration());

        }

        // Lấy thông tin kỳ học từ cơ sở dữ liệu
        Semester semester = semesterRepository.findByStatus(SemesterEnum.UPCOMING)
                .orElseThrow(() -> new NotFoundException("Semester not found"));

        LocalDateTime semesterStartDate = semester.getDateFrom();
        LocalDateTime semesterEndDate = semester.getDateTo();

        // Lặp qua từng ngày trong kỳ học
        for (LocalDateTime date = semesterStartDate; !date.isAfter(semesterEndDate); date = date.plusDays(1)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            List<TimeFrameRequest> timeFramesForDay = getTimeFramesForDay(scheduleRequest, dayOfWeek);

            totalDuration = totalDuration.plus(calculateDurationSemester(timeFramesForDay, messages, dayOfWeek.name(), minTimeSlotDuration, scheduleRequest.getSlotDuration()));
        }

        // Kiểm tra giờ tối thiểu
        int totalHours = (int) totalDuration.toHours();
        int totalMinutes = (int) (totalDuration.toMinutes() % 60);

        if (totalHours < config.getMinimumHours()) {
            overallErrorMessage = "Tổng số giờ của bạn là " + totalHours + "h" + totalMinutes + "m" +
                    " không đủ cho học kỳ. Bạn cần ít nhất " + config.getMinimumHours() + " giờ.";
            error = true;
        }

        return TotalHoursResponse.builder()
                .currentTotalHours(totalHours + "h" + totalMinutes + "m")
                .minimumRequiredHours(config.getMinimumHours())
                .messages(messages)
                .overallErrorMessage(overallErrorMessage)
                .error(error)
                .build();
    }

    private boolean checkForOverlappingAndIdenticalFrames(List<TimeFrameRequest> timeFrames, Map<String, List<String>> messages, String dayOfWeek) {
        boolean error = false;

        for (int j = 0; j < timeFrames.size(); j++) {
            TimeFrameRequest currentFrame = timeFrames.get(j);
            for (int t = j + 1; t < timeFrames.size(); t++) {
                TimeFrameRequest comparingFrame = timeFrames.get(t);

                // Kiểm tra xem hai khoảng thời gian có giống hệt nhau không
                if (currentFrame.getStartTime().equals(comparingFrame.getStartTime()) &&
                        currentFrame.getEndTime().equals(comparingFrame.getEndTime())) {
                    messages.computeIfAbsent(dayOfWeek, k -> new ArrayList<>())
                            .add("Error: Khung thời gian giống hệt nhau: " + currentFrame + " và " + comparingFrame + ".");
                    error = true;
                } else {
                    // Kiểm tra xem hai khoảng thời gian có chồng lấp không
                    if (currentFrame.getStartTime().isBefore(comparingFrame.getEndTime()) &&
                            comparingFrame.getStartTime().isBefore(currentFrame.getEndTime())) {
                        messages.computeIfAbsent(dayOfWeek, k -> new ArrayList<>())
                                .add("Error: Khung thời gian chồng chéo giữa " + currentFrame + " và " + comparingFrame + ".");
                        error = true;
                    }
                }
            }
        }

        return error;
    }

    private boolean checkDayDuration(List<TimeFrameRequest> timeFramesForDay, boolean error, Map<String, List<String>> messages, String dayOfWeek, Duration minTimeSlotDuration, Duration slotDuration) {
        Duration dailyTotalDuration = Duration.ZERO;

        // Kiểm tra tính hợp lệ và tính tổng thời gian cho khung giờ
        for (TimeFrameRequest timeFrameRequest : timeFramesForDay) {
            if (!isValidTimeFrame(timeFrameRequest)) {
                messages.computeIfAbsent(dayOfWeek, k -> new ArrayList<>())
                        .add("Error: Khung thời gian cho " + dayOfWeek + " không hợp lệ: Thời gian bắt đầu phải trước thời gian kết thúc.");
                error = true;
            }

            Duration duration = Duration.between(timeFrameRequest.getStartTime(), timeFrameRequest.getEndTime());
            dailyTotalDuration = dailyTotalDuration.plus(duration);
        }

        // Kiểm tra phần thời gian dư cho từng khung giờ
        if (dailyTotalDuration.toMinutes() % slotDuration.toMinutes() != 0) {
            Duration remainingDuration = dailyTotalDuration.minus(slotDuration.multipliedBy(dailyTotalDuration.toMinutes() / slotDuration.toMinutes()));
            if (remainingDuration.compareTo(minTimeSlotDuration) < 0) {
                messages.computeIfAbsent(dayOfWeek, k -> new ArrayList<>())
                        .add("Error: Thời gian còn lại không đủ để tạo một slot mới cho " + dayOfWeek + ".");
                error = true;
            } else {
                messages.computeIfAbsent(dayOfWeek, k -> new ArrayList<>())
                        .add("Warning: Bạn còn " + remainingDuration.toMinutes() + " phút cho " + dayOfWeek + ". Bạn có muốn tạo một slot với thời gian này không?");
            }
        }

        return error;
    }

    private Duration calculateDurationSemester(List<TimeFrameRequest> timeFramesForDay, Map<String, List<String>> messages, String dayOfWeek, Duration minTimeSlotDuration, Duration slotDuration) {
        Duration dailyTotalDuration = Duration.ZERO;

        // Kiểm tra tính hợp lệ và tính tổng thời gian cho khung giờ
        for (TimeFrameRequest timeFrameRequest : timeFramesForDay) {
            if (isValidTimeFrame(timeFrameRequest)) {
                Duration duration = Duration.between(timeFrameRequest.getStartTime(), timeFrameRequest.getEndTime());
                dailyTotalDuration = dailyTotalDuration.plus(duration);
            }
        }


        return dailyTotalDuration;
    }
    @Override
    public TimeFrame getById(UUID id){
        return timeFrameRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("TimeFrame not found"));
    }
}

