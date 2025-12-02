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
@Builder
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

}
