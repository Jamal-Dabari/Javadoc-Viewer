package com.javadocviewer.util;

import com.javadocviewer.model.FileNode;
import javafx.scene.control.TreeItem;
import java.io.File;
import java.util.Arrays;

/**
 * Builds a tree structure from a directory of HTML documentation files.
 * Creates a hierarchical TreeView representation of the file system.
 */
public class FileTreeBuilder {
    
    /**
     * Builds a tree structure from the given directory.
     * Directories are sorted first, then files alphabetically.
     * Only includes .html files.
     * 
     * @param directory The root directory to build the tree from
     * @return TreeItem representing the directory structure
     */
    public static TreeItem<FileNode> buildFileTree(File directory) {
        TreeItem<FileNode> root = new TreeItem<>(
            new FileNode(
                directory.getName(),
                directory.getAbsolutePath(),
                false
            )
        );
        root.setExpanded(true);
        
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                // Sort: directories first, then files, alphabetically
                Arrays.sort(files, (f1, f2) -> {
                    if (f1.isDirectory() && !f2.isDirectory()) return -1;
                    if (!f1.isDirectory() && f2.isDirectory()) return 1;
                    return f1.getName().compareToIgnoreCase(f2.getName());
                });
                
                for (File file : files) {
                    if (file.isDirectory()) {
                        // Recursively build tree for subdirectories
                        TreeItem<FileNode> item = buildFileTree(file);
                        root.getChildren().add(item);
                    } else if (file.getName().endsWith(".html")) {
                        // Add HTML files to the tree
                        TreeItem<FileNode> item = new TreeItem<>(
                            new FileNode(
                                file.getName().replace(".html", ""),
                                file.getAbsolutePath(),
                                true
                            )
                        );
                        root.getChildren().add(item);
                    }
                }
            }
        }
        
        return root;
    }
    
    /**
     * Creates a search results tree from files matching a query.
     * 
     * @param docsPath The root path to search in
     * @param query The search query
     * @return TreeItem containing search results
     */
    public static TreeItem<FileNode> buildSearchTree(String docsPath, String query) {
        TreeItem<FileNode> root = new TreeItem<>(
            new FileNode("🔍 Search Results", "", false)
        );
        root.setExpanded(true);
        
        // This would typically use Files.walk() to search
        // For now, return the empty search results root
        return root;
    }
}
