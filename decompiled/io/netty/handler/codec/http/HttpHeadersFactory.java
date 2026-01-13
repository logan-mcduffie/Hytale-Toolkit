package io.netty.handler.codec.http;

public interface HttpHeadersFactory {
   HttpHeaders newHeaders();

   HttpHeaders newEmptyHeaders();
}
