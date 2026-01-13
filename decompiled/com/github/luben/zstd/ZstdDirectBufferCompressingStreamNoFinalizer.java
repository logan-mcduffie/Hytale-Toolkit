package com.github.luben.zstd;

import com.github.luben.zstd.util.Native;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class ZstdDirectBufferCompressingStreamNoFinalizer implements Closeable, Flushable {
   private ByteBuffer target;
   private final long stream;
   private int consumed = 0;
   private int produced = 0;
   private boolean closed = false;
   private boolean initialized = false;
   private int level = Zstd.defaultCompressionLevel();
   private byte[] dict = null;
   private ZstdDictCompress fastDict = null;

   protected ByteBuffer flushBuffer(ByteBuffer var1) throws IOException {
      return var1;
   }

   public ZstdDirectBufferCompressingStreamNoFinalizer(ByteBuffer var1, int var2) throws IOException {
      if (!var1.isDirect()) {
         throw new IllegalArgumentException("Target buffer should be a direct buffer");
      } else {
         this.target = var1;
         this.level = var2;
         this.stream = createCStream();
      }
   }

   public static int recommendedOutputBufferSize() {
      return (int)recommendedCOutSize();
   }

   private static native long recommendedCOutSize();

   private static native long createCStream();

   private static native long freeCStream(long var0);

   private native long initCStream(long var1, int var3);

   private native long initCStreamWithDict(long var1, byte[] var3, int var4, int var5);

   private native long initCStreamWithFastDict(long var1, ZstdDictCompress var3);

   private native long compressDirectByteBuffer(long var1, ByteBuffer var3, int var4, int var5, ByteBuffer var6, int var7, int var8);

   private native long flushStream(long var1, ByteBuffer var3, int var4, int var5);

   private native long endStream(long var1, ByteBuffer var3, int var4, int var5);

   public ZstdDirectBufferCompressingStreamNoFinalizer setDict(byte[] var1) {
      if (this.initialized) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         this.dict = var1;
         this.fastDict = null;
         return this;
      }
   }

   public ZstdDirectBufferCompressingStreamNoFinalizer setDict(ZstdDictCompress var1) {
      if (this.initialized) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         this.dict = null;
         this.fastDict = var1;
         return this;
      }
   }

   public void compress(ByteBuffer var1) throws IOException {
      if (!var1.isDirect()) {
         throw new IllegalArgumentException("Source buffer should be a direct buffer");
      } else if (this.closed) {
         throw new IOException("Stream closed");
      } else {
         if (!this.initialized) {
            long var2 = 0L;
            ZstdDictCompress var4 = this.fastDict;
            if (var4 != null) {
               var4.acquireSharedLock();

               try {
                  var2 = this.initCStreamWithFastDict(this.stream, var4);
               } finally {
                  var4.releaseSharedLock();
               }
            } else if (this.dict != null) {
               var2 = this.initCStreamWithDict(this.stream, this.dict, this.dict.length, this.level);
            } else {
               var2 = this.initCStream(this.stream, this.level);
            }

            if (Zstd.isError(var2)) {
               throw new ZstdIOException(var2);
            }

            this.initialized = true;
         }

         while (var1.hasRemaining()) {
            if (!this.target.hasRemaining()) {
               this.target = this.flushBuffer(this.target);
               if (!this.target.isDirect()) {
                  throw new IllegalArgumentException("Target buffer should be a direct buffer");
               }

               if (!this.target.hasRemaining()) {
                  throw new IOException("The target buffer has no more space, even after flushing, and there are still bytes to compress");
               }
            }

            long var9 = this.compressDirectByteBuffer(
               this.stream, this.target, this.target.position(), this.target.remaining(), var1, var1.position(), var1.remaining()
            );
            if (Zstd.isError(var9)) {
               throw new ZstdIOException(var9);
            }

            ((Buffer)this.target).position(this.target.position() + this.produced);
            ((Buffer)var1).position(var1.position() + this.consumed);
         }
      }
   }

   @Override
   public void flush() throws IOException {
      if (this.closed) {
         throw new IOException("Already closed");
      } else {
         long var1;
         if (this.initialized) {
            do {
               var1 = this.flushStream(this.stream, this.target, this.target.position(), this.target.remaining());
               if (Zstd.isError(var1)) {
                  throw new ZstdIOException(var1);
               }

               ((Buffer)this.target).position(this.target.position() + this.produced);
               this.target = this.flushBuffer(this.target);
               if (!this.target.isDirect()) {
                  throw new IllegalArgumentException("Target buffer should be a direct buffer");
               }

               if (var1 > 0L && !this.target.hasRemaining()) {
                  throw new IOException("The target buffer has no more space, even after flushing, and there are still bytes to compress");
               }
            } while (var1 > 0L);
         }
      }
   }

   @Override
   public void close() throws IOException {
      if (!this.closed) {
         try {
            long var1;
            if (this.initialized) {
               do {
                  var1 = this.endStream(this.stream, this.target, this.target.position(), this.target.remaining());
                  if (Zstd.isError(var1)) {
                     throw new ZstdIOException(var1);
                  }

                  ((Buffer)this.target).position(this.target.position() + this.produced);
                  this.target = this.flushBuffer(this.target);
                  if (!this.target.isDirect()) {
                     throw new IllegalArgumentException("Target buffer should be a direct buffer");
                  }

                  if (var1 > 0L && !this.target.hasRemaining()) {
                     throw new IOException("The target buffer has no more space, even after flushing, and there are still bytes to compress");
                  }
               } while (var1 > 0L);
            }
         } finally {
            freeCStream(this.stream);
            this.closed = true;
            this.initialized = false;
            this.target = null;
         }
      }
   }

   static {
      Native.load();
   }
}
