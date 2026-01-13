package io.netty.buffer;

public interface SizeClassesMetric {
   int sizeIdx2size(int var1);

   int sizeIdx2sizeCompute(int var1);

   long pageIdx2size(int var1);

   long pageIdx2sizeCompute(int var1);

   int size2SizeIdx(int var1);

   int pages2pageIdx(int var1);

   int pages2pageIdxFloor(int var1);

   int normalizeSize(int var1);
}
