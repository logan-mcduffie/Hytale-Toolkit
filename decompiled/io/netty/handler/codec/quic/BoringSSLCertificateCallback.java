package io.netty.handler.codec.quic;

import io.netty.util.CharsetUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.security.auth.x500.X500Principal;
import org.jetbrains.annotations.Nullable;

final class BoringSSLCertificateCallback {
   private static final byte[] BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\n".getBytes(CharsetUtil.US_ASCII);
   private static final byte[] END_PRIVATE_KEY = "\n-----END PRIVATE KEY-----\n".getBytes(CharsetUtil.US_ASCII);
   private static final byte TLS_CT_RSA_SIGN = 1;
   private static final byte TLS_CT_DSS_SIGN = 2;
   private static final byte TLS_CT_RSA_FIXED_DH = 3;
   private static final byte TLS_CT_DSS_FIXED_DH = 4;
   private static final byte TLS_CT_ECDSA_SIGN = 64;
   private static final byte TLS_CT_RSA_FIXED_ECDH = 65;
   private static final byte TLS_CT_ECDSA_FIXED_ECDH = 66;
   static final String KEY_TYPE_RSA = "RSA";
   static final String KEY_TYPE_DH_RSA = "DH_RSA";
   static final String KEY_TYPE_EC = "EC";
   static final String KEY_TYPE_EC_EC = "EC_EC";
   static final String KEY_TYPE_EC_RSA = "EC_RSA";
   private static final Map<String, String> DEFAULT_SERVER_KEY_TYPES = new HashMap<>();
   private static final Set<String> DEFAULT_CLIENT_KEY_TYPES = Collections.unmodifiableSet(
      new LinkedHashSet<>(Arrays.asList("RSA", "DH_RSA", "EC", "EC_RSA", "EC_EC"))
   );
   private static final long[] NO_KEY_MATERIAL_CLIENT_SIDE = new long[]{0L, 0L};
   private final QuicheQuicSslEngineMap engineMap;
   private final X509ExtendedKeyManager keyManager;
   private final String password;
   private final Map<String, String> serverKeyTypes;
   private final Set<String> clientKeyTypes;

   BoringSSLCertificateCallback(
      QuicheQuicSslEngineMap engineMap,
      @Nullable X509ExtendedKeyManager keyManager,
      String password,
      Map<String, String> serverKeyTypes,
      Set<String> clientKeyTypes
   ) {
      this.engineMap = engineMap;
      this.keyManager = keyManager;
      this.password = password;
      this.serverKeyTypes = serverKeyTypes != null ? serverKeyTypes : DEFAULT_SERVER_KEY_TYPES;
      this.clientKeyTypes = clientKeyTypes != null ? clientKeyTypes : DEFAULT_CLIENT_KEY_TYPES;
   }

   long @Nullable [] handle(long ssl, byte[] keyTypeBytes, byte @Nullable [][] asn1DerEncodedPrincipals, String[] authMethods) {
      QuicheQuicSslEngine engine = this.engineMap.get(ssl);
      if (engine == null) {
         return null;
      } else {
         try {
            if (this.keyManager == null) {
               return engine.getUseClientMode() ? NO_KEY_MATERIAL_CLIENT_SIDE : null;
            } else if (!engine.getUseClientMode()) {
               return this.removeMappingIfNeeded(ssl, this.selectKeyMaterialServerSide(ssl, engine, authMethods));
            } else {
               Set<String> keyTypesSet = this.supportedClientKeyTypes(keyTypeBytes);
               String[] keyTypes = keyTypesSet.toArray(new String[0]);
               X500Principal[] issuers;
               if (asn1DerEncodedPrincipals == null) {
                  issuers = null;
               } else {
                  issuers = new X500Principal[asn1DerEncodedPrincipals.length];

                  for (int i = 0; i < asn1DerEncodedPrincipals.length; i++) {
                     issuers[i] = new X500Principal(asn1DerEncodedPrincipals[i]);
                  }
               }

               return this.removeMappingIfNeeded(ssl, this.selectKeyMaterialClientSide(ssl, engine, keyTypes, issuers));
            }
         } catch (SSLException var11) {
            this.engineMap.remove(ssl);
            return null;
         } catch (Throwable var12) {
            this.engineMap.remove(ssl);
            throw var12;
         }
      }
   }

   private long @Nullable [] removeMappingIfNeeded(long ssl, long @Nullable [] result) {
      if (result == null) {
         this.engineMap.remove(ssl);
      }

      return result;
   }

