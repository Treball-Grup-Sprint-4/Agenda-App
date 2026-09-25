package com.agenda.task.repository;

import com.agenda.task.model.Task;
import com.agenda.task.model.TaskId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskInMemoryRepository implements TaskRepository {
    private List<Task> taskList;

    public TaskInMemoryRepository() {
        taskList = new ArrayList<>();
    }

    public void save(Task task) {
        if(task == null) {
            throw new IllegalArgumentException("Task must not be NULL");
        }
        this.taskList.add(task);
    }

    public List<Task> findAll(){
        return List.copyOf(this.taskList);
    }

    public Optional<Task> findById(TaskId id) {
        return this
                .findAll()
                .stream()
                .filter(task -> task.getId().equals(id))
                .findFirst();
    }

    public void deleteById(TaskId id) {
        int i = 0;
        boolean found = false;
        while(i < this.taskList.size() && !found) {
            if(this.taskList.get(i).getId().equals(id)) {
                found = true;
                this.taskList.remove(i);
            }
            i++;
        }
    }
}
