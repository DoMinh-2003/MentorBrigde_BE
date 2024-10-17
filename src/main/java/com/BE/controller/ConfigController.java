package com.BE.controller;

import com.BE.model.request.ConfigRequest;
import com.BE.model.request.SemesterRequest;
import com.BE.model.response.SemesterResponse;
import com.BE.service.interfaceServices.IAdminService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/config")
@SecurityRequirement(name = "api")
public class ConfigController {

    @Autowired
    IAdminService iAdminService;

    @Autowired
    ResponseHandler responseHandler;

    @PostMapping
    public ResponseEntity createConfig(@Valid @RequestBody ConfigRequest configRequest){
        return  responseHandler.response(200,"Create New Config Successfully",iAdminService.createConfig(configRequest));
    }
    @PutMapping("{id}")
    public ResponseEntity updateConfig(@PathVariable UUID id,@Valid @RequestBody ConfigRequest configRequest){
        return  responseHandler.response(200,"Update Config Successfully",iAdminService.updateConfig(id, configRequest));
    }
    @GetMapping
    public ResponseEntity getConfig(){
        return  responseHandler.response(200,"Get Config Successfully",iAdminService.getConfig());
    }

}
