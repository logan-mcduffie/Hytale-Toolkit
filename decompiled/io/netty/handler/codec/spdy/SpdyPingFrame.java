package io.netty.handler.codec.spdy;

public interface SpdyPingFrame extends SpdyFrame {
   int id();

   SpdyPingFrame setId(int var1);
}
