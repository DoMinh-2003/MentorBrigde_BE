package com.BE.service.interfaceServices;

import com.BE.enums.TeamRoleEnum;
import com.BE.model.entity.Team;
import com.BE.model.entity.User;
import com.BE.model.entity.UserTeam;

import java.util.List;
import java.util.UUID;

public interface ITeamService {
    Team createTeam();
    void inviteMember(String email, String teamCode);
    void addMemberToTeam(User user, String teamCode);
    void acceptInvitation(String token,String teamCode);
    void setTeamLeader(String email, String teamCode);
    Team getTeamByCode(String teamCode);
    UserTeam getUserTeamByUserIdAndValidate(UUID userId, String teamCode, String errorMessage);
    UserTeam getCurrentUserTeam();
    List<Team> getTeamsByUserIdAndRole(TeamRoleEnum role);
}
