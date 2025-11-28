package com.task.manager.demo.controller;

import com.task.manager.demo.dto.task.TaskDTO;
import com.task.manager.demo.dto.task.TaskRequest;
import com.task.manager.demo.dto.task.TaskUpdateDTO;
import com.task.manager.demo.service.task.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/task")
@Tag(name = "Tareas", description = "Endpoints para gestión de tareas")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping("/create")
    @Operation(summary = "Crear una nueva tarea")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<TaskDTO> create(@Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @Operation(summary = "Obtener una tarea por ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<TaskDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Obtener todas las tareas de un usuario")
    @GetMapping("/user/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<TaskDTO>> getAllByUser(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getAllUserToDo(id));
    }

    @PostMapping("/complete/{id}")
    @Operation(summary = "Completar una tarea")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<TaskDTO> complete(@PathVariable UUID id) {
        return ResponseEntity.ok(service.complete(id));
    }

    @GetMapping("/")
    @Operation(summary = "Obtener todas las tareas")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<TaskDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/search/{title}")
    @Operation(summary = "Obtener todas las tareas")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<TaskDTO>> searchByTitle(@PathVariable String title) {
        return ResponseEntity.ok(service.searchByTaskByTitle(title));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una tarea")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar una tarea")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<TaskDTO> update(@PathVariable UUID id, @Valid @RequestBody TaskUpdateDTO request) {
        return ResponseEntity.ok(service.update(id, request));
    }
}
