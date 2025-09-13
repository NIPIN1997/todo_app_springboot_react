package com.projectsbynipin.todo_app_backend.repository;

import com.projectsbynipin.todo_app_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    User findByEmailAndIsDeleted(String email, boolean isDeleted);
}
