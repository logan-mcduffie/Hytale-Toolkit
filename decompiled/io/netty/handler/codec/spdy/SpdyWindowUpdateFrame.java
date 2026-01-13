package io.netty.handler.codec.spdy;

public interface SpdyWindowUpdateFrame extends SpdyFrame {
   int streamId();

   SpdyWindowUpdateFrame setStreamId(int var1);

   int deltaWindowSize();

   SpdyWindowUpdateFrame setDeltaWindowSize(int var1);
}
