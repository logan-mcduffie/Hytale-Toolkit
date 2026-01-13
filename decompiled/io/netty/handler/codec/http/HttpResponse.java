package io.netty.handler.codec.http;

public interface HttpResponse extends HttpMessage {
   @Deprecated
   HttpResponseStatus getStatus();

   HttpResponseStatus status();

   HttpResponse setStatus(HttpResponseStatus var1);

   HttpResponse setProtocolVersion(HttpVersion var1);
}
