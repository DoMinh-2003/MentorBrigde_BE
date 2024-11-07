package com.BE.service.interfaceServices;

import com.BE.enums.BookingTypeEnum;
import com.BE.enums.PointChangeType;
import com.BE.model.entity.Team;
import com.BE.model.entity.User;
import com.BE.model.response.PointsHistoryResponse;
import com.BE.model.response.PointsResponse;

import java.util.List;


public interface IPointsHistoryService {

    void createPointsHistory(BookingTypeEnum bookingTypeEnum, PointChangeType pointChangeType, int changePoints, int previousPoints, int newPoints, User student, Team team);


    PointsResponse getUserPoints();


    List<PointsHistoryResponse> getPointsHistory(BookingTypeEnum bookingTypeEnum, PointChangeType pointChangeType);
}
