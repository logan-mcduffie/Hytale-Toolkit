package com.nimbusds.jose.jwk;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.shaded.jcip.Immutable;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.ByteUtils;
import com.nimbusds.jose.util.IntegerOverflowException;
import com.nimbusds.jose.util.JSONObjectUtils;
import java.net.URI;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Immutable
public final class OctetSequenceKey extends JWK implements SecretJWK {
   private static final long serialVersionUID = 1L;
   private final Base64URL k;

   @Deprecated
   public OctetSequenceKey(
      Base64URL k, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, KeyStore ks
   ) {
      this(k, use, ops, alg, kid, x5u, x5t, x5t256, x5c, null, null, null, ks);
   }

   @Deprecated
   public OctetSequenceKey(
      Base64URL k,
      KeyUse use,
      Set<KeyOperation> ops,
      Algorithm alg,
      String kid,
      URI x5u,
      Base64URL x5t,
      Base64URL x5t256,
      List<Base64> x5c,
      Date exp,
      Date nbf,
      Date iat,
      KeyStore ks
   ) {
      this(k, use, ops, alg, kid, x5u, x5t, x5t256, x5c, exp, nbf, iat, null, ks);
   }

   public OctetSequenceKey(
      Base64URL k,
      KeyUse use,
      Set<KeyOperation> ops,
      Algorithm alg,
      String kid,
      URI x5u,
      Base64URL x5t,
      Base64URL x5t256,
      List<Base64> x5c,
      Date exp,
      Date nbf,
      Date iat,
      KeyRevocation revocation,
      KeyStore ks
   ) {
      super(KeyType.OCT, use, ops, alg, kid, x5u, x5t, x5t256, x5c, exp, nbf, iat, revocation, ks);
      this.k = Objects.requireNonNull(k, "The key value must not be null");
   }

   public Base64URL getKeyValue() {
      return this.k;
   }

   public byte[] toByteArray() {
      return this.getKeyValue().decode();
   }

   @Override
   public SecretKey toSecretKey() {
      return this.toSecretKey("NONE");
   }

   public SecretKey toSecretKey(String jcaAlg) {
      return new SecretKeySpec(this.toByteArray(), jcaAlg);
   }

   @Override
   public LinkedHashMap<String, ?> getRequiredParams() {
      LinkedHashMap<String, String> requiredParams = new LinkedHashMap<>();
      requiredParams.put("k", this.k.toString());
      requiredParams.put("kty", this.getKeyType().toString());
      return requiredParams;
   }

   @Override
   public boolean isPrivate() {
      return true;
   }

   public OctetSequenceKey toPublicJWK() {
      return null;
   }

   public OctetSequenceKey toRevokedJWK(KeyRevocation keyRevocation) {
      if (this.getKeyRevocation() != null) {
         throw new IllegalStateException("Already revoked");
      } else {
         return new OctetSequenceKey.Builder(this).keyRevocation(Objects.requireNonNull(keyRevocation)).build();
      }
   }

   @Override
   public int size() {
      try {
         return ByteUtils.safeBitLength(this.k.decode());
      } catch (IntegerOverflowException var2) {
         throw new ArithmeticException(var2.getMessage());
      }
   }

   @Override
   public Map<String, Object> toJSONObject() {
      Map<String, Object> o = super.toJSONObject();
      o.put("k", this.k.toString());
      return o;
   }

   public static OctetSequenceKey parse(String s) throws ParseException {
      return parse(JSONObjectUtils.parse(s));
   }

   public static OctetSequenceKey parse(Map<String, Object> jsonObject) throws ParseException {
      if (!KeyType.OCT.equals(JWKMetadata.parseKeyType(jsonObject))) {
         throw new ParseException("The key type kty must be " + KeyType.OCT.getValue(), 0);
      } else {
         Base64URL k = JSONObjectUtils.getBase64URL(jsonObject, "k");

         try {
            return new OctetSequenceKey(
               k,
               JWKMetadata.parseKeyUse(jsonObject),
               JWKMetadata.parseKeyOperations(jsonObject),
               JWKMetadata.parseAlgorithm(jsonObject),
               JWKMetadata.parseKeyID(jsonObject),
               JWKMetadata.parseX509CertURL(jsonObject),
               JWKMetadata.parseX509CertThumbprint(jsonObject),
               JWKMetadata.parseX509CertSHA256Thumbprint(jsonObject),
               JWKMetadata.parseX509CertChain(jsonObject),
               JWKMetadata.parseExpirationTime(jsonObject),
               JWKMetadata.parseNotBeforeTime(jsonObject),
               JWKMetadata.parseIssueTime(jsonObject),
               JWKMetadata.parseKeyRevocation(jsonObject),
               null
            );
         } catch (Exception var3) {
            throw new ParseException(var3.getMessage(), 0);
         }
      }
   }

