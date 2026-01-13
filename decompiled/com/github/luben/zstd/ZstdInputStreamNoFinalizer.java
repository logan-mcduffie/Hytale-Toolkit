package com.github.luben.zstd;

import com.github.luben.zstd.util.Native;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ZstdInputStreamNoFinalizer extends FilterInputStream {
   private final long stream;
   private long dstPos = 0L;
   private long srcPos = 0L;
   private long srcSize = 0L;
   private boolean needRead = true;
   private final BufferPool bufferPool;
   private final ByteBuffer srcByteBuffer;
   private final byte[] src;
   private static final int srcBuffSize = (int)recommendedDInSize();
   private boolean isContinuous = false;
   private boolean frameFinished = true;
   private boolean isClosed = false;

   public static native long recommendedDInSize();

   public static native long recommendedDOutSize();

   private static native long createDStream();

   private static native int freeDStream(long var0);

   private native int initDStream(long var1);

   private native int decompressStream(long var1, byte[] var3, int var4, byte[] var5, int var6);

   public ZstdInputStreamNoFinalizer(InputStream var1) throws IOException {
      this(var1, NoPool.INSTANCE);
   }

   public ZstdInputStreamNoFinalizer(InputStream var1, BufferPool var2) throws IOException {
      super(var1);
      this.bufferPool = var2;
      this.srcByteBuffer = Zstd.getArrayBackedBuffer(var2, srcBuffSize);
      this.src = this.srcByteBuffer.array();
      synchronized (this) {
         this.stream = createDStream();
         this.initDStream(this.stream);
      }
   }

   public synchronized ZstdInputStreamNoFinalizer setContinuous(boolean var1) {
      this.isContinuous = var1;
      return this;
   }

   public synchronized boolean getContinuous() {
      return this.isContinuous;
   }

   public synchronized ZstdInputStreamNoFinalizer setDict(byte[] var1) throws IOException {
      int var2 = Zstd.loadDictDecompress(this.stream, var1, var1.length);
      if (Zstd.isError(var2)) {
         throw new ZstdIOException(var2);
      } else {
         return this;
      }
   }

   public synchronized ZstdInputStreamNoFinalizer setDict(ZstdDictDecompress var1) throws IOException {
      var1.acquireSharedLock();

      try {
         int var2 = Zstd.loadFastDictDecompress(this.stream, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         }
      } finally {
         var1.releaseSharedLock();
      }

      return this;
   }

   public synchronized ZstdInputStreamNoFinalizer setLongMax(int var1) throws IOException {
      int var2 = Zstd.setDecompressionLongMax(this.stream, var1);
      if (Zstd.isError(var2)) {
         throw new ZstdIOException(var2);
      } else {
         return this;
      }
   }

   public synchronized ZstdInputStreamNoFinalizer setRefMultipleDDicts(boolean var1) throws IOException {
      int var2 = Zstd.setRefMultipleDDicts(this.stream, var1);
      if (Zstd.isError(var2)) {
         throw new ZstdIOException(var2);
      } else {
         return this;
      }
   }

   @Override
   public synchronized int read(byte[] var1, int var2, int var3) throws IOException {
      if (var2 < 0 || var3 > var1.length - var2) {
         throw new IndexOutOfBoundsException("Requested length " + var3 + " from offset " + var2 + " in buffer of size " + var1.length);
      } else if (var3 == 0) {
         return 0;
      } else {
         int var4 = 0;

         while (var4 == 0) {
            var4 = this.readInternal(var1, var2, var3);
         }

         return var4;
      }
   }

   int readInternal(byte[] var1, int var2, int var3) throws IOException {
      if (this.isClosed) {
         throw new IOException("Stream closed");
      } else if (var2 >= 0 && var3 <= var1.length - var2) {
         int var4 = var2 + var3;
         this.dstPos = var2;

         for (long var5 = -1L; this.dstPos < var4 && var5 < this.dstPos; this.needRead = this.dstPos < var4) {
            if (this.needRead && (this.in.available() > 0 || this.dstPos == var2)) {
               this.srcSize = this.in.read(this.src, 0, srcBuffSize);
               this.srcPos = 0L;
               if (this.srcSize < 0L) {
                  this.srcSize = 0L;
                  if (this.frameFinished) {
                     return -1;
                  }

                  if (this.isContinuous) {
                     this.srcSize = (int)(this.dstPos - var2);
                     if (this.srcSize > 0L) {
                        return (int)this.srcSize;
                     }

                     return -1;
                  }

                  throw new ZstdIOException(Zstd.errCorruptionDetected(), "Truncated source");
               }

               this.frameFinished = false;
            }

            var5 = this.dstPos;
            int var7 = this.decompressStream(this.stream, var1, var4, this.src, (int)this.srcSize);
            if (Zstd.isError(var7)) {
               throw new ZstdIOException(var7);
            }

            if (var7 == 0) {
               this.frameFinished = true;
               this.needRead = this.srcPos == this.srcSize;
               return (int)(this.dstPos - var2);
            }
         }

         return (int)(this.dstPos - var2);
      } else {
         throw new IndexOutOfBoundsException("Requested length " + var3 + " from offset " + var2 + " in buffer of size " + var1.length);
      }
   }

   @Override
   public synchronized int read() throws IOException {
      byte[] var1 = new byte[1];
      int var2 = 0;

      while (var2 == 0) {
         var2 = this.readInternal(var1, 0, 1);
      }

      return var2 == 1 ? var1[0] & 0xFF : -1;
   }

   @Override
   public synchronized int available() throws IOException {
      if (this.isClosed) {
         throw new IOException("Stream closed");
      } else {
         return !this.needRead ? 1 : this.in.available();
      }
   }

   @Override
   public boolean markSupported() {
      return false;
   }

   @Override
   public synchronized long skip(long var1) throws IOException {
      if (this.isClosed) {
         throw new IOException("Stream closed");
      } else if (var1 <= 0L) {
         return 0L;
      } else {
         int var3 = (int)recommendedDOutSize();
         if (var3 > var1) {
            var3 = (int)var1;
         }

         ByteBuffer var4 = Zstd.getArrayBackedBuffer(this.bufferPool, var3);
         long var5 = var1;

         try {
            byte[] var7 = var4.array();

            while (var5 > 0L) {
               int var8 = this.read(var7, 0, (int)Math.min((long)var3, var5));
               if (var8 < 0) {
                  break;
               }

               var5 -= var8;
            }
         } finally {
            this.bufferPool.release(var4);
         }

         return var1 - var5;
      }
   }

   @Override
   public synchronized void close() throws IOException {
      if (!this.isClosed) {
         this.isClosed = true;
         this.bufferPool.release(this.srcByteBuffer);
         freeDStream(this.stream);
         this.in.close();
      }
   }

   static {
      Native.load();
   }
}
