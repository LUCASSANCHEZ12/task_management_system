package com.task.manager.demo.controller;

import com.task.manager.demo.dto.ResponseMessage;
import com.task.manager.demo.dto.project.ProjectDTO;
import com.task.manager.demo.dto.project.ProjectRequest;
import com.task.manager.demo.dto.project.ProjectUpdateDTO;
import com.task.manager.demo.dto.task.TaskDTO;
import com.task.manager.demo.dto.epic.EpicDTO;
import com.task.manager.demo.service.project.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/project")
@Tag(name = "Projects", description = "Project management endpoints")
@SecurityRequirement(name = "Authorization")
public class ProjectController {

    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new project")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Project created",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProjectDTO.class)
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
            description = "Access denied"
        )
    })
    public ResponseEntity<ProjectDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProjectRequest.class)
                    )
            )
            @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a project by ID")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Project found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProjectDTO.class)
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
            description = "Access denied"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Project not found"
        )
    })
    @Parameters({
        @Parameter(
            name = "id",
            description = "Unique project identifier",
            required = true,
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
    })
    public ResponseEntity<ProjectDTO> getById(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.findById(id));
    }

    @GetMapping("/")
    @Operation(summary = "Get all projects")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "List of projects",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = ProjectDTO.class))
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
    public ResponseEntity<List<ProjectDTO>> getAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.getAll());
    }

    @GetMapping
    @Operation(
        summary = "Search projects by title",
        description = "Returns a list of projects that partially or fully match the provided title value."
    )
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "List of projects found",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = ProjectDTO.class))
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
            description = "Partial or complete text of the project title to search",
            required = true,
            example = "Backend"
        )
    })
    public ResponseEntity<List<ProjectDTO>> searchByTitle(@RequestParam String title) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.searchProjectByTitle(title));
    }

    @GetMapping("/{id}/tasks")
    @Operation(summary = "Get all tasks in a project")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "List of project tasks",
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
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Proyecto no encontrado"
        )
    })
    @Parameters({
        @Parameter(
            name = "id",
            description = "Unique project identifier",
            required = true,
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
    })
    public ResponseEntity<List<TaskDTO>> getAllTasks(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.getAllTasksInProject(id));
    }

    @GetMapping("/{id}/epics")
    @Operation(summary = "Get all epics in a project")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "List of project epics",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = EpicDTO.class))
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
            description = "Proyecto no encontrado"
        )
    })
    @Parameters({
        @Parameter(
            name = "id",
            description = "Identificador único del proyecto",
            required = true,
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
    })
    public ResponseEntity<List<EpicDTO>> getAllEpics(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.getAllEpicsInProject(id));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update a project")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Project updated",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProjectDTO.class)
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
            description = "Proyecto no encontrado"
        )
    })
    @Parameters({
        @Parameter(
            name = "id",
            description = "Identificador único del proyecto",
            required = true,
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
    })
    public ResponseEntity<ProjectDTO> update(
        @PathVariable UUID id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProjectUpdateDTO.class)
            )
        )
        @Valid @RequestBody ProjectUpdateDTO request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.update(id, request));
    }

    @DeleteMapping("/{id}/user/{userId}")
    @Operation(summary = "Delete a project")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Parameters({
        @Parameter(
            name = "id",
            description = "Unique project identifier",
            required = true,
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
    })
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Project successfully deleted",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ResponseMessage.class)
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
            description = "Access denied"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Project not found"
        )
    })
    public ResponseEntity<ResponseMessage> delete(@PathVariable UUID id, @PathVariable UUID userId) {
        service.deleteById(id, userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseMessage("Project successfully deleted"));
    }
}
