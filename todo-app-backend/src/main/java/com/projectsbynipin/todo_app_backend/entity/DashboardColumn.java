package com.projectsbynipin.todo_app_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
        schema = "user_schema",
        name = "dashboard_columns",
        indexes = {
                @Index(name = "idx_dashboard_id_dashboard_columns_table", columnList = "dashboard_id")
        }
)
public class DashboardColumn {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @Column(name = "name", length = 30, nullable = false)
    private String name;
    @ManyToOne
    @JoinColumn(name = "dashboard_id", nullable = false)
    private Dashboard dashboard;
    @OneToMany(mappedBy = "column")
    private List<Task> tasks;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "is_deleted")
    private boolean deleted = false;
    @Column(name = "position", nullable = false)
    private long position;
}
