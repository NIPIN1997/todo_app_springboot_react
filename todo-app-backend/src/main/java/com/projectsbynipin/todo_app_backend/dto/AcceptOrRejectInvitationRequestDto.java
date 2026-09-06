package com.projectsbynipin.todo_app_backend.dto;

import com.projectsbynipin.todo_app_backend.enums.InvitationStatus;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AcceptOrRejectInvitationRequestDto(
        @NotNull
        UUID id,
        @NotNull
        InvitationStatus invitationStatus) {
}
