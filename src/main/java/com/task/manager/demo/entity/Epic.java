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
@Builder
public class Epic {
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
}
