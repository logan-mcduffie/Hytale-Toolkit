package io.netty.handler.codec.spdy;

public interface SpdyGoAwayFrame extends SpdyFrame {
   int lastGoodStreamId();

   SpdyGoAwayFrame setLastGoodStreamId(int var1);

   SpdySessionStatus status();

   SpdyGoAwayFrame setStatus(SpdySessionStatus var1);
}
