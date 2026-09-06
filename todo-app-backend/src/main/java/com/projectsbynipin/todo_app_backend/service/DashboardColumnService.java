package com.projectsbynipin.todo_app_backend.service;

import com.projectsbynipin.todo_app_backend.dto.AddDashboardRequestDto;
import com.projectsbynipin.todo_app_backend.entity.Dashboard;

public interface DashboardColumnService {
    void createDefaultColumnsForUser(Dashboard dashboard1);

    void createColumnsForDashboard(Dashboard dashboard1, AddDashboardRequestDto addDashboardRequestDto);
}
