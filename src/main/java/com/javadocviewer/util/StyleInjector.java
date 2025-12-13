package com.javadocviewer.util;

public class StyleInjector {

  public static String injectCustomStyles(String htmlContent, boolean isDarkMode) {
    String customStyles = getLightModeStyles();

    if (htmlContent.contains("</head>")) {
      String result = htmlContent.replace("</head>", customStyles + "</head>");

      if (isDarkMode) {
        result = injectDarkModeStyles(result);
      }

      return result;
    }

    return htmlContent;
  }

  public static String injectCopyButtons(String htmlContent) {
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

  private static String injectDarkModeStyles(String htmlContent) {
    String darkStyles =
        """
        <style>
            body {
                background: #1e1e2e !important;
                color: #cdd6f4 !important;
            }
            /* ... rest of dark mode styles ... */
        </style>
        """;

    if (htmlContent.contains("</head>")) {
      return htmlContent.replace("</head>", darkStyles + "</head>");
    }
    return htmlContent;
  }

  private static String getLightModeStyles() {
    return """
    <style>
        * {
            box-sizing: border-box;
        }
        /* ... rest of light mode styles ... */
    </style>
    """;
  }
}
