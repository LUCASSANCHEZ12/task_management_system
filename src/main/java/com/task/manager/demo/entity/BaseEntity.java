package com.task.manager.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "uuid")
    protected UUID id;

    @Column(name = "created_at")
    protected LocalDateTime createdAt;

    @Column(name = "updated_at")
    protected LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    protected LocalDateTime deletedAt;

    @Column
    protected boolean deleted;

    @Column(columnDefinition = "uuid")
    protected UUID deletedBy;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public abstract static class Builder<T extends Builder<T>> {
        protected UUID id;
        protected LocalDateTime createdAt;
        protected LocalDateTime updatedAt;
        protected LocalDateTime deletedAt;
        protected boolean deleted;
        protected UUID deletedBy;

        public T id(UUID id) { this.id = id; return self(); }
        public T createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return self(); }
        public T updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return self(); }
        public T deletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; return self(); }
        public T deleted(boolean deleted) { this.deleted = deleted; return self(); }
        public T deletedBy(UUID deletedBy) { this.deletedBy = deletedBy; return self(); }

        protected abstract T self();
    }
}