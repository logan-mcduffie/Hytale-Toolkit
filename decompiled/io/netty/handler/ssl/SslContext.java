package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.ssl.util.BouncyCastleUtil;
import io.netty.util.AttributeMap;
import io.netty.util.DefaultAttributeMap;
import io.netty.util.concurrent.ImmediateExecutor;
import io.netty.util.internal.EmptyArrays;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Executor;
import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManagerFactory;

public abstract class SslContext {
   static final String ALIAS = "key";
   static final CertificateFactory X509_CERT_FACTORY;
   private final boolean startTls;
   private final AttributeMap attributes = new DefaultAttributeMap();
   final ResumptionController resumptionController;
   private static final String OID_PKCS5_PBES2 = "1.2.840.113549.1.5.13";
   private static final String PBES2 = "PBES2";

   public static SslProvider defaultServerProvider() {
      return defaultProvider();
   }

   public static SslProvider defaultClientProvider() {
      return defaultProvider();
   }

   private static SslProvider defaultProvider() {
      return OpenSsl.isAvailable() ? SslProvider.OPENSSL : SslProvider.JDK;
   }

   @Deprecated
   public static SslContext newServerContext(File certChainFile, File keyFile) throws SSLException {
      return newServerContext(certChainFile, keyFile, null);
   }

   @Deprecated
   public static SslContext newServerContext(File certChainFile, File keyFile, String keyPassword) throws SSLException {
      return newServerContext(null, certChainFile, keyFile, keyPassword);
   }

   @Deprecated
   public static SslContext newServerContext(
      File certChainFile,
      File keyFile,
      String keyPassword,
      Iterable<String> ciphers,
      Iterable<String> nextProtocols,
      long sessionCacheSize,
      long sessionTimeout
   ) throws SSLException {
      return newServerContext(null, certChainFile, keyFile, keyPassword, ciphers, nextProtocols, sessionCacheSize, sessionTimeout);
   }

   @Deprecated
   public static SslContext newServerContext(
      File certChainFile,
      File keyFile,
      String keyPassword,
      Iterable<String> ciphers,
      CipherSuiteFilter cipherFilter,
      ApplicationProtocolConfig apn,
      long sessionCacheSize,
      long sessionTimeout
   ) throws SSLException {
      return newServerContext(null, certChainFile, keyFile, keyPassword, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout);
   }

   @Deprecated
   public static SslContext newServerContext(SslProvider provider, File certChainFile, File keyFile) throws SSLException {
      return newServerContext(provider, certChainFile, keyFile, null);
   }

   @Deprecated
   public static SslContext newServerContext(SslProvider provider, File certChainFile, File keyFile, String keyPassword) throws SSLException {
      return newServerContext(provider, certChainFile, keyFile, keyPassword, null, IdentityCipherSuiteFilter.INSTANCE, null, 0L, 0L);
   }

   @Deprecated
   public static SslContext newServerContext(
      SslProvider provider,
      File certChainFile,
      File keyFile,
      String keyPassword,
      Iterable<String> ciphers,
      Iterable<String> nextProtocols,
      long sessionCacheSize,
      long sessionTimeout
   ) throws SSLException {
      return newServerContext(
         provider,
         certChainFile,
         keyFile,
         keyPassword,
         ciphers,
         IdentityCipherSuiteFilter.INSTANCE,
         toApplicationProtocolConfig(nextProtocols),
         sessionCacheSize,
         sessionTimeout
      );
   }

   @Deprecated
   public static SslContext newServerContext(
      SslProvider provider,
      File certChainFile,
      File keyFile,
      String keyPassword,
      TrustManagerFactory trustManagerFactory,
      Iterable<String> ciphers,
      Iterable<String> nextProtocols,
      long sessionCacheSize,
      long sessionTimeout
   ) throws SSLException {
      return newServerContext(
         provider,
         null,
         trustManagerFactory,
         certChainFile,
         keyFile,
         keyPassword,
         null,
         ciphers,
         IdentityCipherSuiteFilter.INSTANCE,
         toApplicationProtocolConfig(nextProtocols),
         sessionCacheSize,
         sessionTimeout
      );
   }

