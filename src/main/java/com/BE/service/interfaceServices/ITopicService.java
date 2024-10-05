package com.BE.service.interfaceServices;

import com.BE.enums.TopicEnum;
import com.BE.model.request.TopicRequest;
import com.BE.model.response.FileResponse;
import com.BE.model.response.TopicResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface ITopicService {

   TopicResponse createTopic(TopicRequest topicRequest, MultipartFile file) throws IOException;

   FileResponse getFile(UUID id);

   TopicResponse getTopic(UUID id);
   TopicResponse getTopicsAdmin();
   TopicResponse getTopicsMentor();

   TopicResponse changeStatus(UUID id,TopicEnum topicEnum);


}
