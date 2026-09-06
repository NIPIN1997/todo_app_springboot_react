package com.projectsbynipin.todo_app_backend.controller;

import com.projectsbynipin.todo_app_backend.dto.*;
import com.projectsbynipin.todo_app_backend.service.TaskService;
import com.projectsbynipin.todo_app_backend.service.jwt.UserInfoDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(
        name = "Task",
        description = "APIs related to task"
)
@RestController
@RequestMapping(path = "/api/v1/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @Operation(
            summary = "Add new task",
            description = "Add new task",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(path = "/add-task")
    public ResponseEntity<ApiResponse<Void>> addTask(@Valid @RequestBody AddTaskRequestDto addTaskRequestDto, @AuthenticationPrincipal UserInfoDetails userInfoDetails) {
        ApiResponse<Void> apiResponse = taskService.addTask(addTaskRequestDto, userInfoDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Drag task",
            description = "Change task status by dragging from one column to another",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping(path = "/drag-task")
    public ResponseEntity<ApiResponse<Void>> dragTask(@Valid @RequestBody DragTaskRequestDto dragTaskRequestDto, @AuthenticationPrincipal UserInfoDetails userInfoDetails) {
        ApiResponse<Void> apiResponse = taskService.dragTask(dragTaskRequestDto, userInfoDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Get task",
            description = "Get task by id",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(path = "/get-task/{id}")
    public ResponseEntity<ApiResponse<ViewTaskResponseDto>> getTaskById(@PathVariable UUID id) {
        ApiResponse<ViewTaskResponseDto> apiResponse = taskService.getTaskById(id);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete task",
            description = "Delete task by id",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping(path = "/delete-task/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTaskById(@PathVariable UUID id) {
        ApiResponse<Void> apiResponse = taskService.deleteTaskById(id);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Get task details",
            description = "Get task details for editing",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(path = "/get-task-details-edit/{id}")
    public ResponseEntity<ApiResponse<EditTaskViewDto>> getTaskDetailsEdit(@PathVariable UUID id) {
        ApiResponse<EditTaskViewDto> apiResponse = taskService.getTaskDetailsEdit(id);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Edit task",
            description = "Edit task by id",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping(path = "/edit-task")
    public ResponseEntity<ApiResponse<Void>> editTaskById(@Valid @RequestBody EditTaskRequestDto editTaskRequestDto) {
        ApiResponse<Void> apiResponse = taskService.editTaskById(editTaskRequestDto);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