   @Deprecated
   public static SslContext newServerContext(
      SslProvider provider,
      File certChainFile,
      File keyFile,
      String keyPassword,
      Iterable<String> ciphers,
      CipherSuiteFilter cipherFilter,
      ApplicationProtocolConfig apn,
      long sessionCacheSize,
      long sessionTimeout
   ) throws SSLException {
      return newServerContext(
         provider,
         null,
         null,
         certChainFile,
         keyFile,
         keyPassword,
         null,
         ciphers,
         cipherFilter,
         apn,
         sessionCacheSize,
         sessionTimeout,
         KeyStore.getDefaultType()
      );
   }

   @Deprecated
   public static SslContext newServerContext(
      SslProvider provider,
      File trustCertCollectionFile,
      TrustManagerFactory trustManagerFactory,
      File keyCertChainFile,
      File keyFile,
      String keyPassword,
      KeyManagerFactory keyManagerFactory,
      Iterable<String> ciphers,
      CipherSuiteFilter cipherFilter,
      ApplicationProtocolConfig apn,
      long sessionCacheSize,
      long sessionTimeout
   ) throws SSLException {
      return newServerContext(
         provider,
         trustCertCollectionFile,
         trustManagerFactory,
         keyCertChainFile,
         keyFile,
         keyPassword,
         keyManagerFactory,
         ciphers,
         cipherFilter,
         apn,
         sessionCacheSize,
         sessionTimeout,
         KeyStore.getDefaultType()
      );
   }

   static SslContext newServerContext(
      SslProvider provider,
      File trustCertCollectionFile,
      TrustManagerFactory trustManagerFactory,
      File keyCertChainFile,
      File keyFile,
      String keyPassword,
      KeyManagerFactory keyManagerFactory,
      Iterable<String> ciphers,
      CipherSuiteFilter cipherFilter,
      ApplicationProtocolConfig apn,
      long sessionCacheSize,
      long sessionTimeout,
      String keyStore
   ) throws SSLException {
      try {
         return newServerContextInternal(
            provider,
            null,
            toX509Certificates(trustCertCollectionFile),
            trustManagerFactory,
            toX509Certificates(keyCertChainFile),
            toPrivateKey(keyFile, keyPassword),
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
            null,
            keyStore
         );
      } catch (Exception var16) {
         if (var16 instanceof SSLException) {
            throw (SSLException)var16;
         } else {
            throw new SSLException("failed to initialize the server-side SSL context", var16);
         }
      }
   }

   static SslContext newServerContextInternal(
      SslProvider provider,
      Provider sslContextProvider,
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
      SecureRandom secureRandom,
      String keyStoreType,
      Entry<SslContextOption<?>, Object>... ctxOptions
   ) throws SSLException {
      if (provider == null) {
         provider = defaultServerProvider();
      }

      ResumptionController resumptionController = new ResumptionController();
      switch (provider) {
         case JDK:
            if (enableOcsp) {
               throw new IllegalArgumentException("OCSP is not supported with this SslProvider: " + provider);
            }

            return new JdkSslServerContext(
               sslContextProvider,
               trustCertCollection,
               trustManagerFactory,
               keyCertChain,
               key,
               keyPassword,
               keyManagerFactory,
               ciphers,
               cipherFilter,
               apn,
               sessionCacheSize,
               sessionTimeout,
               clientAuth,
               protocols,
               startTls,
               secureRandom,
               keyStoreType,
               resumptionController
            );
         case OPENSSL:
            verifyNullSslContextProvider(provider, sslContextProvider);
            return new OpenSslServerContext(
               trustCertCollection,
               trustManagerFactory,
               keyCertChain,
               key,
               keyPassword,
               keyManagerFactory,
               ciphers,
               cipherFilter,
               apn,
               sessionCacheSize,
               sessionTimeout,
               clientAuth,
               protocols,
               startTls,
               enableOcsp,
               keyStoreType,
               resumptionController,
               ctxOptions
            );
         case OPENSSL_REFCNT:
            verifyNullSslContextProvider(provider, sslContextProvider);
            return new ReferenceCountedOpenSslServerContext(
               trustCertCollection,
               trustManagerFactory,
               keyCertChain,
               key,
               keyPassword,
               keyManagerFactory,
               ciphers,
               cipherFilter,
               apn,
               sessionCacheSize,
               sessionTimeout,
               clientAuth,
               protocols,
               startTls,
               enableOcsp,
               keyStoreType,
               resumptionController,
               ctxOptions
            );
         default:
            throw new Error("Unexpected provider: " + provider);
      }
   }

