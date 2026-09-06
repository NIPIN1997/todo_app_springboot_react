package com.projectsbynipin.todo_app_backend.repository;

import com.projectsbynipin.todo_app_backend.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {

    @Query("select u from UserSession u  where u.deviceId = :deviceId")
    UserSession findByDeviceId(UUID deviceId);

    List<UserSession> findByUserIdAndActive(UUID userId, boolean active);

    @Query("select u from UserSession u where u.active = true and u.rememberMe = false")
    List<UserSession> getAllActiveNotRememberedSessions();

    @Query("select u from UserSession u where u.active = true and u.rememberMe = true")
    List<UserSession> getAllActiveRememberedSessions();

    @Query("select u from UserSession u where u.active = false")
    List<UserSession> getAllInactiveSessions();

    @Query("select u from UserSession u where u.redisKey = :redisKey and u.active = true and u.rememberMe = false")
    UserSession findSessionByRedisKey(String redisKey);
}
