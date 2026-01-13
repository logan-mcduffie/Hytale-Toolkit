package com.github.luben.zstd;

public class ZstdException extends RuntimeException {
   private long code;

   public ZstdException(long var1) {
      this(Zstd.getErrorCode(var1), Zstd.getErrorName(var1));
   }

   public ZstdException(long var1, String var3) {
      super(var3);
      this.code = var1;
   }

   public long getErrorCode() {
      return this.code;
   }
}
