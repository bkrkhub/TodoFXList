package dev.bkrk.ui;

import dev.bkrk.controller.DraggableWindowController;
import dev.bkrk.controller.LoginController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public class LoginUI extends Application {

    @Override
    public void start(Stage primaryStage) {

        // Window title is removed when using StageStyle.UNDECORATED
        primaryStage.initStyle(StageStyle.UNDECORATED);

        // Taskbar and window icon
        primaryStage.getIcons().add(new Image(
                Objects.requireNonNull(getClass().getResource("/resources/images/to-do-list.png")).toExternalForm()
        ));

        // leftPanel size.
        Pane leftPanel = new Pane();
        leftPanel.setPrefSize(500, 600);
        // background
        String imagePath = getClass().getResource("/resources/images/test.png").toExternalForm();
        leftPanel.setStyle("-fx-background-image: url('" + imagePath + "');" +
                "-fx-background-size: cover;" +
                "-fx-background-position: center center;");


        // right panel for login.
        VBox rightPanel = new VBox(20);
        rightPanel.setAlignment(Pos.CENTER);
        rightPanel.setPadding(new Insets(50, 50, 50, 50));
        rightPanel.setStyle("-fx-background-color: #A9A9A9;");

        // Vbox for texts and header.
        VBox textContainer = new VBox(30);
        textContainer.setAlignment(Pos.TOP_CENTER);
        // firstText.
        Text headerForRight = new Text("Welcome Your Future !");
        headerForRight.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        headerForRight.setFill(Color.WHITE);

        // secondText.
        Text secondText = new Text("\"Don’t count the days, make the days count.\"\n" +
                "– Muhammad Ali");
        secondText.setFont(Font.font("Arial", FontWeight.THIN, 18));
        secondText.setFill(Color.BLANCHEDALMOND);

        // Add texts to container.
        textContainer.getChildren().addAll(headerForRight, secondText);

        // Adding posts to rightPanel.
        rightPanel.getChildren().add(textContainer);

        // text for login.
        Text loginHeader = new Text("Let's Jump ☺");
        loginHeader.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        loginHeader.setFill(Color.WHITE);

        //username
        TextField usernameField = new TextField();
        usernameField.setPromptText("User Name");
        usernameField.setMaxWidth(300);

        //password
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);

        // Login button.
        Button loginButton = new Button("Login");
        loginButton.setMaxWidth(200);
        // action.
        loginButton.setOnAction(event -> {
            String userName = usernameField.getText();
            String password = passwordField.getText();

            LoginController loginController = new LoginController();
            if (loginController.validateUser(userName, password)) {
                HomePageUI homePage = new HomePageUI();
                homePage.start(primaryStage);
            } else {
                javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                errorAlert.setTitle("Login Failed");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Invalid Username or Password");
                errorAlert.showAndWait();
            }
        });


        // exit button.
        Button exitButton = new Button("Exit");
        exitButton.setMaxWidth(75);
        exitButton.getStyleClass().add("exitButton");
        exitButton.setOnAction(e -> primaryStage.close());

        // rightPanel Update.
        rightPanel.getChildren().addAll(loginHeader, usernameField, passwordField, loginButton,exitButton);

        // Main Pane (BorderPane)
        BorderPane root = new BorderPane();
        root.setLeft(leftPanel);
        root.setCenter(rightPanel);

        // WindowDragController.
        DraggableWindowController dragController = new DraggableWindowController();
        dragController.enableDragging(root, primaryStage);

        // create a scene.
        Scene scene = new Scene(root, 1000, 800);
        scene.getStylesheets().add(getClass().getResource("/resources/css/LoginUI.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args); // Launch JavaFX Application
    }
}