   private static void verifyNullSslContextProvider(SslProvider provider, Provider sslContextProvider) {
      if (sslContextProvider != null) {
         throw new IllegalArgumentException("Java Security Provider unsupported for SslProvider: " + provider);
      }
   }

   @Deprecated
   public static SslContext newClientContext() throws SSLException {
      return newClientContext(null, null, null);
   }

   @Deprecated
   public static SslContext newClientContext(File certChainFile) throws SSLException {
      return newClientContext(null, certChainFile);
   }

   @Deprecated
   public static SslContext newClientContext(TrustManagerFactory trustManagerFactory) throws SSLException {
      return newClientContext(null, null, trustManagerFactory);
   }

   @Deprecated
   public static SslContext newClientContext(File certChainFile, TrustManagerFactory trustManagerFactory) throws SSLException {
      return newClientContext(null, certChainFile, trustManagerFactory);
   }

   @Deprecated
   public static SslContext newClientContext(
      File certChainFile,
      TrustManagerFactory trustManagerFactory,
      Iterable<String> ciphers,
      Iterable<String> nextProtocols,
      long sessionCacheSize,
      long sessionTimeout
   ) throws SSLException {
      return newClientContext(null, certChainFile, trustManagerFactory, ciphers, nextProtocols, sessionCacheSize, sessionTimeout);
   }

   @Deprecated
   public static SslContext newClientContext(
      File certChainFile,
      TrustManagerFactory trustManagerFactory,
      Iterable<String> ciphers,
      CipherSuiteFilter cipherFilter,
      ApplicationProtocolConfig apn,
      long sessionCacheSize,
      long sessionTimeout
   ) throws SSLException {
      return newClientContext(null, certChainFile, trustManagerFactory, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout);
   }

   @Deprecated
   public static SslContext newClientContext(SslProvider provider) throws SSLException {
      return newClientContext(provider, null, null);
   }

   @Deprecated
   public static SslContext newClientContext(SslProvider provider, File certChainFile) throws SSLException {
      return newClientContext(provider, certChainFile, null);
   }

   @Deprecated
   public static SslContext newClientContext(SslProvider provider, TrustManagerFactory trustManagerFactory) throws SSLException {
      return newClientContext(provider, null, trustManagerFactory);
   }

   @Deprecated
   public static SslContext newClientContext(SslProvider provider, File certChainFile, TrustManagerFactory trustManagerFactory) throws SSLException {
      return newClientContext(provider, certChainFile, trustManagerFactory, null, IdentityCipherSuiteFilter.INSTANCE, null, 0L, 0L);
   }

   @Deprecated
   public static SslContext newClientContext(
      SslProvider provider,
      File certChainFile,
      TrustManagerFactory trustManagerFactory,
      Iterable<String> ciphers,
      Iterable<String> nextProtocols,
      long sessionCacheSize,
      long sessionTimeout
   ) throws SSLException {
      return newClientContext(
         provider,
         certChainFile,
         trustManagerFactory,
         null,
         null,
         null,
         null,
         ciphers,
         IdentityCipherSuiteFilter.INSTANCE,
         toApplicationProtocolConfig(nextProtocols),
         sessionCacheSize,
         sessionTimeout
      );
   }

