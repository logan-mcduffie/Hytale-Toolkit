package io.netty.buffer;

interface ChunkInfo {
   int capacity();

   boolean isDirect();

   long memoryAddress();
}
