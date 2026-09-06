package com.projectsbynipin.todo_app_backend.controller;

import com.projectsbynipin.todo_app_backend.dto.*;
import com.projectsbynipin.todo_app_backend.enums.ViewDashboardCategories;
import com.projectsbynipin.todo_app_backend.service.DashboardService;
import com.projectsbynipin.todo_app_backend.service.InvitationService;
import com.projectsbynipin.todo_app_backend.service.TaskService;
import com.projectsbynipin.todo_app_backend.service.UserService;
import com.projectsbynipin.todo_app_backend.service.jwt.UserInfoDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "Users",
        description = "APIs related to users"
)
@RestController
@RequestMapping(path = "/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final DashboardService dashboardService;
    private final InvitationService invitationService;
    private final TaskService taskService;

    @Value("${spring.https}")
    private boolean isHttps;

    @Operation(
            summary = "Create an admin",
            description = "Create a new admin in the system"
    )
    @PostMapping(path = "/create-admin")
    public ResponseEntity<ApiResponse<Void>> createAdmin(@Valid @RequestBody AddUserRequestDto addUserRequestDto) {
        ApiResponse<Void> apiResponse = userService.createAdmin(addUserRequestDto);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Signup",
            description = "Create a new user in the system"
    )
    @PostMapping(path = "/signup")
    public ResponseEntity<ApiResponse<Void>> createUser(@Valid @RequestBody AddUserRequestDto addUserRequestDto) {
        ApiResponse<Void> apiResponse = userService.createUser(addUserRequestDto);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Login",
            description = "Login API for user"
    )
    @PostMapping(path = "/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletRequest httpServletRequest) {
        ApiResponse<LoginResponseDto> apiResponse = userService.login(loginRequestDto, httpServletRequest);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Fetch user",
            description = "Fetch the details of a particular user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(path = "/get-user")
    public ResponseEntity<ApiResponse<ViewUserResponseDto>> getUser(@AuthenticationPrincipal UserInfoDetails userInfoDetails) {
        ApiResponse<ViewUserResponseDto> apiResponse = userService.getUser(userInfoDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Edit user",
            description = "Edit the details of an user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping(path = "/edit-user")
    public ResponseEntity<ApiResponse<Void>> editUser(@RequestBody EditUserRequestDto editUserRequestDto, @AuthenticationPrincipal UserInfoDetails userInfoDetails) {
        ApiResponse<Void> apiResponse = userService.editUser(editUserRequestDto, userInfoDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Refresh JWT token",
            description = "API to refresh the JWT token",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(path = "/refresh-token")
    public ResponseEntity<ApiResponse<LoginResponseDto>> refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto, HttpServletRequest httpServletRequest) {
        ApiResponse<LoginResponseDto> apiResponse = userService.refreshToken(refreshTokenRequestDto, httpServletRequest);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Logout",
            description = "Logout API for the user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(path = "/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody LogoutRequestDto logoutRequestDto, @AuthenticationPrincipal UserInfoDetails userInfoDetails, HttpServletRequest httpServletRequest) {
        ApiResponse<Void> apiResponse = userService.logout(userInfoDetails.getUsername(), logoutRequestDto, httpServletRequest);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Fetch logged in devices",
            description = "Fetch the logged in devices for a particular user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(path = "/logged-in-devices")
    public ResponseEntity<ApiResponse<List<LoggedInDevicesResponseDto>>> loggedInDevices(@AuthenticationPrincipal UserInfoDetails userInfoDetails) {
        ApiResponse<List<LoggedInDevicesResponseDto>> apiResponse = userService.loggedInDevices(userInfoDetails.getUsername());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Logout from a device",
            description = "Logout from a particular device for an user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(path = "/logout-device")
    public ResponseEntity<ApiResponse<Void>> logoutDevices(@RequestBody LogoutRequestDto logoutRequestDto, @AuthenticationPrincipal UserInfoDetails userInfoDetails) {
        ApiResponse<Void> apiResponse = userService.logoutDevices(UUID.fromString(logoutRequestDto.deviceId()), userInfoDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Login API for remember me",
            description = "Login API for remember me activated devices"
    )
    @PostMapping(path = "/remember-me-login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> rememberMeLogin(@Valid @RequestBody RememberMeLoginRequestDto rememberMeLoginRequestDto, HttpServletRequest httpServletRequest) {
        ApiResponse<LoginResponseDto> apiResponse = userService.rememberMeLogin(rememberMeLoginRequestDto, httpServletRequest);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Fetch dashboards",
            description = "Fetch the list of dashboards for a particular user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(path = "/get-dashboards-by-user")
    public ResponseEntity<ApiResponse<List<DashboardResponseDto>>> getDashboardsByUser(@AuthenticationPrincipal UserInfoDetails userInfoDetails) {
        ApiResponse<List<DashboardResponseDto>> apiResponse = dashboardService.getDashboardsByUser(userInfoDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Check username existence",
            description = "Check the existence of an username in the system",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(path = "/check-username-existence")
    public ResponseEntity<ApiResponse<String>> checkUsernameExistence(@RequestParam String username, @AuthenticationPrincipal UserInfoDetails userInfoDetails) {
        ApiResponse<String> apiResponse = userService.checkUsernameExistence(username, userInfoDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Add dashboard",
            description = "Add new dashboard by a user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(path = "/add-dashboard")
    public ResponseEntity<ApiResponse<Void>> addDashboard(@Valid @RequestBody AddDashboardRequestDto addDashboardRequestDto, @AuthenticationPrincipal UserInfoDetails userInfoDetails) {
        ApiResponse<Void> apiResponse = dashboardService.addDashboard(addDashboardRequestDto, userInfoDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Get invitations",
            description = "Get invitations for an user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(path = "/get-invitations")
    public ResponseEntity<ApiResponse<List<ViewInvitationResponseDto>>> getInvitations(@AuthenticationPrincipal UserInfoDetails userInfoDetails) {
        ApiResponse<List<ViewInvitationResponseDto>> apiResponse = invitationService.getInvitations(userInfoDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Accept or reject invitation",
            description = "API to accept or reject a particular invitation",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping(path = "/accept-or-reject-invitation")
    public ResponseEntity<ApiResponse<Void>> acceptOrRejectInvitation(@Valid @RequestBody AcceptOrRejectInvitationRequestDto acceptOrRejectInvitationRequestDto, @AuthenticationPrincipal UserInfoDetails userInfoDetails) {
        ApiResponse<Void> apiResponse = invitationService.acceptOrRejectInvitation(acceptOrRejectInvitationRequestDto, userInfoDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Fetch all dashboards with details",
            description = "Fetch all dashboards of an user with details",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(path = "/get-all-dashboards")
    public ResponseEntity<ApiResponse<List<DetailedDashboardResponseDto>>> getAllDashboards(@RequestParam ViewDashboardCategories category, @AuthenticationPrincipal UserInfoDetails userInfoDetails) {
        ApiResponse<List<DetailedDashboardResponseDto>> apiResponse = dashboardService.getAllDashboards(category, userInfoDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Fetch the dashboard of a user with all tasks",
            description = "Fetch the dashboard of a user with all the columns and tasks",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(path = "/get-dashboard-by-id/{id}")
    public ResponseEntity<ApiResponse<DashboardWithColumnsAndTasksDto>> getDashboardById(@PathVariable UUID id) {
        ApiResponse<DashboardWithColumnsAndTasksDto> apiResponse = dashboardService.getDashboardById(id);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Fetch the column names and id",
            description = "Fetch the columns with their ids and names for a particular dashboard",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(path = "/get-column-names/{dashboardId}")
    public ResponseEntity<ApiResponse<List<ColumnNameIdResponseDto>>> getColumnNames(@PathVariable UUID dashboardId) {
        ApiResponse<List<ColumnNameIdResponseDto>> apiResponse = dashboardService.getColumnNames(dashboardId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Fetch the members of a dashboard",
            description = "Fetch the ids and names of members for a particular dashboard",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(path = "/get-member-names/{dashboardId}")
    public ResponseEntity<ApiResponse<List<MemberNameIdResponseDto>>> getMemberNames(@PathVariable UUID dashboardId) {
        ApiResponse<List<MemberNameIdResponseDto>> apiResponse = dashboardService.getMemberNames(dashboardId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Add new task",
            description = "Add new task",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(path = "/add-task")
    public ResponseEntity<ApiResponse<Void>> addTask(@Valid @RequestBody AddTaskRequestDto addTaskRequestDto, @AuthenticationPrincipal UserInfoDetails userInfoDetails) {
        ApiResponse<Void> apiResponse = taskService.addTask(addTaskRequestDto, userInfoDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Drag task",
            description = "Change task status by dragging from one column to another",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping(path = "/drag-task")
    public ResponseEntity<ApiResponse<Void>> dragTask(@Valid @RequestBody DragTaskRequestDto dragTaskRequestDto, @AuthenticationPrincipal UserInfoDetails userInfoDetails) {
        ApiResponse<Void> apiResponse = taskService.dragTask(dragTaskRequestDto, userInfoDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Get task",
            description = "Get task by id",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(path = "/get-task/{id}")
    public ResponseEntity<ApiResponse<ViewTaskResponseDto>> getTaskById(@PathVariable UUID id) {
        ApiResponse<ViewTaskResponseDto> apiResponse = taskService.getTaskById(id);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete dashboard",
            description = "Delete dashboard by id",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping(path = "/delete-dashboard/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDashboard(@PathVariable UUID id, @AuthenticationPrincipal UserInfoDetails userInfoDetails) {
        ApiResponse<Void> apiResponse = dashboardService.deleteDashboard(id, userInfoDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Archive dashboard",
            description = "Archive dashboard by id",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping(path = "/archive-dashboard/{id}")
    public ResponseEntity<ApiResponse<Void>> archiveDashboard(@PathVariable UUID id, @AuthenticationPrincipal UserInfoDetails userInfoDetails) {
        ApiResponse<Void> apiResponse = dashboardService.archiveDashboard(id, userInfoDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete task",
            description = "Delete task by id",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping(path = "/delete-task/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTaskById(@PathVariable UUID id) {
        ApiResponse<Void> apiResponse = taskService.deleteTaskById(id);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Unarchive dashboard",
            description = "Unarchive dashboard by id",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping(path = "/unarchive-dashboard/{id}")
    public ResponseEntity<ApiResponse<Void>> unarchiveDashboard(@PathVariable UUID id, @AuthenticationPrincipal UserInfoDetails userInfoDetails) {
        ApiResponse<Void> apiResponse = dashboardService.unarchiveDashboard(id, userInfoDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @Operation(
            summary = "Get task details",
            description = "Get task details for editing",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(path = "/get-task-details-edit/{id}")
    public ResponseEntity<ApiResponse<EditTaskViewDto>> getTaskDetailsEdit(@PathVariable UUID id) {
        ApiResponse<EditTaskViewDto> apiResponse = taskService.getTaskDetailsEdit(id);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Edit task",
            description = "Edit task by id",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping(path = "/edit-task")
    public ResponseEntity<ApiResponse<Void>> editTaskById(@Valid @RequestBody EditTaskRequestDto editTaskRequestDto) {
        ApiResponse<Void> apiResponse = taskService.editTaskById(editTaskRequestDto);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Get dashboard details for editing",
            description = "Get dashboard details for editing by id",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(path = "/get-dashboard-details-for-edit/{id}")
    public ResponseEntity<ApiResponse<EditDashboardViewDto>> getDashboardDetailsForEdit(@PathVariable UUID id) {
        ApiResponse<EditDashboardViewDto> apiResponse = dashboardService.getDashboardDetailsForEdit(id);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Edit dashboard name",
            description = "Edit dashboard name by id",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping(path = "/edit-dashboard-name")
    public ResponseEntity<ApiResponse<Void>> editDashboardName(@Valid @RequestBody EditDashboardNameRequestDto editDashboardNameRequestDto, @AuthenticationPrincipal UserInfoDetails userInfoDetails) {
        ApiResponse<Void> apiResponse = dashboardService.editDashboardName(editDashboardNameRequestDto, userInfoDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Remove dashboard member",
            description = "Remove a member from the dashboard",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping(path = "/remove-dashboard-member")
    public ResponseEntity<ApiResponse<Void>> removeDashboardMember(@Valid @RequestBody RemoveDashboardMemberRequestDto removeDashboardMemberRequestDto, @AuthenticationPrincipal UserInfoDetails userInfoDetails) {
        ApiResponse<Void> apiResponse = dashboardService.removeDashboardMember(removeDashboardMemberRequestDto, userInfoDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Promote dashboard member",
            description = "Promote dashboard member to master",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping(path = "/promote-member-to-master")
    public ResponseEntity<ApiResponse<Void>> promoteMemberToMaster(@Valid @RequestBody PromoteDashboardMemberRequestDto promoteDashboardMemberRequestDto, @AuthenticationPrincipal UserInfoDetails userInfoDetails) {
        ApiResponse<Void> apiResponse = dashboardService.promoteDashboardMember(promoteDashboardMemberRequestDto, userInfoDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Add a dashboard member",
            description = "Add a new member to the dashboard",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(path = "/add-dashboard-member")
    public ResponseEntity<ApiResponse<Void>> addDashboardMember(@Valid @RequestBody AddDashboardMemberRequestDto addDashboardMemberRequestDto, @AuthenticationPrincipal UserInfoDetails userInfoDetails) {
        ApiResponse<Void> apiResponse = dashboardService.addDashboardMember(addDashboardMemberRequestDto, userInfoDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}