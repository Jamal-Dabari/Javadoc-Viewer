package com.javadocviewer.util;

public class WelcomePageGenerator {

  public static String generateWelcomePage() {
    return """
    <!DOCTYPE html>
    <html>
    <head>
        <style>
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }
            /* ... welcome page styles ... */
        </style>
    </head>
    <body>
        <div class="logo">⚡</div>
        <h1>Java Documentation Viewer</h1>
        <!-- ... rest of welcome page HTML ... -->
    </body>
    </html>
    """;
  }
}
