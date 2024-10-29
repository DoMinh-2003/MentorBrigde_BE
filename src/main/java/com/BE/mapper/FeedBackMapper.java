package com.BE.mapper;

import com.BE.model.entity.Feedback;
import com.BE.model.request.UpdateFeedbackRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FeedBackMapper {
    Feedback toFeedback(UpdateFeedbackRequest request);
    void updateFeedback(@MappingTarget Feedback feedback, UpdateFeedbackRequest request);
}
