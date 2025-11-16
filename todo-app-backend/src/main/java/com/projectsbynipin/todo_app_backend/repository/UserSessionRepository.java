package com.projectsbynipin.todo_app_backend.repository;

import com.projectsbynipin.todo_app_backend.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {
    UserSession findByDeviceId(UUID deviceId);

    List<UserSession> findByUserIdAndIsActive(UUID userId, boolean isActive);
}
