package com.javadocviewer.util;

import java.util.LinkedList;

public class NavigationHistory {
                                private LinkedList<String> history = new LinkedList<>();
                                private int historyIndex = -1;
                                private static final int MAX_HISTORY = 50;

                                public void add(String filePath) {
                                                                if (historyIndex < history.size() - 1) {
                                                                                                history.subList(historyIndex + 1, history.size()).clear();
                                                                }

                                                                history.add(filePath);
                                                                if (history.size() > MAX_HISTORY) {
                                                                                                history.removeFirst();
                                                                } else {
                                                                                                historyIndex++;
                                                                }
                                }

                                public boolean canGoBack() {
                                                                return historyIndex > 0;
                                }

                                public boolean canGoForward() {
                                                                return historyIndex < history.size() - 1;
                                }

                                public String goBack() {
                                                                if (canGoBack()) {
                                                                                                historyIndex--;
                                                                                                return history.get(historyIndex);
                                                                }
                                                                return null;
                                }

                                public String goForward() {
                                                                if (canGoForward()) {
                                                                                                historyIndex++;
                                                                                                return history.get(historyIndex);
                                                                }
                                                                return null;
                                }

                                public String getCurrent() {
                                                                if (historyIndex >= 0 && historyIndex < history.size()) {
                                                                                                return history.get(historyIndex);
                                                                }
                                                                return null;
                                }

                                public boolean isEmpty() {
                                                                return history.isEmpty();
                                }

                                public int getCurrentIndex() {
                                                                return historyIndex;
                                }

                                public int size() {
                                                                return history.size();
                                }
}
