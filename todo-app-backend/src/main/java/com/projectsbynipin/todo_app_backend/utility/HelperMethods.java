package com.projectsbynipin.todo_app_backend.utility;

import com.projectsbynipin.todo_app_backend.entity.Task;
import com.projectsbynipin.todo_app_backend.enums.TaskStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class HelperMethods {

    public TaskStatus getTaskStatus(long maxPosition, Task task) {
        LocalDate today = LocalDate.now();
        long currentPosition = task.getColumn().getPosition();
        LocalDate dueDate = task.getDueDate();
        if (currentPosition == maxPosition) {
            LocalDate completedDate = task.getCompletedDate();
            if (completedDate.equals(dueDate) || completedDate.isBefore(dueDate)) {
                return TaskStatus.COMPLETED_ON_TIME;
            }
            else{
                return TaskStatus.COMPLETED_LATE;
            }
        } else {
            if (today.isBefore(dueDate)) {
                return TaskStatus.HAVE_TIME;
            } else if (today.isAfter(dueDate)) {
                return TaskStatus.DUE;
            } else {
                return TaskStatus.DUE_TODAY;
            }
        }
    }
}
