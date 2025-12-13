package com.javadocviewer.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * Builder class for creating the application's status bar. Creates the bottom bar with status
 * messages and keyboard shortcuts info.
 */
public class StatusBarBuilder {

  private Label statusLabel;

  /**
   * Creates and configures the status bar.
   *
   * @return Configured HBox status bar
   */
  public HBox build() {
    HBox statusBar = new HBox(10);
    statusBar.setStyle(
        "-fx-background-color: #f8f9fa; "
            + "-fx-padding: 6 24; "
            + "-fx-border-color: #e5e7eb; "
            + "-fx-border-width: 1 0 0 0;");
    statusBar.setAlignment(Pos.CENTER_LEFT);

    // Status label
    statusLabel = new Label("Ready");
    statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

    // Spacer
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    // Shortcuts hint
    Label shortcutsLabel = new Label("Ctrl+F: Search | Ctrl+B: Toggle Sidebar | Ctrl+D: Dark Mode");
    shortcutsLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");

    statusBar.getChildren().addAll(statusLabel, spacer, shortcutsLabel);

    return statusBar;
  }

  /**
   * Gets the status label for updating status messages.
   *
   * @return The status label
   */
  public Label getStatusLabel() {
    return statusLabel;
  }

  /**
   * Updates the status message.
   *
   * @param message The message to display
   */
  public void setStatus(String message) {
    if (statusLabel != null) {
      statusLabel.setText(message);
    }
  }
}
