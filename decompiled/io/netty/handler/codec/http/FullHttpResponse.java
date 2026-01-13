package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;

public interface FullHttpResponse extends HttpResponse, FullHttpMessage {
   FullHttpResponse copy();

   FullHttpResponse duplicate();

   FullHttpResponse retainedDuplicate();

   FullHttpResponse replace(ByteBuf var1);

   FullHttpResponse retain(int var1);

   FullHttpResponse retain();

   FullHttpResponse touch();

   FullHttpResponse touch(Object var1);

   FullHttpResponse setProtocolVersion(HttpVersion var1);

   FullHttpResponse setStatus(HttpResponseStatus var1);
}
