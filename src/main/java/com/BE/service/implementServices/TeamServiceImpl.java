package com.BE.service.implementServices;

import com.BE.enums.TeamRoleEnum;
import com.BE.model.EmailDetail;
import com.BE.model.entity.Semester;
import com.BE.model.entity.Team;
import com.BE.model.entity.User;
import com.BE.model.entity.UserTeam;
import com.BE.repository.TeamRepository;
import com.BE.repository.UserTeamRepository;
import com.BE.service.EmailService;
import com.BE.service.JWTService;
import com.BE.service.interfaceServices.ISemesterService;

import com.BE.service.interfaceServices.IStudentService;
import com.BE.service.interfaceServices.ITeamService;
import com.BE.utils.AccountUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class TeamServiceImpl implements ITeamService {

    private final TeamRepository teamRepository;
    private final UserTeamRepository userTeamRepository;
    private final AccountUtils accountUtils;
    private final ISemesterService semesterService;
    private final EmailService emailService;
    private final JWTService jwtService;
    private final IStudentService studentService;

    public TeamServiceImpl(TeamRepository teamRepository,
                           UserTeamRepository userTeamRepository,
                           AccountUtils accountUtils,
                           ISemesterService semesterService,
                           EmailService emailService,
                           JWTService jwtService,
                           IStudentService studentService) {
        this.teamRepository = teamRepository;
        this.userTeamRepository = userTeamRepository;
        this.accountUtils = accountUtils;
        this.semesterService = semesterService;
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.studentService = studentService;
    }

    @Override
    @Transactional
    public Team createTeam() {
        User user = accountUtils.getCurrentUser();

        // Create a new team
        Team team = new Team();
        // Generate team code and set it
        Semester semester = semesterService.getCurrentSemester();
        team.setCode(generateGroupCode(semester));
        team.setCreatedAt(LocalDate.now());
        team.setSemester(semester);
        // Save the team
        teamRepository.save(team);

        // Create relationship between user and team
        UserTeam userTeam = new UserTeam();
        userTeam.setUser(user);
        userTeam.setTeam(team);
        userTeam.setRole(TeamRoleEnum.LEADER);
        // Save relationship between user and team
        userTeamRepository.save(userTeam);

        return team;
    }

    @Override
    public void inviteMember(String email, String teamCode) {
        User user = studentService.getStudentByEmail(email);
        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setRecipient(user.getEmail());
        emailDetail.setSubject("You're Invited to Join the Team: " + teamCode);
        emailDetail.setButtonValue("Accept Invitation");
        emailDetail.setFullName(user.getFullName());
        emailDetail.setLink("http://localhost:8080/api/accept-invitation?token=" + jwtService.generateToken(user) + "&teamCode=" + teamCode);
        Runnable r = () -> emailService.sendMailTemplate(emailDetail);
        new Thread(r).start();
    }
    @Override
    public void acceptInvitation(String token, String teamCode) {
        User user = jwtService.getUserByToken(token);
        if (user != null && !userTeamRepository.existsByUserId(user.getId())) {
            addMemberToTeam(user, teamCode);
        }else {
            throw new IllegalArgumentException("The Invitation is not valid or expired!");
        }
    }

    @Transactional
    @Override
    public void setTeamLeader(String email, String teamCode) {
        User currentUser = accountUtils.getCurrentUser();
        User user = studentService.getStudentByEmail(email);
        UserTeam currentUserTeam = getUserTeamByUserIdAndValidate(currentUser.getId(), teamCode,
                "You must be the current team leader to assign a new leader.");

        if (!currentUserTeam.getRole().equals(TeamRoleEnum.LEADER)) {
            throw new IllegalArgumentException("Only a team leader can assign a new leader.");
        }

        UserTeam newLeaderUserTeam = getUserTeamByUserIdAndValidate(user.getId(), teamCode,
                "The selected user is not part of the team.");

        // Swap roles
        newLeaderUserTeam.setRole(TeamRoleEnum.LEADER);
        currentUserTeam.setRole(TeamRoleEnum.MEMBER);

        // Save changes
        userTeamRepository.save(currentUserTeam);
        userTeamRepository.save(newLeaderUserTeam);
    }

    private UserTeam getUserTeamByUserIdAndValidate(UUID userId, String teamCode, String errorMessage) {
        UserTeam userTeam = userTeamRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User team relationship not found"));
        if (!userTeam.getTeam().getCode().equalsIgnoreCase(teamCode)) {
            throw new IllegalArgumentException(errorMessage);
        }
        return userTeam;
    }

    @Override
    public void addMemberToTeam(User user, String teamCode) {
        Team team = teamRepository.findByCode(teamCode).orElse(null);
        UserTeam userTeam = new UserTeam();
        userTeam.setUser(user);
        userTeam.setTeam(team);
        userTeam.setRole(TeamRoleEnum.MEMBER);
        userTeamRepository.save(userTeam);
    }

    private String generateGroupCode(Semester semester) {
        // Count the number of teams in the current semester
        int teamCount = teamRepository.countBySemester(semester);

        // Increment the counter based on the number of teams
        int counter = teamCount + 1;

        // Get semester details
        String year = String.valueOf(semester.getDateFrom().getYear()).substring(2); // 2-digit year
        String symbol = semester.getCode();

        // Format counter to ensure it's always 3 digits
        String formattedCounter = String.format("%03d", counter);

        // Generate ID: G + semester symbol + year + 3-digit counter

        return "G" + symbol + year + formattedCounter;
    }
}
