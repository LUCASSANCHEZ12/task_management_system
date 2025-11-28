package com.task.manager.demo.repository;

import com.task.manager.demo.entity.Epic;
import com.task.manager.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EpicRepository extends JpaRepository<Epic, UUID> {
    // JPQL query
    //@Query("SELECT e FROM epic e WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Optional<Epic> findByTitle(String title);


}
