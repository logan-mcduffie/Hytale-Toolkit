package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.internal.tcnative.CertificateCallback;
import io.netty.internal.tcnative.SSL;
import io.netty.internal.tcnative.SSLContext;
import io.netty.internal.tcnative.SniHostNameMatcher;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

public final class ReferenceCountedOpenSslServerContext extends ReferenceCountedOpenSslContext {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ReferenceCountedOpenSslServerContext.class);
   private static final byte[] ID = new byte[]{110, 101, 116, 116, 121};
   private final OpenSslServerSessionContext sessionContext;

   ReferenceCountedOpenSslServerContext(
      X509Certificate[] trustCertCollection,
      TrustManagerFactory trustManagerFactory,
      X509Certificate[] keyCertChain,
      PrivateKey key,
      String keyPassword,
      KeyManagerFactory keyManagerFactory,
      Iterable<String> ciphers,
      CipherSuiteFilter cipherFilter,
      ApplicationProtocolConfig apn,
      long sessionCacheSize,
      long sessionTimeout,
      ClientAuth clientAuth,
      String[] protocols,
      boolean startTls,
      boolean enableOcsp,
      String keyStore,
      ResumptionController resumptionController,
      Entry<SslContextOption<?>, Object>... options
   ) throws SSLException {
      this(
         trustCertCollection,
         trustManagerFactory,
         keyCertChain,
         key,
         keyPassword,
         keyManagerFactory,
         ciphers,
         cipherFilter,
         toNegotiator(apn),
         sessionCacheSize,
         sessionTimeout,
         clientAuth,
         protocols,
         startTls,
         enableOcsp,
         keyStore,
         resumptionController,
         options
      );
   }

   ReferenceCountedOpenSslServerContext(
      X509Certificate[] trustCertCollection,
      TrustManagerFactory trustManagerFactory,
      X509Certificate[] keyCertChain,
      PrivateKey key,
      String keyPassword,
      KeyManagerFactory keyManagerFactory,
      Iterable<String> ciphers,
      CipherSuiteFilter cipherFilter,
      OpenSslApplicationProtocolNegotiator apn,
      long sessionCacheSize,
      long sessionTimeout,
      ClientAuth clientAuth,
      String[] protocols,
      boolean startTls,
      boolean enableOcsp,
      String keyStore,
      ResumptionController resumptionController,
      Entry<SslContextOption<?>, Object>... options
   ) throws SSLException {
      super(ciphers, cipherFilter, apn, 1, keyCertChain, clientAuth, protocols, startTls, null, enableOcsp, true, null, resumptionController, options);
      boolean success = false;

      try {
         this.sessionContext = newSessionContext(
            this,
            this.ctx,
            this.engines,
            trustCertCollection,
            trustManagerFactory,
            keyCertChain,
            key,
            keyPassword,
            keyManagerFactory,
            keyStore,
            sessionCacheSize,
            sessionTimeout,
            resumptionController,
            isJdkSignatureFallbackEnabled(options)
         );
         if (SERVER_ENABLE_SESSION_TICKET) {
            this.sessionContext.setTicketKeys();
         }

         success = true;
      } finally {
         if (!success) {
            this.release();
         }
      }
   }

   public OpenSslServerSessionContext sessionContext() {
      return this.sessionContext;
   }

   static OpenSslServerSessionContext newSessionContext(
      ReferenceCountedOpenSslContext thiz,
      long ctx,
      Map<Long, ReferenceCountedOpenSslEngine> engines,
      X509Certificate[] trustCertCollection,
      TrustManagerFactory trustManagerFactory,
      X509Certificate[] keyCertChain,
      PrivateKey key,
      String keyPassword,
      KeyManagerFactory keyManagerFactory,
      String keyStore,
      long sessionCacheSize,
      long sessionTimeout,
      ResumptionController resumptionController,
      boolean fallbackToJdkSignatureProviders
   ) throws SSLException {
      OpenSslKeyMaterialProvider keyMaterialProvider = null;

      OpenSslServerSessionContext var42;
      try {
         try {
            SSLContext.setVerify(ctx, 0, 10);
            if (keyManagerFactory == null && key != null && key.getEncoded() == null) {
               if (!fallbackToJdkSignatureProviders) {
                  throw new SSLException(
                     "Private key requiring alternative signature provider detected (such as hardware security key, smart card, or remote signing service) but alternative key fallback is disabled."
                  );
               }

               keyMaterialProvider = setupSecurityProviderSignatureSource(
                  thiz, ctx, keyCertChain, key, manager -> new ReferenceCountedOpenSslServerContext.OpenSslServerCertificateCallback(engines, manager)
               );
            } else if (!OpenSsl.useKeyManagerFactory()) {
               if (keyManagerFactory != null) {
                  throw new IllegalArgumentException("KeyManagerFactory not supported with external keys");
               }

               ObjectUtil.checkNotNull(keyCertChain, "keyCertChain");
               setKeyMaterial(ctx, keyCertChain, key, keyPassword);
            } else {
               if (keyManagerFactory == null) {
                  keyManagerFactory = certChainToKeyManagerFactory(keyCertChain, key, keyPassword, keyStore);
               }

               keyMaterialProvider = providerFor(keyManagerFactory, keyPassword);
               SSLContext.setCertificateCallback(
                  ctx,
                  new ReferenceCountedOpenSslServerContext.OpenSslServerCertificateCallback(
                     engines, new OpenSslKeyMaterialManager(keyMaterialProvider, thiz.hasTmpDhKeys)
                  )
               );
            }
         } catch (Exception var39) {
            throw new SSLException("failed to set certificate and key", var39);
         }

         try {
            if (trustCertCollection != null) {
               trustManagerFactory = buildTrustManagerFactory(trustCertCollection, trustManagerFactory, keyStore);
            } else if (trustManagerFactory == null) {
               trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
               trustManagerFactory.init((KeyStore)null);
            }

            X509TrustManager manager = chooseTrustManager(trustManagerFactory.getTrustManagers(), resumptionController);
            setVerifyCallback(ctx, engines, manager);
            X509Certificate[] issuers = manager.getAcceptedIssuers();
            if (issuers != null && issuers.length > 0) {
               long bio = 0L;

               try {
                  bio = toBIO(ByteBufAllocator.DEFAULT, issuers);
                  if (!SSLContext.setCACertificateBio(ctx, bio)) {
                     String msg = "unable to setup accepted issuers for trustmanager " + manager;
                     int error = SSL.getLastErrorNumber();
                     if (error != 0) {
                        msg = msg + ". " + SSL.getErrorString(error);
                     }

                     throw new SSLException(msg);
                  }
               } finally {
                  freeBio(bio);
               }
            }

            SSLContext.setSniHostnameMatcher(ctx, new ReferenceCountedOpenSslServerContext.OpenSslSniHostnameMatcher(engines));
         } catch (SSLException var37) {
            throw var37;
         } catch (Exception var38) {
            throw new SSLException("unable to setup trustmanager", var38);
         }

         OpenSslServerSessionContext sessionContext = new OpenSslServerSessionContext(thiz, keyMaterialProvider);
         sessionContext.setSessionIdContext(ID);
         sessionContext.setSessionCacheEnabled(SERVER_ENABLE_SESSION_CACHE);
         if (sessionCacheSize > 0L) {
            sessionContext.setSessionCacheSize((int)Math.min(sessionCacheSize, 2147483647L));
         }

         if (sessionTimeout > 0L) {
            sessionContext.setSessionTimeout((int)Math.min(sessionTimeout, 2147483647L));
         }

         keyMaterialProvider = null;
         var42 = sessionContext;
      } finally {
         if (keyMaterialProvider != null) {
            keyMaterialProvider.destroy();
         }
      }

      return var42;
   }

   private static void setVerifyCallback(long ctx, Map<Long, ReferenceCountedOpenSslEngine> engines, X509TrustManager manager) {
      if (useExtendedTrustManager(manager)) {
         SSLContext.setCertVerifyCallback(
            ctx, new ReferenceCountedOpenSslServerContext.ExtendedTrustManagerVerifyCallback(engines, (X509ExtendedTrustManager)manager)
         );
      } else {
         SSLContext.setCertVerifyCallback(ctx, new ReferenceCountedOpenSslServerContext.TrustManagerVerifyCallback(engines, manager));
      }
   }

   private static final class ExtendedTrustManagerVerifyCallback extends ReferenceCountedOpenSslContext.AbstractCertificateVerifier {
      private final X509ExtendedTrustManager manager;

      ExtendedTrustManagerVerifyCallback(Map<Long, ReferenceCountedOpenSslEngine> engines, X509ExtendedTrustManager manager) {
         super(engines);
         this.manager = manager;
      }

      @Override
      void verify(ReferenceCountedOpenSslEngine engine, X509Certificate[] peerCerts, String auth) throws Exception {
         this.manager.checkClientTrusted(peerCerts, auth, engine);
      }
   }

   private static final class OpenSslServerCertificateCallback implements CertificateCallback {
      private final Map<Long, ReferenceCountedOpenSslEngine> engines;
      private final OpenSslKeyMaterialManager keyManagerHolder;

      OpenSslServerCertificateCallback(Map<Long, ReferenceCountedOpenSslEngine> engines, OpenSslKeyMaterialManager keyManagerHolder) {
         this.engines = engines;
         this.keyManagerHolder = keyManagerHolder;
      }

      public void handle(long ssl, byte[] keyTypeBytes, byte[][] asn1DerEncodedPrincipals) throws Exception {
         ReferenceCountedOpenSslEngine engine = this.engines.get(ssl);
         if (engine != null) {
            try {
               this.keyManagerHolder.setKeyMaterialServerSide(engine);
            } catch (Throwable var7) {
               engine.initHandshakeException(var7);
               if (var7 instanceof Exception) {
                  throw (Exception)var7;
               } else {
                  throw new SSLException(var7);
               }
            }
         }
      }
   }

   private static final class OpenSslSniHostnameMatcher implements SniHostNameMatcher {
      private final Map<Long, ReferenceCountedOpenSslEngine> engines;

      OpenSslSniHostnameMatcher(Map<Long, ReferenceCountedOpenSslEngine> engines) {
         this.engines = engines;
      }

      public boolean match(long ssl, String hostname) {
         ReferenceCountedOpenSslEngine engine = this.engines.get(ssl);
         if (engine != null) {
            return engine.checkSniHostnameMatch(hostname.getBytes(CharsetUtil.UTF_8));
         } else {
            ReferenceCountedOpenSslServerContext.logger.warn("No ReferenceCountedOpenSslEngine found for SSL pointer: {}", ssl);
            return false;
         }
      }
   }

   private static final class TrustManagerVerifyCallback extends ReferenceCountedOpenSslContext.AbstractCertificateVerifier {
      private final X509TrustManager manager;

      TrustManagerVerifyCallback(Map<Long, ReferenceCountedOpenSslEngine> engines, X509TrustManager manager) {
         super(engines);
         this.manager = manager;
      }

      @Override
      void verify(ReferenceCountedOpenSslEngine engine, X509Certificate[] peerCerts, String auth) throws Exception {
         this.manager.checkClientTrusted(peerCerts, auth);
      }
   }
}
