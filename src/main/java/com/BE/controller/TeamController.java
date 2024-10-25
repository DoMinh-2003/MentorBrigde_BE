package com.BE.controller;

import com.BE.enums.TeamRoleEnum;
import com.BE.model.response.DataResponseDTO;
import com.BE.service.interfaceServices.ITeamService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@SecurityRequirement(name ="api")
@CrossOrigin("*")
@RequestMapping("api")
public class TeamController {
    @Autowired
    ResponseHandler responseHandler;
    @Autowired
    private ITeamService teamService;
    @GetMapping("/team/{mentorId}")
    public ResponseEntity<DataResponseDTO<Object>> getTeamByMentorId(@PathVariable UUID mentorId) {
        return responseHandler.response(200,"Create New Schedule Successfully",
                teamService.getTeamsByUserIdAndRole(mentorId, TeamRoleEnum.MENTOR));

    }
}
