package com.github.luben.zstd;

import com.github.luben.zstd.util.Native;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ZstdOutputStreamNoFinalizer extends FilterOutputStream {
   private final long stream;
   private long srcPos = 0L;
   private long dstPos = 0L;
   private final BufferPool bufferPool;
   private final ByteBuffer dstByteBuffer;
   private final byte[] dst;
   private boolean isClosed = false;
   private static final int dstSize = (int)recommendedCOutSize();
   private boolean closeFrameOnFlush = false;
   private boolean frameClosed = true;
   private boolean frameStarted = false;

   public static native long recommendedCOutSize();

   private static native long createCStream();

   private static native int freeCStream(long var0);

   private native int resetCStream(long var1);

   private native int compressStream(long var1, byte[] var3, int var4, byte[] var5, int var6);

   private native int flushStream(long var1, byte[] var3, int var4);

   private native int endStream(long var1, byte[] var3, int var4);

   public ZstdOutputStreamNoFinalizer(OutputStream var1, int var2) throws IOException {
      this(var1, NoPool.INSTANCE);
      Zstd.setCompressionLevel(this.stream, var2);
   }

   public ZstdOutputStreamNoFinalizer(OutputStream var1) throws IOException {
      this(var1, NoPool.INSTANCE);
   }

   public ZstdOutputStreamNoFinalizer(OutputStream var1, BufferPool var2, int var3) throws IOException {
      this(var1, var2);
      Zstd.setCompressionLevel(this.stream, var3);
   }

   public ZstdOutputStreamNoFinalizer(OutputStream var1, BufferPool var2) throws IOException {
      super(var1);
      this.stream = createCStream();
      this.bufferPool = var2;
      this.dstByteBuffer = Zstd.getArrayBackedBuffer(var2, dstSize);
      this.dst = this.dstByteBuffer.array();
   }

   public synchronized ZstdOutputStreamNoFinalizer setChecksum(boolean var1) throws IOException {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         int var2 = Zstd.setCompressionChecksums(this.stream, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         } else {
            return this;
         }
      }
   }

   public synchronized ZstdOutputStreamNoFinalizer setLevel(int var1) throws IOException {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         int var2 = Zstd.setCompressionLevel(this.stream, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         } else {
            return this;
         }
      }
   }

   public synchronized ZstdOutputStreamNoFinalizer setLong(int var1) throws IOException {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         int var2 = Zstd.setCompressionLong(this.stream, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         } else {
            return this;
         }
      }
   }

   public synchronized ZstdOutputStreamNoFinalizer setWorkers(int var1) throws IOException {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         int var2 = Zstd.setCompressionWorkers(this.stream, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         } else {
            return this;
         }
      }
   }

   public synchronized ZstdOutputStreamNoFinalizer setOverlapLog(int var1) throws IOException {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         int var2 = Zstd.setCompressionOverlapLog(this.stream, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         } else {
            return this;
         }
      }
   }

   public synchronized ZstdOutputStreamNoFinalizer setJobSize(int var1) throws IOException {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         int var2 = Zstd.setCompressionJobSize(this.stream, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         } else {
            return this;
         }
      }
   }

   public synchronized ZstdOutputStreamNoFinalizer setTargetLength(int var1) throws IOException {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         int var2 = Zstd.setCompressionTargetLength(this.stream, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         } else {
            return this;
         }
      }
   }

   public synchronized ZstdOutputStreamNoFinalizer setMinMatch(int var1) throws IOException {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         int var2 = Zstd.setCompressionMinMatch(this.stream, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         } else {
            return this;
         }
      }
   }

   public synchronized ZstdOutputStreamNoFinalizer setSearchLog(int var1) throws IOException {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         int var2 = Zstd.setCompressionSearchLog(this.stream, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         } else {
            return this;
         }
      }
   }

   public synchronized ZstdOutputStreamNoFinalizer setChainLog(int var1) throws IOException {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         int var2 = Zstd.setCompressionChainLog(this.stream, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         } else {
            return this;
         }
      }
   }

   public synchronized ZstdOutputStreamNoFinalizer setHashLog(int var1) throws IOException {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         int var2 = Zstd.setCompressionHashLog(this.stream, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         } else {
            return this;
         }
      }
   }

   public synchronized ZstdOutputStreamNoFinalizer setWindowLog(int var1) throws IOException {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         int var2 = Zstd.setCompressionWindowLog(this.stream, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         } else {
            return this;
         }
      }
   }

   public synchronized ZstdOutputStreamNoFinalizer setStrategy(int var1) throws IOException {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         int var2 = Zstd.setCompressionStrategy(this.stream, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         } else {
            return this;
         }
      }
   }

   public synchronized ZstdOutputStreamNoFinalizer setCloseFrameOnFlush(boolean var1) {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         this.closeFrameOnFlush = var1;
         return this;
      }
   }

   public synchronized ZstdOutputStreamNoFinalizer setDict(byte[] var1) throws IOException {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         int var2 = Zstd.loadDictCompress(this.stream, var1, var1.length);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         } else {
            return this;
         }
      }
   }

   public synchronized ZstdOutputStreamNoFinalizer setDict(ZstdDictCompress var1) throws IOException {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         int var2 = Zstd.loadFastDictCompress(this.stream, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         } else {
            return this;
         }
      }
   }

   @Override
   public synchronized void write(byte[] var1, int var2, int var3) throws IOException {
      if (this.isClosed) {
         throw new IOException("StreamClosed");
      } else {
         if (this.frameClosed) {
            int var4 = this.resetCStream(this.stream);
            if (Zstd.isError(var4)) {
               throw new ZstdIOException(var4);
            }

            this.frameClosed = false;
            this.frameStarted = true;
         }

         int var6 = var2 + var3;
         this.srcPos = var2;

         while (this.srcPos < var6) {
            int var5 = this.compressStream(this.stream, this.dst, dstSize, var1, var6);
            if (Zstd.isError(var5)) {
               throw new ZstdIOException(var5);
            }

            if (this.dstPos > 0L) {
               this.out.write(this.dst, 0, (int)this.dstPos);
            }
         }
      }
   }

   @Override
   public void write(int var1) throws IOException {
      byte[] var2 = new byte[]{(byte)var1};
      this.write(var2, 0, 1);
   }

   @Override
   public synchronized void flush() throws IOException {
      if (this.isClosed) {
         throw new IOException("StreamClosed");
      } else {
         if (!this.frameClosed) {
            int var1;
            if (this.closeFrameOnFlush) {
               while (true) {
                  var1 = this.endStream(this.stream, this.dst, dstSize);
                  if (Zstd.isError(var1)) {
                     throw new ZstdIOException(var1);
                  }

                  this.out.write(this.dst, 0, (int)this.dstPos);
                  if (var1 <= 0) {
                     this.frameClosed = true;
                     break;
                  }
               }
            } else {
               do {
                  var1 = this.flushStream(this.stream, this.dst, dstSize);
                  if (Zstd.isError(var1)) {
                     throw new ZstdIOException(var1);
                  }

                  this.out.write(this.dst, 0, (int)this.dstPos);
               } while (var1 > 0);
            }

            this.out.flush();
         }
      }
   }

   @Override
   public synchronized void close() throws IOException {
      this.close(true);
   }

   public synchronized void closeWithoutClosingParentStream() throws IOException {
      this.close(false);
   }

   private void close(boolean var1) throws IOException {
      if (!this.isClosed) {
         try {
            if (!this.frameStarted) {
               int var2 = this.resetCStream(this.stream);
               if (Zstd.isError(var2)) {
                  throw new ZstdIOException(var2);
               }

               this.frameClosed = false;
            }

            int var6;
            if (!this.frameClosed) {
               do {
                  var6 = this.endStream(this.stream, this.dst, dstSize);
                  if (Zstd.isError(var6)) {
                     throw new ZstdIOException(var6);
                  }

                  this.out.write(this.dst, 0, (int)this.dstPos);
               } while (var6 > 0);
            }

            if (var1) {
               this.out.close();
            }
         } finally {
            this.isClosed = true;
            this.bufferPool.release(this.dstByteBuffer);
            freeCStream(this.stream);
         }
      }
   }

   static {
      Native.load();
   }
}
