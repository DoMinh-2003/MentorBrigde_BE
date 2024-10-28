package com.BE.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private int roomID;

    private String name;

    Date lastUpdated = new Date();

    String lastMessage;

    UUID leaderId;


    @JsonManagedReference
    @ManyToMany(mappedBy = "rooms",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<User> users;

    @OneToMany(mappedBy = "room")
    private List<Message> messages;
}
