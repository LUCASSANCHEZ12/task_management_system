package com.backend.project.controller;

import com.task.manager.demo.task.dto.TaskDTO;
import com.task.manager.demo.task.dto.TaskRequest;
import com.task.manager.demo.task.services.ImplTaskService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {

    private final ImplTaskService service;

    public TaskController(ImplTaskService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<TaskDTO> create(@RequestBody TaskRequest request){
        System.out.println(request.toString());
        return ResponseEntity.ok(service.create(request));
    }

    @Operation(summary = "Get a Task using the id")
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getById(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Get all todos assigned to a user")
    @GetMapping("/user/{id}")
    public ResponseEntity<List<TaskDTO>> getAll(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(service.getAllUserToDo(id));
    }

    @PostMapping("/complete/{id}")
    public ResponseEntity<TaskDTO> complete(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(service.complete(id));
    }
}
