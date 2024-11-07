package com.BE.service.implementServices;

import com.BE.enums.BookingTypeEnum;
import com.BE.enums.PointChangeType;
import com.BE.enums.RoleEnum;
import com.BE.model.entity.PointsHistory;
import com.BE.model.entity.Team;
import com.BE.model.entity.User;
import com.BE.model.entity.UserTeam;
import com.BE.model.response.PointsHistoryResponse;
import com.BE.model.response.PointsResponse;
import com.BE.repository.PointsHistoryRepository;
import com.BE.repository.UserTeamRepository;
import com.BE.service.interfaceServices.IPointsHistoryService;
import com.BE.utils.AccountUtils;
import com.BE.utils.DateNowUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class PointsHistoryService implements IPointsHistoryService {

    @Autowired
    PointsHistoryRepository  pointsHistoryRepository;

    @Autowired
    DateNowUtils dateNowUtils;

    @Autowired
    AccountUtils accountUtils;

    @Autowired
    UserTeamRepository userTeamRepository;


    @Override
    public void createPointsHistory(BookingTypeEnum bookingTypeEnum, PointChangeType pointChangeType, int changePoints, int previousPoints, int newPoints, User student, Team team) {

        PointsHistory pointsHistory = new PointsHistory();
        pointsHistory.setBookingTypeEnum(bookingTypeEnum);
        pointsHistory.setPointChangeType(pointChangeType);
        pointsHistory.setChangePoints(changePoints);
        pointsHistory.setPreviousPoints(previousPoints);
        pointsHistory.setNewPoints(newPoints);
        if(student != null) pointsHistory.setStudent(student);
        if(team != null) pointsHistory.setTeam(team);
        pointsHistory.setChangeTime(dateNowUtils.dateNow());

        pointsHistoryRepository.save(pointsHistory);
    }

    @Override
    public PointsResponse getUserPoints() {

        int teamPoints = 0;

        User user =  accountUtils.getCurrentUser();

        Optional<UserTeam> userTeam = userTeamRepository.findByUserId(user.getId());

        if(userTeam.isPresent()){
            if(RoleEnum.STUDENT.equals(userTeam.get().getRole())){
                teamPoints = userTeam.get().getTeam().getPoints();
            }
        }
        return PointsResponse.builder()
                .studentPoints(user.getPoints())
                .teamPoints(teamPoints).build();
    }

    @Override
    public PointsHistoryResponse getPointsHistory() {
        return null;
    }
}
