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
@Tag(name = "Tareas", description = "Endpoints para gestión de tareas")
@SecurityRequirement(name = "Authorization")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping("/create")
    @Operation(summary = "Crear una nueva tarea")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Tarea creada",
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

    @Operation(summary = "Obtener una tarea por ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Tarea con identificador 3fa85f64-5717-4562-b3fc-2c963f66afa6",
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
            description = "Identificador unico de  la tarea que se busca eliminar",
            required = true,
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
    })
    public ResponseEntity<TaskDTO> getById(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.findById(id));
    }

    @Operation(summary = "Obtener todas las tareas de un usuario")
    @GetMapping("/user/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de tareas asignadas al usuario",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = TaskDTO.class))
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
            description = "Identificador unico del usuario asignado a la tarea",
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
    @Operation(summary = "Completar una tarea")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Tarea completada",
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
            description = "Identificador unico de  la tarea que se busca eliminar",
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
    @Operation(summary = "Obtener todas las tareas")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de todas las tareas",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = TaskDTO.class))
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
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<TaskDTO>> getAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.getAll());
    }

    @GetMapping
    @Operation(
        summary = "Buscar tareas por título",
        description = "Devuelve una lista de tareas que coinciden parcial o totalmente con el valor del título proporcionado."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de tareas encontrada",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = TaskDTO.class))
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
            name = "title",
            description = "Texto parcial o completo del título de la tarea a buscar",
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

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una tarea")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Parameters({
        @Parameter(
            name = "id",
            description = "Identificador unico de  la tarea que se busca eliminar",
            required = true,
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
    })
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Tarea eliminada correctamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ResponseMessage.class),
                examples = {
                    @ExampleObject(
                        name = "TareaEliminada",
                        summary = "Ejemplo de respuesta exitosa",
                        value = """
                            {
                                "message": "Tarea eliminada correctamente"
                            }
                            """
                        )
                }
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
        ),
        @ApiResponse(
            responseCode = "404",
            description = "La tarea no fue encontrada"
        )
    })
    public ResponseEntity<ResponseMessage> delete(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseMessage("Tarea eliminada correctamente"));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar una tarea")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Tarea actualizada",
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
            description = "Acceso denegado",
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
}
