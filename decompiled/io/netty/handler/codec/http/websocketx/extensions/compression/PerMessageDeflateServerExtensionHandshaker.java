package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionDecoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionEncoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilterProvider;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtension;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtensionHandshaker;
import io.netty.util.internal.ObjectUtil;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public final class PerMessageDeflateServerExtensionHandshaker implements WebSocketServerExtensionHandshaker {
   public static final int MIN_WINDOW_SIZE = 8;
   public static final int MAX_WINDOW_SIZE = 15;
   static final String PERMESSAGE_DEFLATE_EXTENSION = "permessage-deflate";
   static final String CLIENT_MAX_WINDOW = "client_max_window_bits";
   static final String SERVER_MAX_WINDOW = "server_max_window_bits";
   static final String CLIENT_NO_CONTEXT = "client_no_context_takeover";
   static final String SERVER_NO_CONTEXT = "server_no_context_takeover";
   private final int compressionLevel;
   private final boolean allowServerWindowSize;
   private final int preferredClientWindowSize;
   private final boolean allowServerNoContext;
   private final boolean preferredClientNoContext;
   private final WebSocketExtensionFilterProvider extensionFilterProvider;
   private final int maxAllocation;

   @Deprecated
   public PerMessageDeflateServerExtensionHandshaker() {
      this(0);
   }

   public PerMessageDeflateServerExtensionHandshaker(int maxAllocation) {
      this(6, ZlibCodecFactory.isSupportingWindowSizeAndMemLevel(), 15, false, false, maxAllocation);
   }

   @Deprecated
   public PerMessageDeflateServerExtensionHandshaker(
      int compressionLevel, boolean allowServerWindowSize, int preferredClientWindowSize, boolean allowServerNoContext, boolean preferredClientNoContext
   ) {
      this(compressionLevel, allowServerWindowSize, preferredClientWindowSize, allowServerNoContext, preferredClientNoContext, 0);
   }

   public PerMessageDeflateServerExtensionHandshaker(
      int compressionLevel,
      boolean allowServerWindowSize,
      int preferredClientWindowSize,
      boolean allowServerNoContext,
      boolean preferredClientNoContext,
      int maxAllocation
   ) {
      this(
         compressionLevel,
         allowServerWindowSize,
         preferredClientWindowSize,
         allowServerNoContext,
         preferredClientNoContext,
         WebSocketExtensionFilterProvider.DEFAULT,
         maxAllocation
      );
   }

   @Deprecated
   public PerMessageDeflateServerExtensionHandshaker(
      int compressionLevel,
      boolean allowServerWindowSize,
      int preferredClientWindowSize,
      boolean allowServerNoContext,
      boolean preferredClientNoContext,
      WebSocketExtensionFilterProvider extensionFilterProvider
   ) {
      this(compressionLevel, allowServerWindowSize, preferredClientWindowSize, allowServerNoContext, preferredClientNoContext, extensionFilterProvider, 0);
   }

   public PerMessageDeflateServerExtensionHandshaker(
      int compressionLevel,
      boolean allowServerWindowSize,
      int preferredClientWindowSize,
      boolean allowServerNoContext,
      boolean preferredClientNoContext,
      WebSocketExtensionFilterProvider extensionFilterProvider,
      int maxAllocation
   ) {
      if (preferredClientWindowSize > 15 || preferredClientWindowSize < 8) {
         throw new IllegalArgumentException("preferredServerWindowSize: " + preferredClientWindowSize + " (expected: 8-15)");
      } else if (compressionLevel >= 0 && compressionLevel <= 9) {
         this.compressionLevel = compressionLevel;
         this.allowServerWindowSize = allowServerWindowSize;
         this.preferredClientWindowSize = preferredClientWindowSize;
         this.allowServerNoContext = allowServerNoContext;
         this.preferredClientNoContext = preferredClientNoContext;
         this.extensionFilterProvider = ObjectUtil.checkNotNull(extensionFilterProvider, "extensionFilterProvider");
         this.maxAllocation = ObjectUtil.checkPositiveOrZero(maxAllocation, "maxAllocation");
      } else {
         throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
      }
   }

   @Override
   public WebSocketServerExtension handshakeExtension(WebSocketExtensionData extensionData) {
      if (!"permessage-deflate".equals(extensionData.name())) {
         return null;
      } else {
         boolean deflateEnabled = true;
         int clientWindowSize = 15;
         int serverWindowSize = 15;
         boolean serverNoContext = false;
         boolean clientNoContext = false;
         Iterator<Entry<String, String>> parametersIterator = extensionData.parameters().entrySet().iterator();

         while (deflateEnabled && parametersIterator.hasNext()) {
            Entry<String, String> parameter = parametersIterator.next();
            if ("client_max_window_bits".equalsIgnoreCase(parameter.getKey())) {
               clientWindowSize = this.preferredClientWindowSize;
            } else if ("server_max_window_bits".equalsIgnoreCase(parameter.getKey())) {
               if (this.allowServerWindowSize) {
                  serverWindowSize = Integer.parseInt(parameter.getValue());
                  if (serverWindowSize > 15 || serverWindowSize < 8) {
                     deflateEnabled = false;
                  }
               } else {
                  deflateEnabled = false;
               }
            } else if ("client_no_context_takeover".equalsIgnoreCase(parameter.getKey())) {
               clientNoContext = this.preferredClientNoContext;
            } else if ("server_no_context_takeover".equalsIgnoreCase(parameter.getKey())) {
               if (this.allowServerNoContext) {
                  serverNoContext = true;
               } else {
                  deflateEnabled = false;
               }
            } else {
               deflateEnabled = false;
            }
         }

         return deflateEnabled
            ? new PerMessageDeflateServerExtensionHandshaker.PermessageDeflateExtension(
               this.compressionLevel, serverNoContext, serverWindowSize, clientNoContext, clientWindowSize, this.extensionFilterProvider, this.maxAllocation
            )
            : null;
      }
   }

   private static class PermessageDeflateExtension implements WebSocketServerExtension {
      private final int compressionLevel;
      private final boolean serverNoContext;
      private final int serverWindowSize;
      private final boolean clientNoContext;
      private final int clientWindowSize;
      private final WebSocketExtensionFilterProvider extensionFilterProvider;
      private final int maxAllocation;

      PermessageDeflateExtension(
         int compressionLevel,
         boolean serverNoContext,
         int serverWindowSize,
         boolean clientNoContext,
         int clientWindowSize,
         WebSocketExtensionFilterProvider extensionFilterProvider,
         int maxAllocation
      ) {
         this.compressionLevel = compressionLevel;
         this.serverNoContext = serverNoContext;
         this.serverWindowSize = serverWindowSize;
         this.clientNoContext = clientNoContext;
         this.clientWindowSize = clientWindowSize;
         this.extensionFilterProvider = extensionFilterProvider;
         this.maxAllocation = maxAllocation;
      }

      @Override
      public int rsv() {
         return 4;
      }

      @Override
      public WebSocketExtensionEncoder newExtensionEncoder() {
         return new PerMessageDeflateEncoder(this.compressionLevel, this.serverWindowSize, this.serverNoContext, this.extensionFilterProvider.encoderFilter());
      }

      @Override
      public WebSocketExtensionDecoder newExtensionDecoder() {
         return new PerMessageDeflateDecoder(this.clientNoContext, this.extensionFilterProvider.decoderFilter(), this.maxAllocation);
      }

      @Override
      public WebSocketExtensionData newReponseData() {
         HashMap<String, String> parameters = new HashMap<>(4);
         if (this.serverNoContext) {
            parameters.put("server_no_context_takeover", null);
         }

         if (this.clientNoContext) {
            parameters.put("client_no_context_takeover", null);
         }

         if (this.serverWindowSize != 15) {
            parameters.put("server_max_window_bits", Integer.toString(this.serverWindowSize));
         }

         if (this.clientWindowSize != 15) {
            parameters.put("client_max_window_bits", Integer.toString(this.clientWindowSize));
         }

         return new WebSocketExtensionData("permessage-deflate", parameters);
      }
   }
}
