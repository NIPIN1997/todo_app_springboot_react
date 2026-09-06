package com.projectsbynipin.todo_app_backend.utility;

import com.projectsbynipin.todo_app_backend.entity.Role;
import com.projectsbynipin.todo_app_backend.repository.RoleRepository;
import com.projectsbynipin.todo_app_backend.service.logging.ErrorLoggingService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StartupRunner {

    private final RoleRepository roleRepository;
    private final ErrorLoggingService errorLoggingService;

    @EventListener(ApplicationReadyEvent.class)
    public void createRolesOnStartup() {
        try {
            Set<String> roleNames = Set.of("ROLE_ADMIN", "ROLE_USER");
            Set<String> pendingRoleNames = roleNames.stream().filter(role -> roleRepository.findByName(role) == null).collect(Collectors.toSet());
            List<Role> newRoles = new ArrayList<>();
            for (String roleName : pendingRoleNames) {
                Role role = Role.builder()
                        .name(roleName)
                        .build();
                newRoles.add(role);
            }
            roleRepository.saveAll(newRoles);
        } catch (Exception e) {
            errorLoggingService.log(e);
        }
    }
}
