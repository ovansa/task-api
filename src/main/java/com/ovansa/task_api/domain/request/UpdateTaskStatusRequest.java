package com.ovansa.task_api.domain.request;

import com.ovansa.task_api.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTaskStatusRequest {
    @NotNull
    private TaskStatus status;
}