package com.BE.service.implementServices;

import com.BE.model.entity.Semester;
import com.BE.model.entity.Team;
import com.BE.model.entity.User;
import com.BE.repository.TeamRepository;
import com.BE.service.interfaceServices.ISemesterService;
import com.BE.service.interfaceServices.TeamService;
import com.BE.utils.AccountUtils;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TeamServiceImpl implements TeamService {
    private static final Object lock = new Object();
    private static final AtomicInteger counter = new AtomicInteger(0);
    private static String currentSemesterSymbol;
    private static int currentSemesterYear;

    private final TeamRepository teamRepository;
    private final AccountUtils accountUtils;
    private final ISemesterService semesterService;

    public TeamServiceImpl(TeamRepository teamRepository, AccountUtils accountUtils,
                           ISemesterService semesterService) {
        this.teamRepository = teamRepository;
        this.accountUtils = accountUtils;
        this.semesterService = semesterService;
    }

    @Override
    public Team createTeam() {
        Team team = new Team();
        // Generate team code and set it
        Semester semester = semesterService.getCurrentSemester();
        team.setCode(generateGroupCode(semester));
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

    private String generateGroupCode(Semester semester) {
        // Reset the counter if the semester has changed
        if (hasSemesterChanged(semester)) {
            resetCounter();
        }

        // Get semester details
        String year = String.valueOf(semester.getDateFrom().getYear()).substring(2); // 2-digit year
        String symbol = semester.getCode();

        // Increment counter atomically
        int currentCount = counter.incrementAndGet();

        // Format counter to ensure it's always 3 digits
        String formattedCounter = String.format("%03d", currentCount);

        // Generate ID: G + semester symbol + year + 3-digit counter
        String id = "G" + symbol + year + formattedCounter;

        // Update current semester tracking
        synchronized (lock) {
            currentSemesterSymbol = symbol;
            currentSemesterYear = semester.getDateFrom().getYear();
        }

        return id;
    }

    private boolean hasSemesterChanged(Semester semester) {
        synchronized (lock) {
            return currentSemesterSymbol == null ||
                    !currentSemesterSymbol.equals(semester.getCode()) ||
                    currentSemesterYear != semester.getDateFrom().getYear();
        }
    }

    private void resetCounter() {
        synchronized (lock) {
            counter.set(0); // Reset the counter to zero
        }
    }
}
