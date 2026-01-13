package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;

public interface HttpContent extends HttpObject, ByteBufHolder {
   HttpContent copy();

   HttpContent duplicate();

   HttpContent retainedDuplicate();

   HttpContent replace(ByteBuf var1);

   HttpContent retain();

   HttpContent retain(int var1);

   HttpContent touch();

   HttpContent touch(Object var1);
}
