#!/usr/bin/env python3
"""
Script to inject custom CSS into Java documentation HTML files.
This adds a link to javadoc-readable.css in all HTML files.
"""

import os
import glob
import sys
from pathlib import Path

def inject_css_link(directory, css_filename='javadoc-readable.css', dry_run=False):
    """
    Inject CSS link into HTML files in the specified directory.
    
    Args:
        directory: Path to directory containing HTML files
        css_filename: Name of the CSS file to link
        dry_run: If True, only show what would be changed without modifying files
    """
    # Create the CSS link tag
    css_link = f'<link rel="stylesheet" type="text/css" href="{css_filename}">'
    
    # Find all HTML files recursively
    search_pattern = os.path.join(directory, '**', '*.html')
    html_files = glob.glob(search_pattern, recursive=True)
    
    if not html_files:
        print(f"No HTML files found in {directory}")
        return
    
    print(f"Found {len(html_files)} HTML files")
    print(f"CSS file to link: {css_filename}")
    print(f"Dry run mode: {dry_run}")
    print("-" * 60)
    
    modified_count = 0
    skipped_count = 0
    error_count = 0
    
    for filepath in html_files:
        try:
            # Read the file
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            
            # Check if CSS link already exists
            if css_link in content:
                print(f"SKIP: {filepath} (already has CSS link)")
                skipped_count += 1
                continue
            
            # Check if file has a </head> tag
            if '</head>' not in content:
                print(f"SKIP: {filepath} (no </head> tag found)")
                skipped_count += 1
                continue
            
            # Inject the CSS link before </head>
            new_content = content.replace('</head>', f'{css_link}\n</head>')
            
            if dry_run:
                print(f"WOULD MODIFY: {filepath}")
                modified_count += 1
            else:
                # Write the modified content back
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(new_content)
                print(f"MODIFIED: {filepath}")
                modified_count += 1
                
        except Exception as e:
            print(f"ERROR: {filepath} - {str(e)}")
            error_count += 1
    
    # Print summary
    print("-" * 60)
    print("SUMMARY:")
    print(f"  Total files found: {len(html_files)}")
    print(f"  Modified: {modified_count}")
    print(f"  Skipped: {skipped_count}")
    print(f"  Errors: {error_count}")
    
    if dry_run:
        print("\nThis was a DRY RUN - no files were actually modified.")
        print("Run again without --dry-run to make actual changes.")

def main():
    """Main function to handle command-line usage."""
    import argparse
    
    parser = argparse.ArgumentParser(
        description='Inject CSS link into Java documentation HTML files',
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  # Dry run to see what would be changed
  python inject_css.py /path/to/docs --dry-run
  
  # Actually modify the files
  python inject_css.py /path/to/docs
  
  # Use current directory
  python inject_css.py .
  
  # Specify custom CSS filename
  python inject_css.py /path/to/docs --css custom-style.css
        """
    )
    
    parser.add_argument(
        'directory',
        nargs='?',
        default='.',
        help='Directory containing HTML files (default: current directory)'
    )
    
    parser.add_argument(
        '--css',
        default='javadoc-readable.css',
        help='CSS filename to link (default: javadoc-readable.css)'
    )
    
    parser.add_argument(
        '--dry-run',
        action='store_true',
        help='Show what would be changed without modifying files'
    )
    
    args = parser.parse_args()
    
    # Convert to absolute path
    directory = os.path.abspath(args.directory)
    
    # Check if directory exists
    if not os.path.isdir(directory):
        print(f"Error: Directory not found: {directory}")
        sys.exit(1)
    
    print(f"Processing directory: {directory}\n")
    
    # Run the injection
    inject_css_link(directory, args.css, args.dry_run)

if __name__ == '__main__':
    main()