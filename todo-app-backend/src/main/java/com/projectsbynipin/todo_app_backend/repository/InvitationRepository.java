package com.projectsbynipin.todo_app_backend.repository;

import com.projectsbynipin.todo_app_backend.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, UUID> {

    @Query("select i from Invitation i where i.userId = :id and i.invitationStatus = 'PENDING'")
    List<Invitation> getAllPendingInvitationsForUser(UUID id);

    @Modifying
    @Query("delete from Invitation i where i.dashboardId = :id and i.invitationStatus = 'PENDING'")
    void deletePendingInvitations(UUID id);
}
