package com.projectsbynipin.todo_app_backend.repository;

import com.projectsbynipin.todo_app_backend.entity.Dashboard;
import com.projectsbynipin.todo_app_backend.entity.DashboardColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DashboardColumnRepository extends JpaRepository<DashboardColumn, UUID> {

    @Query("select c from DashboardColumn c where c.dashboard = :dashboard and c.deleted = false order by c.position asc")
    List<DashboardColumn> getColumnNamesForDashboard(Dashboard dashboard);

    @Query("select c from DashboardColumn c where c.id = :id and c.deleted = false")
    DashboardColumn findColumnById(UUID id);

    @Modifying
    @Query("update DashboardColumn c set c.deleted = true where c.dashboard = :dashboard")
    void softDeleteDashboardColumnsForADashboard(Dashboard dashboard);
}
