package com.task.manager.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "project")
@SQLDelete(sql = "UPDATE project SET deleted = true, deleted_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted = false")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project extends BaseEntity{

    @Column(nullable = false, length = 256)
    private String project_title;

    @Column(nullable = false, length = 512)
    private String project_description;

    public static class Builder extends BaseEntity.Builder<Builder> {
        private String project_title;
        private String project_description;

        public Builder project_title(String project_title) { this.project_title = project_title; return this; }
        public Builder project_description(String project_description) { this.project_description = project_description; return this; }

        @Override
        protected Builder self() { return this; }

        public Project build() {
            Project p = new Project();
            p.id = this.id;
            p.createdAt = this.createdAt;
            p.updatedAt = this.updatedAt;
            p.deletedAt = this.deletedAt;
            p.deleted = this.deleted;
            p.deletedBy = this.deletedBy;
            p.project_title = this.project_title;
            p.project_description = this.project_description;
            return p;
        }
    }

    public static Builder builder() { return new Builder(); }
}
