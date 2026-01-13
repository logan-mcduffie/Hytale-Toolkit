package io.netty.buffer;

final class SizeClasses implements SizeClassesMetric {
   static final int LOG2_QUANTUM = 4;
   private static final int LOG2_SIZE_CLASS_GROUP = 2;
   private static final int LOG2_MAX_LOOKUP_SIZE = 12;
   private static final int LOG2GROUP_IDX = 1;
   private static final int LOG2DELTA_IDX = 2;
   private static final int NDELTA_IDX = 3;
   private static final int PAGESIZE_IDX = 4;
   private static final int SUBPAGE_IDX = 5;
   private static final int LOG2_DELTA_LOOKUP_IDX = 6;
   private static final byte no = 0;
   private static final byte yes = 1;
   final int pageSize;
   final int pageShifts;
   final int chunkSize;
   final int directMemoryCacheAlignment;
   final int nSizes;
   final int nSubpages;
   final int nPSizes;
   final int lookupMaxSize;
   final int smallMaxSizeIdx;
   private final int[] pageIdx2sizeTab;
   private final int[] sizeIdx2sizeTab;
   private final int[] size2idxTab;

   SizeClasses(int pageSize, int pageShifts, int chunkSize, int directMemoryCacheAlignment) {
      int group = PoolThreadCache.log2(chunkSize) - 4 - 2 + 1;
      short[][] sizeClasses = new short[group << 2][7];
      int normalMaxSize = -1;
      int nSizes = 0;
      int size = 0;
      int log2Group = 4;
      int log2Delta = 4;
      int ndeltaLimit = 4;

      for (int nDelta = 0; nDelta < ndeltaLimit; nSizes++) {
         short[] sizeClass = newSizeClass(nSizes, log2Group, log2Delta, nDelta, pageShifts);
         sizeClasses[nSizes] = sizeClass;
         size = sizeOf(sizeClass, directMemoryCacheAlignment);
         nDelta++;
      }

      for (int var19 = log2Group + 2; size < chunkSize; log2Delta++) {
         for (int nDelta = 1; nDelta <= ndeltaLimit && size < chunkSize; nSizes++) {
            short[] sizeClass = newSizeClass(nSizes, var19, log2Delta, nDelta, pageShifts);
            sizeClasses[nSizes] = sizeClass;
            size = normalMaxSize = sizeOf(sizeClass, directMemoryCacheAlignment);
            nDelta++;
         }

         var19++;
      }

      assert chunkSize == normalMaxSize;

      int smallMaxSizeIdx = 0;
      int lookupMaxSize = 0;
      int nPSizes = 0;
      int nSubpages = 0;

      for (int idx = 0; idx < nSizes; idx++) {
         short[] sz = sizeClasses[idx];
         if (sz[4] == 1) {
            nPSizes++;
         }

         if (sz[5] == 1) {
            nSubpages++;
            smallMaxSizeIdx = idx;
         }

         if (sz[6] != 0) {
            lookupMaxSize = sizeOf(sz, directMemoryCacheAlignment);
         }
      }

      this.smallMaxSizeIdx = smallMaxSizeIdx;
      this.lookupMaxSize = lookupMaxSize;
      this.nPSizes = nPSizes;
      this.nSubpages = nSubpages;
      this.nSizes = nSizes;
      this.pageSize = pageSize;
      this.pageShifts = pageShifts;
      this.chunkSize = chunkSize;
      this.directMemoryCacheAlignment = directMemoryCacheAlignment;
      this.sizeIdx2sizeTab = newIdx2SizeTab(sizeClasses, nSizes, directMemoryCacheAlignment);
      this.pageIdx2sizeTab = newPageIdx2sizeTab(sizeClasses, nSizes, nPSizes, directMemoryCacheAlignment);
      this.size2idxTab = newSize2idxTab(lookupMaxSize, sizeClasses);
   }

   private static short[] newSizeClass(int index, int log2Group, int log2Delta, int nDelta, int pageShifts) {
      short isMultiPageSize;
      if (log2Delta >= pageShifts) {
         isMultiPageSize = 1;
      } else {
         int pageSize = 1 << pageShifts;
         int size = calculateSize(log2Group, nDelta, log2Delta);
         isMultiPageSize = (short)(size == size / pageSize * pageSize ? 1 : 0);
      }

      int log2Ndelta = nDelta == 0 ? 0 : PoolThreadCache.log2(nDelta);
      byte remove = (byte)(1 << log2Ndelta < nDelta ? 1 : 0);
      int log2Size = log2Delta + log2Ndelta == log2Group ? log2Group + 1 : log2Group;
      if (log2Size == log2Group) {
         remove = 1;
      }

      short isSubpage = (short)(log2Size < pageShifts + 2 ? 1 : 0);
      int log2DeltaLookup = log2Size >= 12 && (log2Size != 12 || remove != 0) ? 0 : log2Delta;
      return new short[]{(short)index, (short)log2Group, (short)log2Delta, (short)nDelta, isMultiPageSize, isSubpage, (short)log2DeltaLookup};
   }

   private static int[] newIdx2SizeTab(short[][] sizeClasses, int nSizes, int directMemoryCacheAlignment) {
      int[] sizeIdx2sizeTab = new int[nSizes];

      for (int i = 0; i < nSizes; i++) {
         short[] sizeClass = sizeClasses[i];
         sizeIdx2sizeTab[i] = sizeOf(sizeClass, directMemoryCacheAlignment);
      }

      return sizeIdx2sizeTab;
   }