   @Deprecated
   public static SslContext newClientContext(
      SslProvider provider,
      File certChainFile,
      TrustManagerFactory trustManagerFactory,
      Iterable<String> ciphers,
      CipherSuiteFilter cipherFilter,
      ApplicationProtocolConfig apn,
      long sessionCacheSize,
      long sessionTimeout
   ) throws SSLException {
      return newClientContext(
         provider, certChainFile, trustManagerFactory, null, null, null, null, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout
      );
   }

   @Deprecated
   public static SslContext newClientContext(
      SslProvider provider,
      File trustCertCollectionFile,
      TrustManagerFactory trustManagerFactory,
      File keyCertChainFile,
      File keyFile,
      String keyPassword,
      KeyManagerFactory keyManagerFactory,
      Iterable<String> ciphers,
      CipherSuiteFilter cipherFilter,
      ApplicationProtocolConfig apn,
      long sessionCacheSize,
      long sessionTimeout
   ) throws SSLException {
      try {
         return newClientContextInternal(
            provider,
            null,
            toX509Certificates(trustCertCollectionFile),
            trustManagerFactory,
            toX509Certificates(keyCertChainFile),
            toPrivateKey(keyFile, keyPassword),
            keyPassword,
            keyManagerFactory,
            ciphers,
            cipherFilter,
            apn,
            null,
            sessionCacheSize,
            sessionTimeout,
            false,
            null,
            KeyStore.getDefaultType(),
            SslUtils.defaultEndpointVerificationAlgorithm,
            Collections.emptyList()
         );
      } catch (Exception var15) {
         if (var15 instanceof SSLException) {
            throw (SSLException)var15;
         } else {
            throw new SSLException("failed to initialize the client-side SSL context", var15);
         }
      }
   }

   static SslContext newClientContextInternal(
      SslProvider provider,
      Provider sslContextProvider,
      X509Certificate[] trustCert,
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
      SecureRandom secureRandom,
      String keyStoreType,
      String endpointIdentificationAlgorithm,
      List<SNIServerName> serverNames,
      Entry<SslContextOption<?>, Object>... options
   ) throws SSLException {
      if (provider == null) {
         provider = defaultClientProvider();
      }

      ResumptionController resumptionController = new ResumptionController();
      switch (provider) {
         case JDK:
            if (enableOcsp) {
               throw new IllegalArgumentException("OCSP is not supported with this SslProvider: " + provider);
            }

            return new JdkSslClientContext(
               sslContextProvider,
               trustCert,
               trustManagerFactory,
               keyCertChain,
               key,
               keyPassword,
               keyManagerFactory,
               ciphers,
               cipherFilter,
               apn,
               protocols,
               sessionCacheSize,
               sessionTimeout,
               secureRandom,
               keyStoreType,
               endpointIdentificationAlgorithm,
               serverNames,
               resumptionController
            );
         case OPENSSL:
            verifyNullSslContextProvider(provider, sslContextProvider);
            OpenSsl.ensureAvailability();
            return new OpenSslClientContext(
               trustCert,
               trustManagerFactory,
               keyCertChain,
               key,
               keyPassword,
               keyManagerFactory,
               ciphers,
               cipherFilter,
               apn,
               protocols,
               sessionCacheSize,
               sessionTimeout,
               enableOcsp,
               keyStoreType,
               endpointIdentificationAlgorithm,
               serverNames,
               resumptionController,
               options
            );
         case OPENSSL_REFCNT:
            verifyNullSslContextProvider(provider, sslContextProvider);
            OpenSsl.ensureAvailability();
            return new ReferenceCountedOpenSslClientContext(
               trustCert,
               trustManagerFactory,
               keyCertChain,
               key,
               keyPassword,
               keyManagerFactory,
               ciphers,
               cipherFilter,
               apn,
               protocols,
               sessionCacheSize,
               sessionTimeout,
               enableOcsp,
               keyStoreType,
               endpointIdentificationAlgorithm,
               serverNames,
               resumptionController,
               options
            );
         default:
            throw new Error("Unexpected provider: " + provider);
      }
   }

