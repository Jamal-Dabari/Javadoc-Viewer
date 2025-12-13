package com.javadocviewer.ui;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Utility class to make UI components draggable for window movement.
 * Allows custom title bars to move the window when dragged.
 */
public class WindowDragHandler {
    
    private static double xOffset = 0;
    private static double yOffset = 0;
    
    /**
     * Makes the given node draggable, allowing it to move the stage.
     * 
     * @param node The node to make draggable (typically a title bar)
     * @param stage The stage to be moved
     */
    public static void makeDraggable(Node node, Stage stage) {
        node.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        
        node.setOnMouseDragged((MouseEvent event) -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }
}
