package com.agenda.task.service;

import com.agenda.common.exception.TaskNotFoundException;
import com.agenda.task.dto.TaskDto;
import com.agenda.task.model.Task;
import com.agenda.task.model.TaskId;
import com.agenda.task.model.TaskPriority;
import com.agenda.task.model.TaskStatus;
import com.agenda.task.repository.TaskRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        checkUpdateInputData(id, taskDto);

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

    private static void checkUpdateInputData(TaskId id, TaskDto taskDto) {
        if(id == null) {
            throw new IllegalArgumentException("Task ID must not be NULL");
        }

        if(taskDto == null) {
            throw new IllegalArgumentException("Task DTO must not be NULL");
        }
    }

    public void delete(TaskId id) {
        if(id == null) {
            throw new IllegalArgumentException("Task ID must not be NULL");
        }

        Optional<Task> task = this.taskRepository.findById(id);

        if(task.isEmpty()) {
            throw new TaskNotFoundException(String.format("Task with ID %d not found", id.value()));
        }
        this.taskRepository.deleteById(id);
    }

    public void complete(TaskId id) {

        if(id == null) {
            throw new IllegalArgumentException("Task ID must not be NULL");
        }

        Optional<Task> task = this.taskRepository.findById(id);

        if(task.isEmpty()) {
            throw new TaskNotFoundException(String.format("Task with ID %d not found", id.value()));
        }

        Task foundTask = task.get();
        foundTask.markAsCompleted();
        this.taskRepository.save(foundTask);
    }

    public List<TaskDto> findAll() {
        return this
                .taskRepository
                .findAll()
                .stream()
                .map(task -> toDto(task))
                .collect(Collectors.toList());
    }

    public List<TaskDto> findPending() {
        return this
                .taskRepository
                .findAll()
                .stream()
                .filter(task -> task.getStatus() == TaskStatus.PENDING)
                .map(task -> toDto(task))
                .collect(Collectors.toList());
    }

    public List<TaskDto> findCompleted() {
        return this
                .taskRepository
                .findAll()
                .stream()
                .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
                .map(task-> toDto(task))
                .collect(Collectors.toList());
    }

    public List<TaskDto> filterByPriority(TaskPriority priority) {
        return this
                .taskRepository
                .findAll()
                .stream()
                .filter(task -> task.getPriority() == priority)
                .map(task -> toDto(task))
                .collect(Collectors.toList());
    }

    public List<TaskDto> filterByStatus(TaskStatus status) {
        return this
                .taskRepository
                .findAll()
                .stream()
                .filter(task-> task.getStatus() == status)
                .map(task-> toDto(task))
                .collect(Collectors.toList());
    }


    public List<TaskDto> filterByDate(LocalDate date) {
        if(date == null) {
            throw new IllegalArgumentException("Date must not be NULL");
        }
        return this
                .taskRepository
                .findAll()
                .stream()
                .filter(task -> task.getExpirationDate().equals(date))
                .map(task -> toDto(task))
                .collect(Collectors.toList());
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

