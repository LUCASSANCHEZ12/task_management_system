package com.task.manager.demo.repository;

import com.task.manager.demo.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    // SQL native query
    @Query(value = "SELECT p FROM project p WHERE LOWER(p.project_title) LIKE LOWER(CONCAT('%', :title, '%'))", nativeQuery = true)
    List<Project> searchByTitle(String title);

    // not implemented yet
    //List<Epic> finByProjectId(UUID project_id);
}
