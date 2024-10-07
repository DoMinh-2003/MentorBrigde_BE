package com.BE.mapper;


import com.BE.enums.StatusEnum;
import com.BE.model.entity.File;
import com.BE.model.entity.Topic;
import com.BE.model.request.TopicRequest;
import com.BE.model.response.FileResponse;
import com.BE.model.response.TopicResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TopicMapper {

    Topic toTopic(TopicRequest topicRequest);


    TopicResponse toTopicResponse(Topic topic);


}
