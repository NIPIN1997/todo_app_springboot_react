package com.projectsbynipin.todo_app_backend.service.impl;

import com.projectsbynipin.todo_app_backend.dto.AcceptOrRejectInvitationRequestDto;
import com.projectsbynipin.todo_app_backend.dto.ApiResponse;
import com.projectsbynipin.todo_app_backend.dto.ViewInvitationResponseDto;
import com.projectsbynipin.todo_app_backend.entity.Dashboard;
import com.projectsbynipin.todo_app_backend.entity.Invitation;
import com.projectsbynipin.todo_app_backend.entity.User;
import com.projectsbynipin.todo_app_backend.enums.InvitationStatus;
import com.projectsbynipin.todo_app_backend.exception.*;
import com.projectsbynipin.todo_app_backend.mapper.InvitationMapper;
import com.projectsbynipin.todo_app_backend.repository.DashboardRepository;
import com.projectsbynipin.todo_app_backend.repository.InvitationRepository;
import com.projectsbynipin.todo_app_backend.repository.UserRepository;
import com.projectsbynipin.todo_app_backend.service.InvitationService;
import com.projectsbynipin.todo_app_backend.service.jwt.UserInfoDetails;
import com.projectsbynipin.todo_app_backend.utility.ApiResponseCreator;
import com.projectsbynipin.todo_app_backend.utility.Constants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {

    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final InvitationMapper invitationMapper;
    private final DashboardRepository dashboardRepository;

    private User findUserByEmailAndDeleted(String email) {
        User user = userRepository.findByEmailAndDeleted(email, false);
        if (user == null) {
            throw new UserNotFoundException(Constants.User.USER_NOT_FOUND);
        }
        return user;
    }

    @Override
    public ApiResponse<List<ViewInvitationResponseDto>> getInvitations(UserInfoDetails userInfoDetails) {
        User user = findUserByEmailAndDeleted(userInfoDetails.getUsername());
        try {
            List<Invitation> invitationList = invitationRepository.getAllPendingInvitationsForUser(user.getId());
            List<ViewInvitationResponseDto> viewInvitationResponseDtoList = invitationMapper.invitationToViewInvitationResponseDto(invitationList);
            return ApiResponseCreator.success(Constants.Invitation.INVITATIONS_FETCHED, viewInvitationResponseDtoList, HttpStatus.OK);
        } catch (Exception e) {
            throw new FailedToFetchInvitationsException(Constants.Invitation.FAILED_TO_FETCH_INVITATIONS, e);
        }

    }

    @Override
    public void createInvitations(Set<UUID> invitationUserIds, UUID dashboardId) {
        List<Invitation> invitations = new ArrayList<>();
        for (UUID invitationUserId : invitationUserIds) {
            Invitation invitation = new Invitation();
            invitation.setUserId(invitationUserId);
            invitation.setDashboardId(dashboardId);
            invitations.add(invitation);
        }
        try {
            invitationRepository.saveAll(invitations);
        } catch (Exception e) {
            throw new FailedToCreateInvitationException(Constants.Invitation.FAILED_TO_CREATE_INVITATION, e);
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> acceptOrRejectInvitation(AcceptOrRejectInvitationRequestDto acceptOrRejectInvitationRequestDto, UserInfoDetails userInfoDetails) {
        try {
            Invitation invitation = invitationRepository.findById(acceptOrRejectInvitationRequestDto.id()).orElseThrow(() -> new InvitationNotFoundException(Constants.Invitation.INVITATION_NOT_FOUND));
            User user = findUserByEmailAndDeleted(userInfoDetails.getUsername());
            if (!invitation.getUserId().equals(user.getId())) {
                throw new AccessDeniedException(Constants.Miscellaneous.ACCESS_DENIED);
            }
            if (acceptOrRejectInvitationRequestDto.invitationStatus().equals(InvitationStatus.ACCEPTED)) {
                invitation.setInvitationStatus(InvitationStatus.ACCEPTED);
                invitationRepository.save(invitation);
                Dashboard dashboard = dashboardRepository.findById(invitation.getDashboardId()).orElseThrow(() -> new DashboardNotFoundException(Constants.Dashboard.DASHBOARD_NOT_FOUND));
                Set<User> users = dashboard.getUsers();
                Set<UUID> userIds = users.stream().map(User::getId).collect(Collectors.toSet());
                if (!userIds.contains(user.getId())) {
                    users.add(user);
                }
                dashboard.setUsers(users);
                dashboardRepository.save(dashboard);
                return ApiResponseCreator.success(Constants.Invitation.INVITATION_ACCEPTED, HttpStatus.OK);
            } else if (acceptOrRejectInvitationRequestDto.invitationStatus().equals(InvitationStatus.REJECTED)) {
                invitation.setInvitationStatus(InvitationStatus.REJECTED);
                invitationRepository.save(invitation);
                return ApiResponseCreator.success(Constants.Invitation.INVITATION_REJECTED, HttpStatus.OK);
            } else {
                throw new FailedToAcceptOrRejectInvitationException(Constants.Invitation.FAILED_TO_ACCEPT_OR_REJECT_INVITATION);
            }
        } catch (Exception e) {
            throw new FailedToAcceptOrRejectInvitationException(Constants.Invitation.FAILED_TO_ACCEPT_OR_REJECT_INVITATION, e);
        }
    }
}
