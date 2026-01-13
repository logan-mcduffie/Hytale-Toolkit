package io.netty.buffer;

import java.util.List;

public interface PoolArenaMetric extends SizeClassesMetric {
   int numThreadCaches();

   @Deprecated
   int numTinySubpages();

   int numSmallSubpages();

   int numChunkLists();

   @Deprecated
   List<PoolSubpageMetric> tinySubpages();

   List<PoolSubpageMetric> smallSubpages();

   List<PoolChunkListMetric> chunkLists();

   long numAllocations();

   @Deprecated
   long numTinyAllocations();

   long numSmallAllocations();

   long numNormalAllocations();

   long numHugeAllocations();

   default long numChunkAllocations() {
      return -1L;
   }

   long numDeallocations();

   @Deprecated
   long numTinyDeallocations();

   long numSmallDeallocations();

   long numNormalDeallocations();

   long numHugeDeallocations();

   default long numChunkDeallocations() {
      return -1L;
   }

   long numActiveAllocations();

   @Deprecated
   long numActiveTinyAllocations();

   long numActiveSmallAllocations();

   long numActiveNormalAllocations();

   long numActiveHugeAllocations();

   default long numActiveChunks() {
      return -1L;
   }

   long numActiveBytes();
}
