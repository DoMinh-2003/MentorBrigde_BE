package com.BE.service.interfaceServices;

import com.BE.model.entity.Team;
import com.BE.model.entity.User;

public interface TeamService {
    Team createTeam();
    Team addMemberToGroup(Team team, User user);
}
