package com.BE.controller;


import com.BE.model.entity.Room;
import com.BE.model.request.GetRoomRequest;
import com.BE.model.request.MessageRequest;
import com.BE.model.request.RoomRequest;
import com.BE.service.interfaceServices.IChatService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("api/chat")
@SecurityRequirement(name = "api")
public class ChatController {

    @Autowired
    IChatService iChatService;

    @PostMapping()
    public ResponseEntity createNewChat(@RequestBody RoomRequest roomRequest) {
        Room room = iChatService.createNewRoom(roomRequest);
        return ResponseEntity.ok(room);
    }

    @GetMapping()
    public ResponseEntity getChatByAccountID() {
        List<Room> rooms = iChatService.getRoomsByAccountID();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/detail/{roomID}")
    public ResponseEntity getChatDetail(@PathVariable int roomID) {
        return ResponseEntity.ok(iChatService.getRoomDetail(roomID));
    }

    @PostMapping("/send/{roomID}")
    public ResponseEntity sendMessage(@PathVariable int roomID, @RequestBody MessageRequest messageRequest) {
        return ResponseEntity.ok(iChatService.sendMessage(messageRequest,roomID));
    }

    @PostMapping("/typing/{roomID}/{name}")
    public void typingMessage(@PathVariable int roomID, @PathVariable String name) {
        iChatService.setTyping(roomID, name);
    }

    @PostMapping("/room")
    public ResponseEntity<Room> getRoom(@RequestBody GetRoomRequest getRoomRequest) {
        return ResponseEntity.ok(iChatService.getRoom(getRoomRequest));
    }
}
