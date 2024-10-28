package com.BE.mapper;

import com.BE.model.entity.Room;
import com.BE.model.response.RoomResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    RoomResponse toRoomResponse(Room room);
}
