package dev.bkrk.service;

import dev.bkrk.model.Task;

import java.io.*;
import java.util.Objects;

public class CSVTaskUpdater {

    private static final String FILE_NAME = "/resources/tasks.csv";

    public boolean updateTask(Task updatedTask) {
        boolean isUpdated = false;

        // Store file content
        StringBuilder fileContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(getFilePath()))) {
            // Read the header.
            String line = reader.readLine();
            if (line != null) {
                // Protect Header
                fileContent.append(line).append("\n");
            }

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(";");
                // Does the ID in the row match the ID that needs to be updated?
                if (values[0].equals(updatedTask.getId())) {
                    // Write the updated task.
                    fileContent.append(updatedTask.toCSV());
                    isUpdated = true;
                } else {
                    // Insert other lines as is
                    fileContent.append(line);
                }
                // Insert line break
                fileContent.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Write updated content to file
        if (isUpdated) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(getFilePath()))) {
                writer.write(fileContent.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Return whether the update was successful
        return isUpdated;
    }

    private String getFilePath() {
        try {
            return Objects.requireNonNull(getClass().getResource(FILE_NAME)).getPath();
        } catch (NullPointerException e) {
            throw new RuntimeException("File not found: " + FILE_NAME);
        }
    }
}