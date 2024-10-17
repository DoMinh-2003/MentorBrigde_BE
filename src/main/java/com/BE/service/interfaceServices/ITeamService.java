package com.BE.service.interfaceServices;

import com.BE.model.entity.Team;
import com.BE.model.entity.User;

public interface ITeamService {
    Team createTeam();
    void inviteMember(String email, String teamCode);
    void addMemberToTeam(User user, String teamCode);
    void acceptInvitation(String token, String teamCode);
    void setTeamLeader(String email, String teamCode);
    Team getTeamByCode(String teamCode);
}
