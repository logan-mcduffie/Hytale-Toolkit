package com.github.luben.zstd;

import java.lang.ref.SoftReference;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RecyclingBufferPool implements BufferPool {
   public static final BufferPool INSTANCE = new RecyclingBufferPool();
   private static final int buffSize = Math.max(
      Math.max((int)ZstdOutputStreamNoFinalizer.recommendedCOutSize(), (int)ZstdInputStreamNoFinalizer.recommendedDInSize()),
      (int)ZstdInputStreamNoFinalizer.recommendedDOutSize()
   );
   private final ConcurrentLinkedQueue<SoftReference<ByteBuffer>> pool = new ConcurrentLinkedQueue<>();

   private RecyclingBufferPool() {
   }

   @Override
   public ByteBuffer get(int var1) {
      if (var1 > buffSize) {
         throw new RuntimeException("Unsupported buffer size: " + var1 + ". Supported buffer sizes: " + buffSize + " or smaller.");
      } else {
         ByteBuffer var3;
         do {
            SoftReference var2 = this.pool.poll();
            if (var2 == null) {
               return ByteBuffer.allocate(buffSize);
            }

            var3 = (ByteBuffer)var2.get();
         } while (var3 == null);

         return var3;
      }
   }

   @Override
   public void release(ByteBuffer var1) {
      if (var1.capacity() >= buffSize) {
         ((Buffer)var1).clear();
         this.pool.add(new SoftReference<>(var1));
      }
   }
}
