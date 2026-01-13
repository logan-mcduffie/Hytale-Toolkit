package io.netty.handler.ssl;

import io.netty.util.CharsetUtil;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedTrustManager;

@Deprecated
public final class JdkSslServerContext extends JdkSslContext {
   private static final boolean WRAP_TRUST_MANAGER;

   static void checkIfWrappingTrustManagerIsSupported() throws CertificateException, InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, IOException, KeyException, KeyStoreException, UnrecoverableKeyException {
      X509Certificate[] certs = toX509Certificates(
         new ByteArrayInputStream(
            "-----BEGIN CERTIFICATE-----\nMIICrjCCAZagAwIBAgIIdSvQPv1QAZQwDQYJKoZIhvcNAQELBQAwFjEUMBIGA1UEAxMLZXhhbXBs\nZS5jb20wIBcNMTgwNDA2MjIwNjU5WhgPOTk5OTEyMzEyMzU5NTlaMBYxFDASBgNVBAMTC2V4YW1w\nbGUuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAggbWsmDQ6zNzRZ5AW8E3eoGl\nqWvOBDb5Fs1oBRrVQHuYmVAoaqwDzXYJ0LOwa293AgWEQ1jpcbZ2hpoYQzqEZBTLnFhMrhRFlH6K\nbJND8Y33kZ/iSVBBDuGbdSbJShlM+4WwQ9IAso4MZ4vW3S1iv5fGGpLgbtXRmBf/RU8omN0Gijlv\nWlLWHWijLN8xQtySFuBQ7ssW8RcKAary3pUm6UUQB+Co6lnfti0Tzag8PgjhAJq2Z3wbsGRnP2YS\nvYoaK6qzmHXRYlp/PxrjBAZAmkLJs4YTm/XFF+fkeYx4i9zqHbyone5yerRibsHaXZWLnUL+rFoe\nMdKvr0VS3sGmhQIDAQABMA0GCSqGSIb3DQEBCwUAA4IBAQADQi441pKmXf9FvUV5EHU4v8nJT9Iq\nyqwsKwXnr7AsUlDGHBD7jGrjAXnG5rGxuNKBQ35wRxJATKrUtyaquFUL6H8O6aGQehiFTk6zmPbe\n12Gu44vqqTgIUxnv3JQJiox8S2hMxsSddpeCmSdvmalvD6WG4NthH6B9ZaBEiep1+0s0RUaBYn73\nI7CCUaAtbjfR6pcJjrFk5ei7uwdQZFSJtkP2z8r7zfeANJddAKFlkaMWn7u+OIVuB4XPooWicObk\nNAHFtP65bocUYnDpTVdiyvn8DdqyZ/EO8n1bBKBzuSLplk2msW4pdgaFgY7Vw/0wzcFXfUXmL1uy\nG8sQD/wx\n-----END CERTIFICATE-----"
               .getBytes(CharsetUtil.US_ASCII)
         )
      );
      PrivateKey privateKey = toPrivateKey(
         new ByteArrayInputStream(
            "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCCBtayYNDrM3NFnkBbwTd6gaWp\na84ENvkWzWgFGtVAe5iZUChqrAPNdgnQs7Brb3cCBYRDWOlxtnaGmhhDOoRkFMucWEyuFEWUfops\nk0PxjfeRn+JJUEEO4Zt1JslKGUz7hbBD0gCyjgxni9bdLWK/l8YakuBu1dGYF/9FTyiY3QaKOW9a\nUtYdaKMs3zFC3JIW4FDuyxbxFwoBqvLelSbpRRAH4KjqWd+2LRPNqDw+COEAmrZnfBuwZGc/ZhK9\nihorqrOYddFiWn8/GuMEBkCaQsmzhhOb9cUX5+R5jHiL3OodvKid7nJ6tGJuwdpdlYudQv6sWh4x\n0q+vRVLewaaFAgMBAAECggEAP8tPJvFtTxhNJAkCloHz0D0vpDHqQBMgntlkgayqmBqLwhyb18pR\ni0qwgh7HHc7wWqOOQuSqlEnrWRrdcI6TSe8R/sErzfTQNoznKWIPYcI/hskk4sdnQ//Yn9/Jvnsv\nU/BBjOTJxtD+sQbhAl80JcA3R+5sArURQkfzzHOL/YMqzAsn5hTzp7HZCxUqBk3KaHRxV7NefeOE\nxlZuWSmxYWfbFIs4kx19/1t7h8CHQWezw+G60G2VBtSBBxDnhBWvqG6R/wpzJ3nEhPLLY9T+XIHe\nipzdMOOOUZorfIg7M+pyYPji+ZIZxIpY5OjrOzXHciAjRtr5Y7l99K1CG1LguQKBgQDrQfIMxxtZ\nvxU/1cRmUV9l7pt5bjV5R6byXq178LxPKVYNjdZ840Q0/OpZEVqaT1xKVi35ohP1QfNjxPLlHD+K\niDAR9z6zkwjIrbwPCnb5kuXy4lpwPcmmmkva25fI7qlpHtbcuQdoBdCfr/KkKaUCMPyY89LCXgEw\n5KTDj64UywKBgQCNfbO+eZLGzhiHhtNJurresCsIGWlInv322gL8CSfBMYl6eNfUTZvUDdFhPISL\nUljKWzXDrjw0ujFSPR0XhUGtiq89H+HUTuPPYv25gVXO+HTgBFZEPl4PpA+BUsSVZy0NddneyqLk\n42Wey9omY9Q8WsdNQS5cbUvy0uG6WFoX7wKBgQDZ1jpW8pa0x2bZsQsm4vo+3G5CRnZlUp+XlWt2\ndDcp5dC0xD1zbs1dc0NcLeGDOTDv9FSl7hok42iHXXq8AygjEm/QcuwwQ1nC2HxmQP5holAiUs4D\nWHM8PWs3wFYPzE459EBoKTxeaeP/uWAn+he8q7d5uWvSZlEcANs/6e77eQKBgD21Ar0hfFfj7mK8\n9E0FeRZBsqK3omkfnhcYgZC11Xa2SgT1yvs2Va2n0RcdM5kncr3eBZav2GYOhhAdwyBM55XuE/sO\neokDVutNeuZ6d5fqV96TRaRBpvgfTvvRwxZ9hvKF4Vz+9wfn/JvCwANaKmegF6ejs7pvmF3whq2k\ndrZVAoGAX5YxQ5XMTD0QbMAl7/6qp6S58xNoVdfCkmkj1ZLKaHKIjS/benkKGlySVQVPexPfnkZx\np/Vv9yyphBoudiTBS9Uog66ueLYZqpgxlM/6OhYg86Gm3U2ycvMxYjBM1NFiyze21AqAhI+HX+Ot\nmraV2/guSgDgZAhukRZzeQ2RucI=\n-----END PRIVATE KEY-----"
               .getBytes(CharsetUtil.UTF_8)
         ),
         null
      );
      char[] keyStorePassword = keyStorePassword(null);
      KeyStore ks = buildKeyStore(certs, privateKey, keyStorePassword, null);
      KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      kmf.init(ks, keyStorePassword);
      SSLContext ctx = SSLContext.getInstance("TLS");
      TrustManagerFactory tm = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      tm.init((KeyStore)null);
      TrustManager[] managers = tm.getTrustManagers();
      ctx.init(kmf.getKeyManagers(), wrapTrustManagerIfNeeded(managers, null), null);
   }

