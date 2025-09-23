#!/usr/bin/env python3
"""
Script to replace ALL REST Assured calls with HttpClient calls
"""

import re

def fix_rest_assured_file(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # Remove any remaining ContentType references
    content = re.sub(r'\.contentType\(ContentType\.JSON\)', '', content)
    content = re.sub(r'\.urlEncodingEnabled\(false\)', '', content)
    content = re.sub(r'\.queryParam\([^)]+\)', '', content)

    # Replace any remaining given() calls with simple HTTP calls
    # Pattern for POST with body
    content = re.sub(
        r'given\(\).*?\.body\("([^"]*?)"\).*?\.request\("POST",\s*"([^"]+)"\).*?\.statusCode\((\d+)\);',
        r'HttpClient.post("\2", "\1", \3);',
        content,
        flags=re.DOTALL
    )

    # Pattern for GET requests
    content = re.sub(
        r'given\(\).*?\.request\("GET",\s*"([^"]+)"\).*?\.statusCode\((\d+)\);',
        r'HttpClient.get("\1", \2);',
        content,
        flags=re.DOTALL
    )

    # Pattern for POST without body
    content = re.sub(
        r'given\(\).*?\.request\("POST",\s*"([^"]+)"\).*?\.statusCode\((\d+)\);',
        r'HttpClient.post("\1", "", \2);',
        content,
        flags=re.DOTALL
    )

    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)

    print(f"Fixed ALL REST Assured calls in {file_path}")

if __name__ == "__main__":
    fix_rest_assured_file("src/test/java/generated/GeneratedApiTest.java")
