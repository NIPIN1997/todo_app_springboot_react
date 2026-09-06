package com.projectsbynipin.todo_app_backend.repository;

import com.projectsbynipin.todo_app_backend.entity.Dashboard;
import com.projectsbynipin.todo_app_backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    @Query("select t from Task t where t.dashboard in :dashboards and t.deleted = false")
    List<Task> findTasksInDashboards(List<Dashboard> dashboards);

    @Modifying
    @Query("update Task t set t.deleted = true where t.dashboard = :dashboard")
    void softDeleteTasksForADashboard(Dashboard dashboard);
}
