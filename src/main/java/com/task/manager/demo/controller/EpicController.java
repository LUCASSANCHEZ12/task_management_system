package com.task.manager.demo.controller;

import com.task.manager.demo.dto.ResponseMessage;
import com.task.manager.demo.dto.epic.EpicDTO;
import com.task.manager.demo.dto.epic.EpicRequest;
import com.task.manager.demo.dto.epic.EpicUpdateDTO;
import com.task.manager.demo.dto.task.TaskDTO;
import com.task.manager.demo.service.epic.EpicService;
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
@RequestMapping("/api/epic")
@Tag(name = "Epics", description = "Epic management endpoints")
@SecurityRequirement(name = "Authorization")
public class EpicController {

    private final EpicService service;

    public EpicController(EpicService service) {
        this.service = service;
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new epic")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Epic created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EpicDTO.class)
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
    public ResponseEntity<EpicDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EpicRequest.class)
                    )
            )
            @Valid @RequestBody EpicRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @Operation(summary = "Get an epic by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Epic with identifier 3fa85f64-5717-4562-b3fc-2c963f66afa6",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EpicDTO.class)
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
    @Parameters({
            @Parameter(
                    name = "id",
                    description = "Unique epic identifier to search for",
                    required = true,
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
            )
    })
    public ResponseEntity<EpicDTO> getById(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.findById(id));
    }

    @Operation(summary = "Get all tasks in an epic")
    @GetMapping("/{id}/tasks")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of tasks associated with the epic",
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
                    description = "Access denied"
            )
    })
    @Parameters({
            @Parameter(
                    name = "id",
                    description = "Unique epic identifier",
                    required = true,
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
            )
    })
    public ResponseEntity<List<TaskDTO>> getAllTasksInEpic(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.getAllTasksInEpic(id));
    }

    @PostMapping("/complete/{id}")
    @Operation(summary = "Complete an epic")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Epic completed",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "epicCompleted",
                                            summary = "Example of an epic marked as completed",
                                            value = """
                    {
                        "id": "2f8d2f18-7c74-4d28-9a5a-0654a023b17c",
                        "title": "Implementation and design of website",
                        "description": "Design and create a landing page for the company",
                        "story_points": 5,
                        "completed": true,
                        "createdAt": "2025-01-10T10:00:00",
                        "updatedAt": "2025-01-12T14:30:00",
                        "finishedAt": "2025-01-12T14:30:00",
                        "finishedAt": null,
                        "deletedBy": null,
                        "project_id": "0f7a2a9e-4d74-4b31-bdaf-698fc2619c86",
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
                    description = "Access denied"
            )
    })
    @Parameters({
            @Parameter(
                    name = "id",
                    description = "Unique epic identifier to complete",
                    required = true,
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
            )
    })
    public ResponseEntity<EpicDTO> complete(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.complete(id));
    }

    @GetMapping("/")
    @Operation(summary = "Get all epics")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of all epics",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = EpicDTO.class))
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
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<EpicDTO>> getAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.getAll());
    }

    @GetMapping
    @Operation(
            summary = "Search epics by title",
            description = "Returns a list of epics that partially or fully match the provided title value."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of epics found",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = EpicDTO.class))
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
    @Parameters({
            @Parameter(
                    name = "title",
                    description = "Partial or complete text of the epic title to search",
                    required = true,
                    example = "bug"
            )
    })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<EpicDTO>> searchByTitle(@RequestParam String title) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.searchEpicByTitle(title));
    }

    @DeleteMapping("/{id}/user/{requester}")
    @Operation(summary = "Delete an epic")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Parameters({
            @Parameter(
                    name = "id",
                    description = "Unique epic identifier to delete",
                    required = true,
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
            )
    })
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Epic successfully deleted",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class),
                            examples = {
                                    @ExampleObject(
                                            name = "EpicDeleted",
                                            summary = "Example of successful response",
                                            value = """
                            {
                                "message": "Epic successfully deleted"
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
                    description = "Access denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Epic not found"
            )
    })
    public ResponseEntity<ResponseMessage> delete(@PathVariable UUID id, @PathVariable UUID requester) {
        service.deleteById(id, requester);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseMessage("Epic successfully deleted"));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update an epic")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Epic updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EpicDTO.class)
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
                    description = "Access denied",
                    content = @Content(

                    )
            )
    })
    public ResponseEntity<EpicDTO> update(
            @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EpicUpdateDTO.class)
                    )
            )
            @Valid @RequestBody EpicUpdateDTO request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.update(id, request));
    }
}
