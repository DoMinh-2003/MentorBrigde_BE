package com.BE.repository;

import com.BE.model.entity.Room;
import com.BE.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    List<Room> findRoomsByUsersIsContaining(User user);
    Room findRoomByUsersIsContainingAndUsersIsContaining(User user1, User user2);

    Room findRoomByRoomID(int roomID);

    @Query("SELECT r FROM Room r JOIN r.users u WHERE u IN :users GROUP BY r HAVING COUNT(u) = :userCount")
    Room findRoomByUsers(@Param("users") List<User> users, @Param("userCount") long userCount);
}
