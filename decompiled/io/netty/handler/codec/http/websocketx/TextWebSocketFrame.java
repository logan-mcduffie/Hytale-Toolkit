package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

public class TextWebSocketFrame extends WebSocketFrame {
   public TextWebSocketFrame() {
      super(Unpooled.buffer(0));
   }

   public TextWebSocketFrame(String text) {
      super(fromText(text));
   }

   public TextWebSocketFrame(ByteBuf binaryData) {
      super(binaryData);
   }

   public TextWebSocketFrame(boolean finalFragment, int rsv, String text) {
      super(finalFragment, rsv, fromText(text));
   }

   private static ByteBuf fromText(String text) {
      return text != null && !text.isEmpty() ? Unpooled.copiedBuffer(text, CharsetUtil.UTF_8) : Unpooled.EMPTY_BUFFER;
   }

   public TextWebSocketFrame(boolean finalFragment, int rsv, ByteBuf binaryData) {
      super(finalFragment, rsv, binaryData);
   }

   public String text() {
      return this.content().toString(CharsetUtil.UTF_8);
   }

   public TextWebSocketFrame copy() {
      return (TextWebSocketFrame)super.copy();
   }

   public TextWebSocketFrame duplicate() {
      return (TextWebSocketFrame)super.duplicate();
   }

   public TextWebSocketFrame retainedDuplicate() {
      return (TextWebSocketFrame)super.retainedDuplicate();
   }

   public TextWebSocketFrame replace(ByteBuf content) {
      return new TextWebSocketFrame(this.isFinalFragment(), this.rsv(), content);
   }

   public TextWebSocketFrame retain() {
      super.retain();
      return this;
   }

   public TextWebSocketFrame retain(int increment) {
      super.retain(increment);
      return this;
   }

   public TextWebSocketFrame touch() {
      super.touch();
      return this;
   }

   public TextWebSocketFrame touch(Object hint) {
      super.touch(hint);
      return this;
   }
}
