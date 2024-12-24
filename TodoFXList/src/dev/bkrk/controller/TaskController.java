package dev.bkrk.controller;

import dev.bkrk.model.Task;
import dev.bkrk.service.CSVTaskReader;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class TaskController {

    private final List<Task> tasks;

    // Constructor: upload from CSV.
    public TaskController() {
        this.tasks = CSVTaskReader.readTasks(); // read from CSV.
    }

    public List<Task> getTodayTasks() {
        LocalDate today = LocalDate.now();
        String todayName = today.getDayOfWeek().name();

        return tasks.stream()
                .filter(task -> "1".equals(task.getStatus()))
                .filter(task -> {
                    if ("Continuous".equals(task.getCategory())) {
                        // Day control for continuous tasks
                        return task.getDaysOfWeek() != null &&
                                task.getDaysOfWeek()
                                        .stream()
                                        .anyMatch(day -> day.equalsIgnoreCase(todayName));
                    } else {
                        // Check today's date for other tasks
                        return task.getDeadline() != null && task.getDeadline().isEqual(today);
                    }
                })
                .collect(Collectors.toList());
    }

    public List<Task> getThisMonthTasks() {
        LocalDate today = LocalDate.now();
        return tasks.stream()
                .filter(task -> "1".equals(task.getStatus()))
                .filter(task -> task.getDeadline() != null
                        && task.getDeadline().getMonth().equals(today.getMonth())
                        && task.getDeadline().isAfter(today))
                .collect(Collectors.toList());
    }

    // get with category.
    public List<Task> getTasksByCategory(String category) {
        return tasks.stream()
                .filter(task -> "1".equals(task.getStatus()))
                .filter(task -> task.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public List<Task> getAllTasks() {
        CSVTaskReader reader = new CSVTaskReader();
        return reader.readTasks(); // Reads all tasks from CSV and returns them as a list
    }
}