#!/bin/bash

# Script to prepare environment file from Firebase service account JSON
# Usage: ./setup-firebase-env.sh path/to/firebase-service-account.json

if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <path-to-firebase-service-account.json>"
    exit 1
fi

FIREBASE_JSON_PATH="$1"

if [ ! -f "$FIREBASE_JSON_PATH" ]; then
    echo "Error: File not found: $FIREBASE_JSON_PATH"
    exit 1
fi

# Remove whitespace and newlines from JSON
FIREBASE_JSON=$(cat "$FIREBASE_JSON_PATH" | tr -d '\n' | tr -d ' ')

echo "================================================"
echo "Firebase Credentials JSON (for environment variable):"
echo "================================================"
echo ""
echo "$FIREBASE_JSON"
echo ""
echo "================================================"
echo "Copy the above JSON and set it as FIREBASE_CREDENTIALS_JSON"
echo "================================================"

# Optionally save to a file
echo "$FIREBASE_JSON" > firebase-credentials-oneline.txt
echo ""
echo "Also saved to: firebase-credentials-oneline.txt"
echo "⚠️  Remember to add this file to .gitignore!"
