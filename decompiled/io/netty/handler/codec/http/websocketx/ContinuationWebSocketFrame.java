package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

public class ContinuationWebSocketFrame extends WebSocketFrame {
   public ContinuationWebSocketFrame() {
      this(Unpooled.buffer(0));
   }

   public ContinuationWebSocketFrame(ByteBuf binaryData) {
      super(binaryData);
   }

   public ContinuationWebSocketFrame(boolean finalFragment, int rsv, ByteBuf binaryData) {
      super(finalFragment, rsv, binaryData);
   }

   public ContinuationWebSocketFrame(boolean finalFragment, int rsv, String text) {
      this(finalFragment, rsv, fromText(text));
   }

   public String text() {
      return this.content().toString(CharsetUtil.UTF_8);
   }

   private static ByteBuf fromText(String text) {
      return text != null && !text.isEmpty() ? Unpooled.copiedBuffer(text, CharsetUtil.UTF_8) : Unpooled.EMPTY_BUFFER;
   }

   public ContinuationWebSocketFrame copy() {
      return (ContinuationWebSocketFrame)super.copy();
   }

   public ContinuationWebSocketFrame duplicate() {
      return (ContinuationWebSocketFrame)super.duplicate();
   }

   public ContinuationWebSocketFrame retainedDuplicate() {
      return (ContinuationWebSocketFrame)super.retainedDuplicate();
   }

   public ContinuationWebSocketFrame replace(ByteBuf content) {
      return new ContinuationWebSocketFrame(this.isFinalFragment(), this.rsv(), content);
   }

   public ContinuationWebSocketFrame retain() {
      super.retain();
      return this;
   }

   public ContinuationWebSocketFrame retain(int increment) {
      super.retain(increment);
      return this;
   }

   public ContinuationWebSocketFrame touch() {
      super.touch();
      return this;
   }

   public ContinuationWebSocketFrame touch(Object hint) {
      super.touch(hint);
      return this;
   }
}
