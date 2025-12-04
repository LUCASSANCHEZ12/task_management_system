package com.task.manager.demo.repository;

import com.task.manager.demo.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findAllByUser_Id(UUID task_id);

    // JPQL query
    @Query(value = "SELECT * FROM task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))", nativeQuery = true)
    List<Task> searchByTitle(String title);

    List<Task> findAllByEpic_Id(UUID epic_id);
    List<Task> findAllByProject_Id(UUID project_id);
    boolean existsByTitleAndProjectId(String title,  UUID project_id);
}


