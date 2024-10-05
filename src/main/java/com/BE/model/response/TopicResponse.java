package com.BE.model.response;


import com.BE.enums.TopicEnum;
import com.BE.model.entity.Team;
import com.BE.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicResponse {

    UUID id;

    String name;

    String description;

    Team team;

    User creator;

    TopicEnum status;

    List<FileResponse> files;
}
