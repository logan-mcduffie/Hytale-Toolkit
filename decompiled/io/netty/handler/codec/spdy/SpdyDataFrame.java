package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;

public interface SpdyDataFrame extends ByteBufHolder, SpdyStreamFrame {
   SpdyDataFrame setStreamId(int var1);

   SpdyDataFrame setLast(boolean var1);

   @Override
   ByteBuf content();

   SpdyDataFrame copy();

   SpdyDataFrame duplicate();

   SpdyDataFrame retainedDuplicate();

   SpdyDataFrame replace(ByteBuf var1);

   SpdyDataFrame retain();

   SpdyDataFrame retain(int var1);

   SpdyDataFrame touch();

   SpdyDataFrame touch(Object var1);
}
