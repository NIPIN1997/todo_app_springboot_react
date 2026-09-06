package com.projectsbynipin.todo_app_backend.utility;

import java.util.stream.Stream;

public class Endpoints {

    private Endpoints() {
    }

    public static final String[] publicEndpoints = {
            "/api/v1/users/login",
            "/api/v1/users/signup",
            "/api/v1/users/remember-me-login",
            "/api/v1/users/refresh-token",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**"
    };
    public static final String[] userEndpoints = {
            "/api/v1/users/get-user",
            "/api/v1/users/edit-user",
            "/api/v1/users/logout",
            "/api/v1/users/logged-in-devices",
            "/api/v1/users/logout-device",
            "/api/v1/users/check-username-existence",
    };
    public static final String[] dashboardEndpoints = {
            "/api/v1/dashboard/get-dashboards-by-user",
            "/api/v1/dashboard/add-dashboard",
            "/api/v1/dashboard/get-invitations",
            "/api/v1/dashboard/accept-or-reject-invitation",
            "/api/v1/dashboard/get-all-dashboards",
            "/api/v1/dashboard/get-dashboard-by-id/{id}",
            "/api/v1/dashboard/get-column-names/{dashboardId}",
            "/api/v1/dashboard/delete-dashboard/{id}",
            "/api/v1/dashboard/archive-dashboard/{id}",
            "/api/v1/dashboard/unarchive-dashboard/{id}",
            "/api/v1/dashboard/get-dashboard-details-for-edit/{id}",
            "/api/v1/dashboard/edit-dashboard-name",
            "/api/v1/dashboard/remove-dashboard-member",
            "/api/v1/dashboard/promote-member-to-master",
            "/api/v1/dashboard/add-dashboard-member",
            "/api/v1/dashboard/edit-dashboard-column-name",
            "/api/v1/dashboard/get-member-names/{dashboardId}"
    };
    public static final String[] taskEndpoints = {
            "/api/v1/task/add-task",
            "/api/v1/task/drag-task",
            "/api/v1/task/get-task/{id}",
            "/api/v1/task/delete-task/{id}",
            "/api/v1/task/get-task-details-edit/{id}",
            "/api/v1/task/edit-task"
    };
    public static final String[] adminEndpoints = {
            "/api/v1/users/create-admin",
            "/api/v1/roles/create-role"
    };
    public static final String[] userOnlyEndpoints = Stream.of(
            userEndpoints,
            dashboardEndpoints,
            taskEndpoints
    ).flatMap(Stream::of).toArray(String[]::new);
}
