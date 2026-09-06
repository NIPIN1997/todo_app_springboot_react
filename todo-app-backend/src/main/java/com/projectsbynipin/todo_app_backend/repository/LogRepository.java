package com.projectsbynipin.todo_app_backend.repository;

import com.projectsbynipin.todo_app_backend.entity.Log;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends MongoRepository<Log, String> {
}
