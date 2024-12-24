package dev.bkrk.controller;

import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class DraggableWindowController {

    private double offSetX;
    private double offSetY;

    public void enableDragging(BorderPane root, Stage stage) {
        root.setOnMousePressed(event -> {
            offSetX = event.getSceneX();
            offSetY = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - offSetX);
            stage.setY(event.getScreenY() - offSetY);
        });
    }
}
