package com.github.luben.zstd;

import com.github.luben.zstd.util.Native;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ZstdDirectBufferCompressingStream implements Closeable, Flushable {
   ZstdDirectBufferCompressingStreamNoFinalizer inner;
   private boolean finalize;

   protected ByteBuffer flushBuffer(ByteBuffer var1) throws IOException {
      return var1;
   }

   public ZstdDirectBufferCompressingStream(ByteBuffer var1, int var2) throws IOException {
      this.inner = new ZstdDirectBufferCompressingStreamNoFinalizer(var1, var2) {
         @Override
         protected ByteBuffer flushBuffer(ByteBuffer var1) throws IOException {
            return ZstdDirectBufferCompressingStream.this.flushBuffer(var1);
         }
      };
   }

   public static int recommendedOutputBufferSize() {
      return ZstdDirectBufferCompressingStreamNoFinalizer.recommendedOutputBufferSize();
   }

   public synchronized ZstdDirectBufferCompressingStream setDict(byte[] var1) throws IOException {
      this.inner.setDict(var1);
      return this;
   }

   public synchronized ZstdDirectBufferCompressingStream setDict(ZstdDictCompress var1) throws IOException {
      this.inner.setDict(var1);
      return this;
   }

   public void setFinalize(boolean var1) {
      this.finalize = var1;
   }

   public synchronized void compress(ByteBuffer var1) throws IOException {
      this.inner.compress(var1);
   }

   @Override
   public synchronized void flush() throws IOException {
      this.inner.flush();
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
