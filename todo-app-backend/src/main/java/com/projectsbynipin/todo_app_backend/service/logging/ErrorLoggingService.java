package com.projectsbynipin.todo_app_backend.service.logging;

import com.projectsbynipin.todo_app_backend.dto.loggingdtos.ErrorLogEvent;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ErrorLoggingService {

    private final ApplicationEventPublisher applicationEventPublisher;

    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return authentication.getName();
    }

    public void log(Exception exception) {
        applicationEventPublisher.publishEvent(
                new ErrorLogEvent(
                        getAuthenticatedUsername(),
                        exception.getClass().getSimpleName(),
                        exception.getMessage(),
                        ExceptionUtils.getStackTrace(exception),
                        LocalDateTime.now()
                )
        );
    }
}
