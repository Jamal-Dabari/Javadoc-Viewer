package com.javadocviewer;

public class FileNode {
    private String name;
    private String path;
    private boolean isFile;

    public FileNode(String name, String path, boolean isFile) {
        this.name = name;
        this.path = path;
        this.isFile = isFile;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public boolean isFile() {
        return isFile;
    }

    @Override
    public String toString() {
        return (isFile ? "📄 " : "📁 ") + name;
    }
}
