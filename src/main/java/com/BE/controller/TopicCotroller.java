package com.BE.controller;

import com.BE.enums.TopicEnum;
import com.BE.model.request.TopicRequest;
import com.BE.model.response.FileResponse;
import com.BE.service.interfaceServices.ITopicService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@SecurityRequirement(name = "api")
@RequestMapping(name = "api/topic")
public class TopicCotroller {

    @Autowired
    ResponseHandler responseHandler;

    @Autowired
    ITopicService iTopicService;


    @PostMapping
    public ResponseEntity createTopic(
            @RequestPart("topic") TopicRequest topicRequest,
            @RequestPart("file") MultipartFile file) throws IOException {
        return  responseHandler.response(200,"Create New Topic Successfully", iTopicService.createTopic(topicRequest, file));
    }

    @GetMapping("{id}")
    public ResponseEntity getTopic(@PathVariable UUID id) {
        return  responseHandler.response(200,"Get Topic Detail Successfully", iTopicService.getTopic(id));
    }

    @GetMapping("admin")
    public ResponseEntity getTopicsAdmin() {
        return  responseHandler.response(200,"Get Topic Successfully", iTopicService.getTopicsAdmin());
    }

    @GetMapping("mentor")
    public ResponseEntity getTopicsMentor() {
        return  responseHandler.response(200,"Get Topic Successfully", iTopicService.getTopicsMentor());
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

    @DeleteMapping("{id}")
    public ResponseEntity delete(@PathVariable UUID id) {
        return  responseHandler.response(200,"Delete Topic Successfully", iTopicService.changeStatus(id,TopicEnum.INACTIVE));
    }


}
