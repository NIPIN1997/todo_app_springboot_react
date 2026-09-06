package com.projectsbynipin.todo_app_backend.mapper;

import com.projectsbynipin.todo_app_backend.dto.ViewInvitationResponseDto;
import com.projectsbynipin.todo_app_backend.entity.Dashboard;
import com.projectsbynipin.todo_app_backend.entity.Invitation;
import com.projectsbynipin.todo_app_backend.entity.User;
import com.projectsbynipin.todo_app_backend.repository.DashboardRepository;
import com.projectsbynipin.todo_app_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InvitationMapper {

    private final DashboardRepository dashboardRepository;
    private final UserRepository userRepository;

    public List<ViewInvitationResponseDto> invitationToViewInvitationResponseDto(List<Invitation> invitationList) {
        Set<UUID> dashboardIds = invitationList.stream().map(Invitation::getDashboardId).collect(Collectors.toSet());
        List<Dashboard> dashboardList = dashboardRepository.findAllById(dashboardIds);
        Map<UUID, Dashboard> uuidDashboardMap = dashboardList.stream().collect(Collectors.toMap(Dashboard::getId, dashboard -> dashboard));
        Set<UUID> userIds = dashboardList.stream().map(e -> e.getMaster().getId()).collect(Collectors.toSet());
        List<User> userList = userRepository.findAllById(userIds);
        Map<UUID, User> uuidUserMap = userList.stream().collect(Collectors.toMap(User::getId, user -> user));
        Map<UUID, UUID> dashboardMasterMap = dashboardList.stream().collect(Collectors.toMap(Dashboard::getId, dashboard -> dashboard.getMaster().getId()));
        List<ViewInvitationResponseDto> viewInvitationResponseDtos = new ArrayList<>();
        for (Invitation invitation : invitationList) {
            viewInvitationResponseDtos.add(
                    new ViewInvitationResponseDto(
                            invitation.getId(),
                            uuidDashboardMap.get(invitation.getDashboardId()).getName(),
                            uuidUserMap.get(dashboardMasterMap.get(invitation.getDashboardId())).getName()
                    )
            );
        }
        return viewInvitationResponseDtos;
    }
}