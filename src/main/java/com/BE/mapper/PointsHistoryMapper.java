package com.BE.mapper;

import com.BE.model.entity.PointsHistory;
import com.BE.model.response.PointsHistoryResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PointsHistoryMapper {

    PointsHistoryResponse toPointsHistoryResponse(PointsHistory pointsHistory);

}
