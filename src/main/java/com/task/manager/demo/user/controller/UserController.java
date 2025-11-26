package com.task.manager.demo.user.controller;

import com.task.manager.demo.user.dto.UserDto;
import com.task.manager.demo.user.dto.UserRequest;
import com.task.manager.demo.user.dto.UserUpdateDTO;
import com.task.manager.demo.user.services.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserServiceImpl service;

    public UserController(UserServiceImpl service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserRequest req) throws Exception {
        return ResponseEntity.ok(service.register(req));
    }

    @Operation(summary = "Get a User using the id")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Get all users")
    @GetMapping("/")
    public ResponseEntity<List<UserDto>> getAll() throws Exception {
        return ResponseEntity.ok(service.getAll());
    }


    @Operation(summary = "Partial update of a Workspace")
    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> update(
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = "{\"name\": \"string\", \"name\": \"string\",\"password\": \"string\" }"
                                    )
                            }
                    )
            )
            @Valid @RequestBody UserUpdateDTO request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }
}
