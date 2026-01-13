package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.util.BouncyCastleUtil;
import javax.net.ssl.SSLEngine;

@Deprecated
public final class JdkAlpnApplicationProtocolNegotiator extends JdkBaseApplicationProtocolNegotiator {
   private static final boolean AVAILABLE = Conscrypt.isAvailable()
      || JdkAlpnSslUtils.supportsAlpn()
      || BouncyCastleUtil.isBcTlsAvailable() && BouncyCastleAlpnSslUtils.isAlpnSupported();
   private static final JdkApplicationProtocolNegotiator.SslEngineWrapperFactory ALPN_WRAPPER = (JdkApplicationProtocolNegotiator.SslEngineWrapperFactory)(AVAILABLE
      ? new JdkAlpnApplicationProtocolNegotiator.AlpnWrapper()
      : new JdkAlpnApplicationProtocolNegotiator.FailureWrapper());

   public JdkAlpnApplicationProtocolNegotiator(Iterable<String> protocols) {
      this(false, protocols);
   }

   public JdkAlpnApplicationProtocolNegotiator(String... protocols) {
      this(false, protocols);
   }

   public JdkAlpnApplicationProtocolNegotiator(boolean failIfNoCommonProtocols, Iterable<String> protocols) {
      this(failIfNoCommonProtocols, failIfNoCommonProtocols, protocols);
   }

   public JdkAlpnApplicationProtocolNegotiator(boolean failIfNoCommonProtocols, String... protocols) {
      this(failIfNoCommonProtocols, failIfNoCommonProtocols, protocols);
   }

   public JdkAlpnApplicationProtocolNegotiator(boolean clientFailIfNoCommonProtocols, boolean serverFailIfNoCommonProtocols, Iterable<String> protocols) {
      this(
         serverFailIfNoCommonProtocols ? FAIL_SELECTOR_FACTORY : NO_FAIL_SELECTOR_FACTORY,
         clientFailIfNoCommonProtocols ? FAIL_SELECTION_LISTENER_FACTORY : NO_FAIL_SELECTION_LISTENER_FACTORY,
         protocols
      );
   }

   public JdkAlpnApplicationProtocolNegotiator(boolean clientFailIfNoCommonProtocols, boolean serverFailIfNoCommonProtocols, String... protocols) {
      this(
         serverFailIfNoCommonProtocols ? FAIL_SELECTOR_FACTORY : NO_FAIL_SELECTOR_FACTORY,
         clientFailIfNoCommonProtocols ? FAIL_SELECTION_LISTENER_FACTORY : NO_FAIL_SELECTION_LISTENER_FACTORY,
         protocols
      );
   }

   public JdkAlpnApplicationProtocolNegotiator(
      JdkApplicationProtocolNegotiator.ProtocolSelectorFactory selectorFactory,
      JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory listenerFactory,
      Iterable<String> protocols
   ) {
      super(ALPN_WRAPPER, selectorFactory, listenerFactory, protocols);
   }

   public JdkAlpnApplicationProtocolNegotiator(
      JdkApplicationProtocolNegotiator.ProtocolSelectorFactory selectorFactory,
      JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory listenerFactory,
      String... protocols
   ) {
      super(ALPN_WRAPPER, selectorFactory, listenerFactory, protocols);
   }

   static boolean isAlpnSupported() {
      return AVAILABLE;
   }

   private static final class AlpnWrapper extends JdkApplicationProtocolNegotiator.AllocatorAwareSslEngineWrapperFactory {
      private AlpnWrapper() {
      }

      @Override
      public SSLEngine wrapSslEngine(SSLEngine engine, ByteBufAllocator alloc, JdkApplicationProtocolNegotiator applicationNegotiator, boolean isServer) {
         if (Conscrypt.isEngineSupported(engine)) {
            return isServer
               ? ConscryptAlpnSslEngine.newServerEngine(engine, alloc, applicationNegotiator)
               : ConscryptAlpnSslEngine.newClientEngine(engine, alloc, applicationNegotiator);
         } else if (BouncyCastleUtil.isBcJsseInUse(engine) && BouncyCastleAlpnSslUtils.isAlpnSupported()) {
            return new BouncyCastleAlpnSslEngine(engine, applicationNegotiator, isServer);
         } else if (JdkAlpnSslUtils.supportsAlpn()) {
            return new JdkAlpnSslEngine(engine, applicationNegotiator, isServer);
         } else {
            throw new UnsupportedOperationException("ALPN not supported. Unable to wrap SSLEngine of type '" + engine.getClass().getName() + "')");
         }
      }
   }

   private static final class FailureWrapper extends JdkApplicationProtocolNegotiator.AllocatorAwareSslEngineWrapperFactory {
      private FailureWrapper() {
      }

      @Override
      public SSLEngine wrapSslEngine(SSLEngine engine, ByteBufAllocator alloc, JdkApplicationProtocolNegotiator applicationNegotiator, boolean isServer) {
         throw new RuntimeException(
            "ALPN unsupported. Does your JDK version support it? For Conscrypt, add the appropriate Conscrypt JAR to classpath and set the security provider."
         );
      }
   }
}
