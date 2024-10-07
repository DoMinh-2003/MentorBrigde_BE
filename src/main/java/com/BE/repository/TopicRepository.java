package com.BE.repository;


import com.BE.enums.StatusEnum;
import com.BE.enums.TopicEnum;
import com.BE.model.entity.Topic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TopicRepository extends JpaRepository<Topic, UUID>, JpaSpecificationExecutor<Topic> {

//    List<Topic> findByCreatorIdAndSemesterId(UUID creatorId, UUID semesterId, Pageable pageable);
//
//
//
//    List<Topic> findByStatusAndSemesterId(TopicEnum status, UUID semesterId);



}
