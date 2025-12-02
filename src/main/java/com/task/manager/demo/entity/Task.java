package com.task.manager.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "task")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, length = 256)
    private String title;

    @Column(nullable = false, length = 512)
    private String description;

    @Column(nullable = true)
    private int story_points;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "finished_At")
    private LocalDateTime finishedAt;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "epic_id")
    private Epic epic;

    @Column(name = "task_type")
    @Enumerated(EnumType.STRING)
    private Type_Enum type;

    @ManyToOne
    @JoinColumn(name = "task_parent_id")
    private Task task_parent = null;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String title;
        private String description;
        private int story_points;
        private boolean completed;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime finishedAt;
        private User user;
        private Epic epic;
        private Type_Enum type;
        private Task task_parent;
        private Project project;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder story_points(int story_points) { this.story_points = story_points; return this; }
        public Builder completed(boolean completed) { this.completed = completed; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder finishedAt(LocalDateTime finishedAt) { this.finishedAt = finishedAt; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder epic(Epic epic) { this.epic = epic; return this; }
        public Builder type(Type_Enum type) { this.type = type; return this; }
        public Builder task_parent(Task task_parent) { this.task_parent = task_parent; return this; }
        public Builder project(Project project) { this.project = project; return this; }

        public Task build() {
            Task task = new Task();
            task.id = this.id;
            task.title = this.title;
            task.description = this.description;
            task.story_points = this.story_points;
            task.completed = this.completed;
            task.createdAt = this.createdAt;
            task.updatedAt = this.updatedAt;
            task.finishedAt = this.finishedAt;
            task.user = this.user;
            task.epic = this.epic;
            task.type = this.type;
            task.task_parent = this.task_parent;
            task.project = this.project;
            return task;
        }
    }


}
