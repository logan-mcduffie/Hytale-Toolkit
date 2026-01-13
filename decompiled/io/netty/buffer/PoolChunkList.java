package io.netty.buffer;

import io.netty.util.internal.StringUtil;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

final class PoolChunkList<T> implements PoolChunkListMetric {
   private static final Iterator<PoolChunkMetric> EMPTY_METRICS = Collections.emptyIterator();
   private final PoolArena<T> arena;
   private final PoolChunkList<T> nextList;
   private final int minUsage;
   private final int maxUsage;
   private final int maxCapacity;
   private PoolChunk<T> head;
   private final int freeMinThreshold;
   private final int freeMaxThreshold;
   private PoolChunkList<T> prevList;

   PoolChunkList(PoolArena<T> arena, PoolChunkList<T> nextList, int minUsage, int maxUsage, int chunkSize) {
      assert minUsage <= maxUsage;

      this.arena = arena;
      this.nextList = nextList;
      this.minUsage = minUsage;
      this.maxUsage = maxUsage;
      this.maxCapacity = calculateMaxCapacity(minUsage, chunkSize);
      this.freeMinThreshold = maxUsage == 100 ? 0 : (int)(chunkSize * (100.0 - maxUsage + 0.99999999) / 100.0);
      this.freeMaxThreshold = minUsage == 100 ? 0 : (int)(chunkSize * (100.0 - minUsage + 0.99999999) / 100.0);
   }

   private static int calculateMaxCapacity(int minUsage, int chunkSize) {
      minUsage = minUsage0(minUsage);
      return minUsage == 100 ? 0 : (int)(chunkSize * (100L - minUsage) / 100L);
   }

   void prevList(PoolChunkList<T> prevList) {
      assert this.prevList == null;

      this.prevList = prevList;
   }

   boolean allocate(PooledByteBuf<T> buf, int reqCapacity, int sizeIdx, PoolThreadCache threadCache) {
      int normCapacity = this.arena.sizeClass.sizeIdx2size(sizeIdx);
      if (normCapacity > this.maxCapacity) {
         return false;
      } else {
         for (PoolChunk<T> cur = this.head; cur != null; cur = cur.next) {
            if (cur.allocate(buf, reqCapacity, sizeIdx, threadCache)) {
               if (cur.freeBytes <= this.freeMinThreshold) {
                  this.remove(cur);
                  this.nextList.add(cur);
               }

               return true;
            }
         }

         return false;
      }
   }

   boolean free(PoolChunk<T> chunk, long handle, int normCapacity, ByteBuffer nioBuffer) {
      chunk.free(handle, normCapacity, nioBuffer);
      if (chunk.freeBytes > this.freeMaxThreshold) {
         this.remove(chunk);
         return this.move0(chunk);
      } else {
         return true;
      }
   }

   private boolean move(PoolChunk<T> chunk) {
      assert chunk.usage() < this.maxUsage;

      if (chunk.freeBytes > this.freeMaxThreshold) {
         return this.move0(chunk);
      } else {
         this.add0(chunk);
         return true;
      }
   }

   private boolean move0(PoolChunk<T> chunk) {
      if (this.prevList == null) {
         assert chunk.usage() == 0;

         return false;
      } else {
         return this.prevList.move(chunk);
      }
   }

   void add(PoolChunk<T> chunk) {
      if (chunk.freeBytes <= this.freeMinThreshold) {
         this.nextList.add(chunk);
      } else {
         this.add0(chunk);
      }
   }

   void add0(PoolChunk<T> chunk) {
      chunk.parent = this;
      if (this.head == null) {
         this.head = chunk;
         chunk.prev = null;
         chunk.next = null;
      } else {
         chunk.prev = null;
         chunk.next = this.head;
         this.head.prev = chunk;
         this.head = chunk;
      }
   }

   private void remove(PoolChunk<T> cur) {
      if (cur == this.head) {
         this.head = cur.next;
         if (this.head != null) {
            this.head.prev = null;
         }
      } else {
         PoolChunk<T> next = cur.next;
         cur.prev.next = next;
         if (next != null) {
            next.prev = cur.prev;
         }
      }
   }

   @Override
   public int minUsage() {
      return minUsage0(this.minUsage);
   }

   @Override
   public int maxUsage() {
      return Math.min(this.maxUsage, 100);
   }

   private static int minUsage0(int value) {
      return Math.max(1, value);
   }

   @Override
   public Iterator<PoolChunkMetric> iterator() {
      this.arena.lock();

      Iterator metrics;
      try {
         if (this.head != null) {
            List<PoolChunkMetric> metricsx = new ArrayList<>();
            PoolChunk<T> cur = this.head;

            do {
               metricsx.add(cur);
               cur = cur.next;
            } while (cur != null);

            return metricsx.iterator();
         }

         metrics = EMPTY_METRICS;
      } finally {
         this.arena.unlock();
      }

      return metrics;
   }

   @Override
   public String toString() {
      StringBuilder buf = new StringBuilder();
      this.arena.lock();

      try {
         if (this.head == null) {
            return "none";
         } else {
            PoolChunk<T> cur = this.head;

            while (true) {
               buf.append(cur);
               cur = cur.next;
               if (cur == null) {
                  return buf.toString();
               }

               buf.append(StringUtil.NEWLINE);
            }
         }
      } finally {
         this.arena.unlock();
      }
   }

   void destroy(PoolArena<T> arena) {
      for (PoolChunk<T> chunk = this.head; chunk != null; chunk = chunk.next) {
         arena.destroyChunk(chunk);
      }

      this.head = null;
   }
}
