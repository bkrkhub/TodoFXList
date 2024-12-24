package dev.bkrk.service;

import dev.bkrk.model.Task;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CSVTaskReader {

    private static final String FILE_NAME = "/resources/tasks.csv";

    public static List<Task> readTasks() {
        List<Task> tasks = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (BufferedReader reader = new BufferedReader(new FileReader(getFilePath()))) {
            String line = reader.readLine(); // skip the Header
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(";");

                if (values.length < 7) {
                    System.err.println("Error parsing line: " + Arrays.toString(values));
                    continue;
                }

                String id = values[0]; // ID
                String shortDescription = values[1];
                String details = values[2];
                String category = values[4];
                String status = values[6];

                if ("Continuous".equals(category)) {
                    // Continuous tasks
                    List<String> daysOfWeek = Arrays.asList(values[5].split(","));
                    tasks.add(new Task(id, shortDescription, details, null, category, daysOfWeek, status));
                } else {
                    // Other tasks
                    try {
                        LocalDate deadline = LocalDate.parse(values[3], formatter);
                        tasks.add(new Task(id, shortDescription, details, deadline, category, null, status));
                    } catch (Exception e) {
                        System.err.println("Error parsing deadline: " + Arrays.toString(values));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tasks;
    }

    private static String getFilePath() {
        try {
            return Objects.requireNonNull(CSVTaskReader.class.getResource(FILE_NAME)).getPath();
        } catch (NullPointerException e) {
            throw new RuntimeException("File not found: " + FILE_NAME);
        }
    }
}