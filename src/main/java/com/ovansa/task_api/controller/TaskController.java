package com.ovansa.task_api.controller;

import com.ovansa.task_api.domain.Messages;
import com.ovansa.task_api.domain.entities.Task;
import com.ovansa.task_api.domain.request.CreateTaskRequest;
import com.ovansa.task_api.domain.request.UpdateTaskRequest;
import com.ovansa.task_api.domain.request.UpdateTaskStatusRequest;
import com.ovansa.task_api.domain.response.CreateTaskResponse;
import com.ovansa.task_api.domain.response.TaskResponseDto;
import com.ovansa.task_api.domain.response.UpdateTaskResponse;
import com.ovansa.task_api.enums.TaskStatus;
import com.ovansa.task_api.exception.BadRequestException;
import com.ovansa.task_api.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<CreateTaskResponse> createTask(@RequestBody CreateTaskRequest request) throws BadRequestException {
        Task task = taskService.createTaskForUser (request.getTitle ());

        return new ResponseEntity<>(
                new CreateTaskResponse(Messages.TASK_CREATED, TaskResponseDto.from(task)),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<UpdateTaskResponse> updateTask(
            @PathVariable UUID taskId,
            @RequestBody UpdateTaskRequest request
    ) {
        Task task = taskService.updateTask(taskId, request.getTitle());
        return new ResponseEntity<>(new UpdateTaskResponse (Messages.TASK_UPDATED, TaskResponseDto.from (task)),
                HttpStatus.OK);
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<UpdateTaskResponse> changeStatus(
            @PathVariable UUID taskId,
            @RequestBody UpdateTaskStatusRequest request
            ) {
        Task task = taskService.changeStatus(taskId, request.getStatus ());
        return new ResponseEntity<>(new UpdateTaskResponse (Messages.TASK_UPDATED, TaskResponseDto.from (task)),
                HttpStatus.OK);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponseDto> getTask(@PathVariable UUID taskId) {
        Task task = taskService.getTask(taskId);
        return ResponseEntity.ok(TaskResponseDto.from(task));
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponseDto>> getTasks(Pageable pageable) {
        Page<Task> tasks = taskService.getTasksForAuthenticatedUser(pageable);

        return new ResponseEntity<> (tasks.map (TaskResponseDto::from), HttpStatus.OK);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}