   private long @Nullable [] selectKeyMaterialServerSide(long ssl, QuicheQuicSslEngine engine, String[] authMethods) throws SSLException {
      if (authMethods.length == 0) {
         throw new SSLHandshakeException("Unable to find key material");
      } else {
         Set<String> typeSet = new HashSet<>(this.serverKeyTypes.size());

         for (String authMethod : authMethods) {
            String type = this.serverKeyTypes.get(authMethod);
            if (type != null && typeSet.add(type)) {
               String alias = this.chooseServerAlias(engine, type);
               if (alias != null) {
                  return this.selectMaterial(ssl, engine, alias);
               }
            }
         }

         throw new SSLHandshakeException("Unable to find key material for auth method(s): " + Arrays.toString((Object[])authMethods));
      }
   }

   private long @Nullable [] selectKeyMaterialClientSide(long ssl, QuicheQuicSslEngine engine, String[] keyTypes, X500Principal @Nullable [] issuer) {
      String alias = this.chooseClientAlias(engine, keyTypes, issuer);
      return alias != null ? this.selectMaterial(ssl, engine, alias) : NO_KEY_MATERIAL_CLIENT_SIDE;
   }

   private long @Nullable [] selectMaterial(long ssl, QuicheQuicSslEngine engine, String alias) {
      X509Certificate[] certificates = this.keyManager.getCertificateChain(alias);
      if (certificates != null && certificates.length != 0) {
         byte[][] certs = new byte[certificates.length][];

         for (int i = 0; i < certificates.length; i++) {
            try {
               certs[i] = certificates[i].getEncoded();
            } catch (CertificateEncodingException var12) {
               return null;
            }
         }

         PrivateKey privateKey = this.keyManager.getPrivateKey(alias);
         long key;
         if (privateKey == BoringSSLKeylessPrivateKey.INSTANCE) {
            key = 0L;
         } else {
            byte[] pemKey = toPemEncoded(privateKey);
            if (pemKey == null) {
               return null;
            }

            key = BoringSSL.EVP_PKEY_parse(pemKey, this.password);
         }

         long chain = BoringSSL.CRYPTO_BUFFER_stack_new(ssl, certs);
         engine.setLocalCertificateChain(certificates);
         return new long[]{key, chain};
      } else {
         return null;
      }
   }

   private static byte @Nullable [] toPemEncoded(PrivateKey key) {
      try {
         ByteArrayOutputStream out = new ByteArrayOutputStream();

         byte[] var2;
         try {
            out.write(BEGIN_PRIVATE_KEY);
            out.write(Base64.getEncoder().encode(key.getEncoded()));
            out.write(END_PRIVATE_KEY);
            var2 = out.toByteArray();
         } catch (Throwable var5) {
            try {
               out.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }

            throw var5;
         }

         out.close();
         return var2;
      } catch (IOException var6) {
         return null;
      }
   }

   @Nullable
   private String chooseClientAlias(QuicheQuicSslEngine engine, String[] keyTypes, X500Principal @Nullable [] issuer) {
      return this.keyManager.chooseEngineClientAlias(keyTypes, issuer, engine);
   }

   @Nullable
   private String chooseServerAlias(QuicheQuicSslEngine engine, String type) {
      return this.keyManager.chooseEngineServerAlias(type, null, engine);
   }

   private Set<String> supportedClientKeyTypes(byte @Nullable [] clientCertificateTypes) {
      if (clientCertificateTypes == null) {
         return this.clientKeyTypes;
      } else {
         Set<String> result = new HashSet<>(clientCertificateTypes.length);

         for (byte keyTypeCode : clientCertificateTypes) {
            String keyType = clientKeyType(keyTypeCode);
            if (keyType != null) {
               result.add(keyType);
            }
         }

         return result;
      }
   }

   @Nullable
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

   static {
      DEFAULT_SERVER_KEY_TYPES.put("RSA", "RSA");
      DEFAULT_SERVER_KEY_TYPES.put("DHE_RSA", "RSA");
      DEFAULT_SERVER_KEY_TYPES.put("ECDHE_RSA", "RSA");
      DEFAULT_SERVER_KEY_TYPES.put("ECDHE_ECDSA", "EC");
      DEFAULT_SERVER_KEY_TYPES.put("ECDH_RSA", "EC_RSA");
      DEFAULT_SERVER_KEY_TYPES.put("ECDH_ECDSA", "EC_EC");
      DEFAULT_SERVER_KEY_TYPES.put("DH_RSA", "DH_RSA");
   }
}
