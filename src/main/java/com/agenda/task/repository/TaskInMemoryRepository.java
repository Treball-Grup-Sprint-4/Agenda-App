package com.agenda.task.repository;

import com.agenda.task.model.Task;
import com.agenda.task.model.TaskId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskInMemoryRepository implements TaskRepository {
    private final List<Task> taskList;

    public TaskInMemoryRepository() {
        taskList = new ArrayList<>();
    }

    @Override
    public Task save(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task must not be NULL");
        }

        int pos = this.taskList.indexOf(task);
        if(pos >= 0) {
            this.taskList.set(pos, task);
        } else {
            this.taskList.add(task);
        }
        return task;
    }

    @Override
    public List<Task> findAll() {
        return List.copyOf(this.taskList);
    }

    @Override
    public Optional<Task> findById(TaskId id) {
        if(id == null) {
            throw new IllegalArgumentException("Task ID must not be NULL");
        }

        return this.taskList
                .stream()
                .filter(task -> id.equals(task.getId()))
                .findFirst();
    }

    @Override
    public boolean deleteById(TaskId id) {
        if(id == null) {
            throw new IllegalArgumentException("Task ID must not be NULL");
        }
        return this.taskList.removeIf(task -> id.equals(task.getId()));
    }
}
