package com.javadocviewer.ui;

import java.util.function.DoubleSupplier;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Duration;

/**
 * Builder class for creating the application's toolbar. Creates toolbar with navigation buttons,
 * recent files, and zoom controls.
 */
public class ToolbarBuilder {

  private Button backButton;
  private Button forwardButton;
  private Button zoomInButton;
  private Button zoomOutButton;
  private Button resetZoomButton;
  private ComboBox<String> recentFilesCombo;
  private ProgressIndicator loadingIndicator;

  /**
   * Creates and configures the toolbar.
   *
   * @param onBack Callback for back navigation
   * @param onForward Callback for forward navigation
   * @param onRecentFileSelect Callback when a recent file is selected
   * @param onZoomIn Callback for zoom in
   * @param onZoomOut Callback for zoom out
   * @param onResetZoom Callback for reset zoom
   * @param zoomSupplier Supplier for current zoom level
   * @return Configured HBox toolbar
   */
  public HBox build(
      Runnable onBack,
      Runnable onForward,
      Runnable onRecentFileSelect,
      Runnable onZoomIn,
      Runnable onZoomOut,
      Runnable onResetZoom,
      DoubleSupplier zoomSupplier) {

    HBox toolbar = new HBox(10);
    toolbar.setStyle(
        "-fx-background-color: #f8f9fa; "
            + "-fx-padding: 8 24; "
            + "-fx-border-color: #e5e7eb; "
            + "-fx-border-width: 0 0 1 0;");
    toolbar.setAlignment(Pos.CENTER_LEFT);

    // Navigation buttons
    backButton = new Button("←");
    backButton.setTooltip(new Tooltip("Back (Alt+Left)"));
    backButton.setDisable(true);
    styleToolButton(backButton);
    backButton.setOnAction(e -> onBack.run());

    forwardButton = new Button("→");
    forwardButton.setTooltip(new Tooltip("Forward (Alt+Right)"));
    forwardButton.setDisable(true);
    styleToolButton(forwardButton);
    forwardButton.setOnAction(e -> onForward.run());

    Separator sep1 = new Separator(Orientation.VERTICAL);

    // Recent files combo box
    recentFilesCombo = new ComboBox<>();
    recentFilesCombo.setPromptText("Recent files...");
    recentFilesCombo.setPrefWidth(250);
    recentFilesCombo.setStyle("-fx-font-size: 12px;");
    recentFilesCombo.setOnAction(e -> onRecentFileSelect.run());

    // Spacer
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    // Zoom controls
    zoomOutButton = new Button("−");
    zoomOutButton.setTooltip(new Tooltip("Zoom Out (Ctrl+-)"));
    styleToolButton(zoomOutButton);
    zoomOutButton.setOnAction(e -> onZoomOut.run());

    Label zoomLabel = new Label("100%");
    zoomLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
    zoomLabel.setMinWidth(45);
    zoomLabel.setAlignment(Pos.CENTER);

    zoomInButton = new Button("+");
    zoomInButton.setTooltip(new Tooltip("Zoom In (Ctrl++)"));
    styleToolButton(zoomInButton);
    zoomInButton.setOnAction(e -> onZoomIn.run());

    resetZoomButton = new Button("⟲");
    resetZoomButton.setTooltip(new Tooltip("Reset Zoom (Ctrl+0)"));
    styleToolButton(resetZoomButton);
    resetZoomButton.setOnAction(e -> onResetZoom.run());

    // Update zoom label periodically
    Timeline zoomUpdater =
        new Timeline(
            new KeyFrame(
                Duration.millis(100),
                e -> {
                  int zoomPercent = (int) (zoomSupplier.getAsDouble() * 100);
                  zoomLabel.setText(zoomPercent + "%");
                }));
    zoomUpdater.setCycleCount(Timeline.INDEFINITE);
    zoomUpdater.play();

    // Loading indicator
    loadingIndicator = new ProgressIndicator();
    loadingIndicator.setMaxSize(20, 20);
    loadingIndicator.setVisible(false);

    toolbar
        .getChildren()
        .addAll(
            backButton,
            forwardButton,
            sep1,
            recentFilesCombo,
            spacer,
            loadingIndicator,
            zoomOutButton,
            zoomLabel,
            zoomInButton,
            resetZoomButton);

    return toolbar;
  }

  private void styleToolButton(Button button) {
    button.setStyle(
        "-fx-background-color: white; "
            + "-fx-text-fill: #475569; "
            + "-fx-font-size: 16px; "
            + "-fx-background-radius: 6; "
            + "-fx-padding: 6 12; "
            + "-fx-border-color: #e5e7eb; "
            + "-fx-border-radius: 6; "
            + "-fx-cursor: hand;");
    button.setOnMouseEntered(
        e -> {
          if (!button.isDisabled()) {
            button.setStyle(button.getStyle() + "-fx-background-color: #f1f5f9;");
          }
        });
    button.setOnMouseExited(
        e -> {
          if (!button.isDisabled()) {
            button.setStyle(
                button
                    .getStyle()
                    .replace("-fx-background-color: #f1f5f9;", "-fx-background-color: white;"));
          }
        });
  }

  // Getters
  public Button getBackButton() {
    return backButton;
  }

  public Button getForwardButton() {
    return forwardButton;
  }

  public ComboBox<String> getRecentFilesCombo() {
    return recentFilesCombo;
  }

  public ProgressIndicator getLoadingIndicator() {
    return loadingIndicator;
  }
}
