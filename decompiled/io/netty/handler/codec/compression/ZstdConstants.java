package io.netty.handler.codec.compression;

final class ZstdConstants {
   static final int DEFAULT_COMPRESSION_LEVEL = com.github.luben.zstd.Zstd.defaultCompressionLevel();
   static final int MIN_COMPRESSION_LEVEL = com.github.luben.zstd.Zstd.minCompressionLevel();
   static final int MAX_COMPRESSION_LEVEL = com.github.luben.zstd.Zstd.maxCompressionLevel();
   static final int DEFAULT_MAX_ENCODE_SIZE = Integer.MAX_VALUE;
   static final int DEFAULT_BLOCK_SIZE = 65536;

   private ZstdConstants() {
   }
}
