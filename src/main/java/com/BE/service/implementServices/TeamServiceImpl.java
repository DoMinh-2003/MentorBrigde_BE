package com.BE.service.implementServices;

import com.BE.model.entity.Team;
import com.BE.model.entity.User;
import com.BE.repository.TeamRepository;
import com.BE.service.interfaceServices.TeamService;
import com.BE.utils.AccountUtils;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class TeamServiceImpl implements TeamService {
    private static int counter = 0;
    private static String currentSemesterSymbol;
    private static int currentSemesterYear;

    private final TeamRepository teamRepository;
    private final AccountUtils accountUtils;

    public TeamServiceImpl(TeamRepository teamRepository, AccountUtils accountUtils) {
        this.teamRepository = teamRepository;
        this.accountUtils = accountUtils;
    }


    @Override
    public Team createTeam() {
        Team team = new Team();
        // Generate team code and set it
        team.setCode(generateGroupCode());
        // Set team users with the current user
        // Use Set.of() for a single user
        team.setUsers(Set.of(accountUtils.getCurrentUser()));
        return teamRepository.save(team);
    }

    @Override
    public Team addMemberToGroup(Team team, User user) {
        team.getUsers().add(user);
        return teamRepository.save(team);
    }


    private String generateGroupCode() {
//        // Reset the counter if the semester has changed
//        if (hasSemesterChanged()) {
//            resetCounter();
//        }
//
//        // Get semester details
//        String year = String.valueOf(semester.getYear()).substring(2); // 2-digit year
//        String symbol = semester.getSymbol();
//
//        // Increment counter
//        counter++;
//
//        // Format counter to ensure it's always 4 digits
//        String formattedCounter = String.format("%04d", counter);
//
//        // Generate ID: G + semester symbol + year + 4-digit counter
//        this.id = "G" + symbol + year + formattedCounter;
//
//        // Update current semester tracking
//        currentSemesterSymbol = symbol;
//        currentSemesterYear = semester.getYear();
        return "id";
    }

//    private boolean hasSemesterChanged() {
//        return currentSemesterSymbol == null ||
//                !currentSemesterSymbol.equals(semester.getSymbol()) ||
//                currentSemesterYear != semester.getYear();
//    }

    private void resetCounter() {
        counter = 0; // Reset the counter to zero
    }
}