   @Deprecated
   public JdkSslServerContext(File certChainFile, File keyFile) throws SSLException {
      this(null, certChainFile, keyFile, null, null, IdentityCipherSuiteFilter.INSTANCE, JdkDefaultApplicationProtocolNegotiator.INSTANCE, 0L, 0L, null);
   }

   @Deprecated
   public JdkSslServerContext(File certChainFile, File keyFile, String keyPassword) throws SSLException {
      this(certChainFile, keyFile, keyPassword, null, IdentityCipherSuiteFilter.INSTANCE, JdkDefaultApplicationProtocolNegotiator.INSTANCE, 0L, 0L);
   }

   @Deprecated
   public JdkSslServerContext(
      File certChainFile,
      File keyFile,
      String keyPassword,
      Iterable<String> ciphers,
      Iterable<String> nextProtocols,
      long sessionCacheSize,
      long sessionTimeout
   ) throws SSLException {
      this(
         null,
         certChainFile,
         keyFile,
         keyPassword,
         ciphers,
         IdentityCipherSuiteFilter.INSTANCE,
         toNegotiator(toApplicationProtocolConfig(nextProtocols), true),
         sessionCacheSize,
         sessionTimeout,
         KeyStore.getDefaultType()
      );
   }

   @Deprecated
   public JdkSslServerContext(
      File certChainFile,
      File keyFile,
      String keyPassword,
      Iterable<String> ciphers,
      CipherSuiteFilter cipherFilter,
      ApplicationProtocolConfig apn,
      long sessionCacheSize,
      long sessionTimeout
   ) throws SSLException {
      this(
         null, certChainFile, keyFile, keyPassword, ciphers, cipherFilter, toNegotiator(apn, true), sessionCacheSize, sessionTimeout, KeyStore.getDefaultType()
      );
   }

