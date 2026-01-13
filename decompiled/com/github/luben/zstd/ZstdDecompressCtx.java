package com.github.luben.zstd;

import com.github.luben.zstd.util.Native;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ZstdDecompressCtx extends AutoCloseBase {
   private long nativePtr = 0L;
   private ZstdDictDecompress decompression_dict = null;

   private static native long init();

   private static native void free(long var0);

   public ZstdDecompressCtx() {
      this.nativePtr = init();
      if (0L == this.nativePtr) {
         throw new IllegalStateException("ZSTD_createDeCompressCtx failed");
      } else {
         this.storeFence();
      }
   }

   @Override
   void doClose() {
      if (this.nativePtr != 0L) {
         free(this.nativePtr);
         this.nativePtr = 0L;
      }
   }

   public ZstdDecompressCtx setMagicless(boolean var1) {
      this.ensureOpen();
      this.acquireSharedLock();
      Zstd.setDecompressionMagicless(this.nativePtr, var1);
      this.releaseSharedLock();
      return this;
   }

   public ZstdDecompressCtx loadDict(ZstdDictDecompress var1) {
      this.ensureOpen();
      this.acquireSharedLock();
      var1.acquireSharedLock();

      try {
         long var2 = loadDDictFast0(this.nativePtr, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }

         this.decompression_dict = var1;
      } finally {
         var1.releaseSharedLock();
         this.releaseSharedLock();
      }

      return this;
   }

   private static native long loadDDictFast0(long var0, ZstdDictDecompress var2);

   public ZstdDecompressCtx loadDict(byte[] var1) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = loadDDict0(this.nativePtr, var1);
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }

         this.decompression_dict = null;
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   private static native long loadDDict0(long var0, byte[] var2);

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

   private void ensureOpen() {
      if (this.nativePtr == 0L) {
         throw new IllegalStateException("Decompression context is closed");
      }
   }

   public boolean decompressDirectByteBufferStream(ByteBuffer var1, ByteBuffer var2) {
      this.ensureOpen();
      this.acquireSharedLock();

      boolean var5;
      try {
         long var3 = decompressDirectByteBufferStream0(this.nativePtr, var1, var1.position(), var1.limit(), var2, var2.position(), var2.limit());
         if ((var3 & 2147483648L) != 0L) {
            long var10 = -(var3 & 255L);
            throw new ZstdException(var10, Zstd.getErrorName(var10));
         }

         ((Buffer)var2).position((int)(var3 & 2147483647L));
         ((Buffer)var1).position((int)(var3 >>> 32) & 2147483647);
         var5 = var3 >>> 63 == 1L;
      } finally {
         this.releaseSharedLock();
      }

      return var5;
   }

   private static native long decompressDirectByteBufferStream0(long var0, ByteBuffer var2, int var3, int var4, ByteBuffer var5, int var6, int var7);

   public int decompressDirectByteBuffer(ByteBuffer var1, int var2, int var3, ByteBuffer var4, int var5, int var6) {
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
            long var7 = decompressDirectByteBuffer0(this.nativePtr, var1, var2, var3, var4, var5, var6);
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

   private static native long decompressDirectByteBuffer0(long var0, ByteBuffer var2, int var3, int var4, ByteBuffer var5, int var6, int var7);

   public int decompressByteArray(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6) {
      Objects.checkFromIndexSize(var5, var6, var4.length);
      Objects.checkFromIndexSize(var2, var3, var1.length);
      this.ensureOpen();
      this.acquireSharedLock();

      int var9;
      try {
         long var7 = decompressByteArray0(this.nativePtr, var1, var2, var3, var4, var5, var6);
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

   private static native long decompressByteArray0(long var0, byte[] var2, int var3, int var4, byte[] var5, int var6, int var7);

   public int decompressByteArrayToDirectByteBuffer(ByteBuffer var1, int var2, int var3, byte[] var4, int var5, int var6) {
      if (!var1.isDirect()) {
         throw new IllegalArgumentException("dstBuff must be a direct buffer");
      } else {
         Objects.checkFromIndexSize(var5, var6, var4.length);
         Objects.checkFromIndexSize(var2, var3, var1.limit());
         this.ensureOpen();
         this.acquireSharedLock();

         int var9;
         try {
            long var7 = decompressByteArrayToDirectByteBuffer0(this.nativePtr, var1, var2, var3, var4, var5, var6);
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

   private static native long decompressByteArrayToDirectByteBuffer0(long var0, ByteBuffer var2, int var3, int var4, byte[] var5, int var6, int var7);

   public int decompressDirectByteBufferToByteArray(byte[] var1, int var2, int var3, ByteBuffer var4, int var5, int var6) {
      if (!var4.isDirect()) {
         throw new IllegalArgumentException("srcBuff must be a direct buffer");
      } else {
         Objects.checkFromIndexSize(var5, var6, var4.limit());
         Objects.checkFromIndexSize(var2, var3, var1.length);
         this.ensureOpen();
         this.acquireSharedLock();

         int var9;
         try {
            long var7 = decompressDirectByteBufferToByteArray0(this.nativePtr, var1, var2, var3, var4, var5, var6);
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

   private static native long decompressDirectByteBufferToByteArray0(long var0, byte[] var2, int var3, int var4, ByteBuffer var5, int var6, int var7);

   public int decompress(ByteBuffer var1, ByteBuffer var2) throws ZstdException {
      int var3 = this.decompressDirectByteBuffer(var1, var1.position(), var1.limit() - var1.position(), var2, var2.position(), var2.limit() - var2.position());
      ((Buffer)var2).position(var2.limit());
      ((Buffer)var1).position(var1.position() + var3);
      return var3;
   }

   public int decompress(ByteBuffer var1, byte[] var2) throws ZstdException {
      int var3 = this.decompressByteArrayToDirectByteBuffer(var1, var1.position(), var1.limit() - var1.position(), var2, 0, var2.length);
      ((Buffer)var1).position(var1.position() + var3);
      return var3;
   }

   public int decompress(byte[] var1, ByteBuffer var2) throws ZstdException {
      int var3 = this.decompressDirectByteBufferToByteArray(var1, 0, var1.length, var2, var2.position(), var2.limit() - var2.position());
      ((Buffer)var2).position(var2.limit());
      return var3;
   }

   public ByteBuffer decompress(ByteBuffer var1, int var2) throws ZstdException {
      ByteBuffer var3 = ByteBuffer.allocateDirect(var2);
      int var4 = this.decompressDirectByteBuffer(var3, 0, var2, var1, var1.position(), var1.limit() - var1.position());
      ((Buffer)var1).position(var1.limit());
      return var3;
   }

   public int decompress(byte[] var1, byte[] var2) {
      return this.decompressByteArray(var1, 0, var1.length, var2, 0, var2.length);
   }

   public byte[] decompress(byte[] var1, int var2) throws ZstdException {
      return this.decompress(var1, 0, var1.length, var2);
   }

   public byte[] decompress(byte[] var1, int var2, int var3, int var4) throws ZstdException {
      if (var4 < 0) {
         throw new ZstdException(Zstd.errGeneric(), "Original size should not be negative");
      } else {
         byte[] var5 = new byte[var4];
         int var6 = this.decompressByteArray(var5, 0, var5.length, var1, var2, var3);
         return var6 != var4 ? Arrays.copyOfRange(var5, 0, var6) : var5;
      }
   }

   static {
      Native.load();
   }
}
