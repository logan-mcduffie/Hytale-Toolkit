package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.CorruptedFrameException;

public final class CorruptedWebSocketFrameException extends CorruptedFrameException {
   private static final long serialVersionUID = 3918055132492988338L;
   private final WebSocketCloseStatus closeStatus;

   public CorruptedWebSocketFrameException() {
      this(WebSocketCloseStatus.PROTOCOL_ERROR, null, null);
   }

   public CorruptedWebSocketFrameException(WebSocketCloseStatus status, String message, Throwable cause) {
      super(message == null ? status.reasonText() : message, cause);
      this.closeStatus = status;
   }

   public CorruptedWebSocketFrameException(WebSocketCloseStatus status, String message) {
      this(status, message, null);
   }

   public CorruptedWebSocketFrameException(WebSocketCloseStatus status, Throwable cause) {
      this(status, null, cause);
   }

   public WebSocketCloseStatus closeStatus() {
      return this.closeStatus;
   }
}
