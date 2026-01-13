package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PongWebSocketFrame extends WebSocketFrame {
   public PongWebSocketFrame() {
      super(Unpooled.buffer(0));
   }

   public PongWebSocketFrame(ByteBuf binaryData) {
      super(binaryData);
   }

   public PongWebSocketFrame(boolean finalFragment, int rsv, ByteBuf binaryData) {
      super(finalFragment, rsv, binaryData);
   }

   public PongWebSocketFrame copy() {
      return (PongWebSocketFrame)super.copy();
   }

   public PongWebSocketFrame duplicate() {
      return (PongWebSocketFrame)super.duplicate();
   }

   public PongWebSocketFrame retainedDuplicate() {
      return (PongWebSocketFrame)super.retainedDuplicate();
   }

   public PongWebSocketFrame replace(ByteBuf content) {
      return new PongWebSocketFrame(this.isFinalFragment(), this.rsv(), content);
   }

   public PongWebSocketFrame retain() {
      super.retain();
      return this;
   }

   public PongWebSocketFrame retain(int increment) {
      super.retain(increment);
      return this;
   }

   public PongWebSocketFrame touch() {
      super.touch();
      return this;
   }

   public PongWebSocketFrame touch(Object hint) {
      super.touch(hint);
      return this;
   }
}
