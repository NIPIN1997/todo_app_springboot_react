package com.projectsbynipin.todo_app_backend.repository;

import com.projectsbynipin.todo_app_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    User findByEmailAndDeleted(String email, boolean deleted);

    @Query("select u from User u where u.email = :username and u.deleted = false")
    User checkUsernameExistence(String username);

    @Query("select u from User u where u.id = :id")
    User findUserById(UUID id);

    @Query("select u from User u where u.email in :members and u.deleted=false")
    List<User> findByEmailInListAndDeleted(List<String> members);
}