   @Deprecated
   public JdkSslServerContext(
      File certChainFile,
      File keyFile,
      String keyPassword,
      Iterable<String> ciphers,
      CipherSuiteFilter cipherFilter,
      JdkApplicationProtocolNegotiator apn,
      long sessionCacheSize,
      long sessionTimeout
   ) throws SSLException {
      this(null, certChainFile, keyFile, keyPassword, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout, KeyStore.getDefaultType());
   }

   JdkSslServerContext(
      Provider provider,
      File certChainFile,
      File keyFile,
      String keyPassword,
      Iterable<String> ciphers,
      CipherSuiteFilter cipherFilter,
      JdkApplicationProtocolNegotiator apn,
      long sessionCacheSize,
      long sessionTimeout,
      String keyStore
   ) throws SSLException {
      super(
         newSSLContext(
            provider,
            null,
            null,
            toX509CertificatesInternal(certChainFile),
            toPrivateKeyInternal(keyFile, keyPassword),
            keyPassword,
            null,
            sessionCacheSize,
            sessionTimeout,
            null,
            keyStore,
            null
         ),
         false,
         ciphers,
         cipherFilter,
         apn,
         ClientAuth.NONE,
         null,
         false
      );
   }

   @Deprecated
   public JdkSslServerContext(
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
      super(
         newSSLContext(
            null,
            toX509CertificatesInternal(trustCertCollectionFile),
            trustManagerFactory,
            toX509CertificatesInternal(keyCertChainFile),
            toPrivateKeyInternal(keyFile, keyPassword),
            keyPassword,
            keyManagerFactory,
            sessionCacheSize,
            sessionTimeout,
            null,
            null,
            null
         ),
         false,
         ciphers,
         cipherFilter,
         apn,
         ClientAuth.NONE,
         null,
         false
      );
   }

   @Deprecated
   public JdkSslServerContext(
      File trustCertCollectionFile,
      TrustManagerFactory trustManagerFactory,
      File keyCertChainFile,
      File keyFile,
      String keyPassword,
      KeyManagerFactory keyManagerFactory,
      Iterable<String> ciphers,
      CipherSuiteFilter cipherFilter,
      JdkApplicationProtocolNegotiator apn,
      long sessionCacheSize,
      long sessionTimeout
   ) throws SSLException {
      super(
         newSSLContext(
            null,
            toX509CertificatesInternal(trustCertCollectionFile),
            trustManagerFactory,
            toX509CertificatesInternal(keyCertChainFile),
            toPrivateKeyInternal(keyFile, keyPassword),
            keyPassword,
            keyManagerFactory,
            sessionCacheSize,
            sessionTimeout,
            null,
            KeyStore.getDefaultType(),
            null
         ),
         false,
         ciphers,
         cipherFilter,
         apn,
         ClientAuth.NONE,
         null,
         false
      );
   }

