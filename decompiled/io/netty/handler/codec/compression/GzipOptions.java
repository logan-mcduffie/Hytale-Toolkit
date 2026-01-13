package io.netty.handler.codec.compression;

public final class GzipOptions extends DeflateOptions {
   static final GzipOptions DEFAULT = new GzipOptions(6, 15, 8);

   GzipOptions(int compressionLevel, int windowBits, int memLevel) {
      super(compressionLevel, windowBits, memLevel);
   }
}
