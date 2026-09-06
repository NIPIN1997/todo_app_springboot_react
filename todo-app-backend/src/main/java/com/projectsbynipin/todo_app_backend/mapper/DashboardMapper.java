package com.projectsbynipin.todo_app_backend.mapper;


import com.projectsbynipin.todo_app_backend.dto.*;
import com.projectsbynipin.todo_app_backend.entity.Dashboard;
import com.projectsbynipin.todo_app_backend.entity.DashboardColumn;
import com.projectsbynipin.todo_app_backend.entity.Task;
import com.projectsbynipin.todo_app_backend.entity.User;
import com.projectsbynipin.todo_app_backend.repository.TaskRepository;
import com.projectsbynipin.todo_app_backend.utility.HelperMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class DashboardMapper {

    private final TaskRepository taskRepository;
    private final HelperMethods helper;

    public List<DashboardResponseDto> dashboardToDashboardResponseDto(List<Dashboard> dashboards) {
        return dashboards.stream().map(e -> new DashboardResponseDto(e.getId(), e.getName(), e.getMaster().getId(), e.getMaster().getName(), e.isPrivate())).toList();
    }

    public List<DetailedDashboardResponseDto> dashboardToDetailedDashboardResponseDto(List<Dashboard> dashboardList, User user) {
        List<DetailedDashboardResponseDto> detailedDashboardResponseDtos = new ArrayList<>();
        List<Task> tasks = taskRepository.findTasksInDashboards(dashboardList);
        for (Dashboard dashboard : dashboardList) {
            List<Task> dashboardTasks = tasks.stream().filter(e -> e.getDashboard().equals(dashboard)).toList();
            Map<String, Long> statusMap = new LinkedHashMap<>();
            List<DashboardColumn> dashboardColumns = dashboard.getColumns().stream().sorted(Comparator.comparingLong(DashboardColumn::getPosition)).toList();
            for (DashboardColumn dashboardColumn : dashboardColumns) {
                statusMap.put(dashboardColumn.getName(), dashboardTasks.stream().filter(e -> e.getColumn().equals(dashboardColumn)).count());
            }
            detailedDashboardResponseDtos.add(
                    new DetailedDashboardResponseDto(
                            dashboard.getId(),
                            dashboard.getName(),
                            dashboard.getMaster().equals(user),
                            dashboard.isPrivate(),
                            dashboard.getUsers().size(),
                            dashboard.getMaster().getName(),
                            dashboard.isArchived(),
                            statusMap
                    )
            );
        }
        return detailedDashboardResponseDtos;
    }


    public DashboardWithColumnsAndTasksDto dashboardToDashboardWithColumnsAndTasksDto(Dashboard dashboard) {
        List<DashboardWithColumnsAndTasksDto.ColumnWithTasksDto> columnWithTasksDtos = new ArrayList<>();
        long maxPosition = dashboard.getColumns().stream().mapToLong(DashboardColumn::getPosition).max().orElse(0);
        for (DashboardColumn dashboardColumn : dashboard.getColumns()) {
            List<DashboardWithColumnsAndTasksDto.ColumnWithTasksDto.TasksDto> tasksDtos = dashboardColumn.getTasks().stream().filter(e -> !e.isDeleted()).map(
                    e -> new DashboardWithColumnsAndTasksDto.ColumnWithTasksDto.TasksDto(
                            e.getId(),
                            e.getTitle(),
                            e.getDueDate(),
                            e.getAssignedTo().getName(),
                            helper.getTaskStatus(maxPosition, e)
                    )
            ).sorted(Comparator.comparing(DashboardWithColumnsAndTasksDto.ColumnWithTasksDto.TasksDto::dueDate)).toList();
            DashboardWithColumnsAndTasksDto.ColumnWithTasksDto columnWithTasksDto = new DashboardWithColumnsAndTasksDto.ColumnWithTasksDto(
                    dashboardColumn.getId(),
                    dashboardColumn.getName(),
                    dashboardColumn.getPosition(),
                    tasksDtos
            );
            columnWithTasksDtos.add(columnWithTasksDto);
            columnWithTasksDtos.sort(Comparator.comparingLong(DashboardWithColumnsAndTasksDto.ColumnWithTasksDto::position));
        }
        return new DashboardWithColumnsAndTasksDto(
                dashboard.getId(),
                dashboard.getName(),
                columnWithTasksDtos,
                dashboard.isPrivate()
        );
    }

    public List<MemberNameIdResponseDto> dashboardToMemberNameIdResponseDto(Dashboard dashboard) {
        return dashboard.getUsers().stream().map(e -> new MemberNameIdResponseDto(e.getId(), e.getName())).toList();
    }

    public EditDashboardViewDto dashboardToEditDashboardViewDto(Dashboard dashboard) {
        Set<User> users = dashboard.getUsers();
        List<DashboardColumn> dashboardColumns = dashboard.getColumns();
        List<EditDashboardViewDto.EditDashboardMember> members = users.stream().filter(e -> !e.getId().equals(dashboard.getMaster().getId())).map(e -> new EditDashboardViewDto.EditDashboardMember(e.getId(), e.getName())).toList();
        List<EditDashboardViewDto.EditDashboardColumn> columns = dashboardColumns.stream().sorted(Comparator.comparingLong(DashboardColumn::getPosition)).map(e -> new EditDashboardViewDto.EditDashboardColumn(e.getId(), e.getName(), e.getPosition())).toList();
        return new EditDashboardViewDto(
                dashboard.getId(),
                dashboard.getName(),
                dashboard.isPrivate(),
                members,
                columns
        );
    }

}
