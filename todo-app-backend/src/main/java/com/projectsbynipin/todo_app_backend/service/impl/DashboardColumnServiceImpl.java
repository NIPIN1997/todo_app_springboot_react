package com.projectsbynipin.todo_app_backend.service.impl;

import com.projectsbynipin.todo_app_backend.dto.AddDashboardRequestDto;
import com.projectsbynipin.todo_app_backend.dto.ApiResponse;
import com.projectsbynipin.todo_app_backend.dto.EditDashboardColumnNameRequestDto;
import com.projectsbynipin.todo_app_backend.entity.Dashboard;
import com.projectsbynipin.todo_app_backend.entity.DashboardColumn;
import com.projectsbynipin.todo_app_backend.entity.User;
import com.projectsbynipin.todo_app_backend.exception.*;
import com.projectsbynipin.todo_app_backend.repository.DashboardColumnRepository;
import com.projectsbynipin.todo_app_backend.repository.UserRepository;
import com.projectsbynipin.todo_app_backend.service.DashboardColumnService;
import com.projectsbynipin.todo_app_backend.service.jwt.UserInfoDetails;
import com.projectsbynipin.todo_app_backend.utility.ApiResponseCreator;
import com.projectsbynipin.todo_app_backend.utility.Constants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardColumnServiceImpl implements DashboardColumnService {

    private final DashboardColumnRepository dashboardColumnRepository;
    private final UserRepository userRepository;

    private User findUserByEmailAndDeleted(String email) {
        User user = userRepository.findByEmailAndDeleted(email, false);
        if (user == null) {
            throw new UserNotFoundException(Constants.User.USER_NOT_FOUND);
        }
        return user;
    }

    @Override
    @Transactional
    public void createDefaultColumnsForUser(Dashboard dashboard1) {
        Map<String, Long> columns = new LinkedHashMap<>();
        columns.put("Pending", 1L);
        columns.put("In Progress", 2L);
        columns.put("Completed", 3L);
        List<DashboardColumn> dashboardColumnList = new ArrayList<>();
        for (Map.Entry<String, Long> entry : columns.entrySet()) {
            DashboardColumn dashboardColumn = new DashboardColumn();
            dashboardColumn.setDashboard(dashboard1);
            dashboardColumn.setName(entry.getKey());
            dashboardColumn.setPosition(entry.getValue());
            dashboardColumnList.add(dashboardColumn);
        }
        try {
            dashboardColumnRepository.saveAll(dashboardColumnList);
        } catch (Exception e) {
            throw new FailedToCreateDashboardColumnException(Constants.DashboardColumn.FAILED_TO_CREATE_DASHBOARD_COLUMN, e);
        }

    }

    @Override
    @Transactional
    public void createColumnsForDashboard(Dashboard dashboard1, AddDashboardRequestDto addDashboardRequestDto) {
        List<DashboardColumn> dashboardColumnList = new ArrayList<>();
        List<AddDashboardRequestDto.AddFieldRequestDto> addFieldRequestDtos = addDashboardRequestDto
                .fields()
                .stream()
                .sorted((p1, p2) -> Integer.parseInt(p1.fieldPosition()) - Integer.parseInt(p2.fieldPosition()))
                .toList();
        int position = 1;
        for (AddDashboardRequestDto.AddFieldRequestDto addFieldRequestDto : addFieldRequestDtos) {
            DashboardColumn dashboardColumn = new DashboardColumn();
            dashboardColumn.setDashboard(dashboard1);
            dashboardColumn.setName(addFieldRequestDto.fieldName());
            dashboardColumn.setPosition(position++);
            dashboardColumnList.add(dashboardColumn);
        }
        try {
            dashboardColumnRepository.saveAll(dashboardColumnList);
        } catch (Exception e) {
            throw new FailedToCreateDashboardColumnException(Constants.DashboardColumn.FAILED_TO_CREATE_DASHBOARD_COLUMN, e);
        }
    }

    @Override
    public ApiResponse<Void> editDashboardColumnName(EditDashboardColumnNameRequestDto editDashboardColumnNameRequestDto, UserInfoDetails userInfoDetails) {
        DashboardColumn dashboardColumn = dashboardColumnRepository.findColumnById(editDashboardColumnNameRequestDto.columnID());
        if (dashboardColumn == null) {
            throw new DashboardColumnNotFoundException(Constants.DashboardColumn.DASHBOARD_COLUMN_NOT_FOUND);
        }
        Dashboard dashboard = dashboardColumn.getDashboard();
        User user = findUserByEmailAndDeleted(userInfoDetails.getUsername());
        if (dashboard.getMaster().equals(user)) {
            try {
                dashboardColumn.setName(editDashboardColumnNameRequestDto.columnName());
                dashboardColumnRepository.save(dashboardColumn);
                return ApiResponseCreator.success(Constants.DashboardColumn.DASHBOARD_COLUMN_NAME_EDITED, HttpStatus.OK);
            } catch (Exception e) {
                throw new FailedToAddDashboardMemberException(Constants.DashboardColumn.FAILED_TO_EDIT_DASHBOARD_COLUMN_NAME, e);
            }
        } else {
            throw new FailedToEditDashboardColumnNameException(Constants.Dashboard.ONLY_MASTER_CAN_EDIT_DASHBOARD);
        }
    }
}
