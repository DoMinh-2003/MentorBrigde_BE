package com.BE.controller;

import com.BE.enums.BookingTypeEnum;
import com.BE.model.entity.Team;
import com.BE.model.response.DataResponseDTO;
import com.BE.model.response.UserResponse;
import com.BE.service.GoogleMeetService;
import com.BE.service.JWTService;
import com.BE.service.interfaceServices.IBookingService;
import com.BE.service.interfaceServices.IStudentService;
import com.BE.service.interfaceServices.ITeamService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api")
@SecurityRequirement(name = "api")
@Tag(name = "Student Controller")
@CrossOrigin("*")
public class StudentController {
    private final IStudentService studentService;
    private final ITeamService teamService;
    private final ResponseHandler<Object> responseHandler;
    private final IBookingService bookingService;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private GoogleMeetService googleMeetService;
    public StudentController(IStudentService studentService,
                             ITeamService teamService,
                             ResponseHandler<Object> responseHandler,
                             IBookingService bookingService) {
        this.studentService = studentService;
        this.teamService = teamService;
        this.responseHandler = responseHandler;
        this.bookingService = bookingService;

    }

    @GetMapping("/students")
    public ResponseEntity<DataResponseDTO<Object>> searchStudents(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "1", required = false) int offset,
            @RequestParam(defaultValue = "10", required = false) int size,
            @Parameter(description = "Sort by", schema = @Schema(allowableValues = {"studentCode", "fullName", "dayOfBirth"}))
            @RequestParam(required = false) String sortBy,
            @Parameter(description = "Sort direction", schema = @Schema(allowableValues = {"asc", "desc"}))
            @RequestParam(required = false) String sortDirection) {

        // Check if the search term is provided
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new DataResponseDTO<>(400,
                    "Search term is required.", null));
        }

        // Search by name or code
        Page<UserResponse> result = studentService.searchStudents(searchTerm, offset, size, sortBy, sortDirection);

        return responseHandler.response(200, "Search successful!", result);
    }


    @PostMapping("/team")
    public ResponseEntity<DataResponseDTO<Object>> createTeam() {
        return responseHandler.response(200, "Create group success!", teamService.createTeam());
    }

    @PostMapping("/team/invite")
    public ResponseEntity<Object> inviteMember(@RequestParam String email,
                                               @RequestParam String teamCode) {
        teamService.inviteMember(email, teamCode);
        return ResponseEntity.ok( "Invite member successfully!");
    }
    @PutMapping("/accept-invitation")
    public ResponseEntity<Object> acceptInvitation(@RequestParam String token,@RequestParam String teamCode) {
        teamService.acceptInvitation(token,teamCode);
        return ResponseEntity.ok( "Accept invitation successfully!");
    }
    @PutMapping("/team/set-leader")
    public ResponseEntity<Object> setLeader(@RequestParam String email,
                                            @RequestParam String teamCode) {
        teamService.setTeamLeader(email, teamCode);
        return ResponseEntity.ok( "Set leader successfully!");
    }
    @PostMapping("/booking")
    public ResponseEntity<DataResponseDTO<Object>> createBooking(@RequestParam UUID timeFrameId,
                                                                 @RequestParam BookingTypeEnum type) {
        return responseHandler.response(200, "Create booking success!",
                bookingService.createBooking(timeFrameId, type));
    }
    @GetMapping("/team")
    public ResponseEntity<DataResponseDTO<Object>> getTeam(@RequestParam(required = false) String teamCode) {
        Team team = teamService.getTeamByCode(teamCode);
        return responseHandler.response(200, "Get team success!", team);
    }
//    @PostMapping("/meet")
//    public ResponseEntity<DataResponseDTO<Object>> getMeet() {
//        return responseHandler.response(200, "Get meet success!", googleMeetService.createGoogleMeetLink());
//    }
}

