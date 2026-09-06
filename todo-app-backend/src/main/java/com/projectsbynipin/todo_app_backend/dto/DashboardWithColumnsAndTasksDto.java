package com.projectsbynipin.todo_app_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.projectsbynipin.todo_app_backend.enums.TaskStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record DashboardWithColumnsAndTasksDto(UUID id, String name, List<ColumnWithTasksDto> columnWithTasksDtos,
                                              boolean isPrivate) {
    public record ColumnWithTasksDto(UUID id, String name, long position, List<TasksDto> tasksDtos) {
        public record TasksDto(
                UUID id,
                String title,
                @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
                LocalDate dueDate,
                String assignedTo,
                TaskStatus taskStatus
        ) {
        }
    }
}