   static ApplicationProtocolConfig toApplicationProtocolConfig(Iterable<String> nextProtocols) {
      ApplicationProtocolConfig apn;
      if (nextProtocols == null) {
         apn = ApplicationProtocolConfig.DISABLED;
      } else {
         apn = new ApplicationProtocolConfig(
            ApplicationProtocolConfig.Protocol.NPN_AND_ALPN,
            ApplicationProtocolConfig.SelectorFailureBehavior.CHOOSE_MY_LAST_PROTOCOL,
            ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT,
            nextProtocols
         );
      }

      return apn;
   }

   protected SslContext() {
      this(false);
   }

   protected SslContext(boolean startTls) {
      this(startTls, null);
   }

   SslContext(boolean startTls, ResumptionController resumptionController) {
      this.startTls = startTls;
      this.resumptionController = resumptionController;
   }

   public final AttributeMap attributes() {
      return this.attributes;
   }

   public final boolean isServer() {
      return !this.isClient();
   }

   public abstract boolean isClient();

   public abstract List<String> cipherSuites();

   public long sessionCacheSize() {
      return this.sessionContext().getSessionCacheSize();
   }

   public long sessionTimeout() {
      return this.sessionContext().getSessionTimeout();
   }

   @Deprecated
   public final List<String> nextProtocols() {
      return this.applicationProtocolNegotiator().protocols();
   }

   public abstract ApplicationProtocolNegotiator applicationProtocolNegotiator();

   public abstract SSLEngine newEngine(ByteBufAllocator var1);

   public abstract SSLEngine newEngine(ByteBufAllocator var1, String var2, int var3);

   public abstract SSLSessionContext sessionContext();

   public final SslHandler newHandler(ByteBufAllocator alloc) {
      return this.newHandler(alloc, this.startTls);
   }

   protected SslHandler newHandler(ByteBufAllocator alloc, boolean startTls) {
      return new SslHandler(this.newEngine(alloc), startTls, ImmediateExecutor.INSTANCE, this.resumptionController);
   }

   public SslHandler newHandler(ByteBufAllocator alloc, Executor delegatedTaskExecutor) {
      return this.newHandler(alloc, this.startTls, delegatedTaskExecutor);
   }

   protected SslHandler newHandler(ByteBufAllocator alloc, boolean startTls, Executor executor) {
      return new SslHandler(this.newEngine(alloc), startTls, executor, this.resumptionController);
   }

   public final SslHandler newHandler(ByteBufAllocator alloc, String peerHost, int peerPort) {
      return this.newHandler(alloc, peerHost, peerPort, this.startTls);
   }

   protected SslHandler newHandler(ByteBufAllocator alloc, String peerHost, int peerPort, boolean startTls) {
      return new SslHandler(this.newEngine(alloc, peerHost, peerPort), startTls, ImmediateExecutor.INSTANCE, this.resumptionController);
   }

   public SslHandler newHandler(ByteBufAllocator alloc, String peerHost, int peerPort, Executor delegatedTaskExecutor) {
      return this.newHandler(alloc, peerHost, peerPort, this.startTls, delegatedTaskExecutor);
   }

   protected SslHandler newHandler(ByteBufAllocator alloc, String peerHost, int peerPort, boolean startTls, Executor delegatedTaskExecutor) {
      return new SslHandler(this.newEngine(alloc, peerHost, peerPort), startTls, delegatedTaskExecutor, this.resumptionController);
   }

   @Deprecated
   protected static PKCS8EncodedKeySpec generateKeySpec(char[] password, byte[] key) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, InvalidAlgorithmParameterException {
      if (password == null) {
         return new PKCS8EncodedKeySpec(key);
      } else {
         EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(key);
         String pbeAlgorithm = getPBEAlgorithm(encryptedPrivateKeyInfo);
         SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(pbeAlgorithm);
         PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
         SecretKey pbeKey = keyFactory.generateSecret(pbeKeySpec);
         Cipher cipher = Cipher.getInstance(pbeAlgorithm);
         cipher.init(2, pbeKey, encryptedPrivateKeyInfo.getAlgParameters());
         return encryptedPrivateKeyInfo.getKeySpec(cipher);
      }
   }

