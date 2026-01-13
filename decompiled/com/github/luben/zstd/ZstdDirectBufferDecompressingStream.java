package com.github.luben.zstd;

import com.github.luben.zstd.util.Native;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ZstdDirectBufferDecompressingStream implements Closeable {
   private ZstdDirectBufferDecompressingStreamNoFinalizer inner;
   private boolean finalize = true;

   protected ByteBuffer refill(ByteBuffer var1) {
      return var1;
   }

   public ZstdDirectBufferDecompressingStream(ByteBuffer var1) {
      this.inner = new ZstdDirectBufferDecompressingStreamNoFinalizer(var1) {
         @Override
         protected ByteBuffer refill(ByteBuffer var1) {
            return ZstdDirectBufferDecompressingStream.this.refill(var1);
         }
      };
   }

   public void setFinalize(boolean var1) {
      this.finalize = var1;
   }

   public synchronized boolean hasRemaining() {
      return this.inner.hasRemaining();
   }

   public static int recommendedTargetBufferSize() {
      return ZstdDirectBufferDecompressingStreamNoFinalizer.recommendedTargetBufferSize();
   }

   public synchronized ZstdDirectBufferDecompressingStream setDict(byte[] var1) throws IOException {
      this.inner.setDict(var1);
      return this;
   }

   public synchronized ZstdDirectBufferDecompressingStream setDict(ZstdDictDecompress var1) throws IOException {
      this.inner.setDict(var1);
      return this;
   }

   public ZstdDirectBufferDecompressingStream setLongMax(int var1) throws IOException {
      this.inner.setLongMax(var1);
      return this;
   }

   public synchronized int read(ByteBuffer var1) throws IOException {
      return this.inner.read(var1);
   }

   @Override
   public synchronized void close() throws IOException {
      this.inner.close();
   }

   @Override
   protected void finalize() throws Throwable {
      if (this.finalize) {
         this.close();
      }
   }

   static {
      Native.load();
   }
}
