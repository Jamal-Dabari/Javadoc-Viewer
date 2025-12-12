package com.javadocviewer.util;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class RecentFilesManager {
    private LinkedList<String> recentFiles = new LinkedList<>();
    private static final int MAX_RECENT = 10;

    public void add(String filePath) {
        recentFiles.remove(filePath);
        recentFiles.addFirst(filePath);
        if (recentFiles.size() > MAX_RECENT) {
            recentFiles.removeLast();
        }
    }

    public List<String> getFileNames() {
        return recentFiles.stream()
                .map(path -> new File(path).getName())
                .collect(Collectors.toList());
    }

    public List<String> getFilePaths() {
        return new LinkedList<>(recentFiles);
    }

    public void clear() {
        recentFiles.clear();
    }

    public int size() {
        return recentFiles.size();
    }
}
