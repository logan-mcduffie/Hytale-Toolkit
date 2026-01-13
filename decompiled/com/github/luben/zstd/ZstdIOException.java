package com.github.luben.zstd;

import java.io.IOException;

public class ZstdIOException extends IOException {
   private long code;

   public ZstdIOException(long var1) {
      this(Zstd.getErrorCode(var1), Zstd.getErrorName(var1));
   }

   public ZstdIOException(long var1, String var3) {
      super(var3);
      this.code = var1;
   }

   public long getErrorCode() {
      return this.code;
   }
}
