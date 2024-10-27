package com.BE.service.interfaceServices;

import com.BE.model.entity.Message;
import com.BE.model.entity.Room;
import com.BE.model.request.GetRoomRequest;
import com.BE.model.request.MessageRequest;
import com.BE.model.request.RoomRequest;

import java.util.List;

public interface IChatService {

     Room getRoom(GetRoomRequest getRoomRequest);
     void setTyping(int roomID, String name);

     Message sendMessage(MessageRequest messageRequest, int roomId);

    Room getRoomDetail(int roomID);

    List<Room> getRoomsByAccountID();

    Room createNewRoom(RoomRequest roomRequest);
}
