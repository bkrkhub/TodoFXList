package dev.bkrk.model;

import java.time.LocalDate;
import java.util.List;

public class Task {

    private String id; // Unique Id
    private String shortDescription;
    private String details;
    private LocalDate deadLine;
    private String category;
    private List<String> daysOfWeek;
    private String status; // Status (1 = active, 0 = removed)

    // Constructor for non-continuous tasks
    public Task(String id, String shortDescription, String details, LocalDate deadLine, String category) {
        this(id, shortDescription, details, deadLine, category, null, "1");
    }

    // Constructor for continuous tasks
    public Task(String id, String shortDescription, String details, List<String> daysOfWeek, String category) {
        this(id, shortDescription, details, null, category, daysOfWeek, "1");
    }

    // Public constructor (special use)
    public Task(String id, String shortDescription, String details,
                LocalDate deadLine, String category, List<String> daysOfWeek, String status) {
        this.id = id;
        this.shortDescription = shortDescription;
        this.details = details;
        this.deadLine = deadLine;
        this.category = category;
        this.daysOfWeek = daysOfWeek;
        this.status = status;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getDetails() {
        return details;
    }

    public LocalDate getDeadline() {
        return deadLine;
    }

    public String getCategory() {
        return category;
    }

    public List<String> getDaysOfWeek() {
        return daysOfWeek;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setDeadline(LocalDate deadLine) {
        this.deadLine = deadLine;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDaysOfWeek(List<String> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return id + " - " + shortDescription + " - " + details + " - " +
                deadLine + " - " + category + " - " + daysOfWeek + " - " + status;
    }

    // Return data in CSV format
    public String toCSV() {
        if ("Continuous".equals(category)) {
            return id + ";" + shortDescription + ";" + details + ";null;" +
                    category + ";" + String.join(",", daysOfWeek) + ";" + status;
        } else {
            return id + ";" + shortDescription + ";" +
                    details + ";" + deadLine + ";" + category + ";" + ";" + status;
        }
    }
}