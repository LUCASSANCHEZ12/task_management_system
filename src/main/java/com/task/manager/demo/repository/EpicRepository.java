package com.task.manager.demo.repository;

import com.task.manager.demo.entity.Epic;
import com.task.manager.demo.entity.Task;
import com.task.manager.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EpicRepository extends JpaRepository<Epic, UUID> {
    // SQL native query
    @Query(value = "SELECT e FROM epic e WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :title, '%'))", nativeQuery = true)
    List<Epic> searchByTitle(String title);

    List<Epic> finAllProject_Id(UUID project_id);
}