   JdkSslServerContext(
      Provider provider,
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
      SecureRandom secureRandom,
      String keyStore,
      ResumptionController resumptionController
   ) throws SSLException {
      super(
         newSSLContext(
            provider,
            trustCertCollection,
            trustManagerFactory,
            keyCertChain,
            key,
            keyPassword,
            keyManagerFactory,
            sessionCacheSize,
            sessionTimeout,
            secureRandom,
            keyStore,
            resumptionController
         ),
         false,
         ciphers,
         cipherFilter,
         toNegotiator(apn, true),
         clientAuth,
         protocols,
         startTls,
         null,
         null,
         resumptionController
      );
   }

   private static SSLContext newSSLContext(
      Provider sslContextProvider,
      X509Certificate[] trustCertCollection,
      TrustManagerFactory trustManagerFactory,
      X509Certificate[] keyCertChain,
      PrivateKey key,
      String keyPassword,
      KeyManagerFactory keyManagerFactory,
      long sessionCacheSize,
      long sessionTimeout,
      SecureRandom secureRandom,
      String keyStore,
      ResumptionController resumptionController
   ) throws SSLException {
      if (key == null && keyManagerFactory == null) {
         throw new NullPointerException("key, keyManagerFactory");
      } else {
         try {
            if (trustCertCollection != null) {
               trustManagerFactory = buildTrustManagerFactory(trustCertCollection, trustManagerFactory, keyStore);
            } else if (trustManagerFactory == null) {
               trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
               trustManagerFactory.init((KeyStore)null);
            }

            if (key != null) {
               keyManagerFactory = buildKeyManagerFactory(keyCertChain, null, key, keyPassword, keyManagerFactory, null);
            }

            SSLContext ctx = sslContextProvider == null ? SSLContext.getInstance("TLS") : SSLContext.getInstance("TLS", sslContextProvider);
            ctx.init(keyManagerFactory.getKeyManagers(), wrapTrustManagerIfNeeded(trustManagerFactory.getTrustManagers(), resumptionController), secureRandom);
            SSLSessionContext sessCtx = ctx.getServerSessionContext();
            if (sessionCacheSize > 0L) {
               sessCtx.setSessionCacheSize((int)Math.min(sessionCacheSize, 2147483647L));
            }

            if (sessionTimeout > 0L) {
               sessCtx.setSessionTimeout((int)Math.min(sessionTimeout, 2147483647L));
            }

            return ctx;
         } catch (Exception var16) {
            if (var16 instanceof SSLException) {
               throw (SSLException)var16;
            } else {
               throw new SSLException("failed to initialize the server-side SSL context", var16);
            }
         }
      }
   }

   private static TrustManager[] wrapTrustManagerIfNeeded(TrustManager[] trustManagers, ResumptionController resumptionController) {
      if (WRAP_TRUST_MANAGER) {
         for (int i = 0; i < trustManagers.length; i++) {
            TrustManager tm = trustManagers[i];
            if (resumptionController != null) {
               tm = resumptionController.wrapIfNeeded(tm);
            }

            if (tm instanceof X509ExtendedTrustManager) {
               trustManagers[i] = new EnhancingX509ExtendedTrustManager((X509ExtendedTrustManager)tm);
            }
         }
      }

      return trustManagers;
   }

   static {
      boolean wrapTrustManager = false;

      try {
         checkIfWrappingTrustManagerIsSupported();
         wrapTrustManager = true;
      } catch (Throwable var2) {
      }

      WRAP_TRUST_MANAGER = wrapTrustManager;
   }
}
