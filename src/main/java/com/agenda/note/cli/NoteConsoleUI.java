package com.agenda.note.cli;

import com.agenda.note.dto.NoteDto;
import com.agenda.note.model.NoteId;
import com.agenda.note.service.NoteService;
import com.agenda.task.model.TaskId;

import java.util.Scanner;
import java.util.List;

public class NoteConsoleUI {
    private final NoteService noteService;
    private final Scanner scanner;

    public NoteConsoleUI(NoteService noteService, Scanner scanner) {
        this.noteService = noteService;
        this.scanner = scanner;
    }

    public void showMenu() {
        System.out.println("""
            
            --- NOTES ---
            1. Create note
            2. Update note
            3. Delete note
            4. List notes
            5. List notes by task
            0. Return to main menu
            """);
    }

    public void createNote() {
        System.out.print("Content: ");
        String content = scanner.nextLine();

        System.out.print("Task ID: ");
        int taskIdValue = Integer.parseInt(scanner.nextLine());

        NoteDto noteDto = new NoteDto(null, content, null, new TaskId(taskIdValue));

        NoteDto createdNote = noteService.create(noteDto);

        System.out.println("Note created successfully. ID: " + createdNote.noteId().value());
    }

    public void updateNote() {
        System.out.print("Note ID: ");
        NoteId noteId = new NoteId(Integer.parseInt(scanner.nextLine()));

        NoteDto currentNote = noteService.findById(noteId);

        System.out.println("Current content: " + currentNote.content());
        System.out.print("New content (press Enter to keep current): ");
        String content = scanner.nextLine();

        if (content.isBlank()) {
            content = currentNote.content();
        }

        System.out.println("Current Task ID: " + currentNote.taskId().value());
        System.out.print("New Task ID (press Enter to keep current): ");
        String taskInput = scanner.nextLine();

        TaskId taskId = currentNote.taskId();

        if (!taskInput.isBlank()) {
            taskId = new TaskId(Integer.parseInt(taskInput));
        }

        NoteDto updatedNote = new NoteDto(currentNote.noteId(), content, currentNote.createdAt(), taskId);

        noteService.update(noteId, updatedNote);

        System.out.println("Note updated successfully.");
    }

    public void deleteNote() {
        System.out.print("Note ID: ");
        NoteId noteId = new NoteId(Integer.parseInt(scanner.nextLine()));

        NoteDto note = noteService.findById(noteId);

        System.out.println("Note: " + note.content());
        System.out.print("Are you sure you want to delete this note? (Y/N): ");
        String confirmation = scanner.nextLine();

        if (!confirmation.equalsIgnoreCase("Y")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        noteService.delete(noteId);

        System.out.println("Note deleted successfully.");
    }

    public void listNotes() {
        List<NoteDto> notes = noteService.findAll();

        if (notes.isEmpty()) {
            System.out.println("No notes found.");
            return;
        }

        notes.forEach(note -> System.out.println("ID: " + note.noteId().value() + "\n Content: " +
                note.content() + "\n Created at: " + note.createdAt() + "\n Task ID: " + note.taskId().value() + "\n"));
    }

    public void listNotesByTask() {
        System.out.print("Task ID: ");
        TaskId taskId = new TaskId(Integer.parseInt(scanner.nextLine()));

        List<NoteDto> notes = noteService.findByTaskId(taskId);

        if (notes.isEmpty()) {
            System.out.println("No notes found for this task.");
            return;
        }

        notes.forEach(note -> System.out.println("ID: " + note.noteId().value() +
                "\nContent: " + note.content() + "\nCreated at: " + note.createdAt() + "\nTask ID: " +
                note.taskId().value() + "\n"));
    }

}
