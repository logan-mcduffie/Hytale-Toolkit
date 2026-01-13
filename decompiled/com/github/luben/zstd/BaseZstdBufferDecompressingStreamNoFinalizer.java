package com.github.luben.zstd;

import java.io.Closeable;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public abstract class BaseZstdBufferDecompressingStreamNoFinalizer implements Closeable {
   protected long stream;
   protected ByteBuffer source;
   protected boolean closed = false;
   private boolean finishedFrame = false;
   private boolean streamEnd = false;
   private int consumed;
   private int produced;

   BaseZstdBufferDecompressingStreamNoFinalizer(ByteBuffer var1) {
      this.source = var1;
   }

   protected ByteBuffer refill(ByteBuffer var1) {
      return var1;
   }

   public boolean hasRemaining() {
      return !this.streamEnd && (this.source.hasRemaining() || !this.finishedFrame);
   }

   public BaseZstdBufferDecompressingStreamNoFinalizer setDict(byte[] var1) throws IOException {
      long var2 = Zstd.loadDictDecompress(this.stream, var1, var1.length);
      if (Zstd.isError(var2)) {
         throw new ZstdIOException(var2);
      } else {
         return this;
      }
   }

   public BaseZstdBufferDecompressingStreamNoFinalizer setDict(ZstdDictDecompress var1) throws IOException {
      var1.acquireSharedLock();

      try {
         long var2 = Zstd.loadFastDictDecompress(this.stream, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         }
      } finally {
         var1.releaseSharedLock();
      }

      return this;
   }

   public BaseZstdBufferDecompressingStreamNoFinalizer setLongMax(int var1) throws IOException {
      long var2 = Zstd.setDecompressionLongMax(this.stream, var1);
      if (Zstd.isError(var2)) {
         throw new ZstdIOException(var2);
      } else {
         return this;
      }
   }

   int readInternal(ByteBuffer var1, boolean var2) throws IOException {
      if (this.closed) {
         throw new IOException("Stream closed");
      } else if (this.streamEnd) {
         return 0;
      } else {
         long var3 = this.decompressStream(this.stream, var1, var1.position(), var1.remaining(), this.source, this.source.position(), this.source.remaining());
         if (Zstd.isError(var3)) {
            throw new ZstdIOException(var3);
         } else {
            ((Buffer)this.source).position(this.source.position() + this.consumed);
            ((Buffer)var1).position(var1.position() + this.produced);
            if (!this.source.hasRemaining()) {
               this.source = this.refill(this.source);
               if (!var2 && this.source.isDirect()) {
                  throw new IllegalArgumentException("Source buffer should be a non-direct buffer");
               }

               if (var2 && !this.source.isDirect()) {
                  throw new IllegalArgumentException("Source buffer should be a direct buffer");
               }
            }

            this.finishedFrame = var3 == 0L;
            if (this.finishedFrame) {
               this.streamEnd = !this.source.hasRemaining();
            }

            return this.produced;
         }
      }
   }

   @Override
   public void close() {
      if (!this.closed) {
         try {
            this.freeDStream(this.stream);
         } finally {
            this.closed = true;
            this.source = null;
         }
      }
   }

   public abstract int read(ByteBuffer var1) throws IOException;

   abstract long createDStream();

   abstract long freeDStream(long var1);

   abstract long initDStream(long var1);

   abstract long decompressStream(long var1, ByteBuffer var3, int var4, int var5, ByteBuffer var6, int var7, int var8);
}
