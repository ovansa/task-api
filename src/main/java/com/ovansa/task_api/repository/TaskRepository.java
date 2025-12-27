package com.ovansa.task_api.repository;

import com.ovansa.task_api.domain.entities.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    Page<Task> findByOwnerId(UUID ownerId, Pageable pageable);
    Page<Task> findByOwnerIdIsNull(Pageable pageable);
}
