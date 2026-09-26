package com.agenda.task.repository;

import com.agenda.task.model.Task;
import com.agenda.task.model.TaskId;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    Task save(Task task);

    Optional<Task> findById(TaskId taskId);

    List<Task> findAll();

    void deleteById(TaskId taskId);
}
