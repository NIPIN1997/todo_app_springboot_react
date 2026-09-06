package com.projectsbynipin.todo_app_backend.service.impl;

import com.projectsbynipin.todo_app_backend.dto.*;
import com.projectsbynipin.todo_app_backend.entity.Dashboard;
import com.projectsbynipin.todo_app_backend.entity.DashboardColumn;
import com.projectsbynipin.todo_app_backend.entity.User;
import com.projectsbynipin.todo_app_backend.enums.ViewDashboardCategories;
import com.projectsbynipin.todo_app_backend.exception.*;
import com.projectsbynipin.todo_app_backend.mapper.DashboardColumnMapper;
import com.projectsbynipin.todo_app_backend.mapper.DashboardMapper;
import com.projectsbynipin.todo_app_backend.repository.*;
import com.projectsbynipin.todo_app_backend.service.DashboardColumnService;
import com.projectsbynipin.todo_app_backend.service.DashboardService;
import com.projectsbynipin.todo_app_backend.service.InvitationService;
import com.projectsbynipin.todo_app_backend.service.jwt.UserInfoDetails;
import com.projectsbynipin.todo_app_backend.utility.ApiResponseCreator;
import com.projectsbynipin.todo_app_backend.utility.Constants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final DashboardRepository dashboardRepository;
    private final DashboardColumnService dashboardColumnService;
    private final UserRepository userRepository;
    private final DashboardMapper dashboardMapper;
    private final InvitationService invitationService;
    private final DashboardColumnRepository dashboardColumnRepository;
    private final DashboardColumnMapper dashboardColumnMapper;
    private final TaskRepository taskRepository;
    private final InvitationRepository invitationRepository;

    private Dashboard findDashboardById(UUID dashboardId) {
        Dashboard dashboard = dashboardRepository.findDashboardById(dashboardId);
        if (dashboard == null) {
            throw new DashboardNotFoundException(Constants.Dashboard.DASHBOARD_NOT_FOUND);
        }
        return dashboard;
    }

    private User findUserByEmailAndDeleted(String email) {
        User user = userRepository.findByEmailAndDeleted(email, false);
        if (user == null) {
            throw new UserNotFoundException(Constants.User.USER_NOT_FOUND);
        }
        return user;
    }

    @Override
    @Transactional
    public void createDefaultDashboardForUser(User user1) {
        Dashboard dashboard = new Dashboard();
        dashboard.setName("Personal Dashboard");
        dashboard.setMaster(user1);
        HashSet<User> users = new HashSet<>();
        users.add(user1);
        dashboard.setUsers(users);
        dashboard.setPrivate(true);
        try {
            Dashboard dashboard1 = dashboardRepository.save(dashboard);
            dashboardColumnService.createDefaultColumnsForUser(dashboard1);
        } catch (Exception e) {
            throw new FailedToCreateDashboardException(Constants.Dashboard.FAILED_TO_CREATE_DASHBOARD, e);
        }
    }

    @Override
    public ApiResponse<List<DashboardResponseDto>> getDashboardsByUser(UserInfoDetails userInfoDetails) {
        User user = findUserByEmailAndDeleted(userInfoDetails.getUsername());
        try {
            List<Dashboard> dashboardList = dashboardRepository.findDashboardsByUser(user);
            List<DashboardResponseDto> dashboards = dashboardMapper.dashboardToDashboardResponseDto(dashboardList);
            return ApiResponseCreator.success(Constants.Dashboard.RETRIEVED_DASHBOARDS, dashboards, HttpStatus.OK);
        } catch (Exception e) {
            throw new FailedToRetrieveDashboardsException(Constants.Dashboard.FAILED_TO_RETRIEVE_DASHBOARDS, e);
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> addDashboard(AddDashboardRequestDto addDashboardRequestDto, UserInfoDetails userInfoDetails) {
        Dashboard dashboard = new Dashboard();
        dashboard.setPrivate(addDashboardRequestDto.isPrivate());
        User masterUser = findUserByEmailAndDeleted(userInfoDetails.getUsername());
        dashboard.setMaster(masterUser);
        dashboard.setName(addDashboardRequestDto.name());
        Set<User> users = new HashSet<>();
        users.add(masterUser);
        dashboard.setUsers(users);
        Dashboard dashboard1 = dashboardRepository.save(dashboard);
        Set<UUID> invitationUserIds = new HashSet<>();
        invitationUserIds.add(masterUser.getId());
        if (!addDashboardRequestDto.isPrivate() && !addDashboardRequestDto.members().isEmpty()) {
            invitationUserIds.addAll(userRepository.findByEmailInListAndDeleted(addDashboardRequestDto.members()).stream().map(User::getId).collect(Collectors.toUnmodifiableSet()));
        }
        invitationUserIds.remove(masterUser.getId());
        try {
            invitationService.createInvitations(invitationUserIds, dashboard1.getId());
            dashboardColumnService.createColumnsForDashboard(dashboard1, addDashboardRequestDto);
            return ApiResponseCreator.success(Constants.Dashboard.DASHBOARD_CREATED, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new FailedToCreateDashboardException(Constants.Dashboard.FAILED_TO_CREATE_DASHBOARD, e);
        }
    }

    @Override
    public ApiResponse<List<DetailedDashboardResponseDto>> getAllDashboards(ViewDashboardCategories category, UserInfoDetails userInfoDetails) {
        User user = findUserByEmailAndDeleted(userInfoDetails.getUsername());
        try {
            List<Dashboard> dashboardList = new ArrayList<>();
            switch (category) {
                case ViewDashboardCategories.ALL -> dashboardList = dashboardRepository.findDashboardsByUser(user);
                case ViewDashboardCategories.OWNED -> dashboardList = dashboardRepository.findDashboardsByMaster(user);
                case ViewDashboardCategories.SHARED ->
                        dashboardList = dashboardRepository.findSharedDashboardsForUser(user);
                case ViewDashboardCategories.ARCHIVED ->
                        dashboardList = dashboardRepository.findArchivedDashboardsForUser(user);
            }
            List<DetailedDashboardResponseDto> detailedDashboardResponseDtos = dashboardMapper.dashboardToDetailedDashboardResponseDto(dashboardList, user);
            return ApiResponseCreator.success(Constants.Dashboard.RETRIEVED_DASHBOARDS, detailedDashboardResponseDtos, HttpStatus.OK);
        } catch (Exception e) {
            throw new FailedToRetrieveDashboardsException(Constants.Dashboard.FAILED_TO_RETRIEVE_DASHBOARDS, e);
        }
    }

    @Override
    public ApiResponse<DashboardWithColumnsAndTasksDto> getDashboardById(UUID id) {
        Dashboard dashboard = findDashboardById(id);
        try {
            DashboardWithColumnsAndTasksDto dashboardWithColumnsAndTasksDto = dashboardMapper.dashboardToDashboardWithColumnsAndTasksDto(dashboard);
            return ApiResponseCreator.success(Constants.Dashboard.RETRIEVED_DASHBOARD, dashboardWithColumnsAndTasksDto, HttpStatus.OK);
        } catch (Exception e) {
            throw new FailedToRetrieveDashboardException(Constants.Dashboard.FAILED_TO_RETRIEVE_DASHBOARD, e);
        }
    }

    @Override
    public ApiResponse<List<ColumnNameIdResponseDto>> getColumnNames(UUID dashboardId) {
        Dashboard dashboard = findDashboardById(dashboardId);
        try {
            List<DashboardColumn> dashboardColumns = dashboardColumnRepository.getColumnNamesForDashboard(dashboard);
            List<ColumnNameIdResponseDto> columnNameIdResponseDtos = dashboardColumnMapper.dashboardColumnToColumnNameIdResponseDto(dashboardColumns);
            return ApiResponseCreator.success(Constants.Dashboard.DASHBOARD_COLUMN_NAMES_RETRIEVED, columnNameIdResponseDtos, HttpStatus.OK);
        } catch (Exception e) {
            throw new FailedToRetrieveDashboardColumnNamesException(Constants.Dashboard.FAILED_TO_RETRIEVE_DASHBOARD_COLUMN_NAMES, e);
        }
    }

    @Override
    public ApiResponse<List<MemberNameIdResponseDto>> getMemberNames(UUID dashboardId) {
        Dashboard dashboard = findDashboardById(dashboardId);
        try {
            List<MemberNameIdResponseDto> memberNameIdResponseDtos = dashboardMapper.dashboardToMemberNameIdResponseDto(dashboard);
            return ApiResponseCreator.success(Constants.Dashboard.DASHBOARD_MEMBER_NAMES_RETRIEVED, memberNameIdResponseDtos, HttpStatus.OK);
        } catch (Exception e) {
            throw new FailedToRetrieveDashboardMemberNamesException(Constants.Dashboard.FAILED_TO_RETRIEVE_DASHBOARD_MEMBER_NAMES, e);
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteDashboard(UUID id, UserInfoDetails userInfoDetails) {
        Dashboard dashboard = findDashboardById(id);
        User user = findUserByEmailAndDeleted(userInfoDetails.getUsername());
        if (dashboard.getMaster().equals(user)) {
            try {
                taskRepository.softDeleteTasksForADashboard(dashboard);
                dashboardColumnRepository.softDeleteDashboardColumnsForADashboard(dashboard);
                invitationRepository.deletePendingInvitations(dashboard.getId());
                dashboard.setDeleted(true);
                dashboardRepository.save(dashboard);
                return ApiResponseCreator.success(Constants.Dashboard.DASHBOARD_DELETED, HttpStatus.OK);
            } catch (Exception e) {
                throw new FailedToDeleteDashboardException(Constants.Dashboard.FAILED_TO_DELETE_DASHBOARD, e);
            }
        } else {
            throw new FailedToDeleteDashboardException(Constants.Dashboard.ONLY_MASTER_CAN_DELETE_DASHBOARD);
        }
    }

    @Override
    public ApiResponse<Void> archiveDashboard(UUID id, UserInfoDetails userInfoDetails) {
        Dashboard dashboard = findDashboardById(id);
        User user = findUserByEmailAndDeleted(userInfoDetails.getUsername());
        if (dashboard.getMaster().equals(user)) {
            try {
                dashboard.setArchived(true);
                dashboardRepository.save(dashboard);
                return ApiResponseCreator.success(Constants.Dashboard.DASHBOARD_ARCHIVED, HttpStatus.OK);
            } catch (Exception e) {
                throw new FailedToArchiveDashboardException(Constants.Dashboard.FAILED_TO_ARCHIVE_DASHBOARD, e);
            }
        } else {
            throw new FailedToArchiveDashboardException(Constants.Dashboard.ONLY_MASTER_CAN_ARCHIVE_DASHBOARD);
        }
    }

    @Override
    public ApiResponse<Void> unarchiveDashboard(UUID id, UserInfoDetails userInfoDetails) {
        Dashboard dashboard = findDashboardById(id);
        User user = findUserByEmailAndDeleted(userInfoDetails.getUsername());
        if (dashboard.getMaster().equals(user)) {
            try {
                dashboard.setArchived(false);
                dashboardRepository.save(dashboard);
                return ApiResponseCreator.success(Constants.Dashboard.DASHBOARD_UNARCHIVED, HttpStatus.OK);
            } catch (Exception e) {
                throw new FailedToUnarchiveDashboardException(Constants.Dashboard.FAILED_TO_UNARCHIVE_DASHBOARD, e);
            }
        } else {
            throw new FailedToUnarchiveDashboardException(Constants.Dashboard.ONLY_MASTER_CAN_UNARCHIVE_DASHBOARD);
        }
    }

    @Override
    public ApiResponse<EditDashboardViewDto> getDashboardDetailsForEdit(UUID id) {
        try {
            Dashboard dashboard = findDashboardById(id);
            return ApiResponseCreator.success(Constants.Dashboard.RETRIEVED_DASHBOARD, dashboardMapper.dashboardToEditDashboardViewDto(dashboard), HttpStatus.OK);
        } catch (Exception e) {
            throw new FailedToRetrieveDashboardException(Constants.Dashboard.FAILED_TO_RETRIEVE_DASHBOARD, e);
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> editDashboardName(EditDashboardNameRequestDto editDashboardNameRequestDto, UserInfoDetails userInfoDetails) {
        User user = findUserByEmailAndDeleted(userInfoDetails.getUsername());
        Dashboard dashboard = findDashboardById(editDashboardNameRequestDto.id());
        if (dashboard.getMaster().equals(user)) {
            try {
                dashboardRepository.updateDashboardName(editDashboardNameRequestDto.id(), editDashboardNameRequestDto.name());
                return ApiResponseCreator.success(Constants.Dashboard.DASHBOARD_EDITED, HttpStatus.OK);
            } catch (Exception e) {
                throw new FailedToEditDashboardException(Constants.Dashboard.FAILED_TO_EDIT_DASHBOARD, e);
            }
        } else {
            throw new FailedToEditDashboardException(Constants.Dashboard.ONLY_MASTER_CAN_EDIT_DASHBOARD);
        }
    }

    @Override
    public ApiResponse<Void> removeDashboardMember(RemoveDashboardMemberRequestDto removeDashboardMemberRequestDto, UserInfoDetails userInfoDetails) {
        User user = findUserByEmailAndDeleted(userInfoDetails.getUsername());
        Dashboard dashboard = findDashboardById(removeDashboardMemberRequestDto.dashboardId());
        if (dashboard.getMaster().equals(user)) {
            try {
                User userToBeRemoved = userRepository.findUserById(removeDashboardMemberRequestDto.memberId());
                dashboard.getUsers().remove(userToBeRemoved);
                dashboardRepository.save(dashboard);
                return ApiResponseCreator.success(Constants.Dashboard.DASHBOARD_MEMBER_REMOVED, HttpStatus.OK);
            } catch (Exception e) {
                throw new FailedToRemoveDashboardMemberException(Constants.Dashboard.FAILED_TO_REMOVE_DASHBOARD_MEMBER, e);
            }
        } else {
            throw new FailedToRemoveDashboardMemberException(Constants.Dashboard.ONLY_MASTER_CAN_EDIT_DASHBOARD);
        }
    }

    @Override
    public ApiResponse<Void> promoteDashboardMember(PromoteDashboardMemberRequestDto promoteDashboardMemberRequestDto, UserInfoDetails userInfoDetails) {
        User user = findUserByEmailAndDeleted(userInfoDetails.getUsername());
        Dashboard dashboard = findDashboardById(promoteDashboardMemberRequestDto.dashboardId());
        if (dashboard.getMaster().equals(user)) {
            try {
                User userToBePromoted = userRepository.findUserById(promoteDashboardMemberRequestDto.memberId());
                dashboard.setMaster(userToBePromoted);
                dashboardRepository.save(dashboard);
                return ApiResponseCreator.success(Constants.Dashboard.MEMBER_PROMOTED, HttpStatus.OK);
            } catch (Exception e) {
                throw new FailedToPromoteMemberException(Constants.Dashboard.FAILED_TO_PROMOTE_MEMBER, e);
            }
        } else {
            throw new FailedToPromoteMemberException(Constants.Dashboard.ONLY_MASTER_CAN_EDIT_DASHBOARD);
        }
    }

    @Override
    public ApiResponse<Void> addDashboardMember(AddDashboardMemberRequestDto addDashboardMemberRequestDto, UserInfoDetails userInfoDetails) {
        User user = findUserByEmailAndDeleted(userInfoDetails.getUsername());
        Dashboard dashboard = findDashboardById(addDashboardMemberRequestDto.dashboardId());
        if (dashboard.getMaster().equals(user)) {
            try {
                User userToBeAdded = userRepository.findByEmailAndDeleted(addDashboardMemberRequestDto.username(), false);
                if (userToBeAdded == null) {
                    throw new FailedToAddDashboardMemberException(Constants.User.USERNAME_DOESNOT_EXIST);
                }
                Set<UUID> memberIds = new HashSet<>();
                memberIds.add(userToBeAdded.getId());
                invitationService.createInvitations(memberIds, dashboard.getId());
                return ApiResponseCreator.success(Constants.Dashboard.INVITATION_SENT_TO_DASHBOARD_MEMBER, HttpStatus.OK);
            } catch (Exception e) {
                throw new FailedToAddDashboardMemberException(Constants.Dashboard.FAILED_TO_ADD_DASHBOARD_MEMBER, e);
            }
        } else {
            throw new FailedToAddDashboardMemberException(Constants.Dashboard.ONLY_MASTER_CAN_EDIT_DASHBOARD);
        }
    }

}
