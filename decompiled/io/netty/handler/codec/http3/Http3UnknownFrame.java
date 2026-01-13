package io.netty.handler.codec.http3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;

public interface Http3UnknownFrame extends Http3RequestStreamFrame, Http3PushStreamFrame, Http3ControlStreamFrame, ByteBufHolder {
   default long length() {
      return this.content().readableBytes();
   }

   Http3UnknownFrame copy();

   Http3UnknownFrame duplicate();

   Http3UnknownFrame retainedDuplicate();

   Http3UnknownFrame replace(ByteBuf var1);

   Http3UnknownFrame retain();

   Http3UnknownFrame retain(int var1);

   Http3UnknownFrame touch();

   Http3UnknownFrame touch(Object var1);
}
