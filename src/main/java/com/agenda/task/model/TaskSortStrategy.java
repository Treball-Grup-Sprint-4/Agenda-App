package com.agenda.task.model;

import java.util.List;

public interface TaskSortStrategy {
    List<Task> sort(List<Task> tasks);
}
