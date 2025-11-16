package com.projectsbynipin.todo_app_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        name = "user_sessions",
        indexes = {
                @Index(name = "idx_device_id", columnList = "device_id")
        }
)
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private UUID id;
    @Column(name = "device_id", updatable = false, nullable = false, unique = true)
    private UUID deviceId;
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;
    @Column(name = "browser", updatable = false, nullable = false)
    private String browser;
    @Column(name = "os", updatable = false, nullable = false)
    private String os;
    @Column(name = "os_version", updatable = false, nullable = false)
    private String osVersion;
    @Column(name = "fingerprint", updatable = false, nullable = false, unique = true)
    private String fingerprint;
    @CreationTimestamp
    @Column(name = "login_time", updatable = false, nullable = false)
    private LocalDateTime loginTime;
    @Column(name = "logout_time")
    private LocalDateTime logoutTime;
    @Column(name = "is_active")
    private boolean isActive = true;
}
