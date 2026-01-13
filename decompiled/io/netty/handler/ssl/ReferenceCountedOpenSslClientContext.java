package io.netty.handler.ssl;

import io.netty.internal.tcnative.CertificateCallback;
import io.netty.internal.tcnative.SSL;
import io.netty.internal.tcnative.SSLContext;
import io.netty.util.internal.EmptyArrays;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.x500.X500Principal;

public final class ReferenceCountedOpenSslClientContext extends ReferenceCountedOpenSslContext {
   private static final String[] SUPPORTED_KEY_TYPES = new String[]{"RSA", "DH_RSA", "EC", "EC_RSA", "EC_EC"};
   private final OpenSslSessionContext sessionContext;

   ReferenceCountedOpenSslClientContext(
      X509Certificate[] trustCertCollection,
      TrustManagerFactory trustManagerFactory,
      X509Certificate[] keyCertChain,
      PrivateKey key,
      String keyPassword,
      KeyManagerFactory keyManagerFactory,
      Iterable<String> ciphers,
      CipherSuiteFilter cipherFilter,
      ApplicationProtocolConfig apn,
      String[] protocols,
      long sessionCacheSize,
      long sessionTimeout,
      boolean enableOcsp,
      String keyStore,
      String endpointIdentificationAlgorithm,
      List<SNIServerName> serverNames,
      ResumptionController resumptionController,
      Entry<SslContextOption<?>, Object>... options
   ) throws SSLException {
      super(
         ciphers,
         cipherFilter,
         toNegotiator(apn),
         0,
         keyCertChain,
         ClientAuth.NONE,
         protocols,
         false,
         endpointIdentificationAlgorithm,
         enableOcsp,
         true,
         serverNames,
         resumptionController,
         options
      );
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
         success = true;
      } finally {
         if (!success) {
            this.release();
         }
      }
   }

   @Override
   public OpenSslSessionContext sessionContext() {
      return this.sessionContext;
   }

   static OpenSslSessionContext newSessionContext(
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
      boolean fallbackToJdkProviders
   ) throws SSLException {
      if ((key != null || keyCertChain == null) && (key == null || keyCertChain != null)) {
         OpenSslKeyMaterialProvider keyMaterialProvider = null;

         ReferenceCountedOpenSslClientContext.OpenSslClientSessionContext var19;
         try {
            try {
               if (keyManagerFactory == null && key != null && key.getEncoded() == null) {
                  if (!fallbackToJdkProviders) {
                     throw new SSLException(
                        "Private key requiring alternative signature provider detected (such as hardware security key, smart card, or remote signing service) but alternative key fallback is disabled."
                     );
                  }

                  keyMaterialProvider = setupSecurityProviderSignatureSource(
                     thiz,
                     ctx,
                     keyCertChain,
                     key,
                     materialManager -> new ReferenceCountedOpenSslClientContext.OpenSslClientCertificateCallback(engines, materialManager)
                  );
               } else if (!OpenSsl.useKeyManagerFactory()) {
                  if (keyManagerFactory != null) {
                     throw new IllegalArgumentException("KeyManagerFactory not supported");
                  }

                  if (keyCertChain != null) {
                     setKeyMaterial(ctx, keyCertChain, key, keyPassword);
                  }
               } else {
                  if (keyManagerFactory == null && keyCertChain != null) {
                     keyManagerFactory = certChainToKeyManagerFactory(keyCertChain, key, keyPassword, keyStore);
                  }

                  if (keyManagerFactory != null) {
                     keyMaterialProvider = providerFor(keyManagerFactory, keyPassword);
                  }

                  if (keyMaterialProvider != null) {
                     OpenSslKeyMaterialManager materialManager = new OpenSslKeyMaterialManager(keyMaterialProvider, thiz.hasTmpDhKeys);
                     SSLContext.setCertificateCallback(ctx, new ReferenceCountedOpenSslClientContext.OpenSslClientCertificateCallback(engines, materialManager));
                  }
               }
            } catch (Exception var25) {
               throw new SSLException("failed to set certificate and key", var25);
            }

            SSLContext.setVerify(ctx, 1, 10);

            try {
               if (trustCertCollection != null) {
                  trustManagerFactory = buildTrustManagerFactory(trustCertCollection, trustManagerFactory, keyStore);
               } else if (trustManagerFactory == null) {
                  trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                  trustManagerFactory.init((KeyStore)null);
               }

               X509TrustManager manager = chooseTrustManager(trustManagerFactory.getTrustManagers(), resumptionController);
               setVerifyCallback(ctx, engines, manager);
            } catch (Exception var24) {
               if (keyMaterialProvider != null) {
                  keyMaterialProvider.destroy();
               }

               throw new SSLException("unable to setup trustmanager", var24);
            }

            ReferenceCountedOpenSslClientContext.OpenSslClientSessionContext context = new ReferenceCountedOpenSslClientContext.OpenSslClientSessionContext(
               thiz, keyMaterialProvider
            );
            context.setSessionCacheEnabled(CLIENT_ENABLE_SESSION_CACHE);
            if (sessionCacheSize > 0L) {
               context.setSessionCacheSize((int)Math.min(sessionCacheSize, 2147483647L));
            }

            if (sessionTimeout > 0L) {
               context.setSessionTimeout((int)Math.min(sessionTimeout, 2147483647L));
            }

            if (CLIENT_ENABLE_SESSION_TICKET) {
               context.setTicketKeys();
            }

            keyMaterialProvider = null;
            var19 = context;
         } finally {
            if (keyMaterialProvider != null) {
               keyMaterialProvider.destroy();
            }
         }

         return var19;
      } else {
         throw new IllegalArgumentException("Either both keyCertChain and key needs to be null or none of them");
      }
   }

   private static void setVerifyCallback(long ctx, Map<Long, ReferenceCountedOpenSslEngine> engines, X509TrustManager manager) {
      if (useExtendedTrustManager(manager)) {
         SSLContext.setCertVerifyCallback(
            ctx, new ReferenceCountedOpenSslClientContext.ExtendedTrustManagerVerifyCallback(engines, (X509ExtendedTrustManager)manager)
         );
      } else {
         SSLContext.setCertVerifyCallback(ctx, new ReferenceCountedOpenSslClientContext.TrustManagerVerifyCallback(engines, manager));
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
         this.manager.checkServerTrusted(peerCerts, auth, engine);
      }
   }

   private static final class OpenSslClientCertificateCallback implements CertificateCallback {
      private final Map<Long, ReferenceCountedOpenSslEngine> engines;
      private final OpenSslKeyMaterialManager keyManagerHolder;

      OpenSslClientCertificateCallback(Map<Long, ReferenceCountedOpenSslEngine> engines, OpenSslKeyMaterialManager keyManagerHolder) {
         this.engines = engines;
         this.keyManagerHolder = keyManagerHolder;
      }

      public void handle(long ssl, byte[] keyTypeBytes, byte[][] asn1DerEncodedPrincipals) throws Exception {
         ReferenceCountedOpenSslEngine engine = this.engines.get(ssl);
         if (engine != null) {
            try {
               String[] keyTypes = supportedClientKeyTypes(keyTypeBytes);
               X500Principal[] issuers;
               if (asn1DerEncodedPrincipals == null) {
                  issuers = null;
               } else {
                  issuers = new X500Principal[asn1DerEncodedPrincipals.length];

                  for (int i = 0; i < asn1DerEncodedPrincipals.length; i++) {
                     issuers[i] = new X500Principal(asn1DerEncodedPrincipals[i]);
                  }
               }

               this.keyManagerHolder.setKeyMaterialClientSide(engine, keyTypes, issuers);
            } catch (Throwable var9) {
               engine.initHandshakeException(var9);
               if (var9 instanceof Exception) {
                  throw (Exception)var9;
               } else {
                  throw new SSLException(var9);
               }
            }
         }
      }

      private static String[] supportedClientKeyTypes(byte[] clientCertificateTypes) {
         if (clientCertificateTypes == null) {
            return (String[])ReferenceCountedOpenSslClientContext.SUPPORTED_KEY_TYPES.clone();
         } else {
            Set<String> result = new HashSet<>(clientCertificateTypes.length);

            for (byte keyTypeCode : clientCertificateTypes) {
               String keyType = clientKeyType(keyTypeCode);
               if (keyType != null) {
                  result.add(keyType);
               }
            }

            return result.toArray(EmptyArrays.EMPTY_STRINGS);
         }
      }

      private static String clientKeyType(byte clientCertificateType) {
         switch (clientCertificateType) {
            case 1:
               return "RSA";
            case 3:
               return "DH_RSA";
            case 64:
               return "EC";
            case 65:
               return "EC_RSA";
            case 66:
               return "EC_EC";
            default:
               return null;
         }
      }
   }

   static final class OpenSslClientSessionContext extends OpenSslSessionContext {
      OpenSslClientSessionContext(ReferenceCountedOpenSslContext context, OpenSslKeyMaterialProvider provider) {
         super(context, provider, SSL.SSL_SESS_CACHE_CLIENT, new OpenSslClientSessionCache(context.engines));
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
         this.manager.checkServerTrusted(peerCerts, auth);
      }
   }
}
