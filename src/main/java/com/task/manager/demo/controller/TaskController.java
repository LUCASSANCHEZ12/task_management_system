package com.task.manager.demo.controller;

import com.task.manager.demo.dto.ResponseMessage;
import com.task.manager.demo.dto.task.TaskDTO;
import com.task.manager.demo.dto.task.TaskRequest;
import com.task.manager.demo.dto.task.TaskUpdateDTO;
import com.task.manager.demo.service.task.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/task")
@Tag(name = "Tasks", description = "Task management endpoints")
@SecurityRequirement(name = "Authorization")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping("/create")
    @Operation(summary = "Create new task")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Task created",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TaskDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden"
        )
    })
    public ResponseEntity<TaskDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskRequest.class)
                    )
            )
            @Valid @RequestBody TaskRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @Operation(summary = "Get task by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Task with identifier 3fa85f64-5717-4562-b3fc-2c963f66afa6",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TaskDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden"
        )
    })
    @Parameters({
        @Parameter(
            name = "id",
            description = "Unique identifier of the task to retrieve",
            required = true,
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
    })
    public ResponseEntity<TaskDTO> getById(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.findById(id));
    }

    @Operation(summary = "Get all tasks for a user")
    @GetMapping("/user/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "List of tasks assigned to the user",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = TaskDTO.class))
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden"
        )
    })
    @Parameters({
        @Parameter(
            name = "id",
            description = "Unique identifier of the user assigned to the tasks",
            required = true,
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
    })
    public ResponseEntity<List<TaskDTO>> getAllByUser(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.getAllUserTasks(id));
    }

    @PostMapping("/complete/{id}")
    @Operation(summary = "Complete task")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Task completed",
            content = @Content(
                mediaType = "application/json",
                examples = {
                @ExampleObject(
                    name = "TareaCompletada",
                    summary = "Ejemplo de una tarea marcada como completada",
                    value = """
                    {
                        "id": "2f8d2f18-7c74-4d28-9a5a-0654a023b17c",
                        "title": "Implementar login",
                        "description": "Finalizar el módulo de autenticación",
                        "story_points": 5,
                        "completed": true,
                        "createdAt": "2025-01-10T10:00:00",
                        "updatedAt": "2025-01-12T14:30:00",
                        "finishedAt": "2025-01-12T14:30:00",
                        "type": "FEATURE",
                        "epicId": "0f7a2a9e-4d74-4b31-bdaf-698fc2619c86",
                        "assigneeId": "d1b3fb41-a6af-4f82-8659-8be2c05c94b3"
                    }
                    """ )
                }
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden"
        )
    })
    @Parameters({
        @Parameter(
            name = "id",
            description = "Unique identifier of the task to complete",
            required = true,
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
    })
    public ResponseEntity<TaskDTO> complete(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.complete(id));
    }

    @GetMapping("/")
    @Operation(summary = "Get all tasks")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "List of all tasks",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = TaskDTO.class))
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden"
        )
    })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<TaskDTO>> getAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.getAll());
    }

    @GetMapping
    @Operation(
        summary = "Search tasks by title",
        description = "Returns a list of tasks that match partially or fully with the provided title value"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Task list found",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = TaskDTO.class))
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden"
        )
    })
    @Parameters({
        @Parameter(
            name = "title",
            description = "Partial or full text of the task title to search",
            required = true,
            example = "bug"
        )
    })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<TaskDTO>> searchByTitle(@RequestParam String title) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.searchByTaskByTitle(title));
    }

    @DeleteMapping("/{id}/user/{userId}")
    @Operation(summary = "Delete task")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Parameters({
        @Parameter(
            name = "id",
            description = "Unique identifier of the task to delete",
            required = true,
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
    })
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Task deleted successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ResponseMessage.class),
                examples = {
                    @ExampleObject(
                        name = "TaskDeleted",
                        summary = "Example of successful response",
                        value = """
                            {
                                "message": "Task deleted successfully"
                            }
                            """
                        )
                }
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Task not found"
        )
    })
    public ResponseEntity<ResponseMessage> delete(@PathVariable UUID id, @PathVariable UUID userId) {
        service.deleteById(id, userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseMessage("Task deleted successfully"));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update task")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Task updated",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TaskDTO.class)
                )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(

            )
        )
    })
    public ResponseEntity<TaskDTO> update(
        @PathVariable UUID id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TaskUpdateDTO.class)
            )
        )
        @Valid @RequestBody TaskUpdateDTO request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.update(id, request));
    }

    @PostMapping("/{id}/epic/{epic_id}")
    @Operation(summary = "Crear una nueva tarea")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Tarea asignada correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Petición inválida"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autorizado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado"
            )
    })
    @Parameters({
            @Parameter(
                    name = "id",
                    description = "Identificador unico de la tarea que se busca asignar",
                    required = true,
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
            ),
            @Parameter(
                    name = "epic_id",
                    description = "Identificador unico de la epica que se busca aasignar",
                    required = true,
                    example = "3fa85f64-5717-4562-b3fc-2c963f44af89"
            )
    })
    public ResponseEntity<TaskDTO> assignToEpic(
            @PathVariable UUID id,
            @PathVariable UUID epic_id
            ) {

        return ResponseEntity.ok(service.assignToEpic(id, epic_id));
    }

    @PostMapping("/{id}/user/{user_id}")
    @Operation(summary = "Assign task to user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Tarea asignada correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Petición inválida"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autorizado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado"
            )
    })
    @Parameters({
            @Parameter(
                    name = "id",
                    description = "Identificador unico de la tarea que se busca asignar",
                    required = true,
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
            ),
            @Parameter(
                    name = "epic_id",
                    description = "Identificador unico del usuario que se busca asignar",
                    required = true,
                    example = "3fa85f64-5717-4562-b3fc-2c963f44af89"
            )
    })
    public ResponseEntity<TaskDTO> assignToUser(
            @PathVariable UUID id,
            @PathVariable UUID user_id
    ) {

        return ResponseEntity.ok(service.assignToUser(id, user_id));
    }
}
