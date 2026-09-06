package com.projectsbynipin.todo_app_backend.service.jwt;

import com.projectsbynipin.todo_app_backend.entity.User;
import com.projectsbynipin.todo_app_backend.repository.UserRepository;
import com.projectsbynipin.todo_app_backend.utility.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInfoService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndDeleted(username, false);
        if (user == null) {
            throw new UsernameNotFoundException(Constants.User.USER_NOT_FOUND);
        }
        return new UserInfoDetails(user);
    }
}
