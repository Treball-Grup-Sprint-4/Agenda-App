package com.agenda.task.model;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DateSortStrategy implements TaskSortStrategy{
    @Override
    public List<Task> sort(List<Task> tasks) {
        return tasks
                .stream()
                .sorted(Comparator.comparing((Task task)-> task.getExpirationDate(),
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }
}
