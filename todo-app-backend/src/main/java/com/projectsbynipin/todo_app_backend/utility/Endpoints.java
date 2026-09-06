package com.projectsbynipin.todo_app_backend.utility;

public class Endpoints {

    private Endpoints() {
    }

    public static final String[] publicEndpoints = {
            "/api/v1/users/login",
            "/api/v1/users/signup",
            "/api/v1/users/remember-me-login",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**"
    };
    public static final String[] userEndpoints = {
            "/api/v1/users/get-user/**",
            "/api/v1/users/edit-user/**",
            "/api/v1/users/logged-in-devices/**",
            "/api/v1/users/logout-device/**",
            "/api/v1/users/get-dashboards-by-user",
            "/api/v1/users/check-username-existence",
            "/api/v1/users/add-dashboard",
            "/api/v1/users/get-invitations",
            "/api/v1/users/accept-or-reject-invitation",
            "/api/v1/users/get-all-dashboards",
            "/get-dashboard-by-id/{id}",
            "/get-column-names/{dashboardId}",
            "/get-member-names/{dashboardId}",
            "/drag-task",
            "/get-task/{id}",
            "/delete-dashboard/{id}",
            "/archive-dashboard/{id}",
            "/delete-task/{id}",
            "/unarchive-dashboard/{id}",
            "/get-task-details-edit/{id}",
            "/edit-task",
            "/get-dashboard-details-for-edit/{id}",
            "/edit-dashboard-name",
            "/remove-dashboard-member",
            "/promote-member-to-master"
    };
    public static final String[] adminEndpoints = {
            "/api/v1/users/create-admin",
            "/api/v1/roles/create-role"
    };
}
