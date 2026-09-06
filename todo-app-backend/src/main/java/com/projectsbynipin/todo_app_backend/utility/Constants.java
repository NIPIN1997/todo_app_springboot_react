package com.projectsbynipin.todo_app_backend.utility;

public class Constants {

    private Constants() {
    }

    public static class Role {

        private Role() {
        }

        public static final String ROLE_ADMIN = "ROLE_ADMIN";
        public static final String ROLE_USER = "ROLE_USER";
        public static final String ROLE_CREATED = "Role created.";
        public static final String FAILED_TO_CREATE_ROLE = "Failed to create role.";
        public static final String ROLE_ALREADY_EXISTS = "Role already exists.";
    }

    public static class User {

        private User() {
        }

        public static final String USER_NOT_FOUND = "User not found.";
        public static final String USER_EMAIL_ALREADY_EXISTS = "User email already exists.";
        public static final String ADMIN_CREATED = "Admin created.";
        public static final String FAILED_TO_CREATE_ADMIN = "Failed to create admin.";
        public static final String USER_CREATED = "User created.";
        public static final String FAILED_TO_CREATE_USER = "Failed to create user.";
        public static final String USER_RETRIEVED = "User retrieved.";
        public static final String FAILED_TO_RETRIEVE_USER = "Failed to retrieve user.";
        public static final String USER_EDITED = "User edited successfully.";
        public static final String FAILED_TO_EDIT_USER = "Failed to edit user.";
        public static final String LOGGED_IN_DEVICES_RETRIEVED = "Logged in devices retrieved successfully.";
        public static final String FAILED_TO_RETRIEVE_LOGGED_IN_DEVICES = "Failed to retrieve devices.";
        public static final String USERNAME_EXISTS = "Username exists.";
        public static final String USERNAME_DOESNOT_EXIST = "Username doesn't exist.";
        public static final String FAILED_TO_CHECK_USERNAME = "Failed to check username.";
    }

    public static class Login {

        private Login() {
        }

        public static final String LOGIN_SUCCESSFUL = "Login successful.";
        public static final String LOGIN_FAILED = "Login failed.";
        public static final String LOGOUT_SUCCESSFUL = "Logout successful.";
        public static final String LOGOUT_FAILED = "Logout failed.";
        public static final String LOGIN_DEVICE_LIMIT_REACHED = "Login failed. Cannot login on more that two devices.";
        public static final String DEVICE_LOGGED_OUT = "Device logged out.";
        public static final String DEVICE_LOG_OUT_FAILED = "Device log out failed.";
        public static final String INVALID_CREDENTIALS = "Invalid credentials.";
        public static final String FAILED_LOGIN_LIMIT_REACHED = "You had three failed login attempt. Your account is locked for: ";
    }

    public static class Jwt {

        private Jwt() {
        }

        public static final String JWT_REFRESH_TOKEN_EXPIRED = "JWT refresh token expired.";
        public static final String FAILED_TO_ENCRYPT_TOKEN = "Failed to encrypt JWT refresh token.";
        public static final String FAILED_TO_DECRYPT_TOKEN = "Failed to decrypt JWT refresh token.";
    }

    public static class Miscellaneous {

        private Miscellaneous() {
        }

        public static final String VALIDATION_FAILED = "Validation failed.";
        public static final String ACCESS_DENIED = "Access denied.";
        public static final String CSRF_TOKEN_CREATED = "CSRF token created.";
        public static final String FAILED_TO_REFRESH_TOKEN = "Failed to refresh token.";
        public static final String AN_UNEXCEPTED_ERROR_OCCURRED = "An unexpected error occurred.";
    }

    public static class Redis {

        private Redis() {
        }

        public static final String REDIS_KEY_PREFIX_TOKEN_PREFIX = "refresh-token-";
        public static final String REDIS_KEY_PREFIX_LOGIN_LOCKOUT_PREFIX = "login-lockout-";
    }

    public static class Dashboard {
        private Dashboard() {
        }

