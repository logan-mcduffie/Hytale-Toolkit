package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

public class CloseWebSocketFrame extends WebSocketFrame {
   public CloseWebSocketFrame() {
      super(Unpooled.buffer(0));
   }

   public CloseWebSocketFrame(WebSocketCloseStatus status) {
      this(requireValidStatusCode(status.code()), status.reasonText());
   }

   public CloseWebSocketFrame(WebSocketCloseStatus status, String reasonText) {
      this(requireValidStatusCode(status.code()), reasonText);
   }

   public CloseWebSocketFrame(int statusCode, String reasonText) {
      this(true, 0, requireValidStatusCode(statusCode), reasonText);
   }

   public CloseWebSocketFrame(boolean finalFragment, int rsv) {
      this(finalFragment, rsv, Unpooled.buffer(0));
   }

   public CloseWebSocketFrame(boolean finalFragment, int rsv, int statusCode, String reasonText) {
      super(finalFragment, rsv, newBinaryData(requireValidStatusCode(statusCode), reasonText));
   }

   private static ByteBuf newBinaryData(int statusCode, String reasonText) {
      if (reasonText == null) {
         reasonText = "";
      }

      ByteBuf binaryData = Unpooled.buffer(2 + reasonText.length());
      binaryData.writeShort(statusCode);
      if (!reasonText.isEmpty()) {
         binaryData.writeCharSequence(reasonText, CharsetUtil.UTF_8);
      }

      return binaryData;
   }

   public CloseWebSocketFrame(boolean finalFragment, int rsv, ByteBuf binaryData) {
      super(finalFragment, rsv, binaryData);
   }

   public int statusCode() {
      ByteBuf binaryData = this.content();
      return binaryData != null && binaryData.readableBytes() >= 2 ? binaryData.getUnsignedShort(binaryData.readerIndex()) : -1;
   }

   public String reasonText() {
      ByteBuf binaryData = this.content();
      return binaryData != null && binaryData.readableBytes() > 2
         ? binaryData.toString(binaryData.readerIndex() + 2, binaryData.readableBytes() - 2, CharsetUtil.UTF_8)
         : "";
   }

   public CloseWebSocketFrame copy() {
      return (CloseWebSocketFrame)super.copy();
   }

   public CloseWebSocketFrame duplicate() {
      return (CloseWebSocketFrame)super.duplicate();
   }

   public CloseWebSocketFrame retainedDuplicate() {
      return (CloseWebSocketFrame)super.retainedDuplicate();
   }

   public CloseWebSocketFrame replace(ByteBuf content) {
      return new CloseWebSocketFrame(this.isFinalFragment(), this.rsv(), content);
   }

   public CloseWebSocketFrame retain() {
      super.retain();
      return this;
   }

   public CloseWebSocketFrame retain(int increment) {
      super.retain(increment);
      return this;
   }

   public CloseWebSocketFrame touch() {
      super.touch();
      return this;
   }

   public CloseWebSocketFrame touch(Object hint) {
      super.touch(hint);
      return this;
   }

   static int requireValidStatusCode(int statusCode) {
      if (WebSocketCloseStatus.isValidStatusCode(statusCode)) {
         return statusCode;
      } else {
         throw new IllegalArgumentException("WebSocket close status code does NOT comply with RFC-6455: " + statusCode);
      }
   }
}
