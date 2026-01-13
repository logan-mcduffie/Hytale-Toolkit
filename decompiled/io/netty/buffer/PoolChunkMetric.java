package io.netty.buffer;

public interface PoolChunkMetric {
   int usage();

   int chunkSize();

   int freeBytes();
}