        public static final String DASHBOARD_CREATED = "Dashboard created.";
        public static final String FAILED_TO_CREATE_DASHBOARD = "Failed to create dashboard.";
        public static final String FAILED_TO_RETRIEVE_DASHBOARDS = "Failed to retrieve dashboards.";
        public static final String RETRIEVED_DASHBOARDS = "Dashboards retrieved.";
        public static final String DASHBOARD_NOT_FOUND = "Dashboard not found.";
        public static final String FAILED_TO_RETRIEVE_DASHBOARD = "Failed to retrieve dashboard.";
        public static final String RETRIEVED_DASHBOARD = "Dashboard retrieved.";
        public static final String DASHBOARD_COLUMN_NAMES_RETRIEVED = "Column names retrieved.";
        public static final String FAILED_TO_RETRIEVE_DASHBOARD_COLUMN_NAMES = "Failed to retrieve column names.";
        public static final String DASHBOARD_MEMBER_NAMES_RETRIEVED = "Dashboard member names retrieved.";
        public static final String FAILED_TO_RETRIEVE_DASHBOARD_MEMBER_NAMES = "Failed to retrieve dashboard member names.";
        public static final String FAILED_TO_DELETE_DASHBOARD = "Failed to delete dashboard.";
        public static final String DASHBOARD_DELETED = "Dashboard deleted.";
        public static final String DASHBOARD_ARCHIVED = "Dashboard archived.";
        public static final String DASHBOARD_UNARCHIVED = "Dashboard unarchived.";
        public static final String FAILED_TO_ARCHIVE_DASHBOARD = "Failed to archive dashboard.";
        public static final String FAILED_TO_UNARCHIVE_DASHBOARD = "Failed to unarchive dashboard.";
        public static final String ONLY_MASTER_CAN_ARCHIVE_DASHBOARD = "Only master can archive dashboard.";
        public static final String ONLY_MASTER_CAN_UNARCHIVE_DASHBOARD = "Only master can unarchive dashboard.";
        public static final String ONLY_MASTER_CAN_DELETE_DASHBOARD = "Only master can delete dashboard.";
        public static final String ONLY_MASTER_CAN_EDIT_DASHBOARD = "Only master can edit dashboard.";
        public static final String FAILED_TO_EDIT_DASHBOARD = "Failed to edit dashboard.";
        public static final String DASHBOARD_EDITED = "Dashboard edited.";
        public static final String DASHBOARD_MEMBER_REMOVED = "Dashboard member removed.";
        public static final String FAILED_TO_REMOVE_DASHBOARD_MEMBER = "Failed to remove dashboard member.";
        public static final String MEMBER_PROMOTED = "Member promoted.";
        public static final String FAILED_TO_PROMOTE_MEMBER = "Failed to promote member.";
        public static final String INVITATION_SENT_TO_DASHBOARD_MEMBER = "Invitation sent to dashboard member.";
        public static final String FAILED_TO_ADD_DASHBOARD_MEMBER = "Failed to add dashboard member.";
    }

    public static class DashboardColumn {
        private DashboardColumn() {
        }

        public static final String FAILED_TO_CREATE_DASHBOARD_COLUMN = "Failed to create dashboard column.";
        public static final String DASHBOARD_COLUMN_NOT_FOUND = "Dashboard column not found.";
    }

    public static class Invitation {
        private Invitation() {
        }

        public static final String INVITATIONS_FETCHED = "Invitations fetched.";
        public static final String FAILED_TO_FETCH_INVITATIONS = "Failed to fetch invitations.";
        public static final String INVITATION_ACCEPTED = "Invitations accepted.";
        public static final String INVITATION_REJECTED = "Invitations rejected.";
        public static final String FAILED_TO_ACCEPT_OR_REJECT_INVITATION = "Failed to accept or reject invitation.";
        public static final String INVITATION_NOT_FOUND = "Invitation not found.";
        public static final String FAILED_TO_CREATE_INVITATION = "Failed to create invitation.";
    }

    public static class KafkaTopics {
        private KafkaTopics() {
        }

        public static final String LOGIN_ACTIVITY_LOGS = "LOGIN-ACTIVITY-LOGS";
        public static final String TOKEN_REFRESH_ACTIVITY_LOGS = "TOKEN-REFRESH-ACTIVITY-LOGS";
        public static final String ERROR_LOGS = "ERROR-LOGS";
    }

    public static class Task {
        private Task() {
        }

        public static final String TASK_CREATE = "Task created.";
        public static final String FAILED_TO_CREATE_TASK = "Failed to create task.";
        public static final String NOT_AUTHORIZED_TO_CREATE_TASK = "Not authorized to create task.";
        public static final String TASK_NOT_FOUND = "Task not found.";
        public static final String TASK_STATUS_UPDATED = "Task status updated.";
        public static final String FAILED_TO_UPDATE_TASK_STATUS = "Failed to update task status.";
        public static final String NOT_AUTHORIZED_TO_UPDATE_TASK = "Not authorized to update task.";
        public static final String TASK_FETCHED_SUCCESSFULLY = "Task fetched successfully.";
        public static final String FAILED_TO_FETCH_TASK = "Failed to fetch task.";
        public static final String TASK_DELETED = "Task deleted.";
        public static final String FAILED_TO_DELETE_TASK = "Failed to delete task.";
        public static final String TASK_EDITED = "Task edited.";
        public static final String FAILED_TO_EDIT_TASK = "Failed to edit task.";
    }
}
