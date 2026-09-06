package com.projectsbynipin.todo_app_backend.service.impl;

import com.projectsbynipin.todo_app_backend.dto.*;
import com.projectsbynipin.todo_app_backend.entity.Dashboard;
import com.projectsbynipin.todo_app_backend.entity.DashboardColumn;
import com.projectsbynipin.todo_app_backend.entity.Task;
import com.projectsbynipin.todo_app_backend.entity.User;
import com.projectsbynipin.todo_app_backend.exception.*;
import com.projectsbynipin.todo_app_backend.mapper.TaskMapper;
import com.projectsbynipin.todo_app_backend.repository.DashboardColumnRepository;
import com.projectsbynipin.todo_app_backend.repository.DashboardRepository;
import com.projectsbynipin.todo_app_backend.repository.TaskRepository;
import com.projectsbynipin.todo_app_backend.repository.UserRepository;
import com.projectsbynipin.todo_app_backend.service.TaskService;
import com.projectsbynipin.todo_app_backend.service.jwt.UserInfoDetails;
import com.projectsbynipin.todo_app_backend.utility.ApiResponseCreator;
import com.projectsbynipin.todo_app_backend.utility.Constants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final DashboardRepository dashboardRepository;
    private final DashboardColumnRepository dashboardColumnRepository;
    private final TaskMapper taskMapper;

    private User findUserByEmailAndDeleted(String email) {
        User user = userRepository.findByEmailAndDeleted(email, false);
        if (user == null) {
            throw new UserNotFoundException(Constants.User.USER_NOT_FOUND);
        }
        return user;
    }

    @Override
    @Transactional
    public ApiResponse<Void> addTask(AddTaskRequestDto addTaskRequestDto, UserInfoDetails userInfoDetails) {
        try {
            Dashboard dashboard = dashboardRepository.findDashboardWithUsers(addTaskRequestDto.dashboard());
            User user = findUserByEmailAndDeleted(userInfoDetails.getUsername());
            if (dashboard == null) {
                throw new DashboardNotFoundException(Constants.Dashboard.DASHBOARD_NOT_FOUND);
            }
            Set<UUID> memberIds = dashboard.getUsers().stream().map(User::getId).collect(Collectors.toSet());
            if (!memberIds.contains(user.getId())) {
                throw new FailedToAddTaskException(Constants.Task.NOT_AUTHORIZED_TO_CREATE_TASK);
            }
            DashboardColumn dashboardColumn = dashboardColumnRepository.findColumnById(addTaskRequestDto.column());
            User assignedUser = userRepository.findUserById(addTaskRequestDto.assignedTo());
            Task task = taskMapper.addTaskRequestDtoToTask(addTaskRequestDto, dashboard, user, dashboardColumn, assignedUser);
            taskRepository.save(task);
            return ApiResponseCreator.success(Constants.Task.TASK_CREATE, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new FailedToAddTaskException(Constants.Task.FAILED_TO_CREATE_TASK, e);
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> dragTask(DragTaskRequestDto dragTaskRequestDto, UserInfoDetails userInfoDetails) {
        try {
            Task task = taskRepository.findById(dragTaskRequestDto.taskId()).orElseThrow(() -> new TaskNotFoundException(Constants.Task.TASK_NOT_FOUND));
            User user = findUserByEmailAndDeleted(userInfoDetails.getUsername());
            DashboardColumn dashboardColumn = dashboardColumnRepository.findById(dragTaskRequestDto.columnId()).orElseThrow(() -> new DashboardColumnNotFoundException(Constants.DashboardColumn.DASHBOARD_COLUMN_NOT_FOUND));
            Dashboard dashboard = task.getDashboard();
            Set<UUID> memberIds = dashboard.getUsers().stream().map(User::getId).collect(Collectors.toSet());
            if (!memberIds.contains(user.getId())) {
                throw new FailedToUpdateTaskStatusException(Constants.Task.NOT_AUTHORIZED_TO_UPDATE_TASK);
            }
            task.setColumn(dashboardColumn);
            long maxPosition = dashboard.getColumns().stream().mapToLong(DashboardColumn::getPosition).max().orElse(0);
            if (dashboardColumn.getPosition() == maxPosition) {
                task.setCompletedDate(LocalDate.now());
            }
            taskRepository.save(task);
            return ApiResponseCreator.success(Constants.Task.TASK_STATUS_UPDATED, HttpStatus.OK);
        } catch (Exception e) {
            throw new FailedToUpdateTaskStatusException(Constants.Task.FAILED_TO_UPDATE_TASK_STATUS, e);
        }
    }

    @Override
    public ApiResponse<ViewTaskResponseDto> getTaskById(UUID id) {
        try {
            Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(Constants.Task.TASK_NOT_FOUND));
            Dashboard dashboard = task.getDashboard();
            return ApiResponseCreator.success(Constants.Task.TASK_FETCHED_SUCCESSFULLY, taskMapper.taskToViewTaskResponseDto(task, dashboard), HttpStatus.OK);
        } catch (Exception e) {
            throw new FailedToFetchTaskException(Constants.Task.FAILED_TO_FETCH_TASK, e);
        }
    }

    @Override
    public ApiResponse<Void> deleteTaskById(UUID id) {
        try {
            Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(Constants.Task.TASK_NOT_FOUND));
            task.setDeleted(true);
            taskRepository.save(task);
            return ApiResponseCreator.success(Constants.Task.TASK_DELETED, HttpStatus.OK);
        } catch (Exception e) {
            throw new FailedToDeleteTaskException(Constants.Task.FAILED_TO_DELETE_TASK, e);
        }
    }

    @Override
    public ApiResponse<Void> editTaskById(EditTaskRequestDto editTaskRequestDto) {
        try {
            Task task = taskRepository.findById(editTaskRequestDto.id()).orElseThrow(() -> new TaskNotFoundException(Constants.Task.TASK_NOT_FOUND));
            if (editTaskRequestDto.title() != null && !editTaskRequestDto.title().equals(task.getTitle())) {
                task.setTitle(editTaskRequestDto.title());
            }
            if (editTaskRequestDto.description() != null && !editTaskRequestDto.description().equals(task.getDescription())) {
                task.setDescription(editTaskRequestDto.description());
            }
            if (editTaskRequestDto.dueDate() != null && !editTaskRequestDto.dueDate().equals(task.getDueDate())) {
                task.setDueDate(editTaskRequestDto.dueDate());
            }
            if (editTaskRequestDto.column() != null && !editTaskRequestDto.column().equals(task.getColumn().getId())) {
                DashboardColumn dashboardColumn = dashboardColumnRepository.findColumnById(editTaskRequestDto.column());
                task.setColumn(dashboardColumn);
                Dashboard dashboard = task.getDashboard();
                long maxPosition = dashboard.getColumns().stream().mapToLong(DashboardColumn::getPosition).max().orElse(0);
                if (dashboardColumn.getPosition() == maxPosition) {
                    task.setCompletedDate(LocalDate.now());
                }
            }
            if (editTaskRequestDto.assignedTo() != null && !editTaskRequestDto.assignedTo().equals(task.getAssignedTo().getId())) {
                User assignedUser = userRepository.findUserById(editTaskRequestDto.assignedTo());
                task.setAssignedTo(assignedUser);
            }
            taskRepository.save(task);
            return ApiResponseCreator.success(Constants.Task.TASK_EDITED, HttpStatus.OK);
        } catch (Exception e) {
            throw new FailedToEditTaskException(Constants.Task.FAILED_TO_EDIT_TASK, e);
        }
    }

    @Override
    public ApiResponse<EditTaskViewDto> getTaskDetailsEdit(UUID id) {
        try {
            Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(Constants.Task.TASK_NOT_FOUND));
            return ApiResponseCreator.success(Constants.Task.TASK_FETCHED_SUCCESSFULLY, taskMapper.taskToEditTaskViewDto(task), HttpStatus.OK);
        } catch (Exception e) {
            throw new FailedToFetchTaskException(Constants.Task.FAILED_TO_FETCH_TASK, e);
        }
    }

}
