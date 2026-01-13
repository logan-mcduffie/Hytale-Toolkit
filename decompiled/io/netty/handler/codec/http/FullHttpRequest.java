package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;

public interface FullHttpRequest extends HttpRequest, FullHttpMessage {
   FullHttpRequest copy();

   FullHttpRequest duplicate();

   FullHttpRequest retainedDuplicate();

   FullHttpRequest replace(ByteBuf var1);

   FullHttpRequest retain(int var1);

   FullHttpRequest retain();

   FullHttpRequest touch();

   FullHttpRequest touch(Object var1);

   FullHttpRequest setProtocolVersion(HttpVersion var1);

   FullHttpRequest setMethod(HttpMethod var1);

   FullHttpRequest setUri(String var1);
}
