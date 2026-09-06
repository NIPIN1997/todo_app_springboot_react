package com.projectsbynipin.todo_app_backend.dto;

import java.util.UUID;

public record MemberNameIdResponseDto(
        UUID id,
        String name
) {
}
