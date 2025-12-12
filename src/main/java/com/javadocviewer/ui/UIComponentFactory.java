package com.javadocviewer.ui;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class UIComponentFactory {

                                public static HBox createCustomTitleBar(Stage stage) {
                                                                HBox titleBar = new HBox();
                                                                titleBar.setAlignment(Pos.CENTER_LEFT);
                                                                titleBar.setStyle(
                                                                                                                                "-fx-background-color: #ffffff; " +
                                                                                                                                                                                                "-fx-background-radius: 12 12 0 0; "
                                                                                                                                                                                                +
                                                                                                                                                                                                "-fx-padding: 8 12; "
                                                                                                                                                                                                +
                                                                                                                                                                                                "-fx-border-color: #e5e7eb; "
                                                                                                                                                                                                +
                                                                                                                                                                                                "-fx-border-width: 0 0 1 0;");
                                                                titleBar.setPrefHeight(40);

                                                                // Make draggable
                                                                WindowDragHandler.makeDraggable(titleBar, stage);

                                                                // Add components
                                                                Label appIcon = new Label("⚡");
                                                                appIcon.setStyle("-fx-font-size: 18px;");

                                                                Label titleLabel = new Label("Java Documentation Viewer");
                                                                titleLabel.setStyle(
                                                                                                                                "-fx-font-size: 13px; " +
                                                                                                                                                                                                "-fx-font-weight: 600; "
                                                                                                                                                                                                +
                                                                                                                                                                                                "-fx-text-fill: #475569; "
                                                                                                                                                                                                +
                                                                                                                                                                                                "-fx-padding: 0 0 0 8;");

                                                                Region spacer = new Region();
                                                                HBox.setHgrow(spacer, Priority.ALWAYS);

                                                                Button minimizeBtn = createWindowButton("─", "#10b981", e -> stage
                                                                                                                                .setIconified(true));
                                                                Button maximizeBtn = createWindowButton("□", "#3b82f6", e -> {
                                                                                                stage.setMaximized(!stage.isMaximized());
                                                                                                ((Button) e.getSource()).setText(stage.isMaximized() ? "❐"
                                                                                                                                                                : "□");
                                                                });
                                                                Button closeBtn = createWindowButton("×", "#ef4444", e -> {
                                                                                                stage.close();
                                                                });

                                                                titleBar.getChildren().addAll(
                                                                                                                                appIcon,
                                                                                                                                titleLabel,
                                                                                                                                spacer,
                                                                                                                                minimizeBtn,
                                                                                                                                maximizeBtn,
                                                                                                                                closeBtn);

                                                                return titleBar;
                                }

                                private static Button createWindowButton(String text, String hoverColor,
                                                                                                javafx.event.EventHandler<javafx.event.ActionEvent> action) {
                                                                Button button = new Button(text);
                                                                button.setStyle(
                                                                                                                                "-fx-background-color: transparent; " +
                                                                                                                                                                                                "-fx-text-fill: #64748b; "
                                                                                                                                                                                                +
                                                                                                                                                                                                "-fx-font-size: 18px; "
                                                                                                                                                                                                +
                                                                                                                                                                                                "-fx-font-weight: bold; "
                                                                                                                                                                                                +
                                                                                                                                                                                                "-fx-padding: 0; "
                                                                                                                                                                                                +
                                                                                                                                                                                                "-fx-min-width: 36px; "
                                                                                                                                                                                                +
                                                                                                                                                                                                "-fx-min-height: 32px; "
                                                                                                                                                                                                +
                                                                                                                                                                                                "-fx-cursor: hand; "
                                                                                                                                                                                                +
                                                                                                                                                                                                "-fx-background-radius: 6;");

                                                                button.setOnMouseEntered(e -> button.setStyle(button.getStyle() +
                                                                                                                                "-fx-background-color: "
                                                                                                                                + hoverColor
                                                                                                                                + "15; "
                                                                                                                                +
                                                                                                                                "-fx-text-fill: "
                                                                                                                                + hoverColor
                                                                                                                                + ";"));

                                                                button.setOnMouseExited(e -> button.setStyle(
                                                                                                                                "-fx-background-color: transparent; " +
                                                                                                                                                                                                "-fx-text-fill: #64748b; "
                                                                                                                                                                                                +
                                                                                                                                                                                                "-fx-font-size: 18px; "
                                                                                                                                                                                                +
                                                                                                                                                                                                "-fx-font-weight: bold; "
                                                                                                                                                                                                +
                                                                                                                                                                                                "-fx-padding: 0; "
                                                                                                                                                                                                +
                                                                                                                                                                                                "-fx-min-width: 36px; "
                                                                                                                                                                                                +
                                                                                                                                                                                                "-fx-min-height: 32px; "
                                                                                                                                                                                                +
                                                                                                                                                                                                "-fx-cursor: hand; "
                                                                                                                                                                                                +
                                                                                                                                                                                                "-fx-background-radius: 6;"));

                                                                button.setOnAction(action);
                                                                return button;
                                }
}
