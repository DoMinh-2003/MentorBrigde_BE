package com.BE.utils;

import com.BE.model.entity.User;
import com.BE.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AccountUtils {
    @Autowired
    UserRepository userRepository;

    public User getCurrentUser(){
        String userName=  SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(userName).orElseThrow();
        return user;
        }
}
