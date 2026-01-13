package com.github.luben.zstd;

import com.github.luben.zstd.util.Native;
import java.nio.ByteBuffer;

public class ZstdDictDecompress extends SharedDictBase {
   private long nativePtr = 0L;
   private ByteBuffer sharedDict = null;

   private native void init(byte[] var1, int var2, int var3);

   private native void initDirect(ByteBuffer var1, int var2, int var3, int var4);

   private native void free();

   public ByteBuffer getByReferenceBuffer() {
      return this.sharedDict;
   }

   public ZstdDictDecompress(byte[] var1) {
      this(var1, 0, var1.length);
   }

   public ZstdDictDecompress(byte[] var1, int var2, int var3) {
      this.init(var1, var2, var3);
      if (this.nativePtr == 0L) {
         throw new IllegalStateException("ZSTD_createDDict failed");
      } else {
         this.storeFence();
      }
   }

   public ZstdDictDecompress(ByteBuffer var1) {
      this(var1, false);
   }

   public ZstdDictDecompress(ByteBuffer var1, boolean var2) {
      int var3 = var1.limit() - var1.position();
      if (!var1.isDirect()) {
         throw new IllegalArgumentException("dict must be a direct buffer");
      } else if (var3 < 0) {
         throw new IllegalArgumentException("dict cannot be empty.");
      } else {
         this.initDirect(var1, var1.position(), var3, var2 ? 1 : 0);
         if (this.nativePtr == 0L) {
            throw new IllegalStateException("ZSTD_createDDict failed");
         } else {
            if (var2) {
               this.sharedDict = var1;
            }

            this.storeFence();
         }
      }
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
