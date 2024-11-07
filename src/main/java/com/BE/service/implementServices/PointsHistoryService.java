package com.BE.service.implementServices;

import com.BE.enums.BookingTypeEnum;
import com.BE.enums.PointChangeType;
import com.BE.enums.RoleEnum;
import com.BE.enums.TeamRoleEnum;
import com.BE.mapper.PointsHistoryMapper;
import com.BE.model.entity.*;
import com.BE.model.response.PointsHistoryResponse;
import com.BE.model.response.PointsResponse;
import com.BE.repository.ConfigRepository;
import com.BE.repository.PointsHistoryRepository;
import com.BE.repository.UserTeamRepository;
import com.BE.service.interfaceServices.IPointsHistoryService;
import com.BE.utils.AccountUtils;
import com.BE.utils.DateNowUtils;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


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

    @Autowired
    PointsHistoryMapper pointsHistoryMapper;

    @Autowired
    ConfigRepository configRepository;


    @Override
    public void createPointsHistory(Booking booking, BookingTypeEnum bookingTypeEnum, PointChangeType pointChangeType, int changePoints, int previousPoints, int newPoints, User student, Team team) {

        PointsHistory pointsHistory = new PointsHistory();
        pointsHistory.setBooking(booking);
        pointsHistory.setBookingTypeEnum(bookingTypeEnum);
        pointsHistory.setPointChangeType(pointChangeType);
        pointsHistory.setChangePoints(changePoints);
        pointsHistory.setPreviousPoints(previousPoints);
        pointsHistory.setNewPoints(newPoints);
        if(student != null) pointsHistory.setStudent(student);
        if(team != null) pointsHistory.setTeam(team);
        pointsHistory.setChangeTime(dateNowUtils.dateNow());
        booking.getPointsHistories().add(pointsHistory);
//        try {
//            pointsHistoryRepository.save(pointsHistory);
//        }catch(Exception e){
//            System.out.println(e.getMessage());
//        }

    }

    @Override
    public PointsResponse getUserPoints() {

        Config config =  configRepository.findFirstBy();

        int teamPoints = 0;

        User user =  accountUtils.getCurrentUser();

        Optional<UserTeam> userTeam = userTeamRepository.findByUserId(user.getId());

        if(userTeam.isPresent()){
            if(!TeamRoleEnum.MENTOR.equals(userTeam.get().getRole())){
                teamPoints = userTeam.get().getTeam().getPoints();
            }
        }
        return PointsResponse.builder()
                .studentPoints(user.getPoints())
                .teamPoints(teamPoints)
                .totalStudentPoints(config.getTotalStudentPoints())
                .totalTeamPoints(config.getTotalTeamPoints())
                .build();
    }

    private Specification<PointsHistory> filterByTypeAndChangeTypeAndUser(
            BookingTypeEnum bookingTypeEnum, PointChangeType pointChangeType, User user) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Predicate for bookingTypeEnum
            if (bookingTypeEnum != null) {
                predicates.add(criteriaBuilder.equal(root.get("bookingTypeEnum"), bookingTypeEnum));
            }

            // Predicate for pointChangeType
            if (pointChangeType != null) {
                predicates.add(criteriaBuilder.equal(root.get("pointChangeType"), pointChangeType));
            }

            // Predicate for direct points history of the user
            Predicate directHistory = criteriaBuilder.equal(root.get("student"), user);

            // Predicate for points history through team membership
            Join<PointsHistory, Team> teamJoin = root.join("team", JoinType.LEFT);
            Join<Team, UserTeam> userTeamJoin = teamJoin.join("userTeams", JoinType.LEFT);
            Predicate teamHistory = criteriaBuilder.equal(userTeamJoin.get("user"), user);

            // Combine direct history and team history with OR
            predicates.add(criteriaBuilder.or(directHistory, teamHistory));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public List<PointsHistoryResponse> getPointsHistory(BookingTypeEnum bookingTypeEnum, PointChangeType pointChangeType) {

        User user = accountUtils.getCurrentUser();

        Specification<PointsHistory> spec = filterByTypeAndChangeTypeAndUser(bookingTypeEnum, pointChangeType, user);
        return pointsHistoryRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "changeTime")).stream().map(pointsHistoryMapper::toPointsHistoryResponse).collect(Collectors.toList());
    }


}
