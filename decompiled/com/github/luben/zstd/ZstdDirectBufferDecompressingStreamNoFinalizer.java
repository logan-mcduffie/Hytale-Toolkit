package com.github.luben.zstd;

import com.github.luben.zstd.util.Native;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ZstdDirectBufferDecompressingStreamNoFinalizer extends BaseZstdBufferDecompressingStreamNoFinalizer {
   public ZstdDirectBufferDecompressingStreamNoFinalizer(ByteBuffer var1) {
      super(var1);
      if (!var1.isDirect()) {
         throw new IllegalArgumentException("Source buffer should be a direct buffer");
      } else {
         this.source = var1;
         this.stream = this.createDStream();
         this.initDStream(this.stream);
      }
   }

   @Override
   public int read(ByteBuffer var1) throws IOException {
      if (!var1.isDirect()) {
         throw new IllegalArgumentException("Target buffer should be a direct buffer");
      } else {
         return this.readInternal(var1, true);
      }
   }

   @Override
   long createDStream() {
      return createDStreamNative();
   }

   @Override
   long freeDStream(long var1) {
      return freeDStreamNative(var1);
   }

   @Override
   long initDStream(long var1) {
      return this.initDStreamNative(var1);
   }

   @Override
   long decompressStream(long var1, ByteBuffer var3, int var4, int var5, ByteBuffer var6, int var7, int var8) {
      return this.decompressStreamNative(var1, var3, var4, var5, var6, var7, var8);
   }

   public static int recommendedTargetBufferSize() {
      return (int)recommendedDOutSizeNative();
   }

   private static native long createDStreamNative();

   private static native long freeDStreamNative(long var0);

   private native long initDStreamNative(long var1);

   private native long decompressStreamNative(long var1, ByteBuffer var3, int var4, int var5, ByteBuffer var6, int var7, int var8);

   private static native long recommendedDOutSizeNative();

   static {
      Native.load();
   }
}
