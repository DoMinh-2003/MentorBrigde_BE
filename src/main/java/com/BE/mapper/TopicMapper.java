package com.BE.mapper;


import com.BE.model.entity.Topic;
import com.BE.model.request.TopicRequest;
import com.BE.model.response.TopicResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TopicMapper {

    Topic toTopic(TopicRequest topicRequest);

    TopicResponse toTopicResponse(Topic topic);
}
