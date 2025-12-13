package com.javadocviewer.util;

import javafx.stage.Stage;
import java.io.File;
import java.util.LinkedList;
import java.util.prefs.Preferences;

/**
 * Manages application preferences and settings persistence.
 * Handles saving/loading window state, recent files, and user preferences.
 */
public class PreferencesManager {
    
    private static final int MAX_RECENT = 10;
    private final Preferences prefs;
    private LinkedList<String> recentFiles = new LinkedList<>();
    
    public PreferencesManager(Class<?> clazz) {
        this.prefs = Preferences.userNodeForPackage(clazz);
    }
    
    /**
     * Loads all preferences from storage.
     */
    public void loadPreferences() {
        // Load recent files
        recentFiles.clear();
        for (int i = 0; i < MAX_RECENT; i++) {
            String recentFile = prefs.get("recentFile" + i, null);
            if (recentFile != null && new File(recentFile).exists()) {
                recentFiles.add(recentFile);
            }
        }
    }
    
    /**
     * Saves all preferences to storage.
     */
    public void savePreferences(Stage stage) {
        // Save window state
        prefs.putBoolean("maximized", stage.isMaximized());
        if (!stage.isMaximized()) {
            prefs.putDouble("windowWidth", stage.getWidth());
            prefs.putDouble("windowHeight", stage.getHeight());
        }
        
        // Save recent files
        for (int i = 0; i < recentFiles.size() && i < MAX_RECENT; i++) {
            prefs.put("recentFile" + i, recentFiles.get(i));
        }
    }
    
    /**
     * Saves additional preferences.
     */
    public void saveAdditionalPrefs(boolean darkMode, double zoom, double dividerPosition) {
        prefs.putBoolean("darkMode", darkMode);
        prefs.putDouble("zoom", zoom);
        prefs.putDouble("dividerPosition", dividerPosition);
    }
    
    // Getters
    public boolean isDarkMode() {
        return prefs.getBoolean("darkMode", false);
    }
    
    public double getZoom() {
        return prefs.getDouble("zoom", 1.0);
    }
    
    public double getDividerPosition() {
        return prefs.getDouble("dividerPosition", 0.2);
    }
    
    public boolean wasMaximized() {
        return prefs.getBoolean("maximized", true);
    }
    
    public double getWindowWidth() {
        return prefs.getDouble("windowWidth", 1400);
    }
    
    public double getWindowHeight() {
        return prefs.getDouble("windowHeight", 900);
    }
    
    public LinkedList<String> getRecentFiles() {
        return recentFiles;
    }
    
    public void addRecentFile(String filePath) {
        recentFiles.remove(filePath);
        recentFiles.addFirst(filePath);
        if (recentFiles.size() > MAX_RECENT) {
            recentFiles.removeLast();
        }
    }
}
