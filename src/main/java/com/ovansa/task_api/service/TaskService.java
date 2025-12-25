package com.ovansa.task_api.service;

import com.ovansa.task_api.domain.Messages;
import com.ovansa.task_api.domain.entities.Task;
import com.ovansa.task_api.domain.entities.User;
import com.ovansa.task_api.enums.TaskStatus;
import com.ovansa.task_api.exception.BadRequestException;
import com.ovansa.task_api.exception.ResourceNotFoundException;
import com.ovansa.task_api.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    private final AuthenticationService authenticationService;
    private final TaskRepository taskRepository;

    public Task createTaskForUser(String title) throws BadRequestException {
        User user = authenticationService.getAuthenticatedUser ();

        // Task validation
        if (title == null || title.trim().isEmpty()) {
            throw new BadRequestException (Messages.TASK_TITLE_REQUIRED);
        }

        if (title.length() > 255) {
            throw new BadRequestException(Messages.TASK_TITLE_TOO_LONG);
        }

        Task task = Task.create (title, user, TaskStatus.PENDING);

        Task savedTask = taskRepository.save (task);

        log.info("Created task id={}, ownerId={}", savedTask.getId(), user.getId ());
        return savedTask;
    }

    public Task createAnonymousTask(String title) {
        Task task = Task.create(title, null, TaskStatus.PENDING);

        Task savedTask = taskRepository.save (task);
            log.info("Created task id={} for anonymous", savedTask.getId());
        return taskRepository.save(task);
    }

    public Task updateTask(UUID taskId, String title) {
        Task task = getTaskOrThrow (taskId);

        task.updateTitle (title);
        return task;
    }

    public Task changeStatus(UUID taskId, TaskStatus status) {
        Task task = getTaskOrThrow (taskId);
        task.changeStatus(status);
        return taskRepository.save(task);
    }

    public Task getTask(UUID taskId) {
        return getTaskOrThrow(taskId);
    }

    public Page<Task> getTasksForAuthenticatedUser(Pageable pageable) {
        UUID userId = authenticationService.getAuthenticatedUser ().getId ();
        return taskRepository.findByOwnerId(userId.toString(), pageable);
    }

    public Page<Task> getAnonymousTasks(Pageable pageable) {
        return taskRepository.findByOwnerIdIsNull(pageable);
    }

    public void deleteTask(UUID taskId) {
        Task task = getTaskOrThrow(taskId);
        taskRepository.delete(task);
    }

    private Task getTaskOrThrow(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Messages.TASK_NOT_FOUND));
    }
}
