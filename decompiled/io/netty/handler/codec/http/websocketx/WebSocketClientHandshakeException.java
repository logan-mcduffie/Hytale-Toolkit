package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpResponse;

public final class WebSocketClientHandshakeException extends WebSocketHandshakeException {
   private static final long serialVersionUID = 1L;
   private final HttpResponse response;

   public WebSocketClientHandshakeException(String message) {
      this(message, null);
   }

   public WebSocketClientHandshakeException(String message, HttpResponse httpResponse) {
      super(message);
      if (httpResponse != null) {
         this.response = new DefaultHttpResponse(httpResponse.protocolVersion(), httpResponse.status(), httpResponse.headers());
      } else {
         this.response = null;
      }
   }

   public HttpResponse response() {
      return this.response;
   }
}
