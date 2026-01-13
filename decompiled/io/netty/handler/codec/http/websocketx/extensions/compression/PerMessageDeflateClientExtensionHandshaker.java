package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtension;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandshaker;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionDecoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionEncoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilterProvider;
import io.netty.util.internal.ObjectUtil;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public final class PerMessageDeflateClientExtensionHandshaker implements WebSocketClientExtensionHandshaker {
   private final int compressionLevel;
   private final boolean allowClientWindowSize;
   private final int requestedServerWindowSize;
   private final boolean allowClientNoContext;
   private final boolean requestedServerNoContext;
   private final WebSocketExtensionFilterProvider extensionFilterProvider;
   private final int maxAllocation;

   @Deprecated
   public PerMessageDeflateClientExtensionHandshaker() {
      this(0);
   }

   public PerMessageDeflateClientExtensionHandshaker(int maxAllocation) {
      this(6, ZlibCodecFactory.isSupportingWindowSizeAndMemLevel(), 15, false, false, maxAllocation);
   }

   @Deprecated
   public PerMessageDeflateClientExtensionHandshaker(
      int compressionLevel, boolean allowClientWindowSize, int requestedServerWindowSize, boolean allowClientNoContext, boolean requestedServerNoContext
   ) {
      this(compressionLevel, allowClientWindowSize, requestedServerWindowSize, allowClientNoContext, requestedServerNoContext, 0);
   }

   public PerMessageDeflateClientExtensionHandshaker(
      int compressionLevel,
      boolean allowClientWindowSize,
      int requestedServerWindowSize,
      boolean allowClientNoContext,
      boolean requestedServerNoContext,
      int maxAllocation
   ) {
      this(
         compressionLevel,
         allowClientWindowSize,
         requestedServerWindowSize,
         allowClientNoContext,
         requestedServerNoContext,
         WebSocketExtensionFilterProvider.DEFAULT,
         maxAllocation
      );
   }

   @Deprecated
   public PerMessageDeflateClientExtensionHandshaker(
      int compressionLevel,
      boolean allowClientWindowSize,
      int requestedServerWindowSize,
      boolean allowClientNoContext,
      boolean requestedServerNoContext,
      WebSocketExtensionFilterProvider extensionFilterProvider
   ) {
      this(compressionLevel, allowClientWindowSize, requestedServerWindowSize, allowClientNoContext, requestedServerNoContext, extensionFilterProvider, 0);
   }

   public PerMessageDeflateClientExtensionHandshaker(
      int compressionLevel,
      boolean allowClientWindowSize,
      int requestedServerWindowSize,
      boolean allowClientNoContext,
      boolean requestedServerNoContext,
      WebSocketExtensionFilterProvider extensionFilterProvider,
      int maxAllocation
   ) {
      if (requestedServerWindowSize > 15 || requestedServerWindowSize < 8) {
         throw new IllegalArgumentException("requestedServerWindowSize: " + requestedServerWindowSize + " (expected: 8-15)");
      } else if (compressionLevel >= 0 && compressionLevel <= 9) {
         this.compressionLevel = compressionLevel;
         this.allowClientWindowSize = allowClientWindowSize;
         this.requestedServerWindowSize = requestedServerWindowSize;
         this.allowClientNoContext = allowClientNoContext;
         this.requestedServerNoContext = requestedServerNoContext;
         this.extensionFilterProvider = ObjectUtil.checkNotNull(extensionFilterProvider, "extensionFilterProvider");
         this.maxAllocation = ObjectUtil.checkPositiveOrZero(maxAllocation, "maxAllocation");
      } else {
         throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
      }
   }

   @Override
   public WebSocketExtensionData newRequestData() {
      HashMap<String, String> parameters = new HashMap<>(4);
      if (this.requestedServerNoContext) {
         parameters.put("server_no_context_takeover", null);
      }

      if (this.allowClientNoContext) {
         parameters.put("client_no_context_takeover", null);
      }

      if (this.requestedServerWindowSize != 15) {
         parameters.put("server_max_window_bits", Integer.toString(this.requestedServerWindowSize));
      }

      if (this.allowClientWindowSize) {
         parameters.put("client_max_window_bits", null);
      }

      return new WebSocketExtensionData("permessage-deflate", parameters);
   }

   @Override
   public WebSocketClientExtension handshakeExtension(WebSocketExtensionData extensionData) {
      if (!"permessage-deflate".equals(extensionData.name())) {
         return null;
      } else {
         boolean succeed = true;
         int clientWindowSize = 15;
         int serverWindowSize = 15;
         boolean serverNoContext = false;
         boolean clientNoContext = false;
         Iterator<Entry<String, String>> parametersIterator = extensionData.parameters().entrySet().iterator();

         while (succeed && parametersIterator.hasNext()) {
            Entry<String, String> parameter = parametersIterator.next();
            if ("client_max_window_bits".equalsIgnoreCase(parameter.getKey())) {
               if (this.allowClientWindowSize) {
                  clientWindowSize = Integer.parseInt(parameter.getValue());
                  if (clientWindowSize > 15 || clientWindowSize < 8) {
                     succeed = false;
                  }
               } else {
                  succeed = false;
               }
            } else if ("server_max_window_bits".equalsIgnoreCase(parameter.getKey())) {
               serverWindowSize = Integer.parseInt(parameter.getValue());
               if (serverWindowSize > 15 || serverWindowSize < 8) {
                  succeed = false;
               }
            } else if ("client_no_context_takeover".equalsIgnoreCase(parameter.getKey())) {
               if (this.allowClientNoContext) {
                  clientNoContext = true;
               } else {
                  succeed = false;
               }
            } else if ("server_no_context_takeover".equalsIgnoreCase(parameter.getKey())) {
               serverNoContext = true;
            } else {
               succeed = false;
            }
         }

         if (this.requestedServerNoContext && !serverNoContext || this.requestedServerWindowSize < serverWindowSize) {
            succeed = false;
         }

         return succeed
            ? new PerMessageDeflateClientExtensionHandshaker.PermessageDeflateExtension(
               serverNoContext, serverWindowSize, clientNoContext, clientWindowSize, this.extensionFilterProvider, this.maxAllocation
            )
            : null;
      }
   }

   private final class PermessageDeflateExtension implements WebSocketClientExtension {
      private final boolean serverNoContext;
      private final int serverWindowSize;
      private final boolean clientNoContext;
      private final int clientWindowSize;
      private final WebSocketExtensionFilterProvider extensionFilterProvider;
      private final int maxAllocation;

      @Override
      public int rsv() {
         return 4;
      }

      PermessageDeflateExtension(
         boolean serverNoContext,
         int serverWindowSize,
         boolean clientNoContext,
         int clientWindowSize,
         WebSocketExtensionFilterProvider extensionFilterProvider,
         int maxAllocation
      ) {
         this.serverNoContext = serverNoContext;
         this.serverWindowSize = serverWindowSize;
         this.clientNoContext = clientNoContext;
         this.clientWindowSize = clientWindowSize;
         this.extensionFilterProvider = extensionFilterProvider;
         this.maxAllocation = maxAllocation;
      }

      @Override
      public WebSocketExtensionEncoder newExtensionEncoder() {
         return new PerMessageDeflateEncoder(
            PerMessageDeflateClientExtensionHandshaker.this.compressionLevel,
            this.clientWindowSize,
            this.clientNoContext,
            this.extensionFilterProvider.encoderFilter()
         );
      }

      @Override
      public WebSocketExtensionDecoder newExtensionDecoder() {
         return new PerMessageDeflateDecoder(this.serverNoContext, this.extensionFilterProvider.decoderFilter(), this.maxAllocation);
      }
   }
}
