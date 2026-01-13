package io.netty.handler.ssl;

import io.netty.internal.tcnative.SSLPrivateKeyMethod;
import io.netty.util.collection.IntCollections;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

final class JdkDelegatingPrivateKeyMethod implements SSLPrivateKeyMethod {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(JdkDelegatingPrivateKeyMethod.class);
   private static final IntObjectMap<String> SSL_TO_JDK_SIGNATURE_ALGORITHM;
   private static final ConcurrentMap<JdkDelegatingPrivateKeyMethod.CacheKey, String> PROVIDER_CACHE = new ConcurrentHashMap<>();
   private final PrivateKey privateKey;
   private final String privateKeyTypeName;

   JdkDelegatingPrivateKeyMethod(PrivateKey privateKey) {
      this.privateKey = ObjectUtil.checkNotNull(privateKey, "privateKey");
      this.privateKeyTypeName = privateKey.getClass().getName();
   }

   public byte[] sign(long ssl, int signatureAlgorithm, byte[] input) throws Exception {
      Signature signature = this.createSignature(signatureAlgorithm);
      signature.update(input);
      byte[] result = signature.sign();
      if (logger.isDebugEnabled()) {
         logger.debug("Signing operation completed successfully, result length: {}", result.length);
      }

      return result;
   }

   public byte[] decrypt(long ssl, byte[] input) {
      throw new UnsupportedOperationException("Direct decryption is not supported");
   }

   private Signature createSignature(int opensslAlgorithm) throws NoSuchAlgorithmException {
      String jdkAlgorithm = SSL_TO_JDK_SIGNATURE_ALGORITHM.get(opensslAlgorithm);
      if (jdkAlgorithm == null) {
         String errorMsg = "Unsupported signature algorithm: " + opensslAlgorithm;
         throw new NoSuchAlgorithmException(errorMsg);
      } else {
         JdkDelegatingPrivateKeyMethod.CacheKey cacheKey = new JdkDelegatingPrivateKeyMethod.CacheKey(jdkAlgorithm, this.privateKeyTypeName);
         String cachedProviderName = PROVIDER_CACHE.get(cacheKey);
         if (cachedProviderName != null) {
            try {
               Signature signature = Signature.getInstance(jdkAlgorithm, cachedProviderName);
               configureOpenSslAlgorithmParameters(signature, opensslAlgorithm);
               signature.initSign(this.privateKey);
               if (logger.isDebugEnabled()) {
                  logger.debug(
                     "Using cached provider {} for OpenSSL algorithm {} ({}) with key type {}",
                     cachedProviderName,
                     opensslAlgorithm,
                     jdkAlgorithm,
                     this.privateKeyTypeName
                  );
               }

               return signature;
            } catch (Exception var6) {
               PROVIDER_CACHE.remove(cacheKey);
               if (logger.isDebugEnabled()) {
                  logger.debug(
                     "Cached provider {} failed for key type {}, removing from cache: {}", cachedProviderName, this.privateKeyTypeName, var6.getMessage()
                  );
               }
            }
         }

         Signature signature = this.findCompatibleSignature(opensslAlgorithm, jdkAlgorithm);
         PROVIDER_CACHE.put(cacheKey, signature.getProvider().getName());
         if (logger.isDebugEnabled()) {
            logger.debug(
               "Discovered and cached provider {} for OpenSSL algorithm {} ({}) with key type {}",
               signature.getProvider().getName(),
               opensslAlgorithm,
               jdkAlgorithm,
               this.privateKeyTypeName
            );
         }

         return signature;
      }
   }

   private Signature findCompatibleSignature(int opensslAlgorithm, String jdkAlgorithm) throws NoSuchAlgorithmException {
      try {
         Signature signature = Signature.getInstance(jdkAlgorithm);
         configureOpenSslAlgorithmParameters(signature, opensslAlgorithm);
         signature.initSign(this.privateKey);
         if (logger.isDebugEnabled()) {
            logger.debug(
               "Default provider {} handles key type {} for OpenSSL algorithm {} ({})",
               signature.getProvider().getName(),
               this.privateKey.getClass().getName(),
               opensslAlgorithm,
               jdkAlgorithm
            );
         }

         return signature;
      } catch (InvalidKeyException var11) {
         if (logger.isDebugEnabled()) {
            logger.debug(
               "Default provider cannot handle key type {} for OpenSSL algorithm {} ({}): {}",
               this.privateKey.getClass().getName(),
               opensslAlgorithm,
               jdkAlgorithm,
               var11.getMessage()
            );
         }
      } catch (Exception var12) {
         if (logger.isDebugEnabled()) {
            logger.debug("Default provider failed for OpenSSL algorithm {} ({}): {}", opensslAlgorithm, jdkAlgorithm, var12.getMessage());
         }
      }

      Provider[] providers = Security.getProviders();

      for (Provider provider : providers) {
         try {
            Signature signature = Signature.getInstance(jdkAlgorithm, provider);
            configureOpenSslAlgorithmParameters(signature, opensslAlgorithm);
            signature.initSign(this.privateKey);
            if (logger.isDebugEnabled()) {
               logger.debug(
                  "Found compatible provider {} for key type {} with OpenSSL algorithm {} ({})",
                  provider.getName(),
                  this.privateKey.getClass().getName(),
                  opensslAlgorithm,
                  jdkAlgorithm
               );
            }

            return signature;
         } catch (InvalidKeyException var9) {
            if (logger.isTraceEnabled()) {
               logger.trace("Provider {} cannot handle key type {}: {}", provider.getName(), this.privateKey.getClass().getName(), var9.getMessage());
            }
         } catch (Exception var10) {
            if (logger.isTraceEnabled()) {
               logger.trace("Provider {} failed for OpenSSL algorithm {} ({}): {}", provider.getName(), opensslAlgorithm, jdkAlgorithm, var10.getMessage());
            }
         }
      }

      throw new NoSuchAlgorithmException(
         "No provider found for OpenSSL algorithm "
            + opensslAlgorithm
            + " ("
            + jdkAlgorithm
            + ") with private key type: "
            + this.privateKey.getClass().getName()
      );
   }

