FROM node:20-slim

WORKDIR /app

# Install build dependencies for native modules (LanceDB) and curl for healthcheck
RUN apt-get update && apt-get install -y \
    python3 \
    make \
    g++ \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Copy package files
COPY package*.json ./

# Install all dependencies (need devDependencies for tsx)
RUN npm ci

# Copy source, config, and entrypoint
COPY src/ ./src/
COPY config/ ./config/
COPY tsconfig.json ./
COPY docker-entrypoint.sh ./
RUN chmod +x docker-entrypoint.sh

# Default environment
ENV HYTALE_RAG_MODE=rest
ENV HYTALE_RAG_PORT=3000
ENV HYTALE_RAG_HOST=0.0.0.0

# Expose ports
EXPOSE 3000 3001

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:3000/health || exit 1

# Entrypoint checks for API key, then runs CMD
ENTRYPOINT ["./docker-entrypoint.sh"]
CMD ["npx", "tsx", "src/index.ts"]
