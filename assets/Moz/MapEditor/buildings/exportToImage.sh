#!/bin/bash

# Path to the input folder containing .tmx files
INPUT_DIR="./"

# Output folder for PNGs
OUTPUT_DIR="./images"

# Ensure the output directory exists
mkdir -p "$OUTPUT_DIR"

# Loop through each .tmx file in the input directory
for file in "$INPUT_DIR"/*Exterior.tmx; do
    if [[ -f "$file" ]]; then
        filename=$(basename "$file" .tmx)
        output_path="$OUTPUT_DIR/$filename.png"

        echo "Exporting $file to $output_path..."
        tmxrasterizer "$file" "$output_path"

        # Check for success
        if [[ $? -ne 0 ]]; then
            echo "❌ Failed to export: $file"
        else
            echo "✅ Exported: $output_path"
        fi
    fi
done

echo "✅ All done."
