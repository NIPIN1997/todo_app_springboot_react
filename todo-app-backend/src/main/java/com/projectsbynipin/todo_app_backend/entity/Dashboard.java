package com.projectsbynipin.todo_app_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
        schema = "user_schema",
        name = "dashboards",
        indexes = {
                @Index(name = "idx_master_id_dashboards_table", columnList = "master_id")
        }
)
public class Dashboard {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @Column(name = "name", length = 30, nullable = false)
    private String name;
    @ManyToOne
    @JoinColumn(name = "master_id", nullable = false)
    private User master;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "dashboard_users",
            schema = "user_schema",
            joinColumns = @JoinColumn(name = "dashboard_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users;
    @OneToMany(mappedBy = "dashboard")
    private List<DashboardColumn> columns;
    @Column(name = "is_archived", nullable = false)
    private boolean archived = false;
    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;
    @Column(name = "is_private", nullable = false)
    private boolean isPrivate = false;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
