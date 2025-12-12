// Enhanced Java Documentation with Interactive Features
// Add this script to make documentation more navigable and user-friendly

(function() {
    'use strict';

    // Wait for DOM to be fully loaded
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

    function init() {
        addBackToTopButton();
        addSmoothScrolling();
        addTOCHighlighting();
        addSearchEnhancement();
        addCodeCopyButtons();
        addSectionLinks();
        addKeyboardShortcuts();
        addDarkModeToggle();
        addTableOfContentsCollapse();
        addBreadcrumbNavigation();
    }

    // ============================================
    // BACK TO TOP BUTTON
    // ============================================
    function addBackToTopButton() {
        const button = document.createElement('button');
        button.id = 'backToTop';
        button.innerHTML = '↑';
        button.title = 'Back to top (Alt+T)';
        button.style.display = 'none';
        document.body.appendChild(button);

        // Show/hide based on scroll position
        window.addEventListener('scroll', () => {
            if (window.pageYOffset > 300) {
                button.style.display = 'block';
            } else {
                button.style.display = 'none';
            }
        });

        // Scroll to top on click
        button.addEventListener('click', () => {
            window.scrollTo({
                top: 0,
                behavior: 'smooth'
            });
        });
    }

    // ============================================
    // SMOOTH SCROLLING FOR ALL ANCHOR LINKS
    // ============================================
    function addSmoothScrolling() {
        document.querySelectorAll('a[href^="#"]').forEach(anchor => {
            anchor.addEventListener('click', function(e) {
                const href = this.getAttribute('href');
                if (href !== '#' && href !== '#skip-navbar-top') {
                    e.preventDefault();
                    const target = document.querySelector(href);
                    if (target) {
                        const offset = 100; // Account for sticky header
                        const targetPosition = target.getBoundingClientRect().top + window.pageYOffset - offset;
                        window.scrollTo({
                            top: targetPosition,
                            behavior: 'smooth'
                        });
                        // Update URL without jumping
                        history.pushState(null, null, href);
                    }
                }
            });
        });
    }

    // ============================================
    // HIGHLIGHT ACTIVE SECTION IN TOC
    // ============================================
    function addTOCHighlighting() {
        const tocLinks = document.querySelectorAll('.toc a');
        const sections = Array.from(tocLinks).map(link => {
            const href = link.getAttribute('href');
            if (href && href.startsWith('#')) {
                return document.querySelector(href);
            }
            return null;
        }).filter(Boolean);

        if (sections.length === 0) return;

        function updateActiveTOC() {
            let current = '';
            const scrollPos = window.pageYOffset + 150;

            sections.forEach(section => {
                const sectionTop = section.offsetTop;
                if (scrollPos >= sectionTop) {
                    current = section.getAttribute('id');
                }
            });

            tocLinks.forEach(link => {
                link.classList.remove('active-toc');
                const href = link.getAttribute('href');
                if (href === '#' + current) {
                    link.classList.add('active-toc');
                    // Add CSS for active state if not already present
                    if (!document.getElementById('toc-active-style')) {
                        const style = document.createElement('style');
                        style.id = 'toc-active-style';
                        style.textContent = `
                            .active-toc {
                                background-color: #e7f0ff !important;
                                color: #667eea !important;
                                border-left-color: #667eea !important;
                                font-weight: 600 !important;
                            }
                        `;
                        document.head.appendChild(style);
                    }
                }
            });
        }

        window.addEventListener('scroll', updateActiveTOC);
        updateActiveTOC();
    }

    // ============================================
    // ENHANCED SEARCH FUNCTIONALITY
    // ============================================
    function addSearchEnhancement() {
        const searchInput = document.getElementById('search-input');
        if (!searchInput) return;

        // Add search results count
        const resultsDiv = document.createElement('div');
        resultsDiv.id = 'search-results-info';
        resultsDiv.style.cssText = `
            position: fixed;
            top: 120px;
            right: 20px;
            background: white;
            padding: 10px 15px;
            border-radius: 5px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            display: none;
            z-index: 999;
        `;
        document.body.appendChild(resultsDiv);

        searchInput.addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase();
            if (searchTerm.length < 2) {
                resultsDiv.style.display = 'none';
                clearHighlights();
                return;
            }

            highlightSearchTerms(searchTerm);
        });
    }

    function highlightSearchTerms(term) {
        clearHighlights();
        const main = document.querySelector('main');
        if (!main) return;

        const walker = document.createTreeWalker(
            main,
            NodeFilter.SHOW_TEXT,
            null,
            false
        );

        const nodesToHighlight = [];
        let node;
        while (node = walker.nextNode()) {
            if (node.textContent.toLowerCase().includes(term)) {
                nodesToHighlight.push(node);
            }
        }

        nodesToHighlight.forEach(node => {
            const span = document.createElement('span');
            const text = node.textContent;
            const regex = new RegExp(`(${term})`, 'gi');
            span.innerHTML = text.replace(regex, '<mark style="background: #ffeb3b; padding: 2px 4px;">$1</mark>');
            node.parentNode.replaceChild(span, node);
            span.classList.add('search-highlight-wrapper');
        });
    }

    function clearHighlights() {
        document.querySelectorAll('.search-highlight-wrapper').forEach(wrapper => {
            wrapper.replaceWith(wrapper.textContent);
        });
    }

    // ============================================
    // COPY CODE BUTTONS
    // ============================================
    function addCodeCopyButtons() {
        document.querySelectorAll('.member-signature').forEach(codeBlock => {
            const button = document.createElement('button');
            button.className = 'copy-code-btn';
            button.innerHTML = '📋 Copy';
            button.style.cssText = `
                position: absolute;
                top: 10px;
                right: 10px;
                background: #667eea;
                color: white;
                border: none;
                padding: 6px 12px;
                border-radius: 4px;
                cursor: pointer;
                font-size: 12px;
                opacity: 0;
                transition: opacity 0.2s;
            `;

            codeBlock.style.position = 'relative';
            codeBlock.appendChild(button);

            codeBlock.addEventListener('mouseenter', () => {
                button.style.opacity = '1';
            });

            codeBlock.addEventListener('mouseleave', () => {
                button.style.opacity = '0';
            });

            button.addEventListener('click', () => {
                const code = codeBlock.textContent.replace('📋 Copy', '').trim();
                navigator.clipboard.writeText(code).then(() => {
                    button.innerHTML = '✓ Copied!';
                    button.style.background = '#28a745';
                    setTimeout(() => {
                        button.innerHTML = '📋 Copy';
                        button.style.background = '#667eea';
                    }, 2000);
                });
            });
        });
    }

    // ============================================
    // SECTION PERMALINK BUTTONS
    // ============================================
    function addSectionLinks() {
        document.querySelectorAll('h2[id], h3[id]').forEach(heading => {
            const link = document.createElement('a');
            link.className = 'section-link';
            link.href = '#' + heading.id;
            link.innerHTML = '🔗';
            link.title = 'Link to this section';
            link.style.cssText = `
                margin-left: 10px;
                opacity: 0;
                font-size: 0.7em;
                text-decoration: none;
                transition: opacity 0.2s;
            `;

            heading.appendChild(link);
            heading.style.position = 'relative';

            heading.addEventListener('mouseenter', () => {
                link.style.opacity = '0.6';
            });

            heading.addEventListener('mouseleave', () => {
                link.style.opacity = '0';
            });

            link.addEventListener('click', (e) => {
                e.preventDefault();
                const url = window.location.origin + window.location.pathname + '#' + heading.id;
                navigator.clipboard.writeText(url).then(() => {
                    const tooltip = document.createElement('span');
                    tooltip.textContent = 'Link copied!';
                    tooltip.style.cssText = `
                        position: absolute;
                        top: -30px;
                        left: 50%;
                        transform: translateX(-50%);
                        background: #28a745;
                        color: white;
                        padding: 5px 10px;
                        border-radius: 4px;
                        font-size: 12px;
                        white-space: nowrap;
                    `;
                    heading.appendChild(tooltip);
                    setTimeout(() => tooltip.remove(), 2000);
                });
            });
        });
    }

    // ============================================
    // KEYBOARD SHORTCUTS
    // ============================================
    function addKeyboardShortcuts() {
        document.addEventListener('keydown', (e) => {
            // Alt+T: Back to top
            if (e.altKey && e.key === 't') {
                e.preventDefault();
                window.scrollTo({ top: 0, behavior: 'smooth' });
            }
            
            // Alt+S: Focus search
            if (e.altKey && e.key === 's') {
                e.preventDefault();
                const searchInput = document.getElementById('search-input');
                if (searchInput) searchInput.focus();
            }

            // Alt+D: Toggle dark mode
            if (e.altKey && e.key === 'd') {
                e.preventDefault();
                toggleDarkMode();
            }
        });

        // Add keyboard shortcuts help
        addKeyboardShortcutsHelp();
    }

    function addKeyboardShortcutsHelp() {
        const helpButton = document.createElement('button');
        helpButton.innerHTML = '⌨️';
        helpButton.title = 'Keyboard shortcuts';
        helpButton.style.cssText = `
            position: fixed;
            bottom: 90px;
            right: 30px;
            background: white;
            color: #667eea;
            border: 2px solid #667eea;
            border-radius: 50%;
            width: 50px;
            height: 50px;
            font-size: 20px;
            cursor: pointer;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            z-index: 1000;
        `;
        document.body.appendChild(helpButton);

        helpButton.addEventListener('click', () => {
            const modal = document.createElement('div');
            modal.style.cssText = `
                position: fixed;
                top: 50%;
                left: 50%;
                transform: translate(-50%, -50%);
                background: white;
                padding: 30px;
                border-radius: 10px;
                box-shadow: 0 8px 32px rgba(0,0,0,0.2);
                z-index: 10000;
                max-width: 400px;
            `;
            modal.innerHTML = `
                <h3 style="margin-top: 0; color: #667eea;">Keyboard Shortcuts</h3>
                <ul style="list-style: none; padding: 0;">
                    <li style="margin: 10px 0;"><kbd>Alt+T</kbd> - Back to top</li>
                    <li style="margin: 10px 0;"><kbd>Alt+S</kbd> - Focus search</li>
                    <li style="margin: 10px 0;"><kbd>Alt+D</kbd> - Toggle dark mode</li>
                </ul>
                <button id="closeModal" style="
                    background: #667eea;
                    color: white;
                    border: none;
                    padding: 10px 20px;
                    border-radius: 5px;
                    cursor: pointer;
                    margin-top: 15px;
                ">Close</button>
            `;

            const overlay = document.createElement('div');
            overlay.style.cssText = `
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(0,0,0,0.5);
                z-index: 9999;
            `;

            document.body.appendChild(overlay);
            document.body.appendChild(modal);

            document.getElementById('closeModal').addEventListener('click', () => {
                modal.remove();
                overlay.remove();
            });

            overlay.addEventListener('click', () => {
                modal.remove();
                overlay.remove();
            });
        });
    }

    // ============================================
    // DARK MODE TOGGLE
    // ============================================
    function addDarkModeToggle() {
        const toggle = document.createElement('button');
        toggle.id = 'darkModeToggle';
        toggle.innerHTML = '🌙';
        toggle.title = 'Toggle dark mode (Alt+D)';
        toggle.style.cssText = `
            position: fixed;
            bottom: 150px;
            right: 30px;
            background: white;
            color: #667eea;
            border: 2px solid #667eea;
            border-radius: 50%;
            width: 50px;
            height: 50px;
            font-size: 20px;
            cursor: pointer;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            z-index: 1000;
        `;
        document.body.appendChild(toggle);

        // Check if dark mode was previously enabled
        if (localStorage.getItem('darkMode') === 'enabled') {
            enableDarkMode();
        }

        toggle.addEventListener('click', toggleDarkMode);
    }

    function toggleDarkMode() {
        if (document.body.classList.contains('dark-mode')) {
            disableDarkMode();
        } else {
            enableDarkMode();
        }
    }

    function enableDarkMode() {
        document.body.classList.add('dark-mode');
        document.getElementById('darkModeToggle').innerHTML = '☀️';
        localStorage.setItem('darkMode', 'enabled');

        // Add dark mode styles if not present
        if (!document.getElementById('dark-mode-style')) {
            const style = document.createElement('style');
            style.id = 'dark-mode-style';
            style.textContent = `
                .dark-mode {
                    background-color: #1a1a1a !important;
                    color: #e0e0e0 !important;
                }
                .dark-mode main,
                .dark-mode .toc {
                    background-color: #2d2d2d !important;
                    color: #e0e0e0 !important;
                }
                .dark-mode h1, .dark-mode h2, .dark-mode h3,
                .dark-mode h4, .dark-mode h5, .dark-mode h6 {
                    color: #bb86fc !important;
                }
                .dark-mode .member-signature,
                .dark-mode code {
                    background-color: #1e1e1e !important;
                    color: #e0e0e0 !important;
                }
                .dark-mode .summary-table > div {
                    border-color: #404040 !important;
                }
                .dark-mode .even-row-color {
                    background-color: #2d2d2d !important;
                }
                .dark-mode .odd-row-color {
                    background-color: #252525 !important;
                }
                .dark-mode a {
                    color: #bb86fc !important;
                }
                .dark-mode .sub-nav {
                    background-color: #2d2d2d !important;
                    border-color: #404040 !important;
                }
                .dark-mode dl.notes {
                    background-color: #1e1e1e !important;
                }
            `;
            document.head.appendChild(style);
        }
    }

    function disableDarkMode() {
        document.body.classList.remove('dark-mode');
        document.getElementById('darkModeToggle').innerHTML = '🌙';
        localStorage.setItem('darkMode', 'disabled');
    }

    // ============================================
    // COLLAPSIBLE TABLE OF CONTENTS SECTIONS
    // ============================================
    function addTableOfContentsCollapse() {
        const tocSections = document.querySelectorAll('.toc-list > li');
        tocSections.forEach(section => {
            const subList = section.querySelector('.toc-list');
            if (subList) {
                const link = section.querySelector('a');
                const toggle = document.createElement('span');
                toggle.innerHTML = '▼';
                toggle.style.cssText = `
                    cursor: pointer;
                    margin-right: 5px;
                    font-size: 10px;
                    display: inline-block;
                    transition: transform 0.2s;
                `;
                link.insertBefore(toggle, link.firstChild);

                toggle.addEventListener('click', (e) => {
                    e.preventDefault();
                    e.stopPropagation();
                    subList.style.display = subList.style.display === 'none' ? 'block' : 'none';
                    toggle.style.transform = subList.style.display === 'none' ? 'rotate(-90deg)' : 'rotate(0deg)';
                });
            }
        });
    }

    // ============================================
    // BREADCRUMB NAVIGATION ENHANCEMENT
    // ============================================
    function addBreadcrumbNavigation() {
        const breadcrumbs = document.querySelector('.sub-nav-list');
        if (breadcrumbs) {
            breadcrumbs.style.display = 'flex';
            breadcrumbs.style.flexWrap = 'wrap';
        }
    }

})();