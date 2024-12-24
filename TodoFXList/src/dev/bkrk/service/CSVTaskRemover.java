package dev.bkrk.service;

import dev.bkrk.model.Task;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CSVTaskRemover {

    private static final String FILE_NAME = "/resources/tasks.csv";

    public boolean softDeleteTask(Task task) {
        boolean isUpdated = false;
        try {
            List<String> updatedLines = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(getFilePath()));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts[0].equals(task.getId())) {
                    // Set status to 0 (soft delete)
                    parts[parts.length - 1] = "0";
                    isUpdated = true;
                }
                updatedLines.add(String.join(";", parts));
            }
            reader.close();

            // Rewrite the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(getFilePath()));
            for (String updatedLine : updatedLines) {
                writer.write(updatedLine);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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