   private static int calculateSize(int log2Group, int nDelta, int log2Delta) {
      return (1 << log2Group) + (nDelta << log2Delta);
   }

   private static int sizeOf(short[] sizeClass, int directMemoryCacheAlignment) {
      int log2Group = sizeClass[1];
      int log2Delta = sizeClass[2];
      int nDelta = sizeClass[3];
      int size = calculateSize(log2Group, nDelta, log2Delta);
      return alignSizeIfNeeded(size, directMemoryCacheAlignment);
   }

   private static int[] newPageIdx2sizeTab(short[][] sizeClasses, int nSizes, int nPSizes, int directMemoryCacheAlignment) {
      int[] pageIdx2sizeTab = new int[nPSizes];
      int pageIdx = 0;

      for (int i = 0; i < nSizes; i++) {
         short[] sizeClass = sizeClasses[i];
         if (sizeClass[4] == 1) {
            pageIdx2sizeTab[pageIdx++] = sizeOf(sizeClass, directMemoryCacheAlignment);
         }
      }

      return pageIdx2sizeTab;
   }

   private static int[] newSize2idxTab(int lookupMaxSize, short[][] sizeClasses) {
      int[] size2idxTab = new int[lookupMaxSize >> 4];
      int idx = 0;
      int size = 0;

      for (int i = 0; size <= lookupMaxSize; i++) {
         int log2Delta = sizeClasses[i][2];

         for (int times = 1 << log2Delta - 4; size <= lookupMaxSize && times-- > 0; size = idx + 1 << 4) {
            size2idxTab[idx++] = i;
         }
      }

      return size2idxTab;
   }

   @Override
   public int sizeIdx2size(int sizeIdx) {
      return this.sizeIdx2sizeTab[sizeIdx];
   }

   @Override
   public int sizeIdx2sizeCompute(int sizeIdx) {
      int group = sizeIdx >> 2;
      int mod = sizeIdx & 3;
      int groupSize = group == 0 ? 0 : 32 << group;
      int shift = group == 0 ? 1 : group;
      int lgDelta = shift + 4 - 1;
      int modSize = mod + 1 << lgDelta;
      return groupSize + modSize;
   }

   @Override
   public long pageIdx2size(int pageIdx) {
      return this.pageIdx2sizeTab[pageIdx];
   }

   @Override
   public long pageIdx2sizeCompute(int pageIdx) {
      int group = pageIdx >> 2;
      int mod = pageIdx & 3;
      long groupSize = group == 0 ? 0L : 1L << this.pageShifts + 2 - 1 << group;
      int shift = group == 0 ? 1 : group;
      int log2Delta = shift + this.pageShifts - 1;
      int modSize = mod + 1 << log2Delta;
      return groupSize + modSize;
   }

   @Override
   public int size2SizeIdx(int size) {
      if (size == 0) {
         return 0;
      } else if (size > this.chunkSize) {
         return this.nSizes;
      } else {
         size = alignSizeIfNeeded(size, this.directMemoryCacheAlignment);
         if (size <= this.lookupMaxSize) {
            return this.size2idxTab[size - 1 >> 4];
         } else {
            int x = PoolThreadCache.log2((size << 1) - 1);
            int shift = x < 7 ? 0 : x - 6;
            int group = shift << 2;
            int log2Delta = x < 7 ? 4 : x - 2 - 1;
            int mod = size - 1 >> log2Delta & 3;
            return group + mod;
         }
      }
   }

   @Override
   public int pages2pageIdx(int pages) {
      return this.pages2pageIdxCompute(pages, false);
   }

   @Override
   public int pages2pageIdxFloor(int pages) {
      return this.pages2pageIdxCompute(pages, true);
   }

   private int pages2pageIdxCompute(int pages, boolean floor) {
      int pageSize = pages << this.pageShifts;
      if (pageSize > this.chunkSize) {
         return this.nPSizes;
      } else {
         int x = PoolThreadCache.log2((pageSize << 1) - 1);
         int shift = x < 2 + this.pageShifts ? 0 : x - (2 + this.pageShifts);
         int group = shift << 2;
         int log2Delta = x < 2 + this.pageShifts + 1 ? this.pageShifts : x - 2 - 1;
         int mod = pageSize - 1 >> log2Delta & 3;
         int pageIdx = group + mod;
         if (floor && this.pageIdx2sizeTab[pageIdx] > pages << this.pageShifts) {
            pageIdx--;
         }

         return pageIdx;
      }
   }

   private static int alignSizeIfNeeded(int size, int directMemoryCacheAlignment) {
      if (directMemoryCacheAlignment <= 0) {
         return size;
      } else {
         int delta = size & directMemoryCacheAlignment - 1;
         return delta == 0 ? size : size + directMemoryCacheAlignment - delta;
      }
   }

   @Override
   public int normalizeSize(int size) {
      if (size == 0) {
         return this.sizeIdx2sizeTab[0];
      } else {
         size = alignSizeIfNeeded(size, this.directMemoryCacheAlignment);
         if (size <= this.lookupMaxSize) {
            int ret = this.sizeIdx2sizeTab[this.size2idxTab[size - 1 >> 4]];

            assert ret == normalizeSizeCompute(size);

            return ret;
         } else {
            return normalizeSizeCompute(size);
         }
      }
   }

   private static int normalizeSizeCompute(int size) {
      int x = PoolThreadCache.log2((size << 1) - 1);
      int log2Delta = x < 7 ? 4 : x - 2 - 1;
      int delta = 1 << log2Delta;
      int delta_mask = delta - 1;
      return size + delta_mask & ~delta_mask;
   }
}
