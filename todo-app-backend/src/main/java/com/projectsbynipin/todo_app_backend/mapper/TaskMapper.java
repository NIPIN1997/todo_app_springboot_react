package com.projectsbynipin.todo_app_backend.mapper;

import com.projectsbynipin.todo_app_backend.dto.AddTaskRequestDto;
import com.projectsbynipin.todo_app_backend.dto.EditTaskViewDto;
import com.projectsbynipin.todo_app_backend.dto.ViewTaskResponseDto;
import com.projectsbynipin.todo_app_backend.entity.Dashboard;
import com.projectsbynipin.todo_app_backend.entity.DashboardColumn;
import com.projectsbynipin.todo_app_backend.entity.Task;
import com.projectsbynipin.todo_app_backend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task addTaskRequestDtoToTask(AddTaskRequestDto addTaskRequestDto, Dashboard dashboard, User user, DashboardColumn dashboardColumn, User assignedUser) {
        Task task = new Task();
        task.setTitle(addTaskRequestDto.title());
        task.setDescription(addTaskRequestDto.description());
        task.setDueDate(addTaskRequestDto.dueDate());
        task.setColumn(dashboardColumn);
        task.setDashboard(dashboard);
        task.setCreatedBy(user);
        task.setAssignedTo(dashboard.isPrivate() ? user : assignedUser);
        return task;
    }

    public ViewTaskResponseDto taskToViewTaskResponseDto(Task task, Dashboard dashboard) {
        long progress = 0;
        long maxPosition = dashboard.getColumns().stream().mapToLong(DashboardColumn::getPosition).max().orElse(0);
        if (maxPosition != 0) {
            progress = (long) Math.ceil(((double) (task.getColumn().getPosition() - 1) / (maxPosition - 1)) * 100);
        }
        return new ViewTaskResponseDto(
                task.getId(),
                task.getTitle(),
                task.getDueDate(),
                task.getAssignedTo().getName(),
                task.getColumn().getName(),
                task.getDescription(),
                progress,
                task.getDashboard().isPrivate()
        );
    }

    public EditTaskViewDto taskToEditTaskViewDto(Task task) {
        return new EditTaskViewDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getDashboard().isPrivate(),
                new EditTaskViewDto.EditTaskViewColumn(
                        task.getColumn().getId(),
                        task.getColumn().getName()
                ),
                new EditTaskViewDto.EditTaskViewUser(
                        task.getAssignedTo().getId(),
                        task.getAssignedTo().getName()
                )
        );
    }
}
