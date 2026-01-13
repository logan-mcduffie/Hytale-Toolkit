package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;

public interface FullHttpMessage extends HttpMessage, LastHttpContent {
   FullHttpMessage copy();

   FullHttpMessage duplicate();

   FullHttpMessage retainedDuplicate();

   FullHttpMessage replace(ByteBuf var1);

   FullHttpMessage retain(int var1);

   FullHttpMessage retain();

   FullHttpMessage touch();

   FullHttpMessage touch(Object var1);
}
