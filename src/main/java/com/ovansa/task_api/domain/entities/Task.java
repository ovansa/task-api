package com.ovansa.task_api.domain.entities;

import com.ovansa.task_api.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Column(nullable = true)
    private Instant completedAt;

    public static Task create(String title, User owner, TaskStatus taskStatus) {
        Task task = new Task ();
        task.title = title;
        task.owner = owner;
        task.status = taskStatus;

        return task;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void changeStatus(TaskStatus newStatus) {
        this.status = newStatus;

        if (newStatus == TaskStatus.COMPLETED) {
            this.completedAt = Instant.now();
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.status = TaskStatus.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}