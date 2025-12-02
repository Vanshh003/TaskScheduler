package com.pers.taskScheduler.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailPayload {
    private String to;
    private String subject;
    private String body;
}
