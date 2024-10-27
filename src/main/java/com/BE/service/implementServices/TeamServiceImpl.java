package com.BE.service.implementServices;

import com.BE.enums.TeamRoleEnum;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.model.EmailDetail;
import com.BE.model.entity.Semester;
import com.BE.model.entity.Team;
import com.BE.model.entity.User;
import com.BE.model.entity.UserTeam;
import com.BE.repository.TeamRepository;
import com.BE.repository.UserTeamRepository;
import com.BE.service.EmailService;
import com.BE.service.JWTService;
import com.BE.service.interfaceServices.INotificationService;
import com.BE.service.interfaceServices.ISemesterService;

import com.BE.service.interfaceServices.IStudentService;
import com.BE.service.interfaceServices.ITeamService;
import com.BE.utils.AccountUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    private final INotificationService notificationService;

    public TeamServiceImpl(TeamRepository teamRepository,
                           UserTeamRepository userTeamRepository,
                           AccountUtils accountUtils,
                           ISemesterService semesterService,
                           EmailService emailService,
                           JWTService jwtService,
                           IStudentService studentService,
                           INotificationService notificationService) {
        this.teamRepository = teamRepository;
        this.userTeamRepository = userTeamRepository;
        this.accountUtils = accountUtils;
        this.semesterService = semesterService;
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.studentService = studentService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public Team createTeam() {
        User user = accountUtils.getCurrentUser();
        // Check if user already has a team
        if (userTeamRepository.existsByUserId(user.getId())) {
            throw new IllegalArgumentException("You already have a team. Please out of it before creating a new one.");
        }
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
        User currentUser = accountUtils.getCurrentUser();
        User user = studentService.getStudentByEmail(email);
        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setRecipient(user.getEmail());
        emailDetail.setSubject("You're Invited to Join the Team: " + teamCode);
        emailDetail.setButtonValue("Accept Invitation");
        emailDetail.setFullName(user.getFullName());
        emailDetail.setLink("http://localhost:5173/team-invite?token=" + jwtService.generateToken(user) + "&teamCode=" + teamCode);
        //
        notificationService.createNotification("Lời mời gia nhập nhóm",
                currentUser.getFullName() + " đã mời bạn vào nhóm " + teamCode,
                user,false);
        Runnable r = () -> emailService.sendMailTemplate(emailDetail);
        new Thread(r).start();
    }

    @Override
    public void acceptInvitation(String token, String teamCode) {
        User user = jwtService.getUserByToken(token);
        if (user != null && !userTeamRepository.existsByUserId(user.getId())) {
            addMemberToTeam(user, teamCode);
            Team team = getTeamByCode(teamCode);
            Set<UserTeam> users = team.getUserTeams();
            for (UserTeam userTeam : users) {
                notificationService.createNotification("Chấp nhận vào nhóm",
                        user.getFullName() + " đã vào nhóm " + teamCode,
                        userTeam.getUser(),true);
            }
        } else {
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
        // notification
        Set<UserTeam> users = newLeaderUserTeam.getTeam().getUserTeams();
        for (UserTeam userTeam : users) {
            notificationService.createNotification("Thay đổi nhóm trưởng",
                    user.getFullName() + " Là nhóm trưởng mới của team  " + teamCode,
                    userTeam.getUser(),true);
        }
        // Save changes
        userTeamRepository.save(currentUserTeam);
        userTeamRepository.save(newLeaderUserTeam);
    }

    public UserTeam getUserTeamByUserIdAndValidate(UUID userId, String teamCode, String errorMessage) {
        UserTeam userTeam = userTeamRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User team relationship not found"));
        if (!userTeam.getTeam().getCode().equalsIgnoreCase(teamCode)) {
            throw new IllegalArgumentException(errorMessage);
        }
        return userTeam;
    }

    @Override
    public void addMemberToTeam(User user, String teamCode) {
        Team team = teamRepository.findByCode(teamCode).orElseThrow(() -> new NotFoundException("Team not found"));
        UserTeam userTeam = new UserTeam();
        userTeam.setUser(user);
        userTeam.setTeam(team);
        userTeam.setRole(TeamRoleEnum.MEMBER);
        userTeamRepository.save(userTeam);
    }

    @Override
    public Team getTeamByCode(String teamCode) {
        if (teamCode != null) {
            return teamRepository.findByCode(teamCode).orElseThrow(() -> new NotFoundException("Team not found"));
        }
        User user = accountUtils.getCurrentUser();
        Optional<UserTeam> userTeamOpt = user.getUserTeams().stream().findFirst();
        if (userTeamOpt.isPresent()) {
            Team team = userTeamOpt.get().getTeam();
            if (team != null) {
                return teamRepository.findByCode(team.getCode()).orElseThrow(() -> new NotFoundException("Team not found"));
            }
        }
        throw new NotFoundException("No team found for the current user");
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

    @Override
    public UserTeam getCurrentUserTeam() {
        User user = accountUtils.getCurrentUser();
        return userTeamRepository.findByUserId(user.getId()).orElseThrow(() -> new NotFoundException("User team relationship not found"));

    }

    @Override
    public List<Team> getTeamsByUserIdAndRole(TeamRoleEnum role) {
        User user = accountUtils.getCurrentUser();
        List<UserTeam> userTeams = userTeamRepository.findByUserIdAndRole(user.getId(), role);
        return userTeams.stream().map(UserTeam::getTeam).toList();
    }
}
