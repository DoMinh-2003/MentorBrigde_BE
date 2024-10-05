package com.BE.service.implementServices;

import com.BE.model.entity.Semester;
import com.BE.model.entity.Team;
import com.BE.model.entity.User;
import com.BE.repository.TeamRepository;
import com.BE.service.interfaceServices.ISemesterService;
import com.BE.service.interfaceServices.TeamService;
import com.BE.utils.AccountUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;

@Service
public class TeamServiceImpl implements TeamService {

    private static final String REDIS_COUNTER_KEY = "teamCodeCounter";
    private static final String REDIS_SEMESTER_SYMBOL_KEY = "currentSemesterSymbol";
    private static final String REDIS_SEMESTER_YEAR_KEY = "currentSemesterYear";

    private final TeamRepository teamRepository;
    private final AccountUtils accountUtils;
    private final ISemesterService semesterService;
    private final RedisTemplate<String, Object> redisTemplate;

    public TeamServiceImpl(TeamRepository teamRepository,
                           AccountUtils accountUtils,
                           ISemesterService semesterService,
                           RedisTemplate<String, Object> redisTemplate) {
        this.teamRepository = teamRepository;
        this.accountUtils = accountUtils;
        this.semesterService = semesterService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Team createTeam() {
        User user = accountUtils.getCurrentUser();
        // Check if the user is already part of a team
        if (user.getTeam() != null) {
            throw new IllegalStateException("User is already have a team. Please sign out first.");
        }
        // Create a new team
        Team team = new Team();
        // Generate team code and set it
        Semester semester = semesterService.getCurrentSemester();
        team.setCode(generateGroupCode(semester));
        team.setCreatedAt(LocalDate.now());
        team.setCreatedBy(user.getFullName());
        // Set the current user in the team
        team.setUsers(Set.of(user));
        user.setTeam(team);
        // Save the team
        teamRepository.save(team);
        return team;
    }


    @Override
    public Team addMemberToGroup(Team team, User user) {
        return null;
    }

    private String generateGroupCode(Semester semester) {
        // Reset the counter if the semester has changed
        if (hasSemesterChanged(semester)) {
            resetCounter();
        }

        // Get semester details
        String year = String.valueOf(semester.getDateFrom().getYear()).substring(2); // 2-digit year
        String symbol = semester.getCode();

        // Increment counter from Redis
        Integer counter = (Integer) redisTemplate.opsForValue().get(REDIS_COUNTER_KEY);
        if (counter == null) {
            counter = 0;
        }
        counter++;
        redisTemplate.opsForValue().set(REDIS_COUNTER_KEY, counter);

        // Format counter to ensure it's always 3 digits
        String formattedCounter = String.format("%03d", counter);

        // Generate ID: G + semester symbol + year + 3-digit counter
        String id = "G" + symbol + year + formattedCounter;

        // Update current semester tracking in Redis
        redisTemplate.opsForValue().set(REDIS_SEMESTER_SYMBOL_KEY, symbol);
        redisTemplate.opsForValue().set(REDIS_SEMESTER_YEAR_KEY, semester.getDateFrom().getYear());

        return id;
    }

    private boolean hasSemesterChanged(Semester semester) {
        String currentSymbol = (String) redisTemplate.opsForValue().get(REDIS_SEMESTER_SYMBOL_KEY);
        Integer currentYear = (Integer) redisTemplate.opsForValue().get(REDIS_SEMESTER_YEAR_KEY);

        return currentSymbol == null ||
                !currentSymbol.equals(semester.getCode()) ||
                currentYear == null || currentYear != semester.getDateFrom().getYear();
    }

    private void resetCounter() {
        // Reset the counter in Redis to zero
        redisTemplate.opsForValue().set(REDIS_COUNTER_KEY, 0);
    }
}
