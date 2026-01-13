package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Map.Entry;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;

public abstract class OpenSslContext extends ReferenceCountedOpenSslContext {
   OpenSslContext(
      Iterable<String> ciphers,
      CipherSuiteFilter cipherFilter,
      ApplicationProtocolConfig apnCfg,
      int mode,
      Certificate[] keyCertChain,
      ClientAuth clientAuth,
      String[] protocols,
      boolean startTls,
      String endpointIdentificationAlgorithm,
      boolean enableOcsp,
      List<SNIServerName> serverNames,
      ResumptionController resumptionController,
      Entry<SslContextOption<?>, Object>... options
   ) throws SSLException {
      super(
         ciphers,
         cipherFilter,
         toNegotiator(apnCfg),
         mode,
         keyCertChain,
         clientAuth,
         protocols,
         startTls,
         endpointIdentificationAlgorithm,
         enableOcsp,
         false,
         serverNames,
         resumptionController,
         options
      );
   }

   OpenSslContext(
      Iterable<String> ciphers,
      CipherSuiteFilter cipherFilter,
      OpenSslApplicationProtocolNegotiator apn,
      int mode,
      Certificate[] keyCertChain,
      ClientAuth clientAuth,
      String[] protocols,
      boolean startTls,
      boolean enableOcsp,
      List<SNIServerName> serverNames,
      ResumptionController resumptionController,
      Entry<SslContextOption<?>, Object>... options
   ) throws SSLException {
      super(
         ciphers, cipherFilter, apn, mode, keyCertChain, clientAuth, protocols, startTls, null, enableOcsp, false, serverNames, resumptionController, options
      );
   }

   @Override
   final SSLEngine newEngine0(ByteBufAllocator alloc, String peerHost, int peerPort, boolean jdkCompatibilityMode) {
      return new OpenSslEngine(this, alloc, peerHost, peerPort, jdkCompatibilityMode, this.endpointIdentificationAlgorithm, this.serverNames);
   }

   @Override
   protected final void finalize() throws Throwable {
      super.finalize();
      OpenSsl.releaseIfNeeded(this);
   }
}
