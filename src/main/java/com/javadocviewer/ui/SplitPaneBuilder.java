package com.javadocviewer.ui;

import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;

/**
 * Builder class for creating the main split pane. Creates the resizable split between sidebar and
 * content area.
 */
public class SplitPaneBuilder {

  /**
   * Creates and configures the main split pane.
   *
   * @param sidebar The sidebar VBox
   * @param content The content area VBox
   * @param dividerPosition Initial divider position (0.0 to 1.0)
   * @return Configured SplitPane
   */
  public static SplitPane build(VBox sidebar, VBox content, double dividerPosition) {
    SplitPane splitPane = new SplitPane();
    splitPane.setOrientation(Orientation.HORIZONTAL);
    splitPane.getItems().addAll(sidebar, content);
    splitPane.setDividerPositions(dividerPosition);

    return splitPane;
  }

  /**
   * Creates a split pane with default divider position.
   *
   * @param sidebar The sidebar VBox
   * @param content The content area VBox
   * @return Configured SplitPane with 0.2 divider position
   */
  public static SplitPane build(VBox sidebar, VBox content) {
    return build(sidebar, content, 0.2);
  }
}
