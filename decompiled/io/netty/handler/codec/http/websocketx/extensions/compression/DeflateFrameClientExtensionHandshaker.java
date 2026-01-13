package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtension;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandshaker;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionDecoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionEncoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilterProvider;
import io.netty.util.internal.ObjectUtil;
import java.util.Collections;

public final class DeflateFrameClientExtensionHandshaker implements WebSocketClientExtensionHandshaker {
   private final int compressionLevel;
   private final boolean useWebkitExtensionName;
   private final WebSocketExtensionFilterProvider extensionFilterProvider;
   private final int maxAllocation;

   @Deprecated
   public DeflateFrameClientExtensionHandshaker(boolean useWebkitExtensionName) {
      this(6, useWebkitExtensionName, 0);
   }

   public DeflateFrameClientExtensionHandshaker(boolean useWebkitExtensionName, int maxAllocation) {
      this(6, useWebkitExtensionName, maxAllocation);
   }

   @Deprecated
   public DeflateFrameClientExtensionHandshaker(int compressionLevel, boolean useWebkitExtensionName) {
      this(compressionLevel, useWebkitExtensionName, 0);
   }

   public DeflateFrameClientExtensionHandshaker(int compressionLevel, boolean useWebkitExtensionName, int maxAllocation) {
      this(compressionLevel, useWebkitExtensionName, WebSocketExtensionFilterProvider.DEFAULT, maxAllocation);
   }

   @Deprecated
   public DeflateFrameClientExtensionHandshaker(int compressionLevel, boolean useWebkitExtensionName, WebSocketExtensionFilterProvider extensionFilterProvider) {
      this(compressionLevel, useWebkitExtensionName, extensionFilterProvider, 0);
   }

   public DeflateFrameClientExtensionHandshaker(
      int compressionLevel, boolean useWebkitExtensionName, WebSocketExtensionFilterProvider extensionFilterProvider, int maxAllocation
   ) {
      if (compressionLevel >= 0 && compressionLevel <= 9) {
         this.compressionLevel = compressionLevel;
         this.useWebkitExtensionName = useWebkitExtensionName;
         this.extensionFilterProvider = ObjectUtil.checkNotNull(extensionFilterProvider, "extensionFilterProvider");
         this.maxAllocation = ObjectUtil.checkPositiveOrZero(maxAllocation, "maxAllocation");
      } else {
         throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
      }
   }

   @Override
   public WebSocketExtensionData newRequestData() {
      return new WebSocketExtensionData(this.useWebkitExtensionName ? "x-webkit-deflate-frame" : "deflate-frame", Collections.emptyMap());
   }

   @Override
   public WebSocketClientExtension handshakeExtension(WebSocketExtensionData extensionData) {
      if (!"x-webkit-deflate-frame".equals(extensionData.name()) && !"deflate-frame".equals(extensionData.name())) {
         return null;
      } else {
         return extensionData.parameters().isEmpty()
            ? new DeflateFrameClientExtensionHandshaker.DeflateFrameClientExtension(this.compressionLevel, this.extensionFilterProvider, this.maxAllocation)
            : null;
      }
   }

   private static class DeflateFrameClientExtension implements WebSocketClientExtension {
      private final int compressionLevel;
      private final WebSocketExtensionFilterProvider extensionFilterProvider;
      private final int maxAllocation;

      DeflateFrameClientExtension(int compressionLevel, WebSocketExtensionFilterProvider extensionFilterProvider, int maxAllocation) {
         this.compressionLevel = compressionLevel;
         this.extensionFilterProvider = extensionFilterProvider;
         this.maxAllocation = maxAllocation;
      }

      @Override
      public int rsv() {
         return 4;
      }

      @Override
      public WebSocketExtensionEncoder newExtensionEncoder() {
         return new PerFrameDeflateEncoder(this.compressionLevel, 15, false, this.extensionFilterProvider.encoderFilter());
      }

      @Override
      public WebSocketExtensionDecoder newExtensionDecoder() {
         return new PerFrameDeflateDecoder(false, this.extensionFilterProvider.decoderFilter(), this.maxAllocation);
      }
   }
}
