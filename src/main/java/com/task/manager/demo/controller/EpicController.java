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
@Tag(name = "Epicas", description = "Endpoints para gestión de epicas")
public class EpicController {

    private final EpicService service;

    public EpicController(EpicService service) {
        this.service = service;
    }

    @PostMapping("/create")
    @Operation(summary = "Crear una nueva epica")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Epica creada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EpicDTO.class)
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

    @Operation(summary = "Obtener una epica por ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Epica con identificador 3fa85f64-5717-4562-b3fc-2c963f66afa6",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EpicDTO.class)
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
                    description = "Identificador unico de la epica que se busca eliminar",
                    required = true,
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
            )
    })
    public ResponseEntity<EpicDTO> getById(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.findById(id));
    }

    @Operation(summary = "Obtener todas las tareas dentro de una epica")
    @GetMapping("/{id}/tasks")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de tareas asociadas a la epica",
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
                    description = "Identificador unico de la epica",
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
    @Operation(summary = "Completar una epica")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "epica completada",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "epicaCompletada",
                                            summary = "Ejemplo de una epica marcada como completada",
                                            value = """
                    {
                        "id": "2f8d2f18-7c74-4d28-9a5a-0654a023b17c",
                        "title": "Implementacion y diseño de pagina web",
                        "description": "Diseñar y crear una landing page para la empresa",
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
                    description = "Identificador unico de  la epica que se busca eliminar",
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
    @Operation(summary = "Obtener todas las epicas")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de todas las epicas",
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
            summary = "Buscar epicas por título",
            description = "Devuelve una lista de epicas que coinciden parcial o totalmente con el valor del título proporcionado."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de epicas encontrada",
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
            )
    })
    @Parameters({
            @Parameter(
                    name = "title",
                    description = "Texto parcial o completo del título de la epica a buscar",
                    required = true,
                    example = "bug"
            )
    })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<EpicDTO>> searchByTitle(@RequestParam String title) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.searchByTaskByTitle(title));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una epica")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Parameters({
            @Parameter(
                    name = "id",
                    description = "Identificador unico de  la epica que se busca eliminar",
                    required = true,
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
            )
    })
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Epica eliminada correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class),
                            examples = {
                                    @ExampleObject(
                                            name = "EpicaEliminada",
                                            summary = "Ejemplo de respuesta exitosa",
                                            value = """
                            {
                                "message": "Epica eliminada correctamente"
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
                    description = "La epica no fue encontrada"
            )
    })
    public ResponseEntity<ResponseMessage> delete(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseMessage("Epica eliminada correctamente"));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar una epica")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Epica actualizada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EpicDTO.class)
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
