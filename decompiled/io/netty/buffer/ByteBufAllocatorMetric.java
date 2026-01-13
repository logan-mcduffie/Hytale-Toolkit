package io.netty.buffer;

public interface ByteBufAllocatorMetric {
   long usedHeapMemory();

   long usedDirectMemory();
}
