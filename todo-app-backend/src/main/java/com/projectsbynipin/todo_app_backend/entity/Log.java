package com.projectsbynipin.todo_app_backend.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "logs")
public class Log {
    @Id
    private String id;
    @Indexed(name = "idx_username_logs")
    private String username;
    private String exceptionType;
    private String message;
    private String stackTrace;
    private LocalDateTime time;
}
