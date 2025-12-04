package com.task.manager.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "epic")
@SQLDelete(sql = "UPDATE epic SET deleted = true, deleted_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted = false")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Epic extends BaseEntity {
    @Column(nullable = false, length = 256)
    private String epicTitle;

    @Column(nullable = false, length = 512)
    private String epicDescription;

    @Column(nullable = true)
    private int epicStoryPoints;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @OneToMany(mappedBy = "epic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    public static class Builder extends BaseEntity.Builder<Builder> {
        private String epicTitle;
        private String epicDescription;
        private int epicStoryPoints;
        private boolean completed;
        private LocalDateTime finishedAt;
        private List<Task> tasks = new ArrayList<>();
        private Project project;

        public Builder epic_title(String epic_title) { this.epicTitle = epic_title; return this; }
        public Builder epic_description(String epic_description) { this.epicDescription = epic_description; return this; }
        public Builder epic_story_points(int epic_story_points) { this.epicStoryPoints = epic_story_points; return this; }
        public Builder completed(boolean completed) { this.completed = completed; return this; }
        public Builder finishedAt(LocalDateTime finishedAt) { this.finishedAt = finishedAt; return this; }
        public Builder tasks(List<Task> tasks) { this.tasks = tasks; return this; }
        public Builder project(Project project) { this.project = project; return this; }

        @Override
        protected Builder self() { return this; }

        public Epic build() {
            Epic epic = new Epic();
            epic.id = this.id;
            epic.createdAt = this.createdAt;
            epic.updatedAt = this.updatedAt;
            epic.deletedAt = this.deletedAt;
            epic.deleted = this.deleted;
            epic.deletedBy = this.deletedBy;
            epic.epicTitle = this.epicTitle;
            epic.epicDescription = this.epicDescription;
            epic.epicStoryPoints = this.epicStoryPoints;
            epic.completed = this.completed;
            epic.finishedAt = this.finishedAt;
            epic.tasks = this.tasks;
            epic.project = this.project;
            return epic;
        }
    }

    public static Builder builder() { return new Builder(); }


}
