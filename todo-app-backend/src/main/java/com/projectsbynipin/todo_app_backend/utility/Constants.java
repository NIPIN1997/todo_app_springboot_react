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

    }

    public static class Login {

        private Login() {
        }

        public static final String LOGIN_SUCCESSFUL = "Login successful.";
        public static final String LOGIN_FAILED = "Login failed.";
    }

    public static class Jwt {

        private Jwt() {
        }

        public static final String JWT_REFRESH_TOKEN_EXPIRED = "Jwt refresh token expired.";
        public static final String FAILED_TO_ENCRYPT_TOKEN = "Failed to encrypt JWT refresh token.";
        public static final String FAILED_TO_DECRYPT_TOKEN = "Failed to decrypt JWT refresh token.";
    }

    public static class Miscellaneous {

        private Miscellaneous() {
        }

        public static final String VALIDATION_FAILED = "Validation failed.";
        public static final String ACCESS_DENIED = "Access denied.";
    }

    public static class Redis {

        private Redis() {
        }

        public static final String REDIS_KEY_PREFIX = "refresh-token-";
    }

}
