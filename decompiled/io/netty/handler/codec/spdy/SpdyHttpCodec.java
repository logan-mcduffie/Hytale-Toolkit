package io.netty.handler.codec.spdy;

import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.handler.codec.http.HttpHeadersFactory;
import java.util.HashMap;

public final class SpdyHttpCodec extends CombinedChannelDuplexHandler<SpdyHttpDecoder, SpdyHttpEncoder> {
   public SpdyHttpCodec(SpdyVersion version, int maxContentLength) {
      super(new SpdyHttpDecoder(version, maxContentLength), new SpdyHttpEncoder(version));
   }

   @Deprecated
   public SpdyHttpCodec(SpdyVersion version, int maxContentLength, boolean validateHttpHeaders) {
      super(new SpdyHttpDecoder(version, maxContentLength, validateHttpHeaders), new SpdyHttpEncoder(version));
   }

   public SpdyHttpCodec(SpdyVersion version, int maxContentLength, HttpHeadersFactory headersFactory, HttpHeadersFactory trailersFactory) {
      super(new SpdyHttpDecoder(version, maxContentLength, new HashMap<>(), headersFactory, trailersFactory), new SpdyHttpEncoder(version));
   }
}
