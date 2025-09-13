package com.projectsbynipin.todo_app_backend.mapper;

import com.projectsbynipin.todo_app_backend.dto.AddUserRequestDto;
import com.projectsbynipin.todo_app_backend.dto.ViewUserResponseDto;
import com.projectsbynipin.todo_app_backend.entity.Role;
import com.projectsbynipin.todo_app_backend.entity.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public User addUserRequestDtoToUser(AddUserRequestDto addUserRequestDto, Role role) {
        User user = new User();
        user.setName(addUserRequestDto.name());
        user.setEmail(addUserRequestDto.email());
        user.setContact(addUserRequestDto.contact());
        user.setRole(role);
        user.setPassword(bCryptPasswordEncoder.encode(addUserRequestDto.password()));
        return user;
    }

    public ViewUserResponseDto userToViewUserResponseDto(User user) {
        return new ViewUserResponseDto(
                user.getName(),
                user.getEmail(),
                user.getContact()
        );
    }
}
