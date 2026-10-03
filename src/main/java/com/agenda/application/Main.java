package com.agenda.application;

import com.agenda.event.service.EventService;
import com.agenda.infrastructure.sql.dao.EventSqlDao;
import com.agenda.infrastructure.sql.dao.NoteSqlDao;
import com.agenda.infrastructure.sql.dao.TaskSqlDao;
import com.agenda.task.service.TaskService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        EventSqlDao eventSqlDao = new EventSqlDao();
        TaskSqlDao taskSqlDao = new TaskSqlDao();
        NoteSqlDao noteSqlDao = new NoteSqlDao();

        EventService eventService = new EventService(eventSqlDao);
        TaskService taskService = new TaskService();

    }
}
