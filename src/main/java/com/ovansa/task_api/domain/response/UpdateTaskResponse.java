package com.ovansa.task_api.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UpdateTaskResponse {
    private String message;
    private TaskResponseDto data;
}