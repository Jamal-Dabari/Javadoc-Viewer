package com.javadocviewer.ui;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Modern window decorator that creates a frameless, custom-styled window. Provides macOS/Windows 11
 * style window controls and rounded corners.
 */
public class ModernWindowDecorator {

  private Stage stage;
  private BorderPane root;
  private HBox titleBar;
  private boolean isMaximized = false;
  private double xOffset = 0;
  private double yOffset = 0;

  // Store window position before maximize
  private double windowX;
  private double windowY;
  private double windowWidth;
  private double windowHeight;

  /**
   * Decorates a stage with modern window styling.
   *
   * @param stage The stage to decorate
   * @param content The main content to display
   * @param appTitle The application title
   * @return The decorated root pane
   */
  public BorderPane decorate(Stage stage, Region content, String appTitle) {
    this.stage = stage;

    // Remove default window decorations
    stage.initStyle(StageStyle.TRANSPARENT);

    // Create main container with rounded corners
    root = new BorderPane();
    root.setStyle(
        "-fx-background-color: white; "
            + "-fx-background-radius: 12; "
            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 5);");

    // Create custom title bar
    titleBar = createTitleBar(appTitle);
    root.setTop(titleBar);

    // Set main content
    root.setCenter(content);

    // Enable window dragging
    setupWindowDragging();

    // Handle resize on maximize
    stage
        .maximizedProperty()
        .addListener(
            (obs, wasMaximized, isNowMaximized) -> {
              if (isNowMaximized) {
                root.setStyle(
                    "-fx-background-color: white; "
                        + "-fx-background-radius: 0; "
                        + "-fx-effect: none;");
              } else {
                root.setStyle(
                    "-fx-background-color: white; "
                        + "-fx-background-radius: 12; "
                        + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 5);");
              }
            });

    return root;
  }

  /** Creates a modern custom title bar with window controls. */
  private HBox createTitleBar(String appTitle) {
    HBox titleBar = new HBox();
    titleBar.setAlignment(Pos.CENTER_LEFT);
    titleBar.setStyle(
        "-fx-background-color: white; "
            + "-fx-background-radius: 12 12 0 0; "
            + "-fx-padding: 10 16; "
            + "-fx-border-color: #e5e7eb; "
            + "-fx-border-width: 0 0 1 0;");
    titleBar.setPrefHeight(50);

    // App icon and title
    Label icon = new Label("⚡");
    icon.setStyle("-fx-font-size: 20px; -fx-padding: 0 8 0 0;");

    Label title = new Label(appTitle);
    title.setStyle("-fx-font-size: 14px; " + "-fx-font-weight: 600; " + "-fx-text-fill: #1e293b;");

    // Spacer
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    // Window control buttons (minimize, maximize, close)
    HBox windowControls = createWindowControls();

    titleBar.getChildren().addAll(icon, title, spacer, windowControls);

    return titleBar;
  }

  /** Creates modern window control buttons (minimize, maximize/restore, close). */
  private HBox createWindowControls() {
    HBox controls = new HBox(8);
    controls.setAlignment(Pos.CENTER_RIGHT);

    // Minimize button
    Button minimizeBtn = createControlButton("−", "#10b981");
    minimizeBtn.setOnAction(e -> stage.setIconified(true));

    // Maximize/Restore button
    Button maximizeBtn = createControlButton("□", "#3b82f6");
    maximizeBtn.setOnAction(e -> toggleMaximize(maximizeBtn));

    // Close button
    Button closeBtn = createControlButton("×", "#ef4444");
    closeBtn.setOnAction(e -> stage.close());

    controls.getChildren().addAll(minimizeBtn, maximizeBtn, closeBtn);

    return controls;
  }

  /** Creates a styled window control button. */
  private Button createControlButton(String symbol, String accentColor) {
    Button button = new Button(symbol);
    button.setStyle(
        "-fx-background-color: transparent; "
            + "-fx-text-fill: #64748b; "
            + "-fx-font-size: 20px; "
            + "-fx-font-weight: bold; "
            + "-fx-padding: 0; "
            + "-fx-min-width: 46px; "
            + "-fx-min-height: 32px; "
            + "-fx-cursor: hand; "
            + "-fx-background-radius: 6;");

    // Hover effect
    button.setOnMouseEntered(
        e -> {
          button.setStyle(
              "-fx-background-color: "
                  + accentColor
                  + "15; "
                  + "-fx-text-fill: "
                  + accentColor
                  + "; "
                  + "-fx-font-size: 20px; "
                  + "-fx-font-weight: bold; "
                  + "-fx-padding: 0; "
                  + "-fx-min-width: 46px; "
                  + "-fx-min-height: 32px; "
                  + "-fx-cursor: hand; "
                  + "-fx-background-radius: 6;");
        });

    button.setOnMouseExited(
        e -> {
          button.setStyle(
              "-fx-background-color: transparent; "
                  + "-fx-text-fill: #64748b; "
                  + "-fx-font-size: 20px; "
                  + "-fx-font-weight: bold; "
                  + "-fx-padding: 0; "
                  + "-fx-min-width: 46px; "
                  + "-fx-min-height: 32px; "
                  + "-fx-cursor: hand; "
                  + "-fx-background-radius: 6;");
        });

    return button;
  }

  /** Toggles between maximized and restored window states. */
  private void toggleMaximize(Button maximizeBtn) {
    if (stage.isMaximized()) {
      stage.setMaximized(false);
      maximizeBtn.setText("□");
    } else {
      stage.setMaximized(true);
      maximizeBtn.setText("❐");
    }
  }

  /** Sets up window dragging functionality. */
  private void setupWindowDragging() {
    titleBar.setOnMousePressed(
        event -> {
          xOffset = event.getSceneX();
          yOffset = event.getSceneY();
        });

    titleBar.setOnMouseDragged(
        event -> {
          if (!stage.isMaximized()) {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
          }
        });

    // Double-click to maximize/restore
    titleBar.setOnMouseClicked(
        event -> {
          if (event.getClickCount() == 2) {
            stage.setMaximized(!stage.isMaximized());
          }
        });
  }

  /** Updates the title bar for dark mode. */
  public void setDarkMode(boolean darkMode) {
    if (darkMode) {
      titleBar.setStyle(
          "-fx-background-color: #1e1e2e; "
              + "-fx-background-radius: 12 12 0 0; "
              + "-fx-padding: 10 16; "
              + "-fx-border-color: #313244; "
              + "-fx-border-width: 0 0 1 0;");

      root.setStyle(
          "-fx-background-color: #1e1e2e; "
              + "-fx-background-radius: 12; "
              + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 20, 0, 0, 5);");

      // Update title color
      ((Label) titleBar.getChildren().get(1))
          .setStyle("-fx-font-size: 14px; " + "-fx-font-weight: 600; " + "-fx-text-fill: #cdd6f4;");
    } else {
      titleBar.setStyle(
          "-fx-background-color: white; "
              + "-fx-background-radius: 12 12 0 0; "
              + "-fx-padding: 10 16; "
              + "-fx-border-color: #e5e7eb; "
              + "-fx-border-width: 0 0 1 0;");

      root.setStyle(
          "-fx-background-color: white; "
              + "-fx-background-radius: 12; "
              + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 5);");

      // Update title color
      ((Label) titleBar.getChildren().get(1))
          .setStyle("-fx-font-size: 14px; " + "-fx-font-weight: 600; " + "-fx-text-fill: #1e293b;");
    }
  }
}
