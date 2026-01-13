package io.netty.channel;

import java.util.Map.Entry;

public interface MaxBytesRecvByteBufAllocator extends RecvByteBufAllocator {
   int maxBytesPerRead();

   MaxBytesRecvByteBufAllocator maxBytesPerRead(int var1);

   int maxBytesPerIndividualRead();

   MaxBytesRecvByteBufAllocator maxBytesPerIndividualRead(int var1);

   Entry<Integer, Integer> maxBytesPerReadPair();

   MaxBytesRecvByteBufAllocator maxBytesPerReadPair(int var1, int var2);
}
