package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionDecoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionEncoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilterProvider;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtension;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtensionHandshaker;
import io.netty.util.internal.ObjectUtil;
import java.util.Collections;

public final class DeflateFrameServerExtensionHandshaker implements WebSocketServerExtensionHandshaker {
   public static final int DEFAULT_COMPRESSION_LEVEL = 6;
   static final String X_WEBKIT_DEFLATE_FRAME_EXTENSION = "x-webkit-deflate-frame";
   static final String DEFLATE_FRAME_EXTENSION = "deflate-frame";
   private final int compressionLevel;
   private final WebSocketExtensionFilterProvider extensionFilterProvider;
   private final int maxAllocation;

   @Deprecated
   public DeflateFrameServerExtensionHandshaker() {
      this(6, 0);
   }

   @Deprecated
   public DeflateFrameServerExtensionHandshaker(int compressionLevel) {
      this(compressionLevel, 0);
   }

   public DeflateFrameServerExtensionHandshaker(int compressionLevel, int maxAllocation) {
      this(compressionLevel, WebSocketExtensionFilterProvider.DEFAULT, maxAllocation);
   }

   @Deprecated
   public DeflateFrameServerExtensionHandshaker(int compressionLevel, WebSocketExtensionFilterProvider extensionFilterProvider) {
      this(compressionLevel, extensionFilterProvider, 0);
   }

   public DeflateFrameServerExtensionHandshaker(int compressionLevel, WebSocketExtensionFilterProvider extensionFilterProvider, int maxAllocation) {
      if (compressionLevel >= 0 && compressionLevel <= 9) {
         this.compressionLevel = compressionLevel;
         this.extensionFilterProvider = ObjectUtil.checkNotNull(extensionFilterProvider, "extensionFilterProvider");
         this.maxAllocation = ObjectUtil.checkPositiveOrZero(maxAllocation, "maxAllocation");
      } else {
         throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
      }
   }

   @Override
   public WebSocketServerExtension handshakeExtension(WebSocketExtensionData extensionData) {
      if (!"x-webkit-deflate-frame".equals(extensionData.name()) && !"deflate-frame".equals(extensionData.name())) {
         return null;
      } else {
         return extensionData.parameters().isEmpty()
            ? new DeflateFrameServerExtensionHandshaker.DeflateFrameServerExtension(
               this.compressionLevel, extensionData.name(), this.extensionFilterProvider, this.maxAllocation
            )
            : null;
      }
   }

   private static class DeflateFrameServerExtension implements WebSocketServerExtension {
      private final String extensionName;
      private final int compressionLevel;
      private final WebSocketExtensionFilterProvider extensionFilterProvider;
      private final int maxAllocation;

      DeflateFrameServerExtension(int compressionLevel, String extensionName, WebSocketExtensionFilterProvider extensionFilterProvider, int maxAllocation) {
         this.extensionName = extensionName;
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

      @Override
      public WebSocketExtensionData newReponseData() {
         return new WebSocketExtensionData(this.extensionName, Collections.emptyMap());
      }
   }
}
