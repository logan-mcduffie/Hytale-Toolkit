package com.github.luben.zstd;

import com.github.luben.zstd.util.Native;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ZstdBufferDecompressingStreamNoFinalizer extends BaseZstdBufferDecompressingStreamNoFinalizer {
   public ZstdBufferDecompressingStreamNoFinalizer(ByteBuffer var1) {
      super(var1);
      if (var1.isDirect()) {
         throw new IllegalArgumentException("Source buffer should be a non-direct buffer");
      } else {
         this.stream = this.createDStream();
         this.initDStream(this.stream);
      }
   }

   @Override
   public int read(ByteBuffer var1) throws IOException {
      if (var1.isDirect()) {
         throw new IllegalArgumentException("Target buffer should be a non-direct buffer");
      } else {
         return this.readInternal(var1, false);
      }
   }

   @Override
   long createDStream() {
      return this.createDStreamNative();
   }

   @Override
   long freeDStream(long var1) {
      return this.freeDStreamNative(var1);
   }

   @Override
   long initDStream(long var1) {
      return this.initDStreamNative(var1);
   }

   @Override
   long decompressStream(long var1, ByteBuffer var3, int var4, int var5, ByteBuffer var6, int var7, int var8) {
      if (!var6.hasArray()) {
         throw new IllegalArgumentException("provided source ByteBuffer lacks array");
      } else if (!var3.hasArray()) {
         throw new IllegalArgumentException("provided destination ByteBuffer lacks array");
      } else {
         byte[] var9 = var3.array();
         byte[] var10 = var6.array();
         return this.decompressStreamNative(var1, var9, var4 + var3.arrayOffset(), var5, var10, var7 + var6.arrayOffset(), var8);
      }
   }

   public static int recommendedTargetBufferSize() {
      return (int)recommendedDOutSizeNative();
   }

   private native long createDStreamNative();

   private native long freeDStreamNative(long var1);

   private native long initDStreamNative(long var1);

   private native long decompressStreamNative(long var1, byte[] var3, int var4, int var5, byte[] var6, int var7, int var8);

   private static native long recommendedDOutSizeNative();

   static {
      Native.load();
   }
}
