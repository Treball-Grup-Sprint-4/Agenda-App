package com.agenda.task.service;

import com.agenda.common.exception.TaskNotFoundException;
import com.agenda.task.dto.TaskDto;
import com.agenda.task.model.*;
import com.agenda.task.repository.TaskRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        if(taskRepository == null) {
            throw new IllegalArgumentException("Repository must not be NULL");
        }
        this.taskRepository = taskRepository;
    }

    private static void checkTaskId(TaskId id) {
        if(id == null) {
            throw new IllegalArgumentException("Task ID must not be NULL");
        }
    }

    private static void checkTaskDto(TaskDto taskDto) {
        if(taskDto == null) {
            throw new IllegalArgumentException("Task DTO must not be NULL");
        }
    }

    private Task findTaskOrThrow(TaskId id) {
        return this.taskRepository.findById(id).orElseThrow(() ->
                new TaskNotFoundException(String.format("Task with ID %d not found", id.value())));
    }

    public TaskDto create(TaskDto taskDto) {
        checkTaskDto(taskDto);

        Task task = new Task(taskDto.text(), taskDto.expirationDate());

        if(taskDto.priority() != null) {
            task.updateDetails(taskDto.priority());
        }

        Task savedTask = this.taskRepository.save(task);

        return toDto(savedTask);
    }

    public TaskDto update(TaskId id, TaskDto taskDto) {
        checkUpdateInputData(id, taskDto);

        Task task = findTaskOrThrow(id);

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
        checkTaskId(id);
        checkTaskDto(taskDto);
    }

    public void delete(TaskId id) {
        checkTaskId(id);

        findTaskOrThrow(id);

        this.taskRepository.deleteById(id);
    }

    public void complete(TaskId id) {

        checkTaskId(id);

        Task foundTask = findTaskOrThrow(id);

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

    public TaskDto findById(TaskId id) {
        checkTaskId(id);

        Task task = findTaskOrThrow(id);

        return toDto(task);
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

        if(priority == null) {
            throw new IllegalArgumentException("Priority must not be NULL");
        }
        return this
                .taskRepository
                .findAll()
                .stream()
                .filter(task -> task.getPriority() == priority)
                .map(task -> toDto(task))
                .collect(Collectors.toList());
    }

    public List<TaskDto> filterByStatus(TaskStatus status) {
        if(status == null) {
            throw new IllegalArgumentException("Status must not be NULL");
        }
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
                .filter(task -> date.equals(task.getExpirationDate()))
                .map(task -> toDto(task))
                .collect(Collectors.toList());
    }

    public List<TaskDto> sortTasks(TaskSortStrategy strategy) {
        if(strategy == null) {
            throw new IllegalArgumentException("Strategy must not be NULL");
        }
        List<Task> sortedTasks = strategy.sort(this.taskRepository.findAll());

        return sortedTasks
                .stream()
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

