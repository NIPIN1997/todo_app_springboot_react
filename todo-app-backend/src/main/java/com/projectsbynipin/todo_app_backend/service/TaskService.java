package com.projectsbynipin.todo_app_backend.service;

import com.projectsbynipin.todo_app_backend.dto.*;
import com.projectsbynipin.todo_app_backend.service.jwt.UserInfoDetails;
import jakarta.validation.Valid;

import java.util.UUID;

public interface TaskService {

    ApiResponse<Void> addTask(@Valid AddTaskRequestDto addTaskRequestDto, UserInfoDetails userInfoDetails);

    ApiResponse<Void> dragTask(@Valid DragTaskRequestDto dragTaskRequestDto, UserInfoDetails userInfoDetails);

    ApiResponse<ViewTaskResponseDto> getTaskById(UUID id);

    ApiResponse<Void> deleteTaskById(UUID id);

    ApiResponse<Void> editTaskById(@Valid EditTaskRequestDto editTaskRequestDto);

    ApiResponse<EditTaskViewDto> getTaskDetailsEdit(UUID id);
}
