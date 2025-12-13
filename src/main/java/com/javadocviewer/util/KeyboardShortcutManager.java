package com.javadocviewer.util;

import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

/**
 * Manages keyboard shortcuts for the application. Centralizes all keyboard shortcut definitions and
 * registrations.
 */
public class KeyboardShortcutManager {

  /**
   * Sets up all keyboard shortcuts for the given scene.
   *
   * @param scene The scene to register shortcuts on
   * @param searchField The search field to focus on Ctrl+F
   * @param callbacks Object containing callback methods for shortcuts
   */
  public static void setupShortcuts(
      Scene scene, TextField searchField, ShortcutCallbacks callbacks) {
    // Ctrl+F - Focus search
    scene
        .getAccelerators()
        .put(
            new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN),
            () -> {
              searchField.requestFocus();
              searchField.selectAll();
            });

    // Ctrl+B - Toggle sidebar
    scene
        .getAccelerators()
        .put(
            new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN),
            callbacks::toggleSidebar);

    // Ctrl+D - Toggle dark mode
    scene
        .getAccelerators()
        .put(
            new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN),
            callbacks::toggleDarkMode);

    // Alt+Left - Back
    scene
        .getAccelerators()
        .put(
            new KeyCodeCombination(KeyCode.LEFT, KeyCombination.ALT_DOWN), callbacks::navigateBack);

    // Alt+Right - Forward
    scene
        .getAccelerators()
        .put(
            new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.ALT_DOWN),
            callbacks::navigateForward);

    // Ctrl+Plus - Zoom in
    scene
        .getAccelerators()
        .put(
            new KeyCodeCombination(KeyCode.EQUALS, KeyCombination.CONTROL_DOWN), callbacks::zoomIn);

    // Ctrl+Minus - Zoom out
    scene
        .getAccelerators()
        .put(
            new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN), callbacks::zoomOut);

    // Ctrl+0 - Reset zoom
    scene
        .getAccelerators()
        .put(
            new KeyCodeCombination(KeyCode.DIGIT0, KeyCombination.CONTROL_DOWN),
            callbacks::resetZoom);
  }

  /**
   * Interface for keyboard shortcut callbacks. Implement this interface to handle shortcut actions.
   */
  public interface ShortcutCallbacks {
    void toggleSidebar();

    void toggleDarkMode();

    void navigateBack();

    void navigateForward();

    void zoomIn();

    void zoomOut();

    void resetZoom();
  }
}
