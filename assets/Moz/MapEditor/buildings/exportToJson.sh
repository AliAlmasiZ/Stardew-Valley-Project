SOURCE_DIR="./"
OUTPUT_DIR="../../../data/maps"

mkdir -p "$OUTPUT_DIR"

for tmx_file in "$SOURCE_DIR"/*.tmx; do
    filename=$(basename -- "$tmx_file")
    base="${filename%.*}"
    tiled --export-map "$tmx_file" "$OUTPUT_DIR/$base.json"
    echo "Exported: $base.json"
done