   private static void configureOpenSslAlgorithmParameters(Signature signature, int opensslAlgorithm) throws InvalidAlgorithmParameterException {
      if (opensslAlgorithm == OpenSslAsyncPrivateKeyMethod.SSL_SIGN_RSA_PSS_RSAE_SHA256) {
         configurePssParameters(signature, MGF1ParameterSpec.SHA256, 32);
      } else if (opensslAlgorithm == OpenSslAsyncPrivateKeyMethod.SSL_SIGN_RSA_PSS_RSAE_SHA384) {
         configurePssParameters(signature, MGF1ParameterSpec.SHA384, 48);
      } else if (opensslAlgorithm == OpenSslAsyncPrivateKeyMethod.SSL_SIGN_RSA_PSS_RSAE_SHA512) {
         configurePssParameters(signature, MGF1ParameterSpec.SHA512, 64);
      } else if (SSL_TO_JDK_SIGNATURE_ALGORITHM.containsKey(opensslAlgorithm)) {
         if (logger.isTraceEnabled()) {
            logger.trace("No parameter configuration needed for OpenSSL algorithm {}", opensslAlgorithm);
         }
      } else if (logger.isDebugEnabled()) {
         logger.debug("Unknown OpenSSL algorithm {}, using default configuration", opensslAlgorithm);
      }
   }

   private static void configurePssParameters(Signature signature, MGF1ParameterSpec mgfSpec, int saltLength) throws InvalidAlgorithmParameterException {
      PSSParameterSpec pssSpec = new PSSParameterSpec(mgfSpec.getDigestAlgorithm(), "MGF1", mgfSpec, saltLength, 1);
      signature.setParameter(pssSpec);
      if (logger.isDebugEnabled()) {
         logger.debug("Configured PSS parameters: hash={}, saltLength={}", mgfSpec.getDigestAlgorithm(), saltLength);
      }
   }

   static {
      IntObjectMap<String> algorithmMap = new IntObjectHashMap<>();
      algorithmMap.put(OpenSslAsyncPrivateKeyMethod.SSL_SIGN_RSA_PKCS1_SHA1, "SHA1withRSA");
      algorithmMap.put(OpenSslAsyncPrivateKeyMethod.SSL_SIGN_RSA_PKCS1_SHA256, "SHA256withRSA");
      algorithmMap.put(OpenSslAsyncPrivateKeyMethod.SSL_SIGN_RSA_PKCS1_SHA384, "SHA384withRSA");
      algorithmMap.put(OpenSslAsyncPrivateKeyMethod.SSL_SIGN_RSA_PKCS1_SHA512, "SHA512withRSA");
      algorithmMap.put(OpenSslAsyncPrivateKeyMethod.SSL_SIGN_RSA_PKCS1_MD5_SHA1, "MD5andSHA1withRSA");
      algorithmMap.put(OpenSslAsyncPrivateKeyMethod.SSL_SIGN_ECDSA_SHA1, "SHA1withECDSA");
      algorithmMap.put(OpenSslAsyncPrivateKeyMethod.SSL_SIGN_ECDSA_SECP256R1_SHA256, "SHA256withECDSA");
      algorithmMap.put(OpenSslAsyncPrivateKeyMethod.SSL_SIGN_ECDSA_SECP384R1_SHA384, "SHA384withECDSA");
      algorithmMap.put(OpenSslAsyncPrivateKeyMethod.SSL_SIGN_ECDSA_SECP521R1_SHA512, "SHA512withECDSA");
      algorithmMap.put(OpenSslAsyncPrivateKeyMethod.SSL_SIGN_RSA_PSS_RSAE_SHA256, "RSASSA-PSS");
      algorithmMap.put(OpenSslAsyncPrivateKeyMethod.SSL_SIGN_RSA_PSS_RSAE_SHA384, "RSASSA-PSS");
      algorithmMap.put(OpenSslAsyncPrivateKeyMethod.SSL_SIGN_RSA_PSS_RSAE_SHA512, "RSASSA-PSS");
      algorithmMap.put(OpenSslAsyncPrivateKeyMethod.SSL_SIGN_ED25519, "EdDSA");
      SSL_TO_JDK_SIGNATURE_ALGORITHM = IntCollections.unmodifiableMap(algorithmMap);
   }

   private static final class CacheKey {
      private final String jdkAlgorithm;
      private final String keyTypeName;
      private final int hashCode;

      @Override
      public boolean equals(Object o) {
         if (o != null && this.getClass() == o.getClass()) {
            JdkDelegatingPrivateKeyMethod.CacheKey cacheKey = (JdkDelegatingPrivateKeyMethod.CacheKey)o;
            return Objects.equals(cacheKey.jdkAlgorithm, this.jdkAlgorithm) && Objects.equals(cacheKey.keyTypeName, this.keyTypeName);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return this.hashCode;
      }

      CacheKey(String jdkAlgorithm, String keyTypeName) {
         this.jdkAlgorithm = jdkAlgorithm;
         this.keyTypeName = keyTypeName;
         this.hashCode = 31 * jdkAlgorithm.hashCode() + keyTypeName.hashCode();
      }
   }
}
