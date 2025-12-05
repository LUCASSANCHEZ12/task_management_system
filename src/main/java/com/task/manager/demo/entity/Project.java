package com.task.manager.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

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
    private String projectTitle;

    @Column(nullable = false, length = 512)
    private String projectDescription;

    public static class Builder extends BaseEntity.Builder<Builder> {
        private String projectTitle;
        private String projectDescription;

        public Builder projectTitle(String project_title) { this.projectTitle = project_title; return this; }
        public Builder projectDescription(String projectDescription) { this.projectDescription = projectDescription; return this; }

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
            p.projectTitle = this.projectTitle;
            p.projectDescription = this.projectDescription;
            return p;
        }
    }

    public static Builder builder() { return new Builder(); }
}
