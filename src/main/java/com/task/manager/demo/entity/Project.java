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
import java.util.UUID;

@Entity
@Table(name = "project")
@SQLDelete(sql = "UPDATE project SET deleted = true, deleted_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted = false")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, length = 256)
    private String project_title;

    @Column(nullable = false, length = 512)
    private String project_description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column
    private boolean deleted;

    @Column(columnDefinition = "uuid")
    private UUID deletedBy;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String project_title;
        private String project_description;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime deletedAt;
        private boolean deleted;
        private UUID deletedBy;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder project_title(String project_title) { this.project_title = project_title; return this; }
        public Builder project_description(String project_description) { this.project_description = project_description; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder deletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; return this; }
        public Builder deleted(boolean deleted) { this.deleted = deleted; return this; }
        public Builder deletedBy(UUID deletedBy) { this.deletedBy = deletedBy; return this; }

        public Project build() {
            Project project = new Project();
            project.id = this.id;
            project.project_title = this.project_title;
            project.project_description = this.project_description;
            project.createdAt = this.createdAt;
            project.updatedAt = this.updatedAt;
            project.deletedAt = this.deletedAt;
            project.deleted = this.deleted;
            project.deletedBy = this.deletedBy;
            return project;
        }
    }

}
