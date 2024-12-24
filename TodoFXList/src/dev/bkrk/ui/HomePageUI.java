package dev.bkrk.ui;

import dev.bkrk.controller.DraggableWindowController;
import dev.bkrk.controller.TaskController;
import dev.bkrk.model.Task;
import dev.bkrk.service.CSVTaskRemover;
import dev.bkrk.service.CSVTaskUpdater;
import dev.bkrk.service.CSVTaskWriter;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class HomePageUI extends Application {

    private BorderPane mainContent;
    private HBox selectedMenuItem;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("TodoFXList");

        // Window Drag Variables
        final double[] offsetX = {0};
        final double[] offsetY = {0};

        // Control Bar
        HBox controlBar = new HBox(20);
        controlBar.setPadding(new Insets(5));
        controlBar.getStyleClass().add("control-bar");

        Button minimizeButton = new Button();
        minimizeButton.setGraphic(new ImageView(new Image(
                Objects.requireNonNull(getClass().getResource("/resources/images/minimize.png")).toExternalForm())));
        minimizeButton.getStyleClass().add("minimize-button");
        minimizeButton.setOnAction(e -> primaryStage.setIconified(true));

        Button closeButton = new Button();
        closeButton.setGraphic(new ImageView(new Image(
                Objects.requireNonNull(getClass().getResource("/resources/images/exit.png")).toExternalForm())));
        closeButton.getStyleClass().add("close-button");
        closeButton.setOnAction(e -> primaryStage.close());

        controlBar.getChildren().addAll(minimizeButton, closeButton);

        // Left Menu
        VBox leftMenu = createLeftMenu();

        // Main Content
        mainContent = new BorderPane();
        mainContent.setStyle("-fx-background-color: #FFFFFF;");

        // Root Layout
        BorderPane root = new BorderPane();
        root.setTop(controlBar);
        root.setLeft(leftMenu);
        root.setCenter(mainContent);

        // Draggable Window Controller
        DraggableWindowController dragController = new DraggableWindowController();
        dragController.enableDragging(root, primaryStage);

        // Scene
        Scene scene = new Scene(root, 1000, 800);
        scene.getStylesheets().add(Objects.requireNonNull(getClass()
                .getResource("/resources/css/HomePageUI.css")).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

        // Default: Today Page
        selectMenuItem((HBox) leftMenu.getChildren().get(0));
        showTasks("Today");
    }

    private VBox createLeftMenu() {
        VBox leftMenu = new VBox(15);
        leftMenu.setPadding(new Insets(50, 10, 10, 10));
        leftMenu.getStyleClass().add("left-menu");

        // Menu Buttons.
        HBox today = createMenuItem("Today", "/resources/images/today.png");
        today.setOnMouseClicked(event -> {
            selectMenuItem(today);
            showTasks("Today");
        });

        HBox continuous = createMenuItem("Continuous", "/resources/images/continuous.png");
        continuous.setOnMouseClicked(event -> {
            selectMenuItem(continuous);
            showTasks("Continuous");
        });

        HBox thisMonth = createMenuItem("This Month", "/resources/images/month.png");
        thisMonth.setOnMouseClicked(event -> {
            selectMenuItem(thisMonth);
            showTasks("This Month");
        });

        HBox important = createMenuItem("Important", "/resources/images/important1.png");
        important.setOnMouseClicked(event -> {
            selectMenuItem(important);
            showTasks("Important");
        });

        HBox pending = createMenuItem("Pending", "/resources/images/pending.png");
        pending.setOnMouseClicked(event -> {
            selectMenuItem(pending);
            showTasks("Pending");
        });

        HBox inProgress = createMenuItem("InProgress", "/resources/images/work-in-progress.png");
        inProgress.setOnMouseClicked(event -> {
            selectMenuItem(inProgress);
            showTasks("InProgress");
        });

        HBox completed = createMenuItem("Completed", "/resources/images/done.png");
        completed.setOnMouseClicked(event -> {
            selectMenuItem(completed);
            showTasks("Completed");
        });

        // We fix the “+” button at the bottom by adding a spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS); // Spacer grows and fills the space


        // Add Task Button (+ Button)
        Button addButton = new Button("+");
        addButton.getStyleClass().add("add-button");
        addButton.setOnAction(event -> openAddTaskDialog());

        leftMenu.getChildren().addAll(today, continuous, thisMonth, important, pending, inProgress, completed, spacer, addButton);
        return leftMenu;
    }

    private HBox createMenuItem(String text, String imagePath) {
        ImageView icon = new ImageView(new Image(imagePath));
        icon.setFitWidth(35);
        icon.setFitHeight(35);

        Label label = new Label(text);
        label.setFont(Font.font("Arial", 16));

        HBox container = new HBox(10);
        container.getChildren().addAll(icon, label);
        container.setPadding(new Insets(5));
        container.getStyleClass().add("menu-item");
        return container;
    }

    private void openAddTaskDialog() {
        // New stage for adding tasks.
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Here is a New Challenge For you !");

        // GridPane Layout
        GridPane form = new GridPane();
        form.setHgap(10); // Horizontal gap.
        form.setVgap(15); // Vertical gap.
        form.setPadding(new Insets(20)); // margins.

        // Short Description
        Label shortDescriptionLabel = new Label("Headline:");
        TextField shortDescriptionField = new TextField();
        shortDescriptionField.setPromptText("Enter a Headline with a maximum of 25 characters.");
        shortDescriptionField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 25) {
                shortDescriptionField.setText(oldValue);
            }
        });
        form.add(shortDescriptionLabel, 0, 0);
        form.add(shortDescriptionField, 1, 0);

        // Details
        Label detailsLabel = new Label("Details:");
        TextArea detailsField = new TextArea();
        detailsField.setPromptText("Enter a Detail with a maximum of 250 characters.");

        detailsField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 250) {
                detailsField.setText(oldValue);
            }
        });
        form.add(detailsLabel, 0, 1);
        form.add(detailsField, 1, 1);

        // Deadline
        Label deadlineLabel = new Label("Deadline:");
        DatePicker deadlinePicker = new DatePicker(LocalDate.now());
        deadlinePicker.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.isBefore(LocalDate.now())) {
                    setDisable(true); // Disable old dates
                    // Add style to old dates
                    setStyle("-fx-background-color: #EEEEEE;");
                }
            }
        });
        form.add(deadlineLabel, 0, 2);
        form.add(deadlinePicker, 1, 2);

        // Category is added below the ComboBox
        Label daysOfWeekLabel = new Label("Days Of Week:");
        ComboBox<String> daysOfWeekComboBox = new ComboBox<>();
        daysOfWeekComboBox.getItems().addAll("Monday", "Tuesday", "Wednesday",
                "Thursday", "Friday", "Saturday", "Sunday");
        daysOfWeekComboBox.setDisable(true);

        // Days of Week CheckBoxes
        CheckBox mondayCheckBox = new CheckBox("Monday");
        CheckBox tuesdayCheckBox = new CheckBox("Tuesday");
        CheckBox wednesdayCheckBox = new CheckBox("Wednesday");
        CheckBox thursdayCheckBox = new CheckBox("Thursday");
        CheckBox fridayCheckBox = new CheckBox("Friday");
        CheckBox saturdayCheckBox = new CheckBox("Saturday");
        CheckBox sundayCheckBox = new CheckBox("Sunday");

        // Placing checkboxes side by side using HBox
        HBox daysOfWeekBox = new HBox(10); // 10px margin.
        daysOfWeekBox.getChildren().addAll(
                mondayCheckBox,
                tuesdayCheckBox,
                wednesdayCheckBox,
                thursdayCheckBox,
                fridayCheckBox,
                saturdayCheckBox,
                sundayCheckBox
        );

        // add to form.
        form.add(daysOfWeekLabel, 0, 5);
        form.add(daysOfWeekBox, 1, 5);

        // Category
        Label categoryLabel = new Label("Category:");
        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll("Important", "InProgress", "Pending", "Completed", "Continuous");
        form.add(categoryLabel, 0, 3);
        form.add(categoryComboBox, 1, 3);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 20, 20, 0));
        Button submitButton = new Button("OK");
        Button cancelButton = new Button("Cancel");
        submitButton.getStyleClass().add("button-ok");
        cancelButton.getStyleClass().add("button-cancel");
        buttonBox.getChildren().addAll(submitButton, cancelButton);

        // Align buttons to the bottom right corner.
        BorderPane buttonPane = new BorderPane();
        buttonPane.setRight(buttonBox);
        BorderPane.setMargin(buttonBox, new Insets(-5, 5, 0, -10));

        submitButton.setOnAction(e -> {
            String shortDescription = shortDescriptionField.getText();
            String details = detailsField.getText();
            String category = categoryComboBox.getValue();

            LocalDate deadline = null;
            if (!"Continuous".equals(category)) {
                deadline = deadlinePicker.getValue();
            }

            List<String> selectedDays = new ArrayList<>();
            if ("Continuous".equals(category)) {
                if (mondayCheckBox.isSelected()) selectedDays.add("Monday");
                if (tuesdayCheckBox.isSelected()) selectedDays.add("Tuesday");
                if (wednesdayCheckBox.isSelected()) selectedDays.add("Wednesday");
                if (thursdayCheckBox.isSelected()) selectedDays.add("Thursday");
                if (fridayCheckBox.isSelected()) selectedDays.add("Friday");
                if (saturdayCheckBox.isSelected()) selectedDays.add("Saturday");
                if (sundayCheckBox.isSelected()) selectedDays.add("Sunday");
            }

            if (shortDescription.isEmpty() || details.isEmpty() || category == null ||
                    (!"Continuous".equals(category) && deadline == null) ||
                    ("Continuous".equals(category) && selectedDays.isEmpty())) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Invalid Input");
                alert.setHeaderText("Missing Required Fields");
                alert.setContentText("Please fill in all fields before submitting.");
                alert.showAndWait();
            } else {
                Task newTask;
                if ("Continuous".equals(category)) {
                    newTask = new Task(null, shortDescription, details, selectedDays, category);
                } else {
                    newTask = new Task(null, shortDescription, details, deadline, category);
                }

                CSVTaskWriter writer = new CSVTaskWriter();
                writer.addTask(newTask);

                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Task Added");
                success.setHeaderText(null);
                success.setContentText("Task successfully added!");
                success.showAndWait();

                showTasks("Today");
                dialog.close();
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        // Show Days of Week field when Continuous category is selected
        categoryComboBox.setOnAction(event -> {
            String selectedCategory = categoryComboBox.getValue();
            if ("Continuous".equals(selectedCategory)) {
                deadlinePicker.setDisable(true);
                mondayCheckBox.setDisable(false);
                tuesdayCheckBox.setDisable(false);
                wednesdayCheckBox.setDisable(false);
                thursdayCheckBox.setDisable(false);
                fridayCheckBox.setDisable(false);
                saturdayCheckBox.setDisable(false);
                sundayCheckBox.setDisable(false);
            } else {
                deadlinePicker.setDisable(false);
                mondayCheckBox.setDisable(true);
                tuesdayCheckBox.setDisable(true);
                wednesdayCheckBox.setDisable(true);
                thursdayCheckBox.setDisable(true);
                fridayCheckBox.setDisable(true);
                saturdayCheckBox.setDisable(true);
                sundayCheckBox.setDisable(true);
            }
        });

        // Root Layout
        BorderPane root = new BorderPane();
        root.setCenter(form);
        root.setBottom(buttonPane);

        // Dialog Scene
        Scene dialogScene = new Scene(root, 700, 450);
        dialogScene.getStylesheets().add(Objects.requireNonNull(getClass()
                .getResource("/resources/css/HomePageUI.css")).toExternalForm());
        dialog.setScene(dialogScene);
        // Clear default focus (no headline selected)
        dialogScene.getRoot().requestFocus();
        // Show Dialog.
        dialog.showAndWait();
    }

    private void openReviewDialog(Task task) {
        // New stage for reviewing tasks.
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Review Task");

        // GridPane Layout
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(15);
        form.setPadding(new Insets(20));

        // Short Description.
        Label shortDescLabel = new Label("Headline:");
        TextField shortDescField = new TextField(task.getShortDescription());
        shortDescField.setEditable(false);
        form.add(shortDescLabel, 0, 1);
        form.add(shortDescField, 1, 1);

        // Details.
        Label detailsLabel = new Label("Details:");
        TextArea detailsField = new TextArea(task.getDetails());
        detailsField.setEditable(false);
        form.add(detailsLabel, 0, 2);
        form.add(detailsField, 1, 2);

        // Deadline.
        Label deadlineLabel = new Label("Deadline");
        DatePicker deadlinePicker = new DatePicker(task.getDeadline());
        deadlinePicker.setEditable(false);
        form.add(deadlineLabel, 0, 3);
        form.add(deadlinePicker, 1, 3);

        // Category.
        Label categoryLabel = new Label("Category:");
        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll("Important", "InProgress", "Pending", "Completed", "Continuous");
        categoryComboBox.setValue(task.getCategory());
        categoryComboBox.setEditable(false);
        form.add(categoryLabel, 0, 4);
        form.add(categoryComboBox, 1, 4);

        // Days Of Weeks.
        Label daysOfWeekLabel = new Label("Days Of Week:");
        HBox daysOfWeekBox = new HBox(10);
        CheckBox mondayCheckBox = new CheckBox("Monday");
        CheckBox tuesdayCheckBox = new CheckBox("Tuesday");
        CheckBox wednesdayCheckBox = new CheckBox("Wednesday");
        CheckBox thursdayCheckBox = new CheckBox("Thursday");
        CheckBox fridayCheckBox = new CheckBox("Friday");
        CheckBox saturdayCheckBox = new CheckBox("Saturday");
        CheckBox sundayCheckBox = new CheckBox("Sunday");

        daysOfWeekBox.getChildren().addAll(
                mondayCheckBox,
                tuesdayCheckBox,
                wednesdayCheckBox,
                thursdayCheckBox,
                fridayCheckBox,
                saturdayCheckBox,
                sundayCheckBox
        );

        if ("Continuous".equals(task.getCategory())) {
            // Marking days in continuous tasks
            for (String day : task.getDaysOfWeek()) {
                switch (day.trim().toUpperCase()) {
                    case "MONDAY":
                        mondayCheckBox.setSelected(true);
                        break;
                    case "TUESDAY":
                        tuesdayCheckBox.setSelected(true);
                        break;
                    case "WEDNESDAY":
                        wednesdayCheckBox.setSelected(true);
                        break;
                    case "THURSDAY":
                        thursdayCheckBox.setSelected(true);
                        break;
                    case "FRIDAY":
                        fridayCheckBox.setSelected(true);
                        break;
                    case "SATURDAY":
                        saturdayCheckBox.setSelected(true);
                        break;
                    case "SUNDAY":
                        sundayCheckBox.setSelected(true);
                        break;
                }
            }

            // Disable checkboxes
            mondayCheckBox.setDisable(true);
            tuesdayCheckBox.setDisable(true);
            wednesdayCheckBox.setDisable(true);
            thursdayCheckBox.setDisable(true);
            fridayCheckBox.setDisable(true);
            saturdayCheckBox.setDisable(true);
            sundayCheckBox.setDisable(true);
        } else {
            // Checkboxes disabled in other categories
            mondayCheckBox.setDisable(true);
            tuesdayCheckBox.setDisable(true);
            wednesdayCheckBox.setDisable(true);
            thursdayCheckBox.setDisable(true);
            fridayCheckBox.setDisable(true);
            saturdayCheckBox.setDisable(true);
            sundayCheckBox.setDisable(true);
        }

        form.add(daysOfWeekLabel, 0, 5);
        form.add(daysOfWeekBox, 1, 5);

        // Close Button.
        Button closeButton = new Button("Close");
        closeButton.getStyleClass().add("button-close");
        closeButton.setOnAction(e -> dialog.close());

        // Button Pane
        HBox buttonBox = new HBox(closeButton);
        buttonBox.setPadding(new Insets(10, 10, 20, -5));
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        form.add(buttonBox, 1, 6);

        // Dialog Scene
        Scene dialogScene = new Scene(form, 700, 450);
        dialogScene.getStylesheets().add(Objects.requireNonNull(getClass()
                .getResource("/resources/css/HomePageUI.css")).toExternalForm());
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    private void openEditDialog(Task task) {
        Stage dialog = new Stage();
        dialog.setTitle("Edit Task");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(15);
        form.setPadding(new Insets(20));

        // Short Description
        Label titleLabel = new Label("Headline:");
        TextField titleField = new TextField(task.getShortDescription());
        form.add(titleLabel, 0, 0);
        form.add(titleField, 1, 0);

        // Details
        Label detailsLabel = new Label("Details:");
        TextArea detailsField = new TextArea(task.getDetails());
        detailsField.setWrapText(true);
        form.add(detailsLabel, 0, 1);
        form.add(detailsField, 1, 1);

        // Deadline
        Label deadlineLabel = new Label("Deadline:");
        DatePicker deadlinePicker = new DatePicker(task.getDeadline());
        form.add(deadlineLabel, 0, 2);
        form.add(deadlinePicker, 1, 2);

        // Category
        Label categoryLabel = new Label("Category:");
        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll("Important", "InProgress", "Pending", "Completed", "Continuous");
        categoryComboBox.setValue(task.getCategory());
        form.add(categoryLabel, 0, 3);
        form.add(categoryComboBox, 1, 3);

        // Days of Weeks
        Label daysOfWeekLabel = new Label("Days Of Week:");
        HBox daysOfWeekBox = new HBox(10);
        CheckBox monday = new CheckBox("Monday");
        CheckBox tuesday = new CheckBox("Tuesday");
        CheckBox wednesday = new CheckBox("Wednesday");
        CheckBox thursday = new CheckBox("Thursday");
        CheckBox friday = new CheckBox("Friday");
        CheckBox saturday = new CheckBox("Saturday");
        CheckBox sunday = new CheckBox("Sunday");
        daysOfWeekBox.getChildren().addAll(monday, tuesday, wednesday, thursday, friday, saturday, sunday);
        form.add(daysOfWeekLabel, 0, 4);
        form.add(daysOfWeekBox, 1, 4);

        // Set Days for Continuous Tasks
        if ("Continuous".equals(task.getCategory())) {
            List<String> days = task.getDaysOfWeek();
            if (days != null) {
                for (String day : days) {
                    switch (day.trim().toUpperCase(Locale.ENGLISH)) {
                        case "MONDAY": monday.setSelected(true); break;
                        case "TUESDAY": tuesday.setSelected(true); break;
                        case "WEDNESDAY": wednesday.setSelected(true); break;
                        case "THURSDAY": thursday.setSelected(true); break;
                        case "FRIDAY": friday.setSelected(true); break;
                        case "SATURDAY": saturday.setSelected(true); break;
                        case "SUNDAY": sunday.setSelected(true); break;
                    }
                }
            }
            deadlinePicker.setDisable(true);
        } else {
            monday.setDisable(true);
            tuesday.setDisable(true);
            wednesday.setDisable(true);
            thursday.setDisable(true);
            friday.setDisable(true);
            saturday.setDisable(true);
            sunday.setDisable(true);
        }

        categoryComboBox.setOnAction(e -> {
            String selectedCategory = categoryComboBox.getValue();
            if ("Continuous".equals(selectedCategory)) {
                deadlinePicker.setDisable(true);
                monday.setDisable(false);
                tuesday.setDisable(false);
                wednesday.setDisable(false);
                thursday.setDisable(false);
                friday.setDisable(false);
                saturday.setDisable(false);
                sunday.setDisable(false);
            } else {
                deadlinePicker.setDisable(false);
                monday.setDisable(true);
                tuesday.setDisable(true);
                wednesday.setDisable(true);
                thursday.setDisable(true);
                friday.setDisable(true);
                saturday.setDisable(true);
                sunday.setDisable(true);
            }
        });

        // Close Button
        Button updateButton = new Button("Update");
        updateButton.getStyleClass().add("update-button");
        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("cancel-button");

        HBox buttonBox = new HBox(10);
        buttonBox.getStyleClass().add("button-box");
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        buttonBox.setPadding(new Insets(10));
        buttonBox.getChildren().addAll(updateButton, cancelButton);
        form.add(buttonBox, 1, 6);

        updateButton.setOnAction(e -> {
            // Control Mechanism
            String shortDescription = titleField.getText();
            String details = detailsField.getText();
            String category = categoryComboBox.getValue();
            LocalDate deadline = deadlinePicker.getValue();

            List<String> selectedDays = new ArrayList<>();
            if ("Continuous".equals(category)) {
                if (monday.isSelected()) selectedDays.add("Monday");
                if (tuesday.isSelected()) selectedDays.add("Tuesday");
                if (wednesday.isSelected()) selectedDays.add("Wednesday");
                if (thursday.isSelected()) selectedDays.add("Thursday");
                if (friday.isSelected()) selectedDays.add("Friday");
                if (saturday.isSelected()) selectedDays.add("Saturday");
                if (sunday.isSelected()) selectedDays.add("Sunday");
            }

            if (shortDescription.isEmpty() || details.isEmpty() || category == null ||
                    (!"Continuous".equals(category) && deadline == null) ||
                    ("Continuous".equals(category) && selectedDays.isEmpty())) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "All required fields must be filled!");
                alert.showAndWait();
                return;
            }

            // Update Task
            task.setShortDescription(shortDescription);
            task.setDetails(details);
            task.setCategory(category);

            if ("Continuous".equals(category)) {
                task.setDaysOfWeek(selectedDays);
                task.setDeadline(null);
            } else {
                task.setDeadline(deadline);
                task.setDaysOfWeek(null);
            }

            // CSV Write
            CSVTaskUpdater updater = new CSVTaskUpdater();
            boolean isUpdated = updater.updateTask(task);

            if (isUpdated) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Task successfully updated!");
                alert.showAndWait();

                // Refresh current page
                if (selectedMenuItem != null) {
                    Label label = (Label) selectedMenuItem.getChildren().get(1); // Get Label in HBox
                    showTasks(label.getText()); // Refresh page based on current category
                }

                dialog.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to update task.");
                alert.showAndWait();
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        Scene scene = new Scene(form, 700, 450);
        scene.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("/resources/css/HomePageUI.css")
        ).toExternalForm());
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void selectMenuItem(HBox menuItem) {
        if (selectedMenuItem != null) {
            selectedMenuItem.getStyleClass().remove("menu-item-selected");
        }
        menuItem.getStyleClass().add("menu-item-selected");
        selectedMenuItem = menuItem;
    }

    private void showTasks(String filterType) {
        TaskController taskController = new TaskController();
        ObservableList<Task> filteredTasks = FXCollections.observableArrayList();

        switch (filterType) {
            case "Today":
                filteredTasks.addAll(taskController.getTodayTasks().stream()
                        .filter(task -> "1".equals(task.getStatus()))
                        .toList());
                break;
            case "This Month":
                filteredTasks.addAll(taskController.getThisMonthTasks().stream()
                        .filter(task -> "1".equals(task.getStatus()))
                        .toList());
                break;
            case "Important":
                filteredTasks.addAll(taskController.getTasksByCategory("Important").stream()
                        .filter(task -> "1".equals(task.getStatus()))
                        .toList());
                break;
            case "Pending":
                filteredTasks.addAll(taskController.getTasksByCategory("Pending").stream()
                        .filter(task -> "1".equals(task.getStatus()))
                        .toList());
                break;
            case "InProgress":
                filteredTasks.addAll(taskController.getTasksByCategory("InProgress").stream()
                        .filter(task -> "1".equals(task.getStatus()))
                        .toList());
                break;
            case "Completed":
                filteredTasks.addAll(taskController.getTasksByCategory("Completed").stream()
                        .filter(task -> "1".equals(task.getStatus()))
                        .toList());
                break;
            case "Continuous":
                filteredTasks.addAll(taskController.getTasksByCategory("Continuous").stream()
                        .filter(task -> "1".equals(task.getStatus()))
                        .toList());
                break;
        }

        GridPane taskGrid = new GridPane();
        taskGrid.setPadding(new Insets(10));
        taskGrid.setHgap(15);
        taskGrid.setVgap(15);

        int column = 0;
        int row = 0;
        for (Task task : filteredTasks) {
            // Space between items inside the card
            VBox card = new VBox(10);
            card.setPadding(new Insets(10));
            card.getStyleClass().add("task-card");

            // title.
            Label title = new Label(task.getShortDescription());
            title.getStyleClass().add("title");

            // separator.
            Region separator = new Region();
            separator.setPrefHeight(2);
            separator.setStyle("-fx-background-color: #FF5722;");
            separator.prefWidthProperty().bind(card.widthProperty());

            // sub texts.
            Label deadline = new Label("Deadline: " + task.getDeadline());
            deadline.getStyleClass().add("subtext");
            Label category = new Label("Category: " + task.getCategory());
            category.getStyleClass().add("subtext");

            // create context menu
            ContextMenu contextMenu = new ContextMenu();

            MenuItem reviewItem = new MenuItem("Preview");
            reviewItem.setOnAction(event -> {
                System.out.println("Review clicked for: " + task.getShortDescription());
                openReviewDialog(task);
            });

            MenuItem editItem = new MenuItem("Edit");
            editItem.setOnAction(event -> {
                System.out.println("Edit clicked for: " + task.getShortDescription());
                openEditDialog(task);
            });

            MenuItem deleteItem = new MenuItem("Remove");
            deleteItem.setOnAction(event -> {
                System.out.println("Delete clicked for: " + task.getShortDescription());
                Alert confirm  = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Confirm Deletion");
                confirm.setHeaderText("Are you sure you want to remove this task?");
                confirm.setContentText("This action cannot be undone.");

                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        System.out.println("Delete confirmed for: " + task.getShortDescription());
                        removeTask(task);
                    } else {
                        System.out.println("Delete cancelled for: " + task.getShortDescription());
                    }
                });
            });

            contextMenu.getItems().addAll(reviewItem, editItem, deleteItem);
            contextMenu.getStyleClass().addAll("context-menu");

            // Show ContextMenu with right click
            card.setOnContextMenuRequested(event -> {
                contextMenu.show(card, event.getScreenX(), event.getScreenY());
            });

            // Card Content
            card.getChildren().addAll(title, separator, deadline, category);

            // Add card to GridPane
            taskGrid.add(card, column, row);
            column++;
            if (column == 3) { // Max 3 card in per row.
                column = 0;
                row++;
            }
        }
        // Create ScrollPane and add GridPane inside it
        ScrollPane scrollPane = new ScrollPane(taskGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("scroll-pane");
        mainContent.setCenter(scrollPane);
    }

    private void removeTask(Task task) {
        CSVTaskRemover remover = new CSVTaskRemover();
        boolean isDeleted = remover.softDeleteTask(task);

        if (isDeleted) {
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Task Removed");
            success.setHeaderText(null);
            success.setContentText("The task has been successfully removed.");
            success.showAndWait();

            // Reload the currently selected category
            if (selectedMenuItem != null) {
                // Define the selected category.
                Label label = (Label) selectedMenuItem.getChildren().get(1); // Get Label in HBox
                String selectedCategory = label.getText();

                // Highlight the current category and show tasks
                selectMenuItem(selectedMenuItem);
                showTasks(selectedCategory);
            }
        } else {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("Task could not be removed.");
            error.setContentText("Please check the logs for more details.");
            error.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}