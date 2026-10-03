package com.agenda.application;

import com.agenda.application.menu.ApplicationMenu;
import com.agenda.event.cli.EventConsoleUI;
import com.agenda.event.repository.EventRepository;
import com.agenda.event.service.EventService;
import com.agenda.event.service.NotificationService;
import com.agenda.event.service.RecurrenceFactory;
import com.agenda.infrastructure.sql.dao.EventSqlDao;
import com.agenda.infrastructure.sql.dao.NoteSqlDao;
import com.agenda.infrastructure.sql.dao.TaskSqlDao;
import com.agenda.note.cli.NoteConsoleUI;
import com.agenda.note.repository.NoteRepository;
import com.agenda.note.service.NoteService;
import com.agenda.task.cli.TaskConsoleUI;
import com.agenda.task.repository.TaskRepository;
import com.agenda.task.service.TaskService;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        EventRepository eventSqlDao = new EventSqlDao();
        TaskRepository taskSqlDao = new TaskSqlDao();
        NoteRepository noteSqlDao = new NoteSqlDao();

        EventService eventService = new EventService(
                eventSqlDao,
                taskSqlDao,
                new RecurrenceFactory(),
                List.of(new NotificationService()));
        TaskService taskService = new TaskService(taskSqlDao);
        NoteService noteService = new NoteService(noteSqlDao, taskSqlDao);

            EventConsoleUI eventConsoleUI = new EventConsoleUI(eventService, scanner);
            TaskConsoleUI taskConsoleUI = new TaskConsoleUI(taskService, scanner);
            NoteConsoleUI noteConsoleUI = new NoteConsoleUI(noteService, scanner);

            ApplicationMenu.displayAppMenu();
            
    }
}
