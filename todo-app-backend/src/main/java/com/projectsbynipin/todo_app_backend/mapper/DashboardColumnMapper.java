package com.projectsbynipin.todo_app_backend.mapper;

import com.projectsbynipin.todo_app_backend.dto.ColumnNameIdResponseDto;
import com.projectsbynipin.todo_app_backend.entity.DashboardColumn;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DashboardColumnMapper {

    public List<ColumnNameIdResponseDto> dashboardColumnToColumnNameIdResponseDto(List<DashboardColumn> dashboardColumns) {
        return dashboardColumns.stream().map(e -> new ColumnNameIdResponseDto(e.getId(), e.getName())).toList();
    }
}
