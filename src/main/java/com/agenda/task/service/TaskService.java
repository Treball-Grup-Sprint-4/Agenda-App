package com.agenda.task.service;

import com.agenda.task.dto.TaskDto;
import com.agenda.task.model.Task;
import com.agenda.task.model.TaskId;
import com.agenda.task.repository.TaskRepository;

import java.util.List;
import java.util.Optional;

public class TaskService {
    TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        if(taskRepository == null) {
            throw new IllegalArgumentException("Repository must not be NULL");
        }
        this.taskRepository = taskRepository;
    }

    public TaskDto create(TaskDto dto) {
        if(dto == null) {
            throw new IllegalArgumentException("Dto must not be NULL");
        }
        Task task = new Task(dto.text(), dto.expirationDate());
        Task savedTask = this.taskRepository.save(task);

        return toDto(savedTask);
    }

    public void update(TaskId id, TaskDto dto) {

    }

    public void delete(TaskId id) {
        this.taskRepository.deleteById(id);
    }

    public void complete(TaskId id) {

        Optional foundTask = this.taskRepository.findById(id);
        Task task;

        if(foundTask.isPresent()) {
            task = (Task)foundTask.get();
            task.markAsCompleted();
        }
        // Need to solve adding back into list
    }

    List<TaskDto> findAll(){
        return null;
    }

    private static TaskDto toDto(Task task) {
        return new TaskDto(
                task.getId(),
                task.getText(),
                task.getPriority(),
                task.getStatus(),
                task.getExpirationDate(),
                task.getCreatedAt(),
                task.getCompletedAt()
        );
    }
}

