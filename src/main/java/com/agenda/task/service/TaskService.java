package com.agenda.task.service;

import com.agenda.common.exception.TaskNotFoundException;
import com.agenda.task.dto.TaskDto;
import com.agenda.task.model.Task;
import com.agenda.task.model.TaskId;
import com.agenda.task.repository.TaskRepository;

import java.util.List;
import java.util.Optional;

public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        if(taskRepository == null) {
            throw new IllegalArgumentException("Repository must not be NULL");
        }
        this.taskRepository = taskRepository;
    }

    public TaskDto create(TaskDto taskDto) {
        if(taskDto == null) {
            throw new IllegalArgumentException("Task DTO must not be NULL");
        }

        Task task = new Task(taskDto.text(), taskDto.expirationDate());

       if(taskDto.priority() != null) {
           task.updateDetails(taskDto.priority());
       }

        Task savedTask = this.taskRepository.save(task);

        return toDto(savedTask);
    }

    public TaskDto update(TaskId id, TaskDto taskDto) {
        checkInputData(id, taskDto);

        Optional<Task>foundTask = this.taskRepository.findById(id);

        if(foundTask.isEmpty()) {
            throw new TaskNotFoundException(String.format("Task with ID %d not found", id.value()));
        }

        Task task = foundTask.get();

        if(taskDto.priority() != null) {
            task.updateDetails(taskDto.text(), taskDto.priority(), taskDto.expirationDate());
        } else {
            task.updateDetails(taskDto.text());
            task.updateDetails(taskDto.expirationDate());
        }

        Task savedTask = this.taskRepository.save(task);
        return toDto(savedTask);
    }

    private static void checkInputData(TaskId id, TaskDto taskDto) {
        if(id == null) {
            throw new IllegalArgumentException("Task ID must not be NULL");
        }

        if(taskDto == null) {
            throw new IllegalArgumentException("Task DTO must not be NULL");
        }
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

