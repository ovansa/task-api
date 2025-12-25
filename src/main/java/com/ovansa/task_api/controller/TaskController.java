package com.ovansa.task_api.controller;

import com.ovansa.task_api.domain.Messages;
import com.ovansa.task_api.domain.entities.Task;
import com.ovansa.task_api.domain.request.CreateTaskRequest;
import com.ovansa.task_api.domain.request.UpdateTaskRequest;
import com.ovansa.task_api.domain.response.CreateTaskResponse;
import com.ovansa.task_api.domain.response.TaskResponseDto;
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
    public ResponseEntity<CreateTaskResponse> createTask(@RequestBody CreateTaskRequest request,
                                                         @RequestParam(required = false) boolean anonymous) throws BadRequestException {
        Task task = anonymous ? taskService.createAnonymousTask (request.getTitle())
                : taskService.createTaskForUser (request.getTitle ());

        return new ResponseEntity<>(
                new CreateTaskResponse(Messages.TASK_CREATED, TaskResponseDto.from(task)),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable UUID taskId,
            @RequestBody UpdateTaskRequest request
    ) {
        Task task = taskService.updateTask(taskId, request.getTitle());
        return ResponseEntity.ok(TaskResponseDto.from(task));
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponseDto> changeStatus(
            @PathVariable UUID taskId,
            @RequestParam TaskStatus status
    ) {
        Task task = taskService.changeStatus(taskId, status);
        return ResponseEntity.ok(TaskResponseDto.from(task));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponseDto> getTask(@PathVariable UUID taskId) {
        Task task = taskService.getTask(taskId);
        return ResponseEntity.ok(TaskResponseDto.from(task));
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponseDto>> getTasks(@RequestParam(required = false) boolean anonymous, Pageable pageable) {
        Page<Task> tasks = anonymous
                ? taskService.getAnonymousTasks(pageable)
                : taskService.getTasksForAuthenticatedUser(pageable);

        return new ResponseEntity<> (tasks.map (TaskResponseDto::from), HttpStatus.OK);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}
