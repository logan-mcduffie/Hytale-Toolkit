package io.netty.handler.codec.compression;

import com.aayushatharva.brotli4j.encoder.Encoder.Parameters;
import io.netty.util.internal.ObjectUtil;

public final class StandardCompressionOptions {
   private StandardCompressionOptions() {
   }

   public static BrotliOptions brotli() {
      return BrotliOptions.DEFAULT;
   }

   @Deprecated
   public static BrotliOptions brotli(Parameters parameters) {
      return new BrotliOptions(parameters);
   }

   public static BrotliOptions brotli(int quality, int window, BrotliMode mode) {
      ObjectUtil.checkInRange(quality, 0, 11, "quality");
      ObjectUtil.checkInRange(window, 10, 24, "window");
      ObjectUtil.checkNotNull(mode, "mode");
      Parameters parameters = new Parameters().setQuality(quality).setWindow(window).setMode(mode.adapt());
      return new BrotliOptions(parameters);
   }

   public static ZstdOptions zstd() {
      return ZstdOptions.DEFAULT;
   }

   public static ZstdOptions zstd(int compressionLevel, int blockSize, int maxEncodeSize) {
      return new ZstdOptions(compressionLevel, blockSize, maxEncodeSize);
   }

   public static SnappyOptions snappy() {
      return new SnappyOptions();
   }

   public static GzipOptions gzip() {
      return GzipOptions.DEFAULT;
   }

   public static GzipOptions gzip(int compressionLevel, int windowBits, int memLevel) {
      return new GzipOptions(compressionLevel, windowBits, memLevel);
   }

   public static DeflateOptions deflate() {
      return DeflateOptions.DEFAULT;
   }

   public static DeflateOptions deflate(int compressionLevel, int windowBits, int memLevel) {
      return new DeflateOptions(compressionLevel, windowBits, memLevel);
   }
}
