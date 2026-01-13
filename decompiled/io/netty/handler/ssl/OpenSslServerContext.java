package io.netty.handler.ssl;

import java.io.File;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Map.Entry;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;

public final class OpenSslServerContext extends OpenSslContext {
   private final OpenSslServerSessionContext sessionContext;

   @Deprecated
   public OpenSslServerContext(File certChainFile, File keyFile) throws SSLException {
      this(certChainFile, keyFile, null);
   }

   @Deprecated
   public OpenSslServerContext(File certChainFile, File keyFile, String keyPassword) throws SSLException {
      this(certChainFile, keyFile, keyPassword, null, IdentityCipherSuiteFilter.INSTANCE, ApplicationProtocolConfig.DISABLED, 0L, 0L);
   }

   @Deprecated
   public OpenSslServerContext(
      File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout
   ) throws SSLException {
      this(certChainFile, keyFile, keyPassword, ciphers, IdentityCipherSuiteFilter.INSTANCE, apn, sessionCacheSize, sessionTimeout);
   }

   @Deprecated
   public OpenSslServerContext(
      File certChainFile,
      File keyFile,
      String keyPassword,
      Iterable<String> ciphers,
      Iterable<String> nextProtocols,
      long sessionCacheSize,
      long sessionTimeout
   ) throws SSLException {
      this(certChainFile, keyFile, keyPassword, ciphers, toApplicationProtocolConfig(nextProtocols), sessionCacheSize, sessionTimeout);
   }

   @Deprecated
   public OpenSslServerContext(
      File certChainFile,
      File keyFile,
      String keyPassword,
      TrustManagerFactory trustManagerFactory,
      Iterable<String> ciphers,
      ApplicationProtocolConfig config,
      long sessionCacheSize,
      long sessionTimeout
   ) throws SSLException {
      this(certChainFile, keyFile, keyPassword, trustManagerFactory, ciphers, toNegotiator(config), sessionCacheSize, sessionTimeout);
   }

   @Deprecated
   public OpenSslServerContext(
      File certChainFile,
      File keyFile,
      String keyPassword,
      TrustManagerFactory trustManagerFactory,
      Iterable<String> ciphers,
      OpenSslApplicationProtocolNegotiator apn,
      long sessionCacheSize,
      long sessionTimeout
   ) throws SSLException {
      this(null, trustManagerFactory, certChainFile, keyFile, keyPassword, null, ciphers, null, apn, sessionCacheSize, sessionTimeout);
   }

   @Deprecated
   public OpenSslServerContext(
      File certChainFile,
      File keyFile,
      String keyPassword,
      Iterable<String> ciphers,
      CipherSuiteFilter cipherFilter,
      ApplicationProtocolConfig apn,
      long sessionCacheSize,
      long sessionTimeout
   ) throws SSLException {
      this(null, null, certChainFile, keyFile, keyPassword, null, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout);
   }

   @Deprecated
   public OpenSslServerContext(
      File trustCertCollectionFile,
      TrustManagerFactory trustManagerFactory,
      File keyCertChainFile,
      File keyFile,
      String keyPassword,
      KeyManagerFactory keyManagerFactory,
      Iterable<String> ciphers,
      CipherSuiteFilter cipherFilter,
      ApplicationProtocolConfig config,
      long sessionCacheSize,
      long sessionTimeout
   ) throws SSLException {
      this(
         trustCertCollectionFile,
         trustManagerFactory,
         keyCertChainFile,
         keyFile,
         keyPassword,
         keyManagerFactory,
         ciphers,
         cipherFilter,
         toNegotiator(config),
         sessionCacheSize,
         sessionTimeout
      );
   }

   @Deprecated
   public OpenSslServerContext(
      File certChainFile,
      File keyFile,
      String keyPassword,
      TrustManagerFactory trustManagerFactory,
      Iterable<String> ciphers,
      CipherSuiteFilter cipherFilter,
      ApplicationProtocolConfig config,
      long sessionCacheSize,
      long sessionTimeout
   ) throws SSLException {
      this(null, trustManagerFactory, certChainFile, keyFile, keyPassword, null, ciphers, cipherFilter, toNegotiator(config), sessionCacheSize, sessionTimeout);
   }

   @Deprecated
   public OpenSslServerContext(
      File certChainFile,
      File keyFile,
      String keyPassword,
      TrustManagerFactory trustManagerFactory,
      Iterable<String> ciphers,
      CipherSuiteFilter cipherFilter,
      OpenSslApplicationProtocolNegotiator apn,
      long sessionCacheSize,
      long sessionTimeout
   ) throws SSLException {
      this(null, trustManagerFactory, certChainFile, keyFile, keyPassword, null, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout);
   }

   @Deprecated
   public OpenSslServerContext(
      File trustCertCollectionFile,
      TrustManagerFactory trustManagerFactory,
      File keyCertChainFile,
      File keyFile,
      String keyPassword,
      KeyManagerFactory keyManagerFactory,
      Iterable<String> ciphers,
      CipherSuiteFilter cipherFilter,
      OpenSslApplicationProtocolNegotiator apn,
      long sessionCacheSize,
      long sessionTimeout
   ) throws SSLException {
      this(
         toX509CertificatesInternal(trustCertCollectionFile),
         trustManagerFactory,
         toX509CertificatesInternal(keyCertChainFile),
         toPrivateKeyInternal(keyFile, keyPassword),
         keyPassword,
         keyManagerFactory,
         ciphers,
         cipherFilter,
         apn,
         sessionCacheSize,
         sessionTimeout,
         ClientAuth.NONE,
         null,
         false,
         false,
         KeyStore.getDefaultType(),
         null
      );
   }

   OpenSslServerContext(
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

   private OpenSslServerContext(
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
      super(ciphers, cipherFilter, apn, 1, keyCertChain, clientAuth, protocols, startTls, enableOcsp, null, resumptionController, options);
      boolean success = false;
      boolean fallbackToJdkSignatureProviders = isJdkSignatureFallbackEnabled(options);

      try {
         OpenSslKeyMaterialProvider.validateKeyMaterialSupported(keyCertChain, key, keyPassword, fallbackToJdkSignatureProviders);
         this.sessionContext = ReferenceCountedOpenSslServerContext.newSessionContext(
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
            fallbackToJdkSignatureProviders
         );
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
}
