#!/usr/bin/env zsh
# Render all PlantUML diagrams in this folder to PNG
set -euo pipefail

# Prefer local plantuml.jar or fallback to docker if available
SCRIPT_DIR=${0:A:h}
cd "$SCRIPT_DIR"

OUT_DIR="$SCRIPT_DIR/out"
mkdir -p "$OUT_DIR"

if [[ -f "$SCRIPT_DIR/plantuml.jar" ]]; then
  echo "Using local plantuml.jar"
  java -jar "$SCRIPT_DIR/plantuml.jar" -tpng -o "$OUT_DIR" ./*.puml
elif command -v plantuml >/dev/null 2>&1; then
  echo "Using system PlantUML"
  plantuml -tpng -o "$OUT_DIR" ./*.puml
elif command -v docker >/dev/null 2>&1; then
  echo "Using Docker PlantUML"
  docker run --rm -v "$SCRIPT_DIR":"/work" -w /work plantuml/plantuml -tpng -o out *.puml
else
  echo "PlantUML not found. Install with: brew install plantuml" >&2
  exit 1
fi

echo "PNGs written to: $OUT_DIR"
