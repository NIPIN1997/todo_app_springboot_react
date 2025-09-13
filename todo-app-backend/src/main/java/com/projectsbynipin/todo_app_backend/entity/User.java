package com.projectsbynipin.todo_app_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_name", columnList = "name"),
                @Index(name = "idx_email", columnList = "email")
        }
)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private UUID id;
    @Column(name = "name", length = 20, nullable = false)
    private String name;
    @Column(name = "email", length = 50, nullable = false, unique = true, updatable = false)
    private String email;
    @Column(name = "contact", length = 20)
    private String contact;
    @Column(name = "password", length = 60, nullable = false)
    private String password;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
}
