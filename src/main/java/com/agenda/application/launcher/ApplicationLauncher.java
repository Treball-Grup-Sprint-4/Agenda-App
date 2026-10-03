package com.agenda.application.launcher;

import com.agenda.application.menu.ApplicationMenu;
import com.agenda.application.service.DatabaseCleanupService;
import com.agenda.event.cli.EventConsoleUI;
import com.agenda.event.service.EventService;
import com.agenda.event.service.NotificationService;
import com.agenda.event.service.RecurrenceFactory;
import com.agenda.infrastructure.sql.dao.EventSqlDao;
import com.agenda.infrastructure.sql.dao.NoteSqlDao;
import com.agenda.infrastructure.sql.dao.TaskSqlDao;
import com.agenda.note.cli.NoteConsoleUI;
import com.agenda.note.service.NoteService;
import com.agenda.task.cli.TaskConsoleUI;
import com.agenda.task.service.TaskService;

import java.util.List;
import java.util.Scanner;

public class ApplicationLauncher {

    public void run() {

        try (Scanner scanner = new Scanner(System.in)) {

            TaskSqlDao taskRepository = new TaskSqlDao();
            NoteSqlDao noteRepository = new NoteSqlDao();
            EventSqlDao eventRepository = new EventSqlDao();

            TaskService taskService = new TaskService(taskRepository);
            NoteService noteService = new NoteService(noteRepository, taskRepository);
            EventService eventService = new EventService(eventRepository, taskRepository, new RecurrenceFactory(),
                    List.of(new NotificationService()));
            DatabaseCleanupService databaseCleanupService = new DatabaseCleanupService(noteRepository,
                    taskRepository, eventRepository);

            TaskConsoleUI taskConsoleUI = new TaskConsoleUI(taskService, noteService, scanner);

            NoteConsoleUI noteConsoleUI = new NoteConsoleUI(noteService, scanner);

            EventConsoleUI eventConsoleUI = new EventConsoleUI(eventService, taskService, noteService, scanner);

            ApplicationMenu applicationMenu = new ApplicationMenu(noteConsoleUI, taskConsoleUI, eventConsoleUI,
                    databaseCleanupService, scanner);

            applicationMenu.run();
        }
    }

}
