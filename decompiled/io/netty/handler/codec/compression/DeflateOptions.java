package io.netty.handler.codec.compression;

import io.netty.util.internal.ObjectUtil;

public class DeflateOptions implements CompressionOptions {
   private final int compressionLevel;
   private final int windowBits;
   private final int memLevel;
   static final DeflateOptions DEFAULT = new DeflateOptions(6, 15, 8);

   DeflateOptions(int compressionLevel, int windowBits, int memLevel) {
      this.compressionLevel = ObjectUtil.checkInRange(compressionLevel, 0, 9, "compressionLevel");
      this.windowBits = ObjectUtil.checkInRange(windowBits, 9, 15, "windowBits");
      this.memLevel = ObjectUtil.checkInRange(memLevel, 1, 9, "memLevel");
   }

   public int compressionLevel() {
      return this.compressionLevel;
   }

   public int windowBits() {
      return this.windowBits;
   }

   public int memLevel() {
      return this.memLevel;
   }
}
