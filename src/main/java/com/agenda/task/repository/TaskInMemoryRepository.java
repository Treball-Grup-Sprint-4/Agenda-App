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
    public void save(Task task) {
        if(task == null) {
            throw new IllegalArgumentException("Task must not be NULL");
        }
        this.taskList.add(task);
    }
    @Override
    public List<Task> findAll(){
        return List.copyOf(this.taskList);
    }
    @Override
    public Optional<Task> findById(TaskId id) {
        return this
                .findAll()
                .stream()
                .filter(task -> task.getId().equals(id))
                .findFirst();
    }
    @Override
    public boolean deleteById(TaskId id) {
        return this.taskList.removeIf(task -> task.getId().equals(id));
    }
}
