package io.netty.handler.codec.http.websocketx.extensions;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class WebSocketClientExtensionHandler extends ChannelDuplexHandler {
   private final List<WebSocketClientExtensionHandshaker> extensionHandshakers;

   public WebSocketClientExtensionHandler(WebSocketClientExtensionHandshaker... extensionHandshakers) {
      this.extensionHandshakers = Arrays.asList(ObjectUtil.checkNonEmpty(extensionHandshakers, "extensionHandshakers"));
   }

   @Override
   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
      if (msg instanceof HttpRequest && WebSocketExtensionUtil.isWebsocketUpgrade(((HttpRequest)msg).headers())) {
         HttpRequest request = (HttpRequest)msg;
         String headerValue = request.headers().getAsString(HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS);
         List<WebSocketExtensionData> extraExtensions = new ArrayList<>(this.extensionHandshakers.size());

         for (WebSocketClientExtensionHandshaker extensionHandshaker : this.extensionHandshakers) {
            extraExtensions.add(extensionHandshaker.newRequestData());
         }

         String newHeaderValue = WebSocketExtensionUtil.computeMergeExtensionsHeaderValue(headerValue, extraExtensions);
         request.headers().set(HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS, newHeaderValue);
      }

      super.write(ctx, msg, promise);
   }

   @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      if (msg instanceof HttpResponse) {
         HttpResponse response = (HttpResponse)msg;
         if (WebSocketExtensionUtil.isWebsocketUpgrade(response.headers())) {
            String extensionsHeader = response.headers().getAsString(HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS);
            if (extensionsHeader != null) {
               List<WebSocketExtensionData> extensions = WebSocketExtensionUtil.extractExtensions(extensionsHeader);
               List<WebSocketClientExtension> validExtensions = new ArrayList<>(extensions.size());
               int rsv = 0;

               for (WebSocketExtensionData extensionData : extensions) {
                  Iterator<WebSocketClientExtensionHandshaker> extensionHandshakersIterator = this.extensionHandshakers.iterator();
                  WebSocketClientExtension validExtension = null;

                  while (validExtension == null && extensionHandshakersIterator.hasNext()) {
                     WebSocketClientExtensionHandshaker extensionHandshaker = extensionHandshakersIterator.next();
                     validExtension = extensionHandshaker.handshakeExtension(extensionData);
                  }

                  if (validExtension == null || (validExtension.rsv() & rsv) != 0) {
                     throw new CodecException("invalid WebSocket Extension handshake for \"" + extensionsHeader + '"');
                  }

                  rsv |= validExtension.rsv();
                  validExtensions.add(validExtension);
               }

               for (WebSocketClientExtension validExtension : validExtensions) {
                  WebSocketExtensionDecoder decoder = validExtension.newExtensionDecoder();
                  WebSocketExtensionEncoder encoder = validExtension.newExtensionEncoder();
                  ctx.pipeline().addAfter(ctx.name(), decoder.getClass().getName(), decoder);
                  ctx.pipeline().addAfter(ctx.name(), encoder.getClass().getName(), encoder);
               }
            }

            ctx.pipeline().remove(ctx.name());
         }
      }

      super.channelRead(ctx, msg);
   }
}
