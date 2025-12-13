package com.projectsbynipin.todo_app_backend.repository;

import com.projectsbynipin.todo_app_backend.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {
    UserSession findByDeviceId(UUID deviceId);

    List<UserSession> findByUserIdAndIsActive(UUID userId, boolean isActive);

    @Query("from UserSession u where u.isActive = true and u.rememberMe = false")
    List<UserSession> getAllActiveNotRememberedSessions();

    @Query("from UserSession u where u.isActive = true and u.rememberMe = true")
    List<UserSession> getAllActiveRememberedSessions();

    @Query("from UserSession u where u.isActive = false")
    List<UserSession> getAllInactiveSessions();
}
