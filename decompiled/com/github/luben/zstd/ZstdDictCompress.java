package com.github.luben.zstd;

import com.github.luben.zstd.util.Native;
import java.nio.ByteBuffer;

public class ZstdDictCompress extends SharedDictBase {
   private long nativePtr = 0L;
   private ByteBuffer sharedDict = null;
   private int level = Zstd.defaultCompressionLevel();

   private native void init(byte[] var1, int var2, int var3, int var4);

   private native void initDirect(ByteBuffer var1, int var2, int var3, int var4, int var5);

   private native void free();

   public ByteBuffer getByReferenceBuffer() {
      return this.sharedDict;
   }

   public ZstdDictCompress(byte[] var1, int var2) {
      this(var1, 0, var1.length, var2);
   }

   public ZstdDictCompress(byte[] var1, int var2, int var3, int var4) {
      this.level = var4;
      if (var1.length - var2 < 0) {
         throw new IllegalArgumentException("Dictionary buffer is to short");
      } else {
         this.init(var1, var2, var3, var4);
         if (0L == this.nativePtr) {
            throw new IllegalStateException("ZSTD_createCDict failed");
         } else {
            this.storeFence();
         }
      }
   }

   public ZstdDictCompress(ByteBuffer var1, int var2) {
      this(var1, var2, false);
   }

   public ZstdDictCompress(ByteBuffer var1, int var2, boolean var3) {
      this.level = var2;
      int var4 = var1.limit() - var1.position();
      if (!var1.isDirect()) {
         throw new IllegalArgumentException("dict must be a direct buffer");
      } else if (var4 < 0) {
         throw new IllegalArgumentException("dict cannot be empty.");
      } else {
         this.initDirect(var1, var1.position(), var4, var2, var3 ? 1 : 0);
         if (this.nativePtr == 0L) {
            throw new IllegalStateException("ZSTD_createCDict failed");
         } else {
            if (var3) {
               this.sharedDict = var1;
            }

            this.storeFence();
         }
      }
   }

   int level() {
      return this.level;
   }

   @Override
   void doClose() {
      if (this.nativePtr != 0L) {
         this.free();
         this.nativePtr = 0L;
         this.sharedDict = null;
      }
   }

   static {
      Native.load();
   }
}
