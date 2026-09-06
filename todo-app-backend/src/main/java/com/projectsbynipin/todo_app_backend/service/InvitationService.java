package com.projectsbynipin.todo_app_backend.service;

import com.projectsbynipin.todo_app_backend.dto.AcceptOrRejectInvitationRequestDto;
import com.projectsbynipin.todo_app_backend.dto.ApiResponse;
import com.projectsbynipin.todo_app_backend.dto.ViewInvitationResponseDto;
import com.projectsbynipin.todo_app_backend.service.jwt.UserInfoDetails;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface InvitationService {

    ApiResponse<List<ViewInvitationResponseDto>> getInvitations(UserInfoDetails userInfoDetails);

    void createInvitations(Set<UUID> invitationUserIds, UUID dashboardId);

    ApiResponse<Void> acceptOrRejectInvitation(AcceptOrRejectInvitationRequestDto acceptOrRejectInvitationRequestDto, UserInfoDetails userInfoDetails);
}
