package com.projectsbynipin.todo_app_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
        schema = "user_schema",
        name = "tasks",
        indexes = {
                @Index(name = "idx_column_id_tasks_table", columnList = "column_id"),
                @Index(name = "idx_dashboard_id_tasks_table", columnList = "dashboard_id")
        }
)
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @Column(name = "title", length = 50, nullable = false)
    private String title;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @Column(name = "due_date")
    private LocalDate dueDate;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "is_deleted")
    private boolean deleted = false;
    @ManyToOne
    @JoinColumn(name = "column_id", nullable = false)
    private DashboardColumn column;
    @ManyToOne
    @JoinColumn(name = "dashboard_id", nullable = false)
    private Dashboard dashboard;
    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
    @ManyToOne
    @JoinColumn(name = "assigned_to", nullable = false)
    private User assignedTo;
    @Column(name = "completed_date")
    private LocalDate completedDate;
}
