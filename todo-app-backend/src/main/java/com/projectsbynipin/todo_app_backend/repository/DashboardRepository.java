package com.projectsbynipin.todo_app_backend.repository;

import com.projectsbynipin.todo_app_backend.entity.Dashboard;
import com.projectsbynipin.todo_app_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DashboardRepository extends JpaRepository<Dashboard, UUID> {

    @Query("select d from Dashboard d where :user member of d.users and d.archived = false and d.deleted = false order by d.createdAt asc")
    List<Dashboard> findDashboardsByUser(User user);

    @Query("select d from Dashboard d where d.master = :user and d.archived = false and d.deleted = false order by d.createdAt asc")
    List<Dashboard> findDashboardsByMaster(User user);

    @Query("select d from Dashboard d where d.master != :user and :user member of d.users and d.archived = false and d.deleted = false order by d.createdAt asc")
    List<Dashboard> findSharedDashboardsForUser(User user);

    @Query("select d from Dashboard d where d.master = :user and d.archived = true and d.deleted = false order by d.createdAt asc")
    List<Dashboard> findArchivedDashboardsForUser(User user);

    @Query("select d from Dashboard d where d.id = :id and d.deleted = false")
    Dashboard findDashboardById(UUID id);

    @Query("select d from Dashboard d left join d.users where d.id = :id and d.deleted = false")
    Dashboard findDashboardWithUsers(UUID id);

    @Modifying
    @Query("update Dashboard d set d.name = :name where d.id = :id")
    void updateDashboardName(UUID id, String name);
}
