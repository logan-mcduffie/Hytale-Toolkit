package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;

public interface SpdyUnknownFrame extends SpdyFrame, ByteBufHolder {
   int frameType();

   byte flags();

   SpdyUnknownFrame copy();

   SpdyUnknownFrame duplicate();

   SpdyUnknownFrame retainedDuplicate();

   SpdyUnknownFrame replace(ByteBuf var1);

   SpdyUnknownFrame retain();

   SpdyUnknownFrame retain(int var1);

   SpdyUnknownFrame touch();

   SpdyUnknownFrame touch(Object var1);
}
