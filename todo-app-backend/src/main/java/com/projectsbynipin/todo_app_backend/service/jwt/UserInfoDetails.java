package com.projectsbynipin.todo_app_backend.service.jwt;

import com.projectsbynipin.todo_app_backend.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UserInfoDetails implements UserDetails {

    private final String username;
    private final String password;
    private final List<GrantedAuthority> grantedAuthorities;

    public UserInfoDetails(User user) {
        this.username = user.getEmail();
        this.password = user.getPassword();
        this.grantedAuthorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getName()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
