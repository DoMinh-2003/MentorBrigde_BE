package com.BE.controller;

import com.BE.enums.TopicEnum;
import com.BE.model.request.TopicRequest;

import com.BE.service.interfaceServices.ITopicService;
import com.BE.utils.ResponseHandler;
import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("api/topic")
@SecurityRequirement(name = "api")
@CrossOrigin("*")
public class TopicCotroller {

    @Autowired
    ResponseHandler responseHandler;

    @Autowired
    ITopicService iTopicService;


    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity createTopic(
            @RequestPart("topic") TopicRequest topicRequest,
             @RequestPart("file") MultipartFile file) throws IOException {
        return  responseHandler.response(200,"Create New Topic Successfully", iTopicService.createTopic(topicRequest, file));
    }

    @GetMapping("{id}")
    public ResponseEntity getTopic(@PathVariable UUID id) {
        return  responseHandler.response(200,"Get Topic Detail Successfully", iTopicService.getTopic(id));
    }

    @GetMapping()
    public ResponseEntity getTopics(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(required = false) TopicEnum status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) String semesterCode
    ) {
        return  responseHandler.response(200,"Get Topic Successfully", iTopicService.getTopics(name, status, page, size, sortBy, sortDirection,semesterCode));
    }


    @PutMapping(value = "{id}",consumes = "multipart/form-data")
    public ResponseEntity updateTopic(@PathVariable UUID id, @RequestPart("file") MultipartFile file) throws IOException {
        return  responseHandler.response(200,"Update Topic Successfully", iTopicService.updateTopic(id,file));

    }


    @GetMapping("download/{fileId}")
    public ResponseEntity getFile(@PathVariable UUID fileId) {
        return  responseHandler.response(200,"Get File Topic Successfully", iTopicService.getFile(fileId));
    }

    @PatchMapping("accepted/{id}")
    public ResponseEntity accepted(@PathVariable UUID id) {
        return  responseHandler.response(200,"Accepted Topic Successfully", iTopicService.changeStatus(id,TopicEnum.ACCEPTED));
    }

    @PatchMapping("rejected/{id}")
    public ResponseEntity rejected(@PathVariable UUID id) {
        return  responseHandler.response(200,"Rejected Topic Successfully", iTopicService.changeStatus(id,TopicEnum.REJECTED));
    }


}
