package io.netty.handler.codec.http.websocketx;

import io.netty.util.AsciiString;

public enum WebSocketVersion {
   UNKNOWN(AsciiString.cached("")),
   V00(AsciiString.cached("0")),
   V07(AsciiString.cached("7")),
   V08(AsciiString.cached("8")),
   V13(AsciiString.cached("13"));

   private final AsciiString headerValue;

   private WebSocketVersion(AsciiString headerValue) {
      this.headerValue = headerValue;
   }

   public String toHttpHeaderValue() {
      return this.toAsciiString().toString();
   }

   AsciiString toAsciiString() {
      if (this == UNKNOWN) {
         throw new IllegalStateException("Unknown web socket version: " + this);
      } else {
         return this.headerValue;
      }
   }
}
