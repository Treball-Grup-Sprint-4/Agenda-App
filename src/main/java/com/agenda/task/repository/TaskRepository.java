package com.agenda.task.repository;

import com.agenda.task.model.Task;
import com.agenda.task.model.TaskId;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    void save(Task task);
    List<Task> findAll();
    Optional<Task> findById(TaskId id);
    boolean deleteById(TaskId id);
}
