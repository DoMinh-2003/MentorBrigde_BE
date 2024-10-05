package com.BE.service.implementServices;


import com.BE.enums.SemesterEnum;
import com.BE.enums.TopicEnum;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.FileMapper;
import com.BE.mapper.TopicMapper;
import com.BE.model.entity.*;
import com.BE.model.request.TopicRequest;
import com.BE.model.response.FileResponse;
import com.BE.model.response.TopicResponse;
import com.BE.repository.FileRepository;
import com.BE.repository.SemesterRepository;
import com.BE.repository.TeamRepository;
import com.BE.repository.TopicRepository;
import com.BE.service.interfaceServices.ITopicService;
import com.BE.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class TopicImpl implements ITopicService {

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    SemesterRepository semesterRepository;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    TopicMapper topicMapper;

    @Autowired
    FileMapper fileMapper;

    @Autowired
    AccountUtils accountUtils;




    @Override
    public TopicResponse createTopic(TopicRequest topicRequest, MultipartFile file) throws IOException {
        File document = new File();
        User creator = accountUtils.getCurrentUser();
        Semester semester = semesterRepository.findByStatus(SemesterEnum.UPCOMING).orElseThrow(() -> new NotFoundException("Semester not found"));

        Topic topic = topicMapper.toTopic(topicRequest);
        if(topicRequest.getTeamId() != null) {
            Team team = teamRepository.findById(topicRequest.getTeamId()).orElseThrow(() -> new NotFoundException("Team not found"));
            team.setTopic(topic);
            topic.setTeam(team);
        };

        topic.setCreator(creator);
        creator.getTopics().add(topic);

        topic.setSemester(semester);
        semester.getTopics().add(topic);

        topic.setStatus(TopicEnum.PENDING);

        document.setName(file.getOriginalFilename());
        document.setContent(file.getBytes());
        document.setTopic(topic);

        topic.getFiles().add(document);

        return topicMapper.toTopicResponse(topicRepository.save(topic));
    }

    @Override
    public FileResponse getFile(UUID id) {
        File file = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        return  fileMapper.toFileResponse(file);
    }

    @Override
    public TopicResponse getTopic(UUID id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found"));
        return topicMapper.toTopicResponse(topicRepository.save(topic));
    }

    @Override
    public TopicResponse getTopicsAdmin() {
        return null;
    }

    @Override
    public TopicResponse getTopicsMentor() {
        return null;
    }




    @Override
    public TopicResponse changeStatus(UUID id,TopicEnum topicEnum) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found"));
        topic.setStatus(topicEnum);

        return topicMapper.toTopicResponse(topicRepository.save(topic));
    }


}
