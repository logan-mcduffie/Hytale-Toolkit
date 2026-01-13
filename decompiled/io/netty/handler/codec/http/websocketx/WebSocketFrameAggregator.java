package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageAggregator;
import io.netty.util.internal.StringUtil;

public class WebSocketFrameAggregator extends MessageAggregator<WebSocketFrame, WebSocketFrame, ContinuationWebSocketFrame, WebSocketFrame> {
   public WebSocketFrameAggregator(int maxContentLength) {
      super(maxContentLength, WebSocketFrame.class);
   }

   protected boolean isStartMessage(WebSocketFrame msg) throws Exception {
      return msg instanceof TextWebSocketFrame || msg instanceof BinaryWebSocketFrame;
   }

   protected boolean isContentMessage(WebSocketFrame msg) throws Exception {
      return msg instanceof ContinuationWebSocketFrame;
   }

   protected boolean isLastContentMessage(ContinuationWebSocketFrame msg) throws Exception {
      return this.isContentMessage((WebSocketFrame)msg) && msg.isFinalFragment();
   }

   protected boolean isAggregated(WebSocketFrame msg) throws Exception {
      return msg.isFinalFragment() ? !this.isContentMessage(msg) : !this.isStartMessage(msg) && !this.isContentMessage(msg);
   }

   protected boolean isContentLengthInvalid(WebSocketFrame start, int maxContentLength) {
      return false;
   }

   protected Object newContinueResponse(WebSocketFrame start, int maxContentLength, ChannelPipeline pipeline) {
      return null;
   }

   @Override
   protected boolean closeAfterContinueResponse(Object msg) throws Exception {
      throw new UnsupportedOperationException();
   }

   @Override
   protected boolean ignoreContentAfterContinueResponse(Object msg) throws Exception {
      throw new UnsupportedOperationException();
   }

   protected WebSocketFrame beginAggregation(WebSocketFrame start, ByteBuf content) throws Exception {
      if (start instanceof TextWebSocketFrame) {
         return new TextWebSocketFrame(true, start.rsv(), content);
      } else if (start instanceof BinaryWebSocketFrame) {
         return new BinaryWebSocketFrame(true, start.rsv(), content);
      } else {
         throw new Error("Unexpected websocket frame type: " + StringUtil.className(start));
      }
   }
}
