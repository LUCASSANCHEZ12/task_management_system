package com.task.manager.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "epic")
@SQLDelete(sql = "UPDATE epic SET deleted = true, deleted_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted = false")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Epic {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, length = 256)
    private String epic_title;

    @Column(nullable = false, length = 512)
    private String epic_description;

    @Column(nullable = true)
    private int epic_story_points;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column
    private boolean deleted;

    @Column(columnDefinition = "uuid")
    private UUID deletedBy;

    @OneToMany(mappedBy = "epic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String epic_title;
        private String epic_description;
        private int epic_story_points;
        private boolean completed;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime finishedAt;
        private LocalDateTime deletedAt;
        private boolean deleted;
        private UUID deletedBy;
        private List<Task> tasks = new ArrayList<>();
        private Project project;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder epic_title(String epic_title) { this.epic_title = epic_title; return this; }
        public Builder epic_description(String epic_description) { this.epic_description = epic_description; return this; }
        public Builder epic_story_points(int epic_story_points) { this.epic_story_points = epic_story_points; return this; }
        public Builder completed(boolean completed) { this.completed = completed; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder finishedAt(LocalDateTime finishedAt) { this.finishedAt = finishedAt; return this; }
        public Builder deletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; return this; }
        public Builder deleted(boolean deleted) { this.deleted = deleted; return this; }
        public Builder deletedBy(UUID deletedBy) { this.deletedBy = deletedBy; return this; }
        public Builder tasks(List<Task> tasks) { this.tasks = tasks; return this; }
        public Builder project(Project project) { this.project = project; return this; }

        public Epic build() {
            Epic epic = new Epic();
            epic.id = this.id;
            epic.epic_title = this.epic_title;
            epic.epic_description = this.epic_description;
            epic.epic_story_points = this.epic_story_points;
            epic.completed = this.completed;
            epic.createdAt = this.createdAt;
            epic.updatedAt = this.updatedAt;
            epic.finishedAt = this.finishedAt;
            epic.deletedAt = this.deletedAt;
            epic.deleted = this.deleted;
            epic.deletedBy = this.deletedBy;
            epic.tasks = this.tasks;
            epic.project = this.project;
            return epic;
        }
    }

}
