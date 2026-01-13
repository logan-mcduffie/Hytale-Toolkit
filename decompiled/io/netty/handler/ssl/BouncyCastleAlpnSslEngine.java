package io.netty.handler.ssl;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import javax.net.ssl.SSLEngine;

final class BouncyCastleAlpnSslEngine extends JdkAlpnSslEngine {
   BouncyCastleAlpnSslEngine(SSLEngine engine, JdkApplicationProtocolNegotiator applicationNegotiator, boolean isServer) {
      super(engine, applicationNegotiator, isServer, new BiConsumer<SSLEngine, JdkAlpnSslEngine.AlpnSelector>() {
         public void accept(SSLEngine e, JdkAlpnSslEngine.AlpnSelector s) {
            BouncyCastleAlpnSslUtils.setHandshakeApplicationProtocolSelector(e, s);
         }
      }, new BiConsumer<SSLEngine, List<String>>() {
         public void accept(SSLEngine e, List<String> p) {
            BouncyCastleAlpnSslUtils.setApplicationProtocols(e, p);
         }
      });
   }

   @Override
   public String getApplicationProtocol() {
      return BouncyCastleAlpnSslUtils.getApplicationProtocol(this.getWrappedEngine());
   }

   @Override
   public String getHandshakeApplicationProtocol() {
      return BouncyCastleAlpnSslUtils.getHandshakeApplicationProtocol(this.getWrappedEngine());
   }

   @Override
   public void setHandshakeApplicationProtocolSelector(BiFunction<SSLEngine, List<String>, String> selector) {
      BouncyCastleAlpnSslUtils.setHandshakeApplicationProtocolSelector(this.getWrappedEngine(), selector);
   }

   @Override
   public BiFunction<SSLEngine, List<String>, String> getHandshakeApplicationProtocolSelector() {
      return BouncyCastleAlpnSslUtils.getHandshakeApplicationProtocolSelector(this.getWrappedEngine());
   }
}
