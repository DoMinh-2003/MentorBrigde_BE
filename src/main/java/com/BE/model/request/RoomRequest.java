package com.BE.model.request;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class RoomRequest {
    UUID leaderId;
    String name;
    List<UUID> members;
}
