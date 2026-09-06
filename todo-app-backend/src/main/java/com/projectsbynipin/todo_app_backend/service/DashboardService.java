package com.projectsbynipin.todo_app_backend.service;

import com.projectsbynipin.todo_app_backend.dto.*;
import com.projectsbynipin.todo_app_backend.entity.User;
import com.projectsbynipin.todo_app_backend.enums.ViewDashboardCategories;
import com.projectsbynipin.todo_app_backend.service.jwt.UserInfoDetails;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface DashboardService {

    void createDefaultDashboardForUser(User user1);

    ApiResponse<List<DashboardResponseDto>> getDashboardsByUser(UserInfoDetails userInfoDetails);

    ApiResponse<Void> addDashboard(AddDashboardRequestDto addDashboardRequestDto, UserInfoDetails userInfoDetails);

    ApiResponse<List<DetailedDashboardResponseDto>> getAllDashboards(ViewDashboardCategories category, UserInfoDetails userInfoDetails);

    ApiResponse<DashboardWithColumnsAndTasksDto> getDashboardById(UUID id);

    ApiResponse<List<ColumnNameIdResponseDto>> getColumnNames(UUID dashboardId);

    ApiResponse<List<MemberNameIdResponseDto>> getMemberNames(UUID dashboardId);

    ApiResponse<Void> deleteDashboard(UUID id, UserInfoDetails userInfoDetails);

    ApiResponse<Void> archiveDashboard(UUID id, UserInfoDetails userInfoDetails);

    ApiResponse<Void> unarchiveDashboard(UUID id, UserInfoDetails userInfoDetails);

    ApiResponse<EditDashboardViewDto> getDashboardDetailsForEdit(UUID id);

    ApiResponse<Void> editDashboardName(@Valid EditDashboardNameRequestDto editDashboardNameRequestDto, UserInfoDetails userInfoDetails);

    ApiResponse<Void> removeDashboardMember(@Valid RemoveDashboardMemberRequestDto removeDashboardMemberRequestDto, UserInfoDetails userInfoDetails);

    ApiResponse<Void> promoteDashboardMember(@Valid PromoteDashboardMemberRequestDto promoteDashboardMemberRequestDto, UserInfoDetails userInfoDetails);

    ApiResponse<Void> addDashboardMember(@Valid AddDashboardMemberRequestDto addDashboardMemberRequestDto, UserInfoDetails userInfoDetails);
}
