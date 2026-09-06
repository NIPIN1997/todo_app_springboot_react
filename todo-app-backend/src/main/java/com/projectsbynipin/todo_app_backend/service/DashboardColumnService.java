package com.projectsbynipin.todo_app_backend.service;

import com.projectsbynipin.todo_app_backend.dto.AddDashboardRequestDto;
import com.projectsbynipin.todo_app_backend.dto.ApiResponse;
import com.projectsbynipin.todo_app_backend.dto.EditDashboardColumnNameRequestDto;
import com.projectsbynipin.todo_app_backend.entity.Dashboard;
import com.projectsbynipin.todo_app_backend.service.jwt.UserInfoDetails;
import jakarta.validation.Valid;

public interface DashboardColumnService {
    void createDefaultColumnsForUser(Dashboard dashboard1);

    void createColumnsForDashboard(Dashboard dashboard1, AddDashboardRequestDto addDashboardRequestDto);

    ApiResponse<Void> editDashboardColumnName(@Valid EditDashboardColumnNameRequestDto editDashboardColumnNameRequestDto, UserInfoDetails userInfoDetails);
}
