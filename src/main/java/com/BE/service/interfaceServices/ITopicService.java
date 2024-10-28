package com.BE.service.interfaceServices;

import com.BE.enums.TopicEnum;
import com.BE.model.entity.Topic;
import com.BE.model.request.TopicRequest;
import com.BE.model.response.FileResponse;
import com.BE.model.response.TopicResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ITopicService {

   TopicResponse createTopic(TopicRequest topicRequest, MultipartFile file) throws IOException;

   FileResponse getFile(UUID id);

   TopicResponse getTopic(UUID id);

   TopicResponse updateTopic(UUID id, MultipartFile file) throws IOException;

   List<TopicResponse> getTopics(String name, TopicEnum status, int page, int size, String sortBy, String sortDirection, String semesterCode);

   TopicResponse changeStatus(UUID id,TopicEnum topicEnum);

   Topic getTopicById(UUID id);

   void saveTopic(Topic topic);

}
