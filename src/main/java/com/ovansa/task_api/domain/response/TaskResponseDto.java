package com.ovansa.task_api.domain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ovansa.task_api.domain.entities.Task;
import com.ovansa.task_api.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskResponseDto {
    private UUID id;
    private String title;
    private TaskStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant completedAt;

    public static TaskResponseDto from(Task task) {
        return new TaskResponseDto (
                task.getId (),
                task.getTitle (),
                task.getStatus (),
                task.getCreatedAt (),
                task.getUpdatedAt (),
                task.getCompletedAt ()
        );
    }
}
