package com.BE.model.response;


import com.BE.model.entity.Message;
import com.BE.model.entity.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomResponse {

     int roomID;

     String name;

     Date lastUpdated = new Date();

     String lastMessage;

     Set<User> users;

     List<Message> messages;
}