   public static OctetSequenceKey load(KeyStore keyStore, String alias, char[] pin) throws KeyStoreException, JOSEException {
      Key key;
      try {
         key = keyStore.getKey(alias, pin);
      } catch (NoSuchAlgorithmException | UnrecoverableKeyException var5) {
         throw new JOSEException("Couldn't retrieve secret key (bad pin?): " + var5.getMessage(), var5);
      }

      return !(key instanceof SecretKey) ? null : new OctetSequenceKey.Builder((SecretKey)key).keyID(alias).keyStore(keyStore).build();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof OctetSequenceKey)) {
         return false;
      } else if (!super.equals(o)) {
         return false;
      } else {
         OctetSequenceKey that = (OctetSequenceKey)o;
         return Objects.equals(this.k, that.k);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(super.hashCode(), this.k);
   }

   public static class Builder {
      private final Base64URL k;
      private KeyUse use;
      private Set<KeyOperation> ops;
      private Algorithm alg;
      private String kid;
      private URI x5u;
      @Deprecated
      private Base64URL x5t;
      private Base64URL x5t256;
      private List<Base64> x5c;
      private Date exp;
      private Date nbf;
      private Date iat;
      private KeyRevocation revocation;
      private KeyStore ks;

      public Builder(Base64URL k) {
         this.k = Objects.requireNonNull(k);
      }

      public Builder(byte[] key) {
         this(Base64URL.encode(key));
         if (key.length == 0) {
            throw new IllegalArgumentException("The key must have a positive length");
         }
      }

      public Builder(SecretKey secretKey) {
         this(secretKey.getEncoded());
      }

      public Builder(OctetSequenceKey octJWK) {
         this.k = octJWK.k;
         this.use = octJWK.getKeyUse();
         this.ops = octJWK.getKeyOperations();
         this.alg = octJWK.getAlgorithm();
         this.kid = octJWK.getKeyID();
         this.x5u = octJWK.getX509CertURL();
         this.x5t = octJWK.getX509CertThumbprint();
         this.x5t256 = octJWK.getX509CertSHA256Thumbprint();
         this.x5c = octJWK.getX509CertChain();
         this.exp = octJWK.getExpirationTime();
         this.nbf = octJWK.getNotBeforeTime();
         this.iat = octJWK.getIssueTime();
         this.revocation = octJWK.getKeyRevocation();
         this.ks = octJWK.getKeyStore();
      }

      public OctetSequenceKey.Builder keyUse(KeyUse use) {
         this.use = use;
         return this;
      }

      public OctetSequenceKey.Builder keyOperations(Set<KeyOperation> ops) {
         this.ops = ops;
         return this;
      }

      public OctetSequenceKey.Builder algorithm(Algorithm alg) {
         this.alg = alg;
         return this;
      }

      public OctetSequenceKey.Builder keyID(String kid) {
         this.kid = kid;
         return this;
      }

      public OctetSequenceKey.Builder keyIDFromThumbprint() throws JOSEException {
         return this.keyIDFromThumbprint("SHA-256");
      }

      public OctetSequenceKey.Builder keyIDFromThumbprint(String hashAlg) throws JOSEException {
         LinkedHashMap<String, String> requiredParams = new LinkedHashMap<>();
         requiredParams.put("k", this.k.toString());
         requiredParams.put("kty", KeyType.OCT.getValue());
         this.kid = ThumbprintUtils.compute(hashAlg, requiredParams).toString();
         return this;
      }

      public OctetSequenceKey.Builder x509CertURL(URI x5u) {
         this.x5u = x5u;
         return this;
      }

      @Deprecated
      public OctetSequenceKey.Builder x509CertThumbprint(Base64URL x5t) {
         this.x5t = x5t;
         return this;
      }

      public OctetSequenceKey.Builder x509CertSHA256Thumbprint(Base64URL x5t256) {
         this.x5t256 = x5t256;
         return this;
      }

      public OctetSequenceKey.Builder x509CertChain(List<Base64> x5c) {
         this.x5c = x5c;
         return this;
      }

      public OctetSequenceKey.Builder expirationTime(Date exp) {
         this.exp = exp;
         return this;
      }

      public OctetSequenceKey.Builder notBeforeTime(Date nbf) {
         this.nbf = nbf;
         return this;
      }

      public OctetSequenceKey.Builder issueTime(Date iat) {
         this.iat = iat;
         return this;
      }

      public OctetSequenceKey.Builder keyRevocation(KeyRevocation revocation) {
         this.revocation = revocation;
         return this;
      }

      public OctetSequenceKey.Builder keyStore(KeyStore keyStore) {
         this.ks = keyStore;
         return this;
      }

      public OctetSequenceKey build() {
         try {
            return new OctetSequenceKey(
               this.k,
               this.use,
               this.ops,
               this.alg,
               this.kid,
               this.x5u,
               this.x5t,
               this.x5t256,
               this.x5c,
               this.exp,
               this.nbf,
               this.iat,
               this.revocation,
               this.ks
            );
         } catch (IllegalArgumentException var2) {
            throw new IllegalStateException(var2.getMessage(), var2);
         }
      }
   }
}
