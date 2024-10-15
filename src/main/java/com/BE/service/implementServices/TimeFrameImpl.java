package com.BE.service.implementServices;

import com.BE.enums.SemesterEnum;
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

            for (TimeFrameRequest timeFrameRequest : timeFramesForDay) {
                if (!isValidTimeFrame(timeFrameRequest)) {
                    return "Invalid time frame for " + dayOfWeek + ": Start time must be before end time.";
                }
            }
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
        Boolean error = false;
        String overallErrorMessage = null;
        Config config = configRepository.findFirstBy();
        Duration minTimeSlotDuration = config.getMinTimeSlotDuration();

        if(minTimeSlotDuration.compareTo(scheduleRequest.getSlotDuration()) > 0){
            overallErrorMessage = "Error: Remaining time is not sufficient to create a new slot";
            error = true;
        }

        // Lặp qua từng ngày trong ScheduleRequest
        List<TimeFrameRequest> allTimeFrames = new ArrayList<>();
        allTimeFrames.addAll(scheduleRequest.getMonday());
        allTimeFrames.addAll(scheduleRequest.getTuesday());
        allTimeFrames.addAll(scheduleRequest.getWednesday());
        allTimeFrames.addAll(scheduleRequest.getThursday());
        allTimeFrames.addAll(scheduleRequest.getFriday());
        allTimeFrames.addAll(scheduleRequest.getSaturday());
        allTimeFrames.addAll(scheduleRequest.getSunday());

        // Kiểm tra tính hợp lệ cho mỗi khung thời gian
        for (TimeFrameRequest timeFrameRequest : allTimeFrames) {
            if (!timeFrameRequest.getStartTime().isBefore(timeFrameRequest.getEndTime())) {
                throw new IllegalArgumentException("Invalid time frame: Start time must be before end time.");
            }
        }

        // Kiểm tra khung giờ chồng lấp theo từng ngày
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        List<List<TimeFrameRequest>> dailySlots = Arrays.asList(
                scheduleRequest.getMonday(),
                scheduleRequest.getTuesday(),
                scheduleRequest.getWednesday(),
                scheduleRequest.getThursday(),
                scheduleRequest.getFriday(),
                scheduleRequest.getSaturday(),
                scheduleRequest.getSunday()
        );

        for (int i = 0; i < dailySlots.size(); i++) {
            List<TimeFrameRequest> dailySlot = dailySlots.get(i);
            for (int j = 0; j < dailySlot.size(); j++) {
                TimeFrameRequest currentFrame = dailySlot.get(j);
                for (int t = j + 1; t < dailySlot.size(); t++) {
                    TimeFrameRequest comparingFrame = dailySlot.get(t);

                    // Kiểm tra xem hai khoảng thời gian có giống hệt nhau không
                    if (currentFrame.getStartTime().equals(comparingFrame.getStartTime()) &&
                            currentFrame.getEndTime().equals(comparingFrame.getEndTime())) {
                        messages.computeIfAbsent(daysOfWeek[i], k -> new ArrayList<>())
                                .add("Error: Time frames are identical: " + currentFrame + " and " + comparingFrame + ".");
                        error = true;
                    }else{
                        // Kiểm tra xem hai khoảng thời gian có chồng lấp không
                        if (currentFrame.getStartTime().isBefore(comparingFrame.getEndTime()) &&
                                comparingFrame.getStartTime().isBefore(currentFrame.getEndTime())) {
                            messages.computeIfAbsent(daysOfWeek[i], k -> new ArrayList<>())
                                    .add("Error: Time frames overlap between " + currentFrame + " and " + comparingFrame + ".");
                            error = true;
                        }
                    }
                }
            }
        }


        // Tính toán tổng thời gian
        for (TimeFrameRequest timeFrameRequest : allTimeFrames) {
            totalDuration = totalDuration.plus(calculateHoursForDay(Collections.singletonList(timeFrameRequest)));
        }



        // Kiểm tra phần thời gian dư cho mỗi ngày
        for (int i = 0; i < dailySlots.size(); i++) {
            List<TimeFrameRequest> dailySlot = dailySlots.get(i);
            if (!dailySlot.isEmpty()) {
                // Tính tổng thời gian cho ngày

                for (TimeFrameRequest slot : dailySlot) {
                    Duration dailyTotalDuration = Duration.ZERO;
                    dailyTotalDuration = dailyTotalDuration.plus(Duration.between(slot.getStartTime(), slot.getEndTime()));


                // Kiểm tra xem thời gian tổng có phải là bội số của slotDuration không
                Duration slotDuration = scheduleRequest.getSlotDuration();
                if (dailyTotalDuration.toMinutes() % slotDuration.toMinutes() != 0) {
                    // Tính phần dư
                    Duration remainingDuration = dailyTotalDuration.minus(slotDuration.multipliedBy(dailyTotalDuration.toMinutes() / slotDuration.toMinutes()));

                    if (remainingDuration.compareTo(minTimeSlotDuration) < 0) {
                        messages.computeIfAbsent(daysOfWeek[i], k -> new ArrayList<>())
                                .add("Error: Remaining time is not sufficient to create a new slot for " + daysOfWeek[i] + ".");
                        error = true;
                    } else {
                        // Gửi cảnh báo đến client
                        messages.computeIfAbsent(daysOfWeek[i], k -> new ArrayList<>())
                                .add("Warning: You have " + remainingDuration.toMinutes() + " minutes remaining for " + daysOfWeek[i] + ". Do you want to create a slot with this time?");
                    }
                }
                }
            }
        }

        int totalHour = (int) totalDuration.toHours();
        int totalMinutes = (int) (totalDuration.toMinutes() % 60);

        if (totalHour < config.getMinimumHours()) {
            overallErrorMessage = "Your total hours are " + totalHour + "h" + totalMinutes + "p" + " not enough for the semester. You need at least " + config.getMinimumHours() + " hours.";
            error = true;
        }

        return TotalHoursResponse.builder()
                .currentTotalHours(totalHour + "h" + totalMinutes + "p")
                .minimumRequiredHours(config.getMinimumHours())
                .messages(messages)
                .overallErrorMessage(overallErrorMessage)
                .error(error)
                .build();
    }

    private Duration calculateHoursForDay(List<TimeFrameRequest> timeFrameRequests) {
        Duration totalDuration = Duration.ZERO;

        for (TimeFrameRequest timeFrame : timeFrameRequests) {
            // Tính toán số giờ giữa startTime và endTime
            Duration duration = Duration.between(timeFrame.getStartTime(), timeFrame.getEndTime());
            totalDuration = totalDuration.plus(duration); // Cộng dồn Duration
        }

        return totalDuration;
    }

}

