package com.github.luben.zstd;

import com.github.luben.zstd.util.Native;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ZstdCompressCtx extends AutoCloseBase {
   private long nativePtr = 0L;
   private ZstdDictCompress compression_dict = null;
   private SequenceProducer seqprod = null;
   private long seqprod_state = 0L;

   private static native long init();

   private static native void free(long var0);

   public ZstdCompressCtx() {
      this.nativePtr = init();
      if (0L == this.nativePtr) {
         throw new IllegalStateException("ZSTD_createCompressCtx failed");
      } else {
         this.storeFence();
      }
   }

   @Override
   void doClose() {
      if (this.nativePtr != 0L) {
         free(this.nativePtr);
         this.nativePtr = 0L;
         if (this.seqprod != null) {
            this.seqprod.freeState(this.seqprod_state);
            this.seqprod = null;
         }
      }
   }

   private void ensureOpen() {
      if (this.nativePtr == 0L) {
         throw new IllegalStateException("Compression context is closed");
      }
   }

   public ZstdCompressCtx setLevel(int var1) {
      this.ensureOpen();
      this.acquireSharedLock();
      setLevel0(this.nativePtr, var1);
      this.releaseSharedLock();
      return this;
   }

   private static native void setLevel0(long var0, int var2);

   public ZstdCompressCtx setMagicless(boolean var1) {
      this.ensureOpen();
      this.acquireSharedLock();
      Zstd.setCompressionMagicless(this.nativePtr, var1);
      this.releaseSharedLock();
      return this;
   }

   public ZstdCompressCtx setChecksum(boolean var1) {
      this.ensureOpen();
      this.acquireSharedLock();
      setChecksum0(this.nativePtr, var1);
      this.releaseSharedLock();
      return this;
   }

   private static native void setChecksum0(long var0, boolean var2);

   public ZstdCompressCtx setWorkers(int var1) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = Zstd.setCompressionWorkers(this.nativePtr, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   public ZstdCompressCtx setOverlapLog(int var1) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = Zstd.setCompressionOverlapLog(this.nativePtr, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   public ZstdCompressCtx setJobSize(int var1) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = Zstd.setCompressionJobSize(this.nativePtr, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   public ZstdCompressCtx setTargetLength(int var1) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = Zstd.setCompressionTargetLength(this.nativePtr, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   public ZstdCompressCtx setMinMatch(int var1) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = Zstd.setCompressionMinMatch(this.nativePtr, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   public ZstdCompressCtx setSearchLog(int var1) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = Zstd.setCompressionSearchLog(this.nativePtr, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   public ZstdCompressCtx setChainLog(int var1) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = Zstd.setCompressionChainLog(this.nativePtr, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   public ZstdCompressCtx setHashLog(int var1) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = Zstd.setCompressionHashLog(this.nativePtr, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   public ZstdCompressCtx setWindowLog(int var1) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = Zstd.setCompressionWindowLog(this.nativePtr, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   public ZstdCompressCtx setStrategy(int var1) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = Zstd.setCompressionStrategy(this.nativePtr, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   public ZstdCompressCtx setContentSize(boolean var1) {
      this.ensureOpen();
      this.acquireSharedLock();
      setContentSize0(this.nativePtr, var1);
      this.releaseSharedLock();
      return this;
   }

   private static native void setContentSize0(long var0, boolean var2);

   public ZstdCompressCtx setDictID(boolean var1) {
      this.ensureOpen();
      this.acquireSharedLock();
      setDictID0(this.nativePtr, var1);
      this.releaseSharedLock();
      return this;
   }

   private static native void setDictID0(long var0, boolean var2);

   public ZstdCompressCtx setLong(int var1) {
      this.ensureOpen();
      this.acquireSharedLock();
      Zstd.setCompressionLong(this.nativePtr, var1);
      this.releaseSharedLock();
      return this;
   }

   public ZstdCompressCtx registerSequenceProducer(SequenceProducer var1) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         if (this.seqprod != null) {
            this.seqprod.freeState(this.seqprod_state);
            this.seqprod = null;
         }

         if (var1 == null) {
            Zstd.registerSequenceProducer(this.nativePtr, 0L, 0L);
         } else {
            this.seqprod_state = var1.createState();
            Zstd.registerSequenceProducer(this.nativePtr, this.seqprod_state, var1.getFunctionPointer());
            this.seqprod = var1;
         }
      } catch (Exception var6) {
         this.seqprod = null;
         Zstd.registerSequenceProducer(this.nativePtr, 0L, 0L);
         throw var6;
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   public ZstdCompressCtx setSequenceProducerFallback(boolean var1) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = Zstd.setSequenceProducerFallback(this.nativePtr, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   public ZstdCompressCtx setSearchForExternalRepcodes(Zstd.ParamSwitch var1) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = Zstd.setSearchForExternalRepcodes(this.nativePtr, var1.getValue());
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   public ZstdCompressCtx setValidateSequences(Zstd.ParamSwitch var1) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = Zstd.setValidateSequences(this.nativePtr, var1.getValue());
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   public ZstdCompressCtx setEnableLongDistanceMatching(Zstd.ParamSwitch var1) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = Zstd.setEnableLongDistanceMatching(this.nativePtr, var1.getValue());
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   long getNativePtr() {
      return this.nativePtr;
   }

   public ZstdCompressCtx loadDict(ZstdDictCompress var1) {
      this.ensureOpen();
      this.acquireSharedLock();
      var1.acquireSharedLock();

      try {
         long var2 = this.loadCDictFast0(this.nativePtr, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }

         this.compression_dict = var1;
      } finally {
         var1.releaseSharedLock();
         this.releaseSharedLock();
      }

      return this;
   }

   private native long loadCDictFast0(long var1, ZstdDictCompress var3);

   public ZstdCompressCtx loadDict(byte[] var1) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = this.loadCDict0(this.nativePtr, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }

         this.compression_dict = null;
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   private native long loadCDict0(long var1, byte[] var3);

   public ZstdFrameProgression getFrameProgression() {
      this.ensureOpen();
      this.acquireSharedLock();

      ZstdFrameProgression var1;
      try {
         var1 = getFrameProgression0(this.nativePtr);
      } finally {
         this.releaseSharedLock();
      }

      return var1;
   }

   private static native ZstdFrameProgression getFrameProgression0(long var0);

   public void reset() {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var1 = reset0(this.nativePtr);
         if (Zstd.isError(var1)) {
            throw new ZstdException(var1);
         }
      } finally {
         this.releaseSharedLock();
      }
   }

   private static native long reset0(long var0);

   public void setPledgedSrcSize(long var1) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var3 = setPledgedSrcSize0(this.nativePtr, var1);
         if (Zstd.isError(var3)) {
            throw new ZstdException(var3);
         }
      } finally {
         this.releaseSharedLock();
      }
   }

   private static native long setPledgedSrcSize0(long var0, long var2);

   public boolean compressDirectByteBufferStream(ByteBuffer var1, ByteBuffer var2, EndDirective var3) {
      this.ensureOpen();
      this.acquireSharedLock();

      boolean var6;
      try {
         long var4 = compressDirectByteBufferStream0(this.nativePtr, var1, var1.position(), var1.limit(), var2, var2.position(), var2.limit(), var3.value());
         if ((var4 & 2147483648L) != 0L) {
            long var11 = -(var4 & 255L);
            throw new ZstdException(var11, Zstd.getErrorName(var11));
         }

         ((Buffer)var2).position((int)(var4 & 2147483647L));
         ((Buffer)var1).position((int)(var4 >>> 32) & 2147483647);
         var6 = var4 >>> 63 == 1L;
      } finally {
         this.releaseSharedLock();
      }

      return var6;
   }

   private static native long compressDirectByteBufferStream0(long var0, ByteBuffer var2, int var3, int var4, ByteBuffer var5, int var6, int var7, int var8);

   public int compressDirectByteBuffer(ByteBuffer var1, int var2, int var3, ByteBuffer var4, int var5, int var6) {
      this.ensureOpen();
      if (!var4.isDirect()) {
         throw new IllegalArgumentException("srcBuff must be a direct buffer");
      } else if (!var1.isDirect()) {
         throw new IllegalArgumentException("dstBuff must be a direct buffer");
      } else {
         Objects.checkFromIndexSize(var5, var6, var4.limit());
         Objects.checkFromIndexSize(var2, var3, var1.limit());
         this.acquireSharedLock();

         int var9;
         try {
            long var7 = compressDirectByteBuffer0(this.nativePtr, var1, var2, var3, var4, var5, var6);
            if (Zstd.isError(var7)) {
               throw new ZstdException(var7);
            }

            if (var7 > 2147483647L) {
               throw new ZstdException(Zstd.errGeneric(), "Output size is greater than MAX_INT");
            }

            var9 = (int)var7;
         } finally {
            this.releaseSharedLock();
         }

         return var9;
      }
   }

   private static native long compressDirectByteBuffer0(long var0, ByteBuffer var2, int var3, int var4, ByteBuffer var5, int var6, int var7);

   public int compressByteArray(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6) {
      Objects.checkFromIndexSize(var5, var6, var4.length);
      Objects.checkFromIndexSize(var2, var3, var1.length);
      this.ensureOpen();
      this.acquireSharedLock();

      int var9;
      try {
         long var7 = compressByteArray0(this.nativePtr, var1, var2, var3, var4, var5, var6);
         if (Zstd.isError(var7)) {
            throw new ZstdException(var7);
         }

         if (var7 > 2147483647L) {
            throw new ZstdException(Zstd.errGeneric(), "Output size is greater than MAX_INT");
         }

         var9 = (int)var7;
      } finally {
         this.releaseSharedLock();
      }

      return var9;
   }

   private static native long compressByteArray0(long var0, byte[] var2, int var3, int var4, byte[] var5, int var6, int var7);

   public int compress(ByteBuffer var1, ByteBuffer var2) {
      int var3 = this.compressDirectByteBuffer(var1, var1.position(), var1.limit() - var1.position(), var2, var2.position(), var2.limit() - var2.position());
      ((Buffer)var2).position(var2.limit());
      ((Buffer)var1).position(var1.position() + var3);
      return var3;
   }

   public ByteBuffer compress(ByteBuffer var1) throws ZstdException {
      long var2 = Zstd.compressBound(var1.limit() - var1.position());
      if (var2 > 2147483647L) {
         throw new ZstdException(Zstd.errGeneric(), "Max output size is greater than MAX_INT");
      } else {
         ByteBuffer var4 = ByteBuffer.allocateDirect((int)var2);
         int var5 = this.compressDirectByteBuffer(var4, 0, (int)var2, var1, var1.position(), var1.limit() - var1.position());
         ((Buffer)var1).position(var1.limit());
         ((Buffer)var4).limit(var5);
         return var4;
      }
   }

   public int compress(byte[] var1, byte[] var2) {
      return this.compressByteArray(var1, 0, var1.length, var2, 0, var2.length);
   }

   public byte[] compress(byte[] var1) {
      long var2 = Zstd.compressBound(var1.length);
      if (var2 > 2147483647L) {
         throw new ZstdException(Zstd.errGeneric(), "Max output size is greater than MAX_INT");
      } else {
         byte[] var4 = new byte[(int)var2];
         int var5 = this.compressByteArray(var4, 0, var4.length, var1, 0, var1.length);
         return Arrays.copyOfRange(var4, 0, var5);
      }
   }

   static {
      Native.load();
   }
}
