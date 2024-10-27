package com.BE.service.implementServices;


import com.BE.model.entity.Message;
import com.BE.model.entity.Room;
import com.BE.model.entity.User;
import com.BE.model.request.GetRoomRequest;
import com.BE.model.request.MessageRequest;
import com.BE.model.request.RoomRequest;
import com.BE.repository.MessageRepository;
import com.BE.repository.RoomRepository;
import com.BE.repository.UserRepository;
import com.BE.service.interfaceServices.IChatService;
import com.BE.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatService implements IChatService {

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    @Autowired
    AccountUtils accountUtils;

    @Autowired
    FcmService fcmService;


    @Override
    public Room createNewRoom(RoomRequest roomRequest) {
        Set<User> users = new HashSet<>();
        User user1 = userRepository.findById(roomRequest.getMembers().get(0)).orElseThrow();
        User user2 = userRepository.findById(roomRequest.getMembers().get(1)).orElseThrow();
        Room roomCheck = roomRepository.findRoomByUsersIsContainingAndUsersIsContaining(user1,user2);
        if(roomCheck!=null) return roomCheck;

        Room room = new Room();
        room.setUsers(users);
        for (UUID accountId : roomRequest.getMembers()) {
            try {
                User user = userRepository.findById(accountId).orElseThrow();
                user.getRooms().add(room);
                users.add(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return roomRepository.save(room);
    }
    @Override
    public List<Room> getRoomsByAccountID() {
        User user = accountUtils.getCurrentUser();
        List<Room> rooms = roomRepository.findRoomsByUsersIsContaining(user);
//        RoomResponseDTO roomResponseDTO = new RoomResponseDTO();
//
//        roomResponseDTO.setRoomID();

        if (rooms != null) {
            return rooms.stream().sorted(Comparator.comparing(Room::getLastUpdated).reversed()).collect(Collectors.toList());
        }
        return null;
    }
    @Override
    public Room getRoomDetail(int roomID) {
        Room roomDTO = roomRepository.findRoomByRoomID(roomID);
        if (roomDTO != null)
            roomDTO.setMessages(roomDTO.getMessages().stream().sorted(Comparator.comparing(Message::getCreateAt)).collect(Collectors.toList()));
        return roomDTO;
    }
    @Override
    public Message sendMessage(MessageRequest messageRequest, int roomId) {
        User user = accountUtils.getCurrentUser();
        Room roomDTO = roomRepository.findRoomByRoomID(roomId);
        Message messageDTO = new Message();
        messageDTO.setUser(user);
        messageDTO.setRoom(roomDTO);
        messageDTO.setMessage(messageRequest.getMessage());
        roomDTO.setLastUpdated(new Date());
        roomDTO.setLastMessage(messageRequest.getMessage());
        roomRepository.save(roomDTO);
        for (User user1 : roomDTO.getUsers()) {
            if (!user1.getId().equals(user.getId())) {
                System.out.println("real time");
                messagingTemplate.convertAndSend("/topic/chat/" + user1.getId(), "New message");
//                for (FCM fcm : account.getFcms()) {
//                    FcmNotification fcmNotification = new FcmNotification();
//                    fcmNotification.setBody(messageRequest.getMessage());
//                    fcmNotification.setTitle(user.getUsername());
//                    fcmNotification.setToken("dLCMVCE6UsmVczaeLJqgdz:APA91bF68pN13e-9_f4s-tMbA1_F86_rb-L0vYFLffhjgBAY0FO77yqBHk6NP3GE6vfeYH6yDqMd7JXBn2tu6KQIqb2xmk602hk9SX7EoYInyJgB1T9vaCzll9I5UqE0XJ09DzHiv2kB");
//                    try {
//                        fcmService.sendPushNotification(fcmNotification);
//                    } catch (FirebaseMessagingException | FirebaseAuthException e) {
//                        e.printStackTrace();
////                    }
//                }
            }
        }
        return messageRepository.save(messageDTO);
    }
    @Override
    public void setTyping(int roomID, String name) {
        Room roomDTO = roomRepository.findRoomByRoomID(roomID);
        for (User account : roomDTO.getUsers()) {
            messagingTemplate.convertAndSend("/topic/chat/" + account.getId(), name + " .gitis typing ... ") ;
        }
    }

    @Override
    public Room getRoom(GetRoomRequest getRoomRequest) {
        User user1 = userRepository.findById(getRoomRequest.getUser1()).orElseThrow();
        User user2 = userRepository.findById(getRoomRequest.getUser2()).orElseThrow();

        Set<User> accountDTOS = new HashSet<>();
        accountDTOS.add(user1);
        accountDTOS.add(user2);

        Room room = roomRepository.findRoomByUsersIsContainingAndUsersIsContaining(user1, user2);
        if (room == null) {
            room = new Room();
            room.setUsers(accountDTOS);
            room.setName("[" + user1.getFullName() + " and "+ user2.getFullName() + "]");
            room = roomRepository.save(room);
        }

        return room;
    }


}
