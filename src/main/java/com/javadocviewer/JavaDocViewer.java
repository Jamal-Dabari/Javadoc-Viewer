package com.javadocviewer;

import com.javadocviewer.model.FileNode;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class JavaDocViewer extends Application {

  private TreeView<FileNode> fileTree;
  private WebView webView;
  private WebEngine webEngine;
  private TextField searchField;
  private ToggleButton darkModeToggle;
  private ToggleButton sidebarToggle;
  private Button backButton;
  private Button forwardButton;
  private Button zoomInButton;
  private Button zoomOutButton;
  private Button resetZoomButton;
  private ComboBox<String> recentFilesCombo;
  private Label statusLabel;
  private ProgressIndicator loadingIndicator;

  private boolean isDarkMode = false;
  private boolean isSidebarVisible = true;
  private String docsPath = "docs";
  private VBox sidebar;
  private BorderPane root;
  private SplitPane splitPane;
  private double currentZoom = 1.0;

  // History management
  private LinkedList<String> history = new LinkedList<>();
  private int historyIndex = -1;
  private static final int MAX_HISTORY = 50;

  // Cache for loaded files
  private Map<String, String> fileCache = new HashMap<>();

  // Recent files
  private LinkedList<String> recentFiles = new LinkedList<>();
  private static final int MAX_RECENT = 10;

  // Preferences
  private Preferences prefs;

  @Override
  public void start(Stage primaryStage) {
    prefs = Preferences.userNodeForPackage(JavaDocViewer.class);
    loadPreferences();

    primaryStage.setTitle("Java Documentation Viewer");

    // Restore window size or maximize
    boolean wasMaximized = prefs.getBoolean("maximized", true);
    if (wasMaximized) {
      primaryStage.setMaximized(true);
    } else {
      double width = prefs.getDouble("windowWidth", 1400);
      double height = prefs.getDouble("windowHeight", 900);
      primaryStage.setWidth(width);
      primaryStage.setHeight(height);
    }

    root = new BorderPane();
    root.setStyle("-fx-background-color: #ffffff;");

    // Top navigation
    VBox topArea = new VBox();
    HBox navbar = createNavBar();
    HBox toolbar = createToolbar();
    topArea.getChildren().addAll(navbar, toolbar);
    root.setTop(topArea);

    // Sidebar and content with resizable split pane
    splitPane = new SplitPane();
    splitPane.setOrientation(Orientation.HORIZONTAL);

    sidebar = createSidebar();
    VBox content = createContentArea();

    splitPane.getItems().addAll(sidebar, content);
    splitPane.setDividerPositions(prefs.getDouble("dividerPosition", 0.2));

    root.setCenter(splitPane);

    // Bottom status bar
    HBox statusBar = createStatusBar();
    root.setBottom(statusBar);

    Scene scene = new Scene(root, 1400, 900);
    setupKeyboardShortcuts(scene);

    primaryStage.setScene(scene);
    primaryStage.show();

    // Save preferences on close
    primaryStage.setOnCloseRequest(e -> savePreferences(primaryStage));

    showWelcome();
  }

  private void setupKeyboardShortcuts(Scene scene) {
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
        .put(new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN), () -> toggleSidebar());

    // Ctrl+D - Toggle dark mode
    scene
        .getAccelerators()
        .put(
            new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN), () -> toggleDarkMode());

    // Alt+Left - Back
    scene
        .getAccelerators()
        .put(new KeyCodeCombination(KeyCode.LEFT, KeyCombination.ALT_DOWN), () -> navigateBack());

    // Alt+Right - Forward
    scene
        .getAccelerators()
        .put(
            new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.ALT_DOWN),
            () -> navigateForward());

    // Ctrl+Plus - Zoom in
    scene
        .getAccelerators()
        .put(new KeyCodeCombination(KeyCode.EQUALS, KeyCombination.CONTROL_DOWN), () -> zoomIn());

    // Ctrl+Minus - Zoom out
    scene
        .getAccelerators()
        .put(new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN), () -> zoomOut());

    // Ctrl+0 - Reset zoom
    scene
        .getAccelerators()
        .put(
            new KeyCodeCombination(KeyCode.DIGIT0, KeyCombination.CONTROL_DOWN), () -> resetZoom());
  }

  private HBox createNavBar() {
    HBox navbar = new HBox(15);
    navbar.setStyle(
        "-fx-background-color: linear-gradient(to right, #646cff, #535bf2); "
            + "-fx-padding: 12 24; "
            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");
    navbar.setAlignment(Pos.CENTER_LEFT);

    sidebarToggle = new ToggleButton("☰");
    sidebarToggle.setSelected(true);
    sidebarToggle.setTooltip(new Tooltip("Toggle Sidebar (Ctrl+B)"));
    styleNavButton(sidebarToggle);
    sidebarToggle.setOnAction(e -> toggleSidebar());

    Label logo = new Label("⚡");
    logo.setStyle("-fx-font-size: 32px; -fx-cursor: pointer;");
    logo.setOnMouseClicked(e -> showWelcome());

    ScaleTransition pulse = new ScaleTransition(Duration.seconds(1.5), logo);
    pulse.setFromX(1.0);
    pulse.setFromY(1.0);
    pulse.setToX(1.1);
    pulse.setToY(1.1);
    pulse.setCycleCount(Timeline.INDEFINITE);
    pulse.setAutoReverse(true);
    pulse.play();

    Label title = new Label("Java Docs");
    title.setStyle(
        "-fx-font-size: 22px; "
            + "-fx-font-weight: bold; "
            + "-fx-text-fill: white; "
            + "-fx-font-family: 'Inter', 'Segoe UI', sans-serif;");

    searchField = new TextField();
    searchField.setPromptText("Search documentation... (Ctrl+F)");
    searchField.setPrefWidth(450);
    styleSearchField();
    searchField.textProperty().addListener((obs, old, newVal) -> filterFiles(newVal));

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    darkModeToggle = new ToggleButton("🌙");
    darkModeToggle.setTooltip(new Tooltip("Toggle Dark Mode (Ctrl+D)"));
    styleNavButton(darkModeToggle);
    darkModeToggle.setOnAction(e -> toggleDarkMode());

    navbar.getChildren().addAll(sidebarToggle, logo, title, searchField, spacer, darkModeToggle);
    return navbar;
  }

  private HBox createToolbar() {
    HBox toolbar = new HBox(10);
    toolbar.setStyle(
        "-fx-background-color: #f8f9fa; "
            + "-fx-padding: 8 24; "
            + "-fx-border-color: #e5e7eb; "
            + "-fx-border-width: 0 0 1 0;");
    toolbar.setAlignment(Pos.CENTER_LEFT);

    backButton = new Button("←");
    backButton.setTooltip(new Tooltip("Back (Alt+Left)"));
    backButton.setDisable(true);
    styleToolButton(backButton);
    backButton.setOnAction(e -> navigateBack());

    forwardButton = new Button("→");
    forwardButton.setTooltip(new Tooltip("Forward (Alt+Right)"));
    forwardButton.setDisable(true);
    styleToolButton(forwardButton);
    forwardButton.setOnAction(e -> navigateForward());

    Separator sep1 = new Separator(Orientation.VERTICAL);

    recentFilesCombo = new ComboBox<>();
    recentFilesCombo.setPromptText("Recent files...");
    recentFilesCombo.setPrefWidth(250);
    recentFilesCombo.setStyle("-fx-font-size: 12px;");
    recentFilesCombo.setOnAction(
        e -> {
          String selected = recentFilesCombo.getValue();
          if (selected != null && !selected.isEmpty()) {
            loadFile(selected);
          }
        });

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    zoomOutButton = new Button("−");
    zoomOutButton.setTooltip(new Tooltip("Zoom Out (Ctrl+-)"));
    styleToolButton(zoomOutButton);
    zoomOutButton.setOnAction(e -> zoomOut());

    Label zoomLabel = new Label("100%");
    zoomLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
    zoomLabel.setMinWidth(45);
    zoomLabel.setAlignment(Pos.CENTER);

    zoomInButton = new Button("+");
    zoomInButton.setTooltip(new Tooltip("Zoom In (Ctrl++)"));
    styleToolButton(zoomInButton);
    zoomInButton.setOnAction(e -> zoomIn());

    resetZoomButton = new Button("⟲");
    resetZoomButton.setTooltip(new Tooltip("Reset Zoom (Ctrl+0)"));
    styleToolButton(resetZoomButton);
    resetZoomButton.setOnAction(e -> resetZoom());

    // Update zoom label
    Timeline zoomUpdater =
        new Timeline(
            new KeyFrame(
                Duration.millis(100),
                e -> {
                  int zoomPercent = (int) (currentZoom * 100);
                  zoomLabel.setText(zoomPercent + "%");
                }));
    zoomUpdater.setCycleCount(Timeline.INDEFINITE);
    zoomUpdater.play();

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

  private HBox createStatusBar() {
    HBox statusBar = new HBox(10);
    statusBar.setStyle(
        "-fx-background-color: #f8f9fa; "
            + "-fx-padding: 6 24; "
            + "-fx-border-color: #e5e7eb; "
            + "-fx-border-width: 1 0 0 0;");
    statusBar.setAlignment(Pos.CENTER_LEFT);

    statusLabel = new Label("Ready");
    statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    Label shortcutsLabel = new Label("Ctrl+F: Search | Ctrl+B: Toggle Sidebar | Ctrl+D: Dark Mode");
    shortcutsLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");

    statusBar.getChildren().addAll(statusLabel, spacer, shortcutsLabel);
    return statusBar;
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

  private VBox createSidebar() {
    VBox sidebar = new VBox(12);
    sidebar.setStyle(
        "-fx-background-color: #fafafa; "
            + "-fx-border-color: #e5e7eb; "
            + "-fx-border-width: 0 1 0 0; "
            + "-fx-padding: 24 12;");
    sidebar.setMinWidth(200);

    Label sidebarTitle = new Label("📚 Documentation");
    sidebarTitle.setStyle(
        "-fx-font-size: 16px; "
            + "-fx-font-weight: bold; "
            + "-fx-text-fill: #646cff; "
            + "-fx-padding: 0 8;");

    fileTree = new TreeView<>();
    fileTree.setStyle(
        "-fx-background-color: transparent; "
            + "-fx-border-color: transparent; "
            + "-fx-focus-color: transparent; "
            + "-fx-faint-focus-color: transparent;");
    fileTree.setShowRoot(false);

    TreeItem<FileNode> root = buildFileTree(new File(docsPath));
    fileTree.setRoot(root);

    fileTree
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (obs, old, newVal) -> {
              if (newVal != null && newVal.getValue().isFile()) {
                String path = newVal.getValue().getPath();
                loadFile(path);
                addToHistory(path);
              }
            });

    VBox.setVgrow(fileTree, Priority.ALWAYS);
    sidebar.getChildren().addAll(sidebarTitle, fileTree);

    return sidebar;
  }

  private void toggleSidebar() {
    isSidebarVisible = !isSidebarVisible;

    if (isSidebarVisible) {
      sidebar.setOpacity(0);
      if (!splitPane.getItems().contains(sidebar)) {
        splitPane.getItems().add(0, sidebar);
        splitPane.setDividerPositions(prefs.getDouble("dividerPosition", 0.2));
      }

      FadeTransition fade = new FadeTransition(Duration.millis(200), sidebar);
      fade.setFromValue(0);
      fade.setToValue(1);
      fade.play();
    } else {
      FadeTransition fade = new FadeTransition(Duration.millis(200), sidebar);
      fade.setFromValue(1);
      fade.setToValue(0);
      fade.setOnFinished(e -> splitPane.getItems().remove(sidebar));
      fade.play();
    }
  }

  private VBox createContentArea() {
    VBox content = new VBox();
    content.setStyle("-fx-background-color: white; -fx-padding: 0;");

    webView = new WebView();
    webEngine = webView.getEngine();
    webEngine.setJavaScriptEnabled(true);

    webView.setOnScroll(
        event -> {
          double deltaY = event.getDeltaY() * 3;
          webEngine.executeScript("window.scrollBy(0, " + (-deltaY) + ");");
          event.consume();
        });

    // Add loading listener
    webEngine
        .getLoadWorker()
        .stateProperty()
        .addListener(
            (obs, oldState, newState) -> {
              switch (newState) {
                case RUNNING:
                  loadingIndicator.setVisible(true);
                  break;
                case SUCCEEDED:
                case FAILED:
                case CANCELLED:
                  loadingIndicator.setVisible(false);
                  break;
              }
            });

    VBox.setVgrow(webView, Priority.ALWAYS);
    content.getChildren().add(webView);

    return content;
  }

  private TreeItem<FileNode> buildFileTree(File directory) {
    TreeItem<FileNode> root =
        new TreeItem<>(new FileNode(directory.getName(), directory.getAbsolutePath(), false));
    root.setExpanded(true);

    if (directory.isDirectory()) {
      File[] files = directory.listFiles();
      if (files != null) {
        java.util.Arrays.sort(
            files,
            (f1, f2) -> {
              if (f1.isDirectory() && !f2.isDirectory()) return -1;
              if (!f1.isDirectory() && f2.isDirectory()) return 1;
              return f1.getName().compareTo(f2.getName());
            });

        for (File file : files) {
          if (file.isDirectory()) {
            TreeItem<FileNode> item = buildFileTree(file);
            root.getChildren().add(item);
          } else if (file.getName().endsWith(".html")) {
            TreeItem<FileNode> item =
                new TreeItem<>(
                    new FileNode(
                        file.getName().replace(".html", ""), file.getAbsolutePath(), true));
            root.getChildren().add(item);
          }
        }
      }
    }

    return root;
  }

  private void loadFile(String filePath) {
    try {
      statusLabel.setText("Loading: " + new File(filePath).getName());

      String content;
      if (fileCache.containsKey(filePath)) {
        content = fileCache.get(filePath);
      } else {
        content = Files.readString(Paths.get(filePath));
        fileCache.put(filePath, content);
      }

      String styledContent = injectCustomStyles(content);
      styledContent = injectCopyButtons(styledContent);
      webEngine.loadContent(styledContent);

      addToRecentFiles(filePath);
      statusLabel.setText("Loaded: " + new File(filePath).getName());

    } catch (IOException e) {
      statusLabel.setText("Error loading file");
      webEngine.loadContent(
          "<div style='padding: 40px; text-align: center;'>"
              + "<h1 style='color: #ef4444; font-family: system-ui;'>⚠️ Error Loading File</h1>"
              + "<p style='color: #6b7280; font-family: system-ui;'>"
              + e.getMessage()
              + "</p>"
              + "</div>");
    }
  }

  private String injectCopyButtons(String htmlContent) {
    String copyScript =
        """
        <script>
        document.addEventListener('DOMContentLoaded', function() {
            document.querySelectorAll('pre').forEach(function(pre) {
                if (!pre.querySelector('.copy-button')) {
                    var button = document.createElement('button');
                    button.className = 'copy-button';
                    button.textContent = '📋 Copy';
                    button.style.cssText = 'position: absolute; top: 8px; right: 8px; padding: 6px 12px; ' +
                        'background: #646cff; color: white; border: none; border-radius: 6px; ' +
                        'cursor: pointer; font-size: 12px; font-weight: 600; opacity: 0; transition: opacity 0.2s;';

                    pre.style.position = 'relative';
                    pre.appendChild(button);

                    pre.addEventListener('mouseenter', function() {
                        button.style.opacity = '1';
                    });
                    pre.addEventListener('mouseleave', function() {
                        button.style.opacity = '0';
                    });

                    button.addEventListener('click', function() {
                        var code = pre.querySelector('code') || pre;
                        var text = code.textContent;
                        navigator.clipboard.writeText(text).then(function() {
                            button.textContent = '✓ Copied!';
                            setTimeout(function() {
                                button.textContent = '📋 Copy';
                            }, 2000);
                        });
                    });
                }
            });
        });
        </script>
        """;

    if (htmlContent.contains("</body>")) {
      return htmlContent.replace("</body>", copyScript + "</body>");
    }
    return htmlContent;
  }

  private void addToHistory(String filePath) {
    if (historyIndex < history.size() - 1) {
      history.subList(historyIndex + 1, history.size()).clear();
    }

    history.add(filePath);
    if (history.size() > MAX_HISTORY) {
      history.removeFirst();
    } else {
      historyIndex++;
    }

    updateNavigationButtons();
  }

  private void addToRecentFiles(String filePath) {
    recentFiles.remove(filePath);
    recentFiles.addFirst(filePath);
    if (recentFiles.size() > MAX_RECENT) {
      recentFiles.removeLast();
    }

    recentFilesCombo.getItems().clear();
    recentFilesCombo
        .getItems()
        .addAll(
            recentFiles.stream()
                .map(path -> new File(path).getName())
                .collect(Collectors.toList()));
  }

  private void navigateBack() {
    if (historyIndex > 0) {
      historyIndex--;
      String filePath = history.get(historyIndex);
      loadFile(filePath);
      updateNavigationButtons();
    }
  }

  private void navigateForward() {
    if (historyIndex < history.size() - 1) {
      historyIndex++;
      String filePath = history.get(historyIndex);
      loadFile(filePath);
      updateNavigationButtons();
    }
  }

  private void updateNavigationButtons() {
    backButton.setDisable(historyIndex <= 0);
    forwardButton.setDisable(historyIndex >= history.size() - 1);
  }

  private void zoomIn() {
    currentZoom = Math.min(currentZoom + 0.1, 3.0);
    webView.setZoom(currentZoom);
  }

  private void zoomOut() {
    currentZoom = Math.max(currentZoom - 0.1, 0.5);
    webView.setZoom(currentZoom);
  }

  private void resetZoom() {
    currentZoom = 1.0;
    webView.setZoom(currentZoom);
  }

  private String injectCustomStyles(String htmlContent) {
    String customStyles =
        """
            <style>
                * {
                    box-sizing: border-box;
                }

                html {
                    scroll-behavior: smooth !important;
                }

                body {
                    font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif !important;
                    line-height: 1.7 !important;
                    color: #1e293b !important;
                    background: #ffffff !important;
                    font-size: 15px !important;
                    margin: 0 !important;
                    padding: 0 !important;
                }

                /* Hide default navigation and TOC */
                header[role="banner"],
                nav.toc {
                    display: none !important;
                }

                /* Main content area */
                .main-grid {
                    display: block !important;
                    max-width: 100% !important;
                }

                main[role="main"] {
                    padding: 48px 64px !important;
                    max-width: 1400px !important;
                    margin: 0 auto !important;
                }

                /* Header section */
                .header {
                    margin-bottom: 40px !important;
                    padding-bottom: 24px !important;
                    border-bottom: 3px solid #e5e7eb !important;
                    position: relative !important;
                }

                .header::after {
                    content: '' !important;
                    position: absolute !important;
                    bottom: -3px !important;
                    left: 0 !important;
                    width: 120px !important;
                    height: 3px !important;
                    background: linear-gradient(90deg, #646cff, #bd34fe) !important;
                }

                .header .title {
                    font-size: 3em !important;
                    background: linear-gradient(120deg, #646cff 0%, #bd34fe 100%) !important;
                    -webkit-background-clip: text !important;
                    -webkit-text-fill-color: transparent !important;
                    background-clip: text !important;
                    font-weight: 800 !important;
                    letter-spacing: -0.03em !important;
                    margin: 0 !important;
                }

                /* Inheritance tree */
                .inheritance {
                    background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%) !important;
                    padding: 20px 24px !important;
                    border-radius: 12px !important;
                    border-left: 4px solid #646cff !important;
                    margin: 24px 0 !important;
                    font-family: 'Fira Code', 'Consolas', monospace !important;
                    font-size: 14px !important;
                }

                .inheritance div {
                    margin-left: 24px !important;
                    padding-left: 20px !important;
                    border-left: 2px solid #cbd5e1 !important;
                }

                .inheritance a {
                    color: #646cff !important;
                    font-weight: 600 !important;
                }

                /* Class description */
                .class-description {
                    margin: 32px 0 !important;
                }

                .type-signature {
                    background: linear-gradient(135deg, #f8fafc 0%, #ffffff 100%) !important;
                    padding: 20px 24px !important;
                    border-radius: 12px !important;
                    border-left: 4px solid #10b981 !important;
                    margin: 20px 0 !important;
                    font-family: 'Fira Code', 'Consolas', monospace !important;
                    font-size: 15px !important;
                    box-shadow: 0 2px 4px rgba(0,0,0,0.05) !important;
                }

                .type-signature .modifiers {
                    color: #8b5cf6 !important;
                    font-weight: 600 !important;
                }

                .type-signature .element-name {
                    color: #0ea5e9 !important;
                    font-weight: 700 !important;
                }

                .type-signature .extends-implements {
                    color: #64748b !important;
                }

                /* Section headers */
                h2 {
                    font-size: 2em !important;
                    color: #646cff !important;
                    font-weight: 700 !important;
                    margin-top: 48px !important;
                    margin-bottom: 24px !important;
                    padding-bottom: 12px !important;
                    border-bottom: 2px solid #e5e7eb !important;
                    position: relative !important;
                }

                h2::before {
                    content: '' !important;
                    position: absolute !important;
                    bottom: -2px !important;
                    left: 0 !important;
                    width: 80px !important;
                    height: 2px !important;
                    background: linear-gradient(90deg, #646cff, #bd34fe) !important;
                }

                h3 {
                    font-size: 1.5em !important;
                    color: #475569 !important;
                    font-weight: 700 !important;
                    margin-top: 32px !important;
                    margin-bottom: 16px !important;
                }

                /* Summary tables */
                .summary-table,
                .two-column-summary,
                .three-column-summary {
                    width: 100% !important;
                    border-radius: 12px !important;
                    overflow: hidden !important;
                    box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1), 0 2px 4px -1px rgba(0,0,0,0.06) !important;
                    margin: 24px 0 !important;
                    border: none !important;
                    display: grid !important;
                }

                .three-column-summary {
                    grid-template-columns: auto 1fr 2fr !important;
                }

                .two-column-summary {
                    grid-template-columns: 1fr 2fr !important;
                }

                .table-header {
                    background: linear-gradient(135deg, #646cff 0%, #535bf2 100%) !important;
                    color: white !important;
                    font-weight: 700 !important;
                    text-transform: uppercase !important;
                    font-size: 12px !important;
                    letter-spacing: 1px !important;
                    padding: 16px 20px !important;
                    border: none !important;
                }

                .col-first,
                .col-second,
                .col-last,
                .col-constructor-name {
                    padding: 16px 20px !important;
                    border-bottom: 1px solid #e5e7eb !important;
                    border-right: 1px solid #f1f5f9 !important;
                }

                .col-last {
                    border-right: none !important;
                }

                .even-row-color {
                    background-color: #ffffff !important;
                }

                .odd-row-color {
                    background-color: #f9fafb !important;
                }

                .even-row-color:hover,
                .odd-row-color:hover {
                    background-color: #f0f4ff !important;
                    transition: background-color 0.2s ease !important;
                }

                /* Code elements */
                code {
                    background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%) !important;
                    padding: 3px 8px !important;
                    border-radius: 6px !important;
                    font-family: 'Fira Code', 'Consolas', 'Monaco', monospace !important;
                    color: #92400e !important;
                    font-size: 0.9em !important;
                    border: 1px solid #fbbf24 !important;
                    font-weight: 600 !important;
                }

                .member-signature code,
                .type-signature code {
                    background: transparent !important;
                    border: none !important;
                    padding: 0 !important;
                    color: inherit !important;
                }

                /* Member signatures */
                .member-signature {
                    background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%) !important;
                    border-left: 4px solid #0ea5e9 !important;
                    padding: 20px 24px !important;
                    border-radius: 12px !important;
                    margin: 20px 0 !important;
                    box-shadow: 0 2px 4px rgba(0,0,0,0.05) !important;
                    font-family: 'Fira Code', 'Consolas', monospace !important;
                    font-size: 14px !important;
                }

                .member-signature .modifiers {
                    color: #8b5cf6 !important;
                    font-weight: 600 !important;
                }

                .member-signature .return-type {
                    color: #059669 !important;
                    font-weight: 600 !important;
                }

                .member-signature .element-name {
                    color: #0ea5e9 !important;
                    font-weight: 700 !important;
                }

                .member-signature .parameters {
                    color: #64748b !important;
                }

                .member-signature .exceptions {
                    color: #dc2626 !important;
                    font-weight: 600 !important;
                }

                /* Links */
                a {
                    color: #646cff !important;
                    text-decoration: none !important;
                    font-weight: 500 !important;
                    transition: all 0.2s ease !important;
                    border-bottom: 2px solid transparent !important;
                }

                a:hover {
                    color: #535bf2 !important;
                    border-bottom-color: #646cff !important;
                }

                .member-name-link {
                    font-weight: 600 !important;
                }

                /* Notes section (dl.notes) */
                dl.notes {
                    background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%) !important;
                    border-left: 4px solid #f59e0b !important;
                    padding: 24px !important;
                    border-radius: 12px !important;
                    margin: 24px 0 !important;
                    box-shadow: 0 2px 4px rgba(0,0,0,0.05) !important;
                }

                dl.notes dt {
                    font-weight: 700 !important;
                    color: #92400e !important;
                    margin-top: 16px !important;
                    margin-bottom: 8px !important;
                    text-transform: uppercase !important;
                    font-size: 12px !important;
                    letter-spacing: 0.5px !important;
                }

                dl.notes dt:first-child {
                    margin-top: 0 !important;
                }

                dl.notes dd {
                    margin-left: 0 !important;
                    margin-bottom: 12px !important;
                    color: #78350f !important;
                }

                /* Tag list */
                .tag-list {
                    list-style: none !important;
                    padding: 0 !important;
                    margin: 8px 0 !important;
                }

                .tag-list li {
                    margin: 4px 0 !important;
                    padding-left: 20px !important;
                    position: relative !important;
                }

                .tag-list li::before {
                    content: '→' !important;
                    position: absolute !important;
                    left: 0 !important;
                    color: #f59e0b !important;
                    font-weight: bold !important;
                }

                /* Horizontal scroll */
                .horizontal-scroll {
                    overflow-x: auto !important;
                }

                /* Block elements */
                .block {
                    line-height: 1.8 !important;
                    color: #475569 !important;
                    margin: 16px 0 !important;
                }

                /* Details sections */
                .details {
                    margin-top: 48px !important;
                }

                .detail {
                    margin: 32px 0 !important;
                    padding: 24px !important;
                    background: #fafafa !important;
                    border-radius: 12px !important;
                    border: 1px solid #e5e7eb !important;
                }

                .detail:hover {
                    background: #f8f9fa !important;
                    border-color: #cbd5e1 !important;
                    transition: all 0.2s ease !important;
                }

                /* Member list */
                .member-list {
                    list-style: none !important;
                    padding: 0 !important;
                }

                /* Inherited list */
                .inherited-list {
                    background: #f8f9fa !important;
                    padding: 20px 24px !important;
                    border-radius: 8px !important;
                    margin: 16px 0 !important;
                    border-left: 3px solid #94a3b8 !important;
                }

                .inherited-list h3 {
                    font-size: 0.95em !important;
                    color: #64748b !important;
                    margin: 0 0 12px 0 !important;
                    font-weight: 600 !important;
                    text-transform: uppercase !important;
                    letter-spacing: 0.5px !important;
                }

                .inherited-list code {
                    background: white !important;
                    border-color: #e5e7eb !important;
                    color: #64748b !important;
                }

                /* Footer */
                footer {
                    margin-top: 64px !important;
                    padding: 32px 0 !important;
                    border-top: 2px solid #e5e7eb !important;
                }

                footer .legal-copy {
                    color: #94a3b8 !important;
                    font-size: 12px !important;
                    line-height: 1.8 !important;
                }

                footer a {
                    color: #646cff !important;
                    border-bottom: 1px solid transparent !important;
                }

                footer a:hover {
                    border-bottom-color: #646cff !important;
                }

                /* Paragraphs */
                p {
                    margin: 16px 0 !important;
                    line-height: 1.8 !important;
                }

                /* Lists */
                ul:not(.summary-list):not(.tag-list):not(.member-list):not(.nav-list):not(.sub-nav-list):not(.toc-list),
                ol:not(.toc-list):not(.sub-nav-list) {
                    line-height: 1.9 !important;
                    padding-left: 28px !important;
                    margin: 16px 0 !important;
                }

                li {
                    margin: 8px 0 !important;
                }

                /* Summary list */
                .summary-list {
                    list-style: none !important;
                    padding: 0 !important;
                }

                /* Caption */
                .caption {
                    font-size: 0.9em !important;
                    color: #64748b !important;
                    margin-bottom: 8px !important;
                    font-weight: 600 !important;
                    text-transform: uppercase !important;
                    letter-spacing: 0.5px !important;
                }

                /* Responsive adjustments */
                @media (max-width: 1200px) {
                    main[role="main"] {
                        padding: 32px 40px !important;
                    }
                }

                @media (max-width: 768px) {
                    main[role="main"] {
                        padding: 24px 20px !important;
                    }

                    .header .title {
                        font-size: 2em !important;
                    }

                    h2 {
                        font-size: 1.5em !important;
                    }
                }

                /* Scrollbar styling */
                ::-webkit-scrollbar {
                    width: 10px !important;
                    height: 10px !important;
                }

                ::-webkit-scrollbar-track {
                    background: #f1f5f9 !important;
                    border-radius: 5px !important;
                }

                ::-webkit-scrollbar-thumb {
                    background: linear-gradient(135deg, #646cff, #bd34fe) !important;
                    border-radius: 5px !important;
                }

                ::-webkit-scrollbar-thumb:hover {
                    background: linear-gradient(135deg, #535bf2, #a020f0) !important;
                }
            </style>
        """;

    if (htmlContent.contains("</head>")) {
      return htmlContent.replace("</head>", customStyles + "</head>");
    }

    return htmlContent;
  }

  private void showWelcome() {
    String welcomeHTML =
        """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }

                    body {
                        font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                        display: flex;
                        flex-direction: column;
                        align-items: center;
                        justify-content: center;
                        height: 100vh;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                        text-align: center;
                        overflow: hidden;
                    }

                    .logo {
                        font-size: 140px;
                        margin-bottom: 24px;
                        animation: float 3s ease-in-out infinite;
                        filter: drop-shadow(0 10px 20px rgba(0,0,0,0.3));
                    }

                    h1 {
                        font-size: 56px;
                        margin: 0 0 16px 0;
                        font-weight: 800;
                        letter-spacing: -0.03em;
                        background: linear-gradient(120deg, #ffffff 0%, #e0e7ff 100%);
                        -webkit-background-clip: text;
                        -webkit-text-fill-color: transparent;
                        background-clip: text;
                    }

                    .subtitle {
                        font-size: 22px;
                        opacity: 0.95;
                        margin: 12px 0;
                        font-weight: 400;
                    }

                    .hint {
                        font-size: 16px;
                        opacity: 0.8;
                        margin-top: 8px;
                        font-weight: 300;
                    }

                    .features {
                        display: flex;
                        gap: 36px;
                        margin-top: 56px;
                        flex-wrap: wrap;
                        justify-content: center;
                    }

                    .feature {
                        background: rgba(255,255,255,0.12);
                        padding: 28px 36px;
                        border-radius: 16px;
                        backdrop-filter: blur(20px);
                        border: 1px solid rgba(255,255,255,0.2);
                        transition: transform 0.3s ease, background 0.3s ease;
                        cursor: pointer;
                        min-width: 140px;
                    }

                    .feature:hover {
                        transform: translateY(-8px);
                        background: rgba(255,255,255,0.18);
                    }

                    .feature-icon {
                        font-size: 42px;
                        margin-bottom: 12px;
                    }

                    .feature-text {
                        font-size: 15px;
                        font-weight: 600;
                        letter-spacing: 0.3px;
                    }

                    .shortcuts {
                        margin-top: 48px;
                        padding: 24px 32px;
                        background: rgba(255,255,255,0.1);
                        border-radius: 12px;
                        backdrop-filter: blur(10px);
                        font-size: 14px;
                        line-height: 1.8;
                    }

                    .shortcuts h3 {
                        font-size: 16px;
                        margin-bottom: 12px;
                        font-weight: 700;
                    }

                    .shortcut-item {
                        display: flex;
                        justify-content: space-between;
                        padding: 4px 0;
                        opacity: 0.9;
                    }

                    .key {
                        background: rgba(255,255,255,0.2);
                        padding: 2px 8px;
                        border-radius: 4px;
                        font-family: monospace;
                        font-weight: 600;
                    }

                    @keyframes float {
                        0%, 100% {
                            transform: translateY(0px);
                        }
                        50% {
                            transform: translateY(-20px);
                        }
                    }

                    .version {
                        position: absolute;
                        bottom: 32px;
                        font-size: 13px;
                        opacity: 0.7;
                        font-weight: 500;
                    }
                </style>
            </head>
            <body>
                <div class="logo">⚡</div>
                <h1>Java Documentation Viewer</h1>
                <p class="subtitle">Lightning fast, beautiful documentation browser</p>
                <p class="hint">Select a file from the sidebar to get started</p>
                <div class="features">
                    <div class="feature">
                        <div class="feature-icon">🔍</div>
                        <div class="feature-text">Instant Search</div>
                    </div>
                    <div class="feature">
                        <div class="feature-icon">⚡</div>
                        <div class="feature-text">Fast Navigation</div>
                    </div>
                    <div class="feature">
                        <div class="feature-icon">🎨</div>
                        <div class="feature-text">Beautiful UI</div>
                    </div>
                    <div class="feature">
                        <div class="feature-icon">🌙</div>
                        <div class="feature-text">Dark Mode</div>
                    </div>
                    <div class="feature">
                        <div class="feature-icon">📋</div>
                        <div class="feature-text">Copy Code</div>
                    </div>
                    <div class="feature">
                        <div class="feature-icon">⌨️</div>
                        <div class="feature-text">Shortcuts</div>
                    </div>
                </div>
                <div class="shortcuts">
                    <h3>⌨️ Keyboard Shortcuts</h3>
                    <div class="shortcut-item">
                        <span>Search</span>
                        <span class="key">Ctrl+F</span>
                    </div>
                    <div class="shortcut-item">
                        <span>Toggle Sidebar</span>
                        <span class="key">Ctrl+B</span>
                    </div>
                    <div class="shortcut-item">
                        <span>Toggle Dark Mode</span>
                        <span class="key">Ctrl+D</span>
                    </div>
                    <div class="shortcut-item">
                        <span>Navigate Back/Forward</span>
                        <span class="key">Alt+←/→</span>
                    </div>
                    <div class="shortcut-item">
                        <span>Zoom In/Out</span>
                        <span class="key">Ctrl+±</span>
                    </div>
                    <div class="shortcut-item">
                        <span>Reset Zoom</span>
                        <span class="key">Ctrl+0</span>
                    </div>
                </div>
                <div class="version">Powered by JavaFX & WebView | Enhanced Edition</div>
            </body>
            </html>
        """;

    webEngine.loadContent(welcomeHTML);
    statusLabel.setText("Welcome - Ready to browse documentation");
  }

  private void filterFiles(String query) {
    if (query == null || query.isEmpty()) {
      fileTree.setRoot(buildFileTree(new File(docsPath)));
      statusLabel.setText("Ready");
      return;
    }

    TreeItem<FileNode> root = new TreeItem<>(new FileNode("🔍 Search Results", "", false));
    root.setExpanded(true);

    try {
      long count =
          Files.walk(Paths.get(docsPath))
              .filter(path -> path.toString().endsWith(".html"))
              .filter(
                  path -> path.getFileName().toString().toLowerCase().contains(query.toLowerCase()))
              .peek(
                  path -> {
                    TreeItem<FileNode> item =
                        new TreeItem<>(
                            new FileNode(
                                path.getFileName().toString().replace(".html", ""),
                                path.toString(),
                                true));
                    root.getChildren().add(item);
                  })
              .count();

      statusLabel.setText("Found " + count + " results for: " + query);
    } catch (IOException e) {
      e.printStackTrace();
      statusLabel.setText("Error searching files");
    }

    fileTree.setRoot(root);
  }

  private void toggleDarkMode() {
    isDarkMode = !isDarkMode;
    darkModeToggle.setText(isDarkMode ? "☀️" : "🌙");

    if (isDarkMode) {
      root.setStyle("-fx-background-color: #1e1e2e;");
      sidebar.setStyle(
          "-fx-background-color: #181825; "
              + "-fx-border-color: #313244; "
              + "-fx-border-width: 0 1 0 0; "
              + "-fx-padding: 24 12;");
    } else {
      root.setStyle("-fx-background-color: #ffffff;");
      sidebar.setStyle(
          "-fx-background-color: #fafafa; "
              + "-fx-border-color: #e5e7eb; "
              + "-fx-border-width: 0 1 0 0; "
              + "-fx-padding: 24 12;");
    }

    // Refresh current content with dark mode styles
    if (!history.isEmpty() && historyIndex >= 0 && historyIndex < history.size()) {
      String currentPath = history.get(historyIndex);
      try {
        String content = Files.readString(Paths.get(currentPath));
        String styledContent = injectCustomStyles(content);
        if (isDarkMode) {
          styledContent = injectDarkModeStyles(styledContent);
        }
        styledContent = injectCopyButtons(styledContent);
        webEngine.loadContent(styledContent);
      } catch (IOException e) {
        // Ignore errors on refresh
      }
    }
  }

  private String injectDarkModeStyles(String htmlContent) {
    String darkStyles =
        """
        <style>
            body {
                background: #1e1e2e !important;
                color: #cdd6f4 !important;
            }

            main[role="main"] {
                background: #1e1e2e !important;
            }

            .header {
                border-bottom-color: #313244 !important;
            }

            .header .title {
                background: linear-gradient(120deg, #89b4fa 0%, #f5c2e7 100%) !important;
                -webkit-background-clip: text !important;
                -webkit-text-fill-color: transparent !important;
                background-clip: text !important;
            }

            h2 {
                color: #89b4fa !important;
                border-bottom-color: #313244 !important;
            }

            h2::before {
                background: linear-gradient(90deg, #89b4fa, #f5c2e7) !important;
            }

            h3 {
                color: #a6adc8 !important;
            }

            .inheritance {
                background: linear-gradient(135deg, #313244 0%, #45475a 100%) !important;
                border-left-color: #89b4fa !important;
            }

            .type-signature {
                background: linear-gradient(135deg, #313244 0%, #1e1e2e 100%) !important;
                border-left-color: #a6e3a1 !important;
            }

            .type-signature .modifiers {
                color: #cba6f7 !important;
            }

            .type-signature .element-name {
                color: #89dceb !important;
            }

            .type-signature .extends-implements {
                color: #a6adc8 !important;
            }

            .table-header {
                background: linear-gradient(135deg, #89b4fa 0%, #74c7ec 100%) !important;
                color: #1e1e2e !important;
            }

            .col-first,
            .col-second,
            .col-last,
            .col-constructor-name {
                border-bottom-color: #313244 !important;
                border-right-color: #45475a !important;
            }

            .even-row-color {
                background-color: #1e1e2e !important;
            }

            .odd-row-color {
                background-color: #181825 !important;
            }

            .even-row-color:hover,
            .odd-row-color:hover {
                background-color: #313244 !important;
            }

            code {
                background: linear-gradient(135deg, #45475a 0%, #585b70 100%) !important;
                color: #f9e2af !important;
                border-color: #6c7086 !important;
            }

            .member-signature {
                background: linear-gradient(135deg, #313244 0%, #45475a 100%) !important;
                border-left-color: #89dceb !important;
            }

            .member-signature .modifiers {
                color: #cba6f7 !important;
            }

            .member-signature .return-type {
                color: #a6e3a1 !important;
            }

            .member-signature .element-name {
                color: #89dceb !important;
            }

            .member-signature .parameters {
                color: #a6adc8 !important;
            }

            .member-signature .exceptions {
                color: #f38ba8 !important;
            }

            a {
                color: #89b4fa !important;
            }

            a:hover {
                color: #74c7ec !important;
                border-bottom-color: #89b4fa !important;
            }

            dl.notes {
                background: linear-gradient(135deg, #45475a 0%, #585b70 100%) !important;
                border-left-color: #f9e2af !important;
            }

            dl.notes dt {
                color: #f9e2af !important;
            }

            dl.notes dd {
                color: #cdd6f4 !important;
            }

            .tag-list li::before {
                color: #f9e2af !important;
            }

            .block {
                color: #bac2de !important;
            }

            .detail {
                background: #181825 !important;
                border-color: #313244 !important;
            }

            .detail:hover {
                background: #313244 !important;
                border-color: #45475a !important;
            }

            .inherited-list {
                background: #313244 !important;
                border-left-color: #6c7086 !important;
            }

            .inherited-list h3 {
                color: #a6adc8 !important;
            }

            .inherited-list code {
                background: #45475a !important;
                border-color: #585b70 !important;
                color: #a6adc8 !important;
            }

            footer {
                border-top-color: #313244 !important;
            }

            footer .legal-copy {
                color: #6c7086 !important;
            }

            footer a {
                color: #89b4fa !important;
            }

            footer a:hover {
                border-bottom-color: #89b4fa !important;
            }

            .caption {
                color: #a6adc8 !important;
            }

            ::-webkit-scrollbar-track {
                background: #313244 !important;
            }

            ::-webkit-scrollbar-thumb {
                background: linear-gradient(135deg, #89b4fa, #f5c2e7) !important;
            }

            ::-webkit-scrollbar-thumb:hover {
                background: linear-gradient(135deg, #74c7ec, #f5c2e7) !important;
            }
        </style>
        """;

    if (htmlContent.contains("</head>")) {
      return htmlContent.replace("</head>", darkStyles + "</head>");
    }
    return htmlContent;
  }

  private void loadPreferences() {
    isDarkMode = prefs.getBoolean("darkMode", false);
    currentZoom = prefs.getDouble("zoom", 1.0);

    // Load recent files
    for (int i = 0; i < MAX_RECENT; i++) {
      String recentFile = prefs.get("recentFile" + i, null);
      if (recentFile != null && new File(recentFile).exists()) {
        recentFiles.add(recentFile);
      }
    }
  }

  private void savePreferences(Stage stage) {
    prefs.putBoolean("maximized", stage.isMaximized());
    if (!stage.isMaximized()) {
      prefs.putDouble("windowWidth", stage.getWidth());
      prefs.putDouble("windowHeight", stage.getHeight());
    }
    prefs.putBoolean("darkMode", isDarkMode);
    prefs.putDouble("zoom", currentZoom);
    prefs.putDouble("dividerPosition", splitPane.getDividerPositions()[0]);

    // Save recent files
    for (int i = 0; i < recentFiles.size() && i < MAX_RECENT; i++) {
      prefs.put("recentFile" + i, recentFiles.get(i));
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
