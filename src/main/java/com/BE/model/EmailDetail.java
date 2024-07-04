package com.BE.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDetail {

    private String recipient;
    private String msgBody;
    private String subject;
    private String fullName;
    private String attachment;
    private String buttonValue;
    private String link;

}
