
<h1 align="center">
  <br>
  <img src="https://raw.githubusercontent.com/Jamal-Dabari/Javadoc-Viewer/main/logo.svg" alt="Javadoc-Viewer" width="200">
  <br>
  Javadoc-Viewer
  <br>
</h1>

<h4 align="center">A minimal desktop app to view Java documentation.</h4>

<p align="center">
 
</p>

<p align="center">
  <a href="#key-features">Key Features</a> •
  <a href="#how-to-use">How To Use</a> •
  <a href="#download">Download</a> •
  <a href="#credits">Credits</a> •
  <a href="#related">Related</a> •
  <a href="#license">License</a>
</p>

![Watch the Javadoc Viewer Demo](https://raw.githubusercontent.com/Jamal-Dabari/Javadoc-Viewer/main/Javadoc-Viewer-Demo-Gif.gif)

![JavaFX](https://img.shields.io/badge/JavaFX-21.0.1-blue) ![Java](https://img.shields.io/badge/Java-17+-orange) ![Maven](https://img.shields.io/badge/Maven-3.8+-red) ![License](https://img.shields.io/badge/license-MIT-green)

## ✨ Key Features

### 🎨 Beautiful Modern UI

- **Custom Window Decorations**
- Frameless window with rounded corners and modern styling
- **Gradient Navbar**
- Eye-catching purple gradient header with smooth animations
- **Professional Styling**
- Enhanced JavaDoc rendering with syntax highlighting
- **Responsive Design**
- Adapts beautifully to any window size

### 🌙 Dark Mode

- Toggle between light and dark themes with a single click (Ctrl+D)
- Carefully crafted color schemes for optimal readability
- Smooth transitions between modes
- Dark mode preference saved between sessions

### 🔍 Powerful Search
- **Instant Search**
- Find documentation files as you type
- **Real-time Filtering**
- Results update instantly
- **Keyboard Shortcut**
- Quick access with Ctrl+F
- **Result Count**
- See how many matches were found

### 🧭 Smart Navigation
- **History Management**
- Navigate back and forward through viewed files (Alt+← / Alt+→)
- **Recent Files**
- Quick access to recently viewed documentation
- **Sidebar Toggle**
- Show/hide file tree with Ctrl+B
- **File Tree**
- Organized hierarchical view of all documentation

### 📋 Code Features
- **Copy Code Blocks**
- Hover over code snippets to reveal copy button
- **Syntax Highlighting**
- Beautiful code presentation
- **Custom Styling**
- Enhanced readability with professional typography
- **Smooth Scrolling**
- Optimized scroll experience

### ⚡ Performance

- **File Caching**
- Lightning-fast loading of previously viewed files
- **Efficient Rendering**
- Smooth WebView integration
- **Minimal Memory Footprint**
- Optimized for speed
- ### ⌨️ Keyboard Shortcuts
- **Ctrl+F**
- Focus search / Find documentation
- **Ctrl+B**
- Toggle sidebar visibility
- **Ctrl+D**
- Toggle dark/light mode
- **Alt+←**
- Navigate back in history
- **Alt+→**
- Navigate forward in history
- **Ctrl++**
- Zoom in
- **Ctrl+-**
- Zoom out
- **Ctrl+0**
- Reset zoom to 100%

### 🪟 Window Controls

- **Draggable Window**
- Click and drag anywhere on the navbar to move window
- **Minimize/Maximize/Close**
- Modern window control buttons
- **Double-Click to Maximize**
- Quick window resize
- **Full Screen Mode**
- Distraction-free documentation reading

### 💾 Persistence

- **Remembers Window Size**
- Restores your preferred dimensions
- **Saves Preferences**
- Dark mode, zoom level, and more
- **Recent Files List**
- Keeps track of your browsing history
- **Sidebar Position**
- Remembers your preferred split pane position

## 🚀 How To Use

### Prerequisites

You'll need the following installed on your computer:
- [Java Development Kit (JDK) 17+](https://adoptium.net/)

- [Apache Maven 3.8+](https://maven.apache.org/download.cgi)

- [Git](https://git-scm.com) (optional, for cloning)

### Running the Application

From your command line:
```
bash

# Clone this repository (or download the ZIP)

$ 
git
 clone https://github.com/Jamal-Dabari/javadoc-viewer.git
# Go into the repository

$ 
cd
 javadoc-viewer
# Place your JavaDoc HTML files in the 'docs' folder

# (The app looks for files in ./docs by default)

# Build the project with Maven

$ mvn clean package
# Run the application

$ mvn javafx:run

```

### Alternative: Run the JAR directly

```
bash

# After building with Maven

$ java -jar target/javadoc-viewer.jar

```

> ** Note **
  
> Make sure your 
`docs`

> folder contains the JavaDoc HTML files you want to browse. You can generate JavaDocs for any Java project using:

```bash
>
 javadoc -d docs -sourcepath src -subpackages com.yourpackage
>
 ```

## 📦 Building Standalone Executable

To create a standalone JAR file:
```
bash

# Create shaded JAR with all dependencies

$ mvn clean package
# The executable JAR will be in target/javadoc-viewer.jar

```

## 🎯 Project Structure

```

javadoc-viewer/
├── src/main/java/com/javadocviewer/
│   ├── JavaDocViewer.java          # Main application class
│   ├── model/
│   │   └── FileNode.java           # File tree node model
│   └── util/
│       ├── NavigationHistory.java  # Browser history manager
│       ├── RecentFilesManager.java # Recent files tracker
│       ├── StyleInjector.java      # CSS injection utility
│       └── WelcomePageGenerator.java
├── docs/                            # Place JavaDoc HTML files here
├── pom.xml                          # Maven configuration
└── README.md

```

## 🛠️ Technologies Used

This software is built with the following open source technologies:
- **[JavaFX 21.0.1](https://openjfx.io/)**
- Modern UI framework for Java
- **[Maven](https://maven.apache.org/)**
- Dependency management and build tool
-**[WebView](https://openjfx.io/javadoc/21/javafx.web/javafx/scene/web/WebView.html)**
- HTML rendering engine
- **[Java NIO](https://docs.oracle.com/en/java/javase/17/core/java-nio.html)**
- File system operations
- **[Java Preferences API](https://docs.oracle.com/en/java/javase/17/docs/api/java.prefs/java/util/prefs/Preferences.html)**
- Settings persistence
   

## 📸 Screenshots

### Light Mode

![Light Mode](screenshots/light-mode.png)

### Dark Mode
![Dark Mode](screenshots/dark-mode.png)

### Welcome Screen

![Welcome Screen](screenshots/welcome.png)

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the 
[LICENSE](LICENSE) file for details.

##🌟 Future Enhancements
- [ ] PDF export of documentation
- [ ] Bookmarks and favorites
- [ ] Multiple documentation sets
- [ ] Search within page content
- [ ] Table of contents panel
- [ ] Export settings/preferences
- [ ] Custom CSS themes
- [ ] Command palette (Ctrl+P)

## 💬 Support

If you found this project helpful, please consider:

-⭐ Starring the repository
-🐛 Reporting bugs and issues
-💡 Suggesting new features
-🔀 Contributing code improvements

## 🙏 Acknowledgments

- Oracle for the comprehensive JavaDoc format specification
- The JavaFX community for excellent documentation and examples
- All contributors who help improve this project

## 📧 Contact

Questions? Suggestions? Feel free to reach out or open an issue!
---

**Made with ❤️ and ☕ by developers, for developers**









