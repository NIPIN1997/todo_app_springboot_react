package com.projectsbynipin.todo_app_backend.dto.loggingdtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.projectsbynipin.todo_app_backend.enums.LoginLogout;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginLogoutActivityLog {
    private String username;
    private LoginLogout action;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime time;
    private String browserName;
}
