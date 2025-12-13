package com.javadocviewer.ui;

import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Duration;

/**
 * Builder class for creating the application's navigation bar. Creates the top bar with logo,
 * title, search field, and mode toggles.
 */
public class NavBarBuilder {

  private TextField searchField;
  private ToggleButton darkModeToggle;
  private ToggleButton sidebarToggle;

  /**
   * Creates and configures the navigation bar.
   *
   * @param onSearchChange Callback when search text changes
   * @param onDarkModeToggle Callback when dark mode is toggled
   * @param onSidebarToggle Callback when sidebar is toggled
   * @param onLogoClick Callback when logo is clicked
   * @return Configured HBox navigation bar
   */
  public HBox build(
      Runnable onSearchChange,
      Runnable onDarkModeToggle,
      Runnable onSidebarToggle,
      Runnable onLogoClick) {

    HBox navbar = new HBox(15);
    navbar.setStyle(
        "-fx-background-color: linear-gradient(to right, #646cff, #535bf2); "
            + "-fx-padding: 12 24; "
            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");
    navbar.setAlignment(Pos.CENTER_LEFT);

    // Sidebar toggle button
    sidebarToggle = new ToggleButton("☰");
    sidebarToggle.setSelected(true);
    sidebarToggle.setTooltip(new Tooltip("Toggle Sidebar (Ctrl+B)"));
    styleNavButton(sidebarToggle);
    sidebarToggle.setOnAction(e -> onSidebarToggle.run());

    // Logo with animation
    Label logo = new Label("⚡");
    logo.setStyle("-fx-font-size: 32px; -fx-cursor: pointer;");
    logo.setOnMouseClicked(e -> onLogoClick.run());

    ScaleTransition pulse = new ScaleTransition(Duration.seconds(1.5), logo);
    pulse.setFromX(1.0);
    pulse.setFromY(1.0);
    pulse.setToX(1.1);
    pulse.setToY(1.1);
    pulse.setCycleCount(Timeline.INDEFINITE);
    pulse.setAutoReverse(true);
    pulse.play();

    // Title
    Label title = new Label("Java Docs");
    title.setStyle(
        "-fx-font-size: 22px; "
            + "-fx-font-weight: bold; "
            + "-fx-text-fill: white; "
            + "-fx-font-family: 'Inter', 'Segoe UI', sans-serif;");

    // Search field
    searchField = new TextField();
    searchField.setPromptText("Search documentation... (Ctrl+F)");
    searchField.setPrefWidth(450);
    styleSearchField();
    searchField.textProperty().addListener((obs, old, newVal) -> onSearchChange.run());

    // Spacer
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    // Dark mode toggle
    darkModeToggle = new ToggleButton("🌙");
    darkModeToggle.setTooltip(new Tooltip("Toggle Dark Mode (Ctrl+D)"));
    styleNavButton(darkModeToggle);
    darkModeToggle.setOnAction(e -> onDarkModeToggle.run());

    navbar.getChildren().addAll(sidebarToggle, logo, title, searchField, spacer, darkModeToggle);

    return navbar;
  }

  private void styleNavButton(ButtonBase button) {
    button.setStyle(
        "-fx-background-color: rgba(255,255,255,0.15); "
            + "-fx-text-fill: white; "
            + "-fx-font-size: 20px; "
            + "-fx-background-radius: 8; "
            + "-fx-padding: 8 16; "
            + "-fx-cursor: hand;");
    addHoverEffect(button);
  }

  private void styleSearchField() {
    searchField.setStyle(
        "-fx-background-color: rgba(255,255,255,0.15); "
            + "-fx-text-fill: white; "
            + "-fx-prompt-text-fill: rgba(255,255,255,0.7); "
            + "-fx-background-radius: 10; "
            + "-fx-padding: 12 20; "
            + "-fx-font-size: 14px; "
            + "-fx-border-color: transparent; "
            + "-fx-border-radius: 10;");

    searchField
        .focusedProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              if (newVal) {
                searchField.setStyle(
                    "-fx-background-color: rgba(255,255,255,0.25); "
                        + "-fx-text-fill: white; "
                        + "-fx-prompt-text-fill: rgba(255,255,255,0.7); "
                        + "-fx-background-radius: 10; "
                        + "-fx-padding: 12 20; "
                        + "-fx-font-size: 14px; "
                        + "-fx-border-color: rgba(255,255,255,0.4); "
                        + "-fx-border-width: 2; "
                        + "-fx-border-radius: 10;");
              } else {
                styleSearchField();
              }
            });
  }

  private void addHoverEffect(Control button) {
    button.setOnMouseEntered(
        e -> {
          button.setStyle(
              button.getStyle()
                  + "-fx-background-color: rgba(255,255,255,0.25); "
                  + "-fx-scale-x: 1.05; "
                  + "-fx-scale-y: 1.05;");
        });
    button.setOnMouseExited(
        e -> {
          String currentStyle = button.getStyle();
          currentStyle =
              currentStyle.replace(
                  "-fx-background-color: rgba(255,255,255,0.25);",
                  "-fx-background-color: rgba(255,255,255,0.15);");
          button.setStyle(currentStyle);
        });
  }

  // Getters
  public TextField getSearchField() {
    return searchField;
  }

  public ToggleButton getDarkModeToggle() {
    return darkModeToggle;
  }

  public ToggleButton getSidebarToggle() {
    return sidebarToggle;
  }
}
