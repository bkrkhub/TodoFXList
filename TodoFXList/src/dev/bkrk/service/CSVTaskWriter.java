package dev.bkrk.service;

import dev.bkrk.model.Task;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class CSVTaskWriter {

    private static final String FILE_PATH = "resources/tasks.csv";
    public void addTask(Task task) {

        // Create a new ID if no ID is assigned
        if (task.getId() == null || task.getId().isEmpty()) {
            String newId = generateNewId();
            task.setId(newId);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getFilePath(), true))) {
            // Write the task in CSV format
            writer.write(task.toCSV());
            // Move to new line
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateNewId() {
        CSVTaskReader reader = new CSVTaskReader();
        List<Task> tasks = reader.readTasks();
        // Find the highest available ID
        int maxId = 0;
        for (Task task : tasks) {
            try {
                int currentId = Integer.parseInt(task.getId());
                if (currentId > maxId) {
                    maxId = currentId;
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid ID format: " + task.getId());
            }
        }
        // Return the new ID
        return String.valueOf(maxId + 1);
    }

    private String getFilePath() {
        try {
            return Objects.requireNonNull(getClass().getResource(FILE_PATH)).getPath();
        } catch (NullPointerException e) {
            throw new RuntimeException("File not found: " + FILE_PATH);
        }
    }
}