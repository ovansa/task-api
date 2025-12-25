package com.ovansa.task_api.domain.response;

import com.ovansa.task_api.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class CreateTaskResponse {
    private String message;
    private TaskResponseDto data;
}