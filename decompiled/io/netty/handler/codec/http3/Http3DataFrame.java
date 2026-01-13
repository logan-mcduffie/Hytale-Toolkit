package io.netty.handler.codec.http3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;

public interface Http3DataFrame extends ByteBufHolder, Http3RequestStreamFrame, Http3PushStreamFrame {
   @Override
   default long type() {
      return 0L;
   }

   Http3DataFrame copy();

   Http3DataFrame duplicate();

   Http3DataFrame retainedDuplicate();

   Http3DataFrame replace(ByteBuf var1);

   Http3DataFrame retain();

   Http3DataFrame retain(int var1);

   Http3DataFrame touch();

   Http3DataFrame touch(Object var1);
}
