package com.task.manager.demo.task.repository;


import com.task.manager.demo.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByUser_Id(Long id);
}
