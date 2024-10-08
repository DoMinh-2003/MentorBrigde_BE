package com.BE.service.implementServices;

import com.BE.enums.RoleEnum;
import com.BE.enums.TeamRoleEnum;
import com.BE.model.entity.Semester;
import com.BE.model.entity.Team;
import com.BE.model.entity.User;
import com.BE.model.entity.UserTeam; // Import UserTeam
import com.BE.repository.TeamRepository;
import com.BE.repository.UserTeamRepository; // Import UserTeamRepository
import com.BE.service.interfaceServices.ISemesterService;

import com.BE.service.interfaceServices.ITeamService;
import com.BE.utils.AccountUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class TeamServiceImpl implements ITeamService {

    private final TeamRepository teamRepository;
    private final UserTeamRepository userTeamRepository;
    private final AccountUtils accountUtils;
    private final ISemesterService semesterService;

    public TeamServiceImpl(TeamRepository teamRepository,
                           UserTeamRepository userTeamRepository,
                           AccountUtils accountUtils,
                           ISemesterService semesterService) {
        this.teamRepository = teamRepository;
        this.userTeamRepository = userTeamRepository;
        this.accountUtils = accountUtils;
        this.semesterService = semesterService;
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
    public Team addMemberToGroup(Team team, User user) {
        // Create relationship between the user and the group
        UserTeam userTeam = new UserTeam();
        userTeam.setUser(user);
        userTeam.setTeam(team);
        if (user.getRole() == RoleEnum.MENTOR) {
            userTeam.setRole(TeamRoleEnum.MENTOR);
        } else {
            userTeam.setRole(TeamRoleEnum.MEMBER);
        }

        // Save relationship between user and group
        userTeamRepository.save(userTeam);
        return team;
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
