package io.netty.handler.codec.http.websocketx;

public interface WebSocketFrameMaskGenerator {
   int nextMask();
}
