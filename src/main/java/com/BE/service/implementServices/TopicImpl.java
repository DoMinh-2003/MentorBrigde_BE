package com.BE.service.implementServices;


import com.BE.enums.RoleEnum;
import com.BE.enums.SemesterEnum;
import com.BE.enums.StatusEnum;
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
import com.BE.utils.DateNowUtils;
import com.BE.utils.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Autowired
    DateNowUtils dateNowUtils;

    @Autowired
    PageUtil pageUtil;




    @Override
    public TopicResponse createTopic(TopicRequest topicRequest, MultipartFile file) throws IOException {
        File document = new File();
        User creator = accountUtils.getCurrentUser();
        Semester semester = semesterRepository.findByStatus(SemesterEnum.UPCOMING).orElseThrow(() -> new NotFoundException("Semester not found"));

        Topic topic = topicMapper.toTopic(topicRequest);
        topic.setCreatedAt(dateNowUtils.dateNow());
        if(topicRequest.getTeamId() != null) {
            Team team = teamRepository.findById(topicRequest.getTeamId()).orElseThrow(() -> new NotFoundException("Team not found"));
            team.getTopics().add(topic);
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
        try {
            topic = topicRepository.save(topic);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return topicMapper.toTopicResponse(topic);
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
    public TopicResponse updateTopic(UUID id, MultipartFile file) throws IOException {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found"));
        File document = new File();
        document.setName(file.getOriginalFilename());
        document.setContent(file.getBytes());
        document.setTopic(topic);
        topic.getFiles().add(document);
        return topicMapper.toTopicResponse(topicRepository.save(topic));
    }



    public Specification<Topic> hasCreatorId(UUID creatorId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("creator").get("id"), creatorId);
    }

    public  Specification<Topic> hasSemesterId(UUID semesterId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("semester").get("id"), semesterId);
    }

    public  Specification<Topic> hasStatus(TopicEnum status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public  Specification<Topic> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }


    @Override
    public List<TopicResponse> getTopics(String name, TopicEnum status, int page, int size, String sortBy, String sortDirection, String semesterCode) {

        pageUtil.checkOffset(page);

        Pageable pageable = pageUtil.getPageable(page - 1, size, sortBy, sortDirection);

        User user = accountUtils.getCurrentUser();

        Semester semester = semesterCode != null ?  semesterRepository.findSemesterByCode(semesterCode) : semesterRepository.findFirstByOrderByCreatedAtDesc();

        Specification<Topic> spec = Specification.where(hasName(name));



        if(user.getRole().equals(RoleEnum.ADMIN)){
            spec = spec.and(hasSemesterId(semester.getId()));
        } else if (user.getRole().equals(RoleEnum.MENTOR)) {
            spec = spec.and(hasSemesterId(semester.getId()));
            spec = spec.and(hasCreatorId(user.getId()));
        }else{
            spec = spec.and(hasSemesterId(semesterRepository.findByStatus(SemesterEnum.UPCOMING).get().getId()));
            status = TopicEnum.INACTIVE;
        }

        if (status != null) {
            spec = spec.and(hasStatus(status));
        }
        Page<Topic> topicPage = topicRepository.findAll(spec, pageable);

        return topicPage.getContent().stream().map(topicMapper::toTopicResponse).collect(Collectors.toList());
    }






    @Override
    public TopicResponse changeStatus(UUID id,TopicEnum topicEnum) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found"));
        topic.setStatus(topicEnum);

        if(topicEnum.equals(TopicEnum.ACCEPTED)){
            if(topic.getTeam() != null) {
                topic.setStatus(TopicEnum.ACTIVE);
            }else{
                topic.setStatus(TopicEnum.INACTIVE);
            }
        }
        return topicMapper.toTopicResponse(topicRepository.save(topic));
    }


}
