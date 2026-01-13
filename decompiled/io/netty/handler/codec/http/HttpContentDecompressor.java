package io.netty.handler.codec.http;

import io.netty.channel.Channel;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.compression.Brotli;
import io.netty.handler.codec.compression.BrotliDecoder;
import io.netty.handler.codec.compression.SnappyFrameDecoder;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.compression.Zstd;
import io.netty.handler.codec.compression.ZstdDecoder;
import io.netty.util.internal.ObjectUtil;

public class HttpContentDecompressor extends HttpContentDecoder {
   private final boolean strict;
   private final int maxAllocation;

   @Deprecated
   public HttpContentDecompressor() {
      this(false, 0);
   }

   public HttpContentDecompressor(int maxAllocation) {
      this(false, maxAllocation);
   }

   @Deprecated
   public HttpContentDecompressor(boolean strict) {
      this(strict, 0);
   }

   public HttpContentDecompressor(boolean strict, int maxAllocation) {
      this.strict = strict;
      this.maxAllocation = ObjectUtil.checkPositiveOrZero(maxAllocation, "maxAllocation");
   }

   @Override
   protected EmbeddedChannel newContentDecoder(String contentEncoding) throws Exception {
      Channel channel = this.ctx.channel();
      if (HttpHeaderValues.GZIP.contentEqualsIgnoreCase(contentEncoding) || HttpHeaderValues.X_GZIP.contentEqualsIgnoreCase(contentEncoding)) {
         return EmbeddedChannel.builder()
            .channelId(channel.id())
            .hasDisconnect(channel.metadata().hasDisconnect())
            .config(channel.config())
            .handlers(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP, this.maxAllocation))
            .build();
      } else if (HttpHeaderValues.DEFLATE.contentEqualsIgnoreCase(contentEncoding) || HttpHeaderValues.X_DEFLATE.contentEqualsIgnoreCase(contentEncoding)) {
         ZlibWrapper wrapper = this.strict ? ZlibWrapper.ZLIB : ZlibWrapper.ZLIB_OR_NONE;
         return EmbeddedChannel.builder()
            .channelId(channel.id())
            .hasDisconnect(channel.metadata().hasDisconnect())
            .config(channel.config())
            .handlers(ZlibCodecFactory.newZlibDecoder(wrapper, this.maxAllocation))
            .build();
      } else if (Brotli.isAvailable() && HttpHeaderValues.BR.contentEqualsIgnoreCase(contentEncoding)) {
         return EmbeddedChannel.builder()
            .channelId(channel.id())
            .hasDisconnect(channel.metadata().hasDisconnect())
            .config(channel.config())
            .handlers(new BrotliDecoder())
            .build();
      } else if (HttpHeaderValues.SNAPPY.contentEqualsIgnoreCase(contentEncoding)) {
         return EmbeddedChannel.builder()
            .channelId(channel.id())
            .hasDisconnect(channel.metadata().hasDisconnect())
            .config(channel.config())
            .handlers(new SnappyFrameDecoder())
            .build();
      } else {
         return Zstd.isAvailable() && HttpHeaderValues.ZSTD.contentEqualsIgnoreCase(contentEncoding)
            ? EmbeddedChannel.builder()
               .channelId(channel.id())
               .hasDisconnect(channel.metadata().hasDisconnect())
               .config(channel.config())
               .handlers(new ZstdDecoder())
               .build()
            : null;
      }
   }
}
