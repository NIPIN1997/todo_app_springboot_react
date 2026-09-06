package com.projectsbynipin.todo_app_backend.service.logging;

import com.projectsbynipin.todo_app_backend.dto.loggingdtos.ErrorLog;
import com.projectsbynipin.todo_app_backend.dto.loggingdtos.ErrorLogEvent;
import com.projectsbynipin.todo_app_backend.entity.Log;
import com.projectsbynipin.todo_app_backend.repository.LogRepository;
import com.projectsbynipin.todo_app_backend.service.kafka.KafkaProducerService;
import com.projectsbynipin.todo_app_backend.utility.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ErrorEventListener {

    private final KafkaProducerService kafkaProducerService;
    private final LogRepository logRepository;

    @Async
    @EventListener
    public void handleKafkaTransaction(ErrorLogEvent errorLogEvent) {
        ErrorLog errorLog = ErrorLog.builder()
                .exceptionType(errorLogEvent.exceptionType())
                .message(errorLogEvent.message())
                .stackTrace(errorLogEvent.stackTrace())
                .time(errorLogEvent.timeStamp())
                .build();
        kafkaProducerService.sendMessage(Constants.KafkaTopics.ERROR_LOGS, errorLog);
    }

    @Async
    @EventListener
    public void handleMongoDBTransaction(ErrorLogEvent errorLogEvent) {
        final Set<String> allowedExceptions = Set.of(
                "UserEmailAlreadyExistsException",
                "RoleAlreadyExistsException",
                "FailedToSaveUserException",
                "FailedToEditUserException",
                "FailedToCreateDashboardException",
                "FailedToAddTaskException",
                "Exception"
        );
        if (allowedExceptions.contains(errorLogEvent.exceptionType())) {
            logRepository.save(
                    Log.builder()
                            .username(errorLogEvent.username())
                            .exceptionType(errorLogEvent.exceptionType())
                            .message(errorLogEvent.message())
                            .stackTrace(errorLogEvent.stackTrace())
                            .time(errorLogEvent.timeStamp())
                            .build()
            );
        }
    }
}
