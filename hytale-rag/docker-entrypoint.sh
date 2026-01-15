#!/bin/sh
set -e

# Check for required data directory
if [ ! -d "/app/data/lancedb" ] || [ -z "$(ls -A /app/data/lancedb 2>/dev/null)" ]; then
    echo ""
    echo "=========================================="
    echo "  ERROR: LanceDB data not found"
    echo "=========================================="
    echo ""
    echo "Download the data from GitHub releases:"
    echo "  https://github.com/logan-mcduffie/Hytale-Toolkit/releases"
    echo ""
    echo "Then extract and run:"
    echo ""
    echo "  Linux/Mac:"
    echo "    tar -xzf lancedb.tar.gz"
    echo "    docker run -e VOYAGE_API_KEY=key -p 3000:3000 \\"
    echo "      -v \$(pwd)/lancedb:/app/data/lancedb:ro \\"
    echo "      ghcr.io/logan-mcduffie/hytale-rag"
    echo ""
    echo "  Windows (PowerShell):"
    echo "    tar -xzf lancedb.tar.gz"
    echo "    docker run -e VOYAGE_API_KEY=key -p 3000:3000 \`"
    echo "      -v \"\${PWD}/lancedb:/app/data/lancedb:ro\" \`"
    echo "      ghcr.io/logan-mcduffie/hytale-rag"
    echo ""
    exit 1
fi

# Check for required API key
if [ -z "$VOYAGE_API_KEY" ]; then
    echo ""
    echo "=========================================="
    echo "  ERROR: VOYAGE_API_KEY is not set"
    echo "=========================================="
    echo ""
    echo "Get a free API key at: https://www.voyageai.com/"
    echo ""
    echo "Then run:"
    echo ""
    echo "  docker run -e VOYAGE_API_KEY=your-key -p 3000:3000 \\"
    echo "    -v /path/to/lancedb:/app/data/lancedb:ro \\"
    echo "    ghcr.io/logan-mcduffie/hytale-rag"
    echo ""
    exit 1
fi

echo "Starting Hytale RAG server..."
exec "$@"