   private static String getPBEAlgorithm(EncryptedPrivateKeyInfo encryptedPrivateKeyInfo) {
      AlgorithmParameters parameters = encryptedPrivateKeyInfo.getAlgParameters();
      String algName = encryptedPrivateKeyInfo.getAlgName();
      return parameters == null || !"1.2.840.113549.1.5.13".equals(algName) && !"PBES2".equals(algName)
         ? encryptedPrivateKeyInfo.getAlgName()
         : parameters.toString();
   }

   protected static KeyStore buildKeyStore(X509Certificate[] certChain, PrivateKey key, char[] keyPasswordChars, String keyStoreType) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
      if (keyStoreType == null) {
         keyStoreType = KeyStore.getDefaultType();
      }

      KeyStore ks = KeyStore.getInstance(keyStoreType);
      ks.load(null, null);
      ks.setKeyEntry("key", key, keyPasswordChars, certChain);
      return ks;
   }

   protected static PrivateKey toPrivateKey(File keyFile, String keyPassword) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, KeyException, IOException {
      return toPrivateKey(keyFile, keyPassword, true);
   }

   static PrivateKey toPrivateKey(File keyFile, String keyPassword, boolean tryBouncyCastle) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, KeyException, IOException {
      if (keyFile == null) {
         return null;
      } else {
         if (tryBouncyCastle && BouncyCastleUtil.isBcPkixAvailable()) {
            PrivateKey pk = BouncyCastlePemReader.getPrivateKey(keyFile, keyPassword);
            if (pk != null) {
               return pk;
            }
         }

         return getPrivateKeyFromByteBuffer(PemReader.readPrivateKey(keyFile), keyPassword);
      }
   }

   protected static PrivateKey toPrivateKey(InputStream keyInputStream, String keyPassword) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, KeyException, IOException {
      if (keyInputStream == null) {
         return null;
      } else {
         if (BouncyCastleUtil.isBcPkixAvailable()) {
            if (!keyInputStream.markSupported()) {
               keyInputStream = new BufferedInputStream(keyInputStream);
            }

            keyInputStream.mark(1048576);
            PrivateKey pk = BouncyCastlePemReader.getPrivateKey(keyInputStream, keyPassword);
            if (pk != null) {
               return pk;
            }

            keyInputStream.reset();
         }

         return getPrivateKeyFromByteBuffer(PemReader.readPrivateKey(keyInputStream), keyPassword);
      }
   }

   private static PrivateKey getPrivateKeyFromByteBuffer(ByteBuf encodedKeyBuf, String keyPassword) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, KeyException, IOException {
      byte[] encodedKey = new byte[encodedKeyBuf.readableBytes()];
      encodedKeyBuf.readBytes(encodedKey).release();
      PKCS8EncodedKeySpec encodedKeySpec = generateKeySpec(keyPassword == null ? null : keyPassword.toCharArray(), encodedKey);

      try {
         return KeyFactory.getInstance("RSA").generatePrivate(encodedKeySpec);
      } catch (InvalidKeySpecException var9) {
         try {
            return KeyFactory.getInstance("DSA").generatePrivate(encodedKeySpec);
         } catch (InvalidKeySpecException var8) {
            try {
               return KeyFactory.getInstance("EC").generatePrivate(encodedKeySpec);
            } catch (InvalidKeySpecException var7) {
               throw new InvalidKeySpecException("Neither RSA, DSA nor EC worked", var7);
            }
         }
      }
   }

   @Deprecated
   protected static TrustManagerFactory buildTrustManagerFactory(File certChainFile, TrustManagerFactory trustManagerFactory) throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
      return buildTrustManagerFactory(certChainFile, trustManagerFactory, null);
   }

   protected static TrustManagerFactory buildTrustManagerFactory(File certChainFile, TrustManagerFactory trustManagerFactory, String keyType) throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
      X509Certificate[] x509Certs = toX509Certificates(certChainFile);
      return buildTrustManagerFactory(x509Certs, trustManagerFactory, keyType);
   }

   protected static X509Certificate[] toX509Certificates(File file) throws CertificateException {
      return file == null ? null : getCertificatesFromBuffers(PemReader.readCertificates(file));
   }

   protected static X509Certificate[] toX509Certificates(InputStream in) throws CertificateException {
      return in == null ? null : getCertificatesFromBuffers(PemReader.readCertificates(in));
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static X509Certificate[] getCertificatesFromBuffers(ByteBuf[] certs) throws CertificateException {
      CertificateFactory cf = CertificateFactory.getInstance("X.509");
      X509Certificate[] x509Certs = new X509Certificate[certs.length];
      boolean var16 = false /* VF: Semaphore variable */;

      try {
         var16 = true;

         for (int i = 0; i < certs.length; i++) {
            try {
               InputStream is = new ByteBufInputStream(certs[i], false);

               try {
                  x509Certs[i] = (X509Certificate)cf.generateCertificate(is);
               } catch (Throwable var18) {
                  try {
                     is.close();
                  } catch (Throwable var17) {
                     var18.addSuppressed(var17);
                  }

                  throw var18;
               }

               is.close();
            } catch (IOException var19) {
               throw new RuntimeException(var19);
            }
         }

         var16 = false;
      } finally {
         if (var16) {
            for (ByteBuf buf : certs) {
               buf.release();
            }
         }
      }

      for (ByteBuf buf : certs) {
         buf.release();
      }

      return x509Certs;
   }

   protected static TrustManagerFactory buildTrustManagerFactory(X509Certificate[] certCollection, TrustManagerFactory trustManagerFactory, String keyStoreType) throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
      if (keyStoreType == null) {
         keyStoreType = KeyStore.getDefaultType();
      }

      KeyStore ks = KeyStore.getInstance(keyStoreType);
      ks.load(null, null);
      int i = 1;

      for (X509Certificate cert : certCollection) {
         String alias = Integer.toString(i);
         ks.setCertificateEntry(alias, cert);
         i++;
      }

      if (trustManagerFactory == null) {
         trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      }

      trustManagerFactory.init(ks);
      return trustManagerFactory;
   }

   static PrivateKey toPrivateKeyInternal(File keyFile, String keyPassword) throws SSLException {
      try {
         return toPrivateKey(keyFile, keyPassword);
      } catch (Exception var3) {
         throw new SSLException(var3);
      }
   }

   static X509Certificate[] toX509CertificatesInternal(File file) throws SSLException {
      try {
         return toX509Certificates(file);
      } catch (CertificateException var2) {
         throw new SSLException(var2);
      }
   }

   protected static KeyManagerFactory buildKeyManagerFactory(
      X509Certificate[] certChainFile, String keyAlgorithm, PrivateKey key, String keyPassword, KeyManagerFactory kmf, String keyStore
   ) throws KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException, UnrecoverableKeyException {
      if (keyAlgorithm == null) {
         keyAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
      }

      char[] keyPasswordChars = keyStorePassword(keyPassword);
      KeyStore ks = buildKeyStore(certChainFile, key, keyPasswordChars, keyStore);
      return buildKeyManagerFactory(ks, keyAlgorithm, keyPasswordChars, kmf);
   }

   static KeyManagerFactory buildKeyManagerFactory(KeyStore ks, String keyAlgorithm, char[] keyPasswordChars, KeyManagerFactory kmf) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
      if (kmf == null) {
         if (keyAlgorithm == null) {
            keyAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
         }

         kmf = KeyManagerFactory.getInstance(keyAlgorithm);
      }

      kmf.init(ks, keyPasswordChars);
      return kmf;
   }

   static char[] keyStorePassword(String keyPassword) {
      return keyPassword == null ? EmptyArrays.EMPTY_CHARS : keyPassword.toCharArray();
   }

   static {
      try {
         X509_CERT_FACTORY = CertificateFactory.getInstance("X.509");
      } catch (CertificateException var1) {
         throw new IllegalStateException("unable to instance X.509 CertificateFactory", var1);
      }
   }
}
