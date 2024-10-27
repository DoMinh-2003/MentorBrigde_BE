package com.BE.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequest {
    int roomID;
    String message;
}
