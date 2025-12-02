package com.task.manager.demo.repository;

import com.task.manager.demo.entity.Epic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EpicRepository extends JpaRepository<Epic, UUID> {
    // JPQL
    @Query("SELECT e FROM Epic e WHERE LOWER(e.epic_title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Epic> searchByTitle(String title);

    List<Epic> findAllByProject_Id(UUID project_id);
}
