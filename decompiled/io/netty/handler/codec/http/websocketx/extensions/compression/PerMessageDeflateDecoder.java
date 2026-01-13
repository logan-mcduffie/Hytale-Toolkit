package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilter;
import java.util.List;

class PerMessageDeflateDecoder extends DeflateDecoder {
   private boolean compressing;

   PerMessageDeflateDecoder(boolean noContext, int maxAllocation) {
      super(noContext, WebSocketExtensionFilter.NEVER_SKIP, maxAllocation);
   }

   PerMessageDeflateDecoder(boolean noContext, WebSocketExtensionFilter extensionDecoderFilter, int maxAllocation) {
      super(noContext, extensionDecoderFilter, maxAllocation);
   }

   @Override
   public boolean acceptInboundMessage(Object msg) throws Exception {
      if (!super.acceptInboundMessage(msg)) {
         return false;
      } else {
         WebSocketFrame wsFrame = (WebSocketFrame)msg;
         if (this.extensionDecoderFilter().mustSkip(wsFrame)) {
            if (this.compressing) {
               throw new IllegalStateException("Cannot skip per message deflate decoder, compression in progress");
            } else {
               return false;
            }
         } else {
            return (wsFrame instanceof TextWebSocketFrame || wsFrame instanceof BinaryWebSocketFrame) && (wsFrame.rsv() & 4) > 0
               || wsFrame instanceof ContinuationWebSocketFrame && this.compressing;
         }
      }
   }

   @Override
   protected int newRsv(WebSocketFrame msg) {
      return (msg.rsv() & 4) > 0 ? msg.rsv() ^ 4 : msg.rsv();
   }

   @Override
   protected boolean appendFrameTail(WebSocketFrame msg) {
      return msg.isFinalFragment();
   }

   @Override
   protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
      super.decode(ctx, msg, out);
      if (msg.isFinalFragment()) {
         this.compressing = false;
      } else if (msg instanceof TextWebSocketFrame || msg instanceof BinaryWebSocketFrame) {
         this.compressing = true;
      }
   }
}
