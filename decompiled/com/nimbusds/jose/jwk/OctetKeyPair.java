package com.nimbusds.jose.jwk;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.shaded.jcip.Immutable;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.ByteUtils;
import com.nimbusds.jose.util.JSONObjectUtils;
import java.net.URI;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Immutable
public class OctetKeyPair extends JWK implements AsymmetricJWK, CurveBasedJWK {
   private static final long serialVersionUID = 1L;
   public static final Set<Curve> SUPPORTED_CURVES = Collections.unmodifiableSet(
      new HashSet<>(Arrays.asList(Curve.Ed25519, Curve.Ed448, Curve.X25519, Curve.X448))
   );
   private final Curve crv;
   private final Base64URL x;
   private final byte[] decodedX;
   private final Base64URL d;
   private final byte[] decodedD;

   @Deprecated
   public OctetKeyPair(
      Curve crv,
      Base64URL x,
      KeyUse use,
      Set<KeyOperation> ops,
      Algorithm alg,
      String kid,
      URI x5u,
      Base64URL x5t,
      Base64URL x5t256,
      List<Base64> x5c,
      KeyStore ks
   ) {
      this(crv, x, use, ops, alg, kid, x5u, x5t, x5t256, x5c, null, null, null, ks);
   }

   @Deprecated
   public OctetKeyPair(
      Curve crv,
      Base64URL x,
      Base64URL d,
      KeyUse use,
      Set<KeyOperation> ops,
      Algorithm alg,
      String kid,
      URI x5u,
      Base64URL x5t,
      Base64URL x5t256,
      List<Base64> x5c,
      KeyStore ks
   ) {
      this(crv, x, d, use, ops, alg, kid, x5u, x5t, x5t256, x5c, null, null, null, ks);
   }

   @Deprecated
   public OctetKeyPair(
      Curve crv,
      Base64URL x,
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
      this(crv, x, use, ops, alg, kid, x5u, x5t, x5t256, x5c, exp, nbf, iat, null, ks);
   }

   public OctetKeyPair(
      Curve crv,
      Base64URL x,
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
      super(KeyType.OKP, use, ops, alg, kid, x5u, x5t, x5t256, x5c, exp, nbf, iat, revocation, ks);
      Objects.requireNonNull(crv, "The curve must not be null");
      if (!SUPPORTED_CURVES.contains(crv)) {
         throw new IllegalArgumentException("Unknown / unsupported curve: " + crv);
      } else {
         this.crv = crv;
         this.x = Objects.requireNonNull(x, "The x parameter must not be null");
         this.decodedX = x.decode();
         this.d = null;
         this.decodedD = null;
      }
   }

   @Deprecated
   public OctetKeyPair(
      Curve crv,
      Base64URL x,
      Base64URL d,
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
      this(crv, x, d, use, ops, alg, kid, x5u, x5t, x5t256, x5c, exp, nbf, iat, null, ks);
   }

   public OctetKeyPair(
      Curve crv,
      Base64URL x,
      Base64URL d,
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
      super(KeyType.OKP, use, ops, alg, kid, x5u, x5t, x5t256, x5c, exp, nbf, iat, revocation, ks);
      Objects.requireNonNull(crv, "The curve must not be null");
      if (!SUPPORTED_CURVES.contains(crv)) {
         throw new IllegalArgumentException("Unknown / unsupported curve: " + crv);
      } else {
         this.crv = crv;
         this.x = Objects.requireNonNull(x, "The x parameter must not be null");
         this.decodedX = x.decode();
         this.d = Objects.requireNonNull(d, "The d parameter must not be null");
         this.decodedD = d.decode();
      }
   }

   @Override
   public Curve getCurve() {
      return this.crv;
   }

   public Base64URL getX() {
      return this.x;
   }

   public byte[] getDecodedX() {
      return (byte[])this.decodedX.clone();
   }

   public Base64URL getD() {
      return this.d;
   }

   public byte[] getDecodedD() {
      return this.decodedD == null ? null : (byte[])this.decodedD.clone();
   }

   @Override
   public PublicKey toPublicKey() throws JOSEException {
      throw new JOSEException("Export to java.security.PublicKey not supported");
   }

   @Override
   public PrivateKey toPrivateKey() throws JOSEException {
      throw new JOSEException("Export to java.security.PrivateKey not supported");
   }

   @Override
   public KeyPair toKeyPair() throws JOSEException {
      throw new JOSEException("Export to java.security.KeyPair not supported");
   }

   @Override
   public boolean matches(X509Certificate cert) {
      return false;
   }

   @Override
   public LinkedHashMap<String, ?> getRequiredParams() {
      LinkedHashMap<String, String> requiredParams = new LinkedHashMap<>();
      requiredParams.put("crv", this.crv.toString());
      requiredParams.put("kty", this.getKeyType().getValue());
      requiredParams.put("x", this.x.toString());
      return requiredParams;
   }

   @Override
   public boolean isPrivate() {
      return this.d != null;
   }

   public OctetKeyPair toPublicJWK() {
      return new OctetKeyPair(
         this.getCurve(),
         this.getX(),
         this.getKeyUse(),
         this.getKeyOperations(),
         this.getAlgorithm(),
         this.getKeyID(),
         this.getX509CertURL(),
         this.getX509CertThumbprint(),
         this.getX509CertSHA256Thumbprint(),
         this.getX509CertChain(),
         this.getExpirationTime(),
         this.getNotBeforeTime(),
         this.getIssueTime(),
         this.getKeyRevocation(),
         this.getKeyStore()
      );
   }

   public OctetKeyPair toRevokedJWK(KeyRevocation keyRevocation) {
      if (this.getKeyRevocation() != null) {
         throw new IllegalStateException("Already revoked");
      } else {
         return new OctetKeyPair.Builder(this).keyRevocation(Objects.requireNonNull(keyRevocation)).build();
      }
   }

   @Override
   public Map<String, Object> toJSONObject() {
      Map<String, Object> o = super.toJSONObject();
      o.put("crv", this.crv.toString());
      o.put("x", this.x.toString());
      if (this.d != null) {
         o.put("d", this.d.toString());
      }

      return o;
   }

   @Override
   public int size() {
      return ByteUtils.bitLength(this.x.decode());
   }

   public static OctetKeyPair parse(String s) throws ParseException {
      return parse(JSONObjectUtils.parse(s));
   }

   public static OctetKeyPair parse(Map<String, Object> jsonObject) throws ParseException {
      if (!KeyType.OKP.equals(JWKMetadata.parseKeyType(jsonObject))) {
         throw new ParseException("The key type kty must be " + KeyType.OKP.getValue(), 0);
      } else {
         Curve crv;
         try {
            crv = Curve.parse(JSONObjectUtils.getString(jsonObject, "crv"));
         } catch (IllegalArgumentException var6) {
            throw new ParseException(var6.getMessage(), 0);
         }

         Base64URL x = JSONObjectUtils.getBase64URL(jsonObject, "x");
         Base64URL d = JSONObjectUtils.getBase64URL(jsonObject, "d");

         try {
            return d == null
               ? new OctetKeyPair(
                  crv,
                  x,
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
               )
               : new OctetKeyPair(
                  crv,
                  x,
                  d,
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
         } catch (Exception var5) {
            throw new ParseException(var5.getMessage(), 0);
         }
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof OctetKeyPair)) {
         return false;
      } else if (!super.equals(o)) {
         return false;
      } else {
         OctetKeyPair that = (OctetKeyPair)o;
         return Objects.equals(this.crv, that.crv)
            && Objects.equals(this.x, that.x)
            && Arrays.equals(this.decodedX, that.decodedX)
            && Objects.equals(this.d, that.d)
            && Arrays.equals(this.decodedD, that.decodedD);
      }
   }

   @Override
   public int hashCode() {
      int result = Objects.hash(super.hashCode(), this.crv, this.x, this.d);
      result = 31 * result + Arrays.hashCode(this.decodedX);
      return 31 * result + Arrays.hashCode(this.decodedD);
   }

   public static class Builder {
      private final Curve crv;
      private final Base64URL x;
      private Base64URL d;
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

      public Builder(Curve crv, Base64URL x) {
         this.crv = Objects.requireNonNull(crv, "The curve must not be null");
         this.x = Objects.requireNonNull(x, "The x coordinate must not be null");
      }

      public Builder(OctetKeyPair okpJWK) {
         this.crv = okpJWK.crv;
         this.x = okpJWK.x;
         this.d = okpJWK.d;
         this.use = okpJWK.getKeyUse();
         this.ops = okpJWK.getKeyOperations();
         this.alg = okpJWK.getAlgorithm();
         this.kid = okpJWK.getKeyID();
         this.x5u = okpJWK.getX509CertURL();
         this.x5t = okpJWK.getX509CertThumbprint();
         this.x5t256 = okpJWK.getX509CertSHA256Thumbprint();
         this.x5c = okpJWK.getX509CertChain();
         this.exp = okpJWK.getExpirationTime();
         this.nbf = okpJWK.getNotBeforeTime();
         this.iat = okpJWK.getIssueTime();
         this.revocation = okpJWK.getKeyRevocation();
         this.ks = okpJWK.getKeyStore();
      }

      public OctetKeyPair.Builder d(Base64URL d) {
         this.d = d;
         return this;
      }

      public OctetKeyPair.Builder keyUse(KeyUse use) {
         this.use = use;
         return this;
      }

      public OctetKeyPair.Builder keyOperations(Set<KeyOperation> ops) {
         this.ops = ops;
         return this;
      }

      public OctetKeyPair.Builder algorithm(Algorithm alg) {
         this.alg = alg;
         return this;
      }

      public OctetKeyPair.Builder keyID(String kid) {
         this.kid = kid;
         return this;
      }

      public OctetKeyPair.Builder keyIDFromThumbprint() throws JOSEException {
         return this.keyIDFromThumbprint("SHA-256");
      }

      public OctetKeyPair.Builder keyIDFromThumbprint(String hashAlg) throws JOSEException {
         LinkedHashMap<String, String> requiredParams = new LinkedHashMap<>();
         requiredParams.put("crv", this.crv.toString());
         requiredParams.put("kty", KeyType.OKP.getValue());
         requiredParams.put("x", this.x.toString());
         this.kid = ThumbprintUtils.compute(hashAlg, requiredParams).toString();
         return this;
      }

      public OctetKeyPair.Builder x509CertURL(URI x5u) {
         this.x5u = x5u;
         return this;
      }

      @Deprecated
      public OctetKeyPair.Builder x509CertThumbprint(Base64URL x5t) {
         this.x5t = x5t;
         return this;
      }

      public OctetKeyPair.Builder x509CertSHA256Thumbprint(Base64URL x5t256) {
         this.x5t256 = x5t256;
         return this;
      }

      public OctetKeyPair.Builder x509CertChain(List<Base64> x5c) {
         this.x5c = x5c;
         return this;
      }

      public OctetKeyPair.Builder expirationTime(Date exp) {
         this.exp = exp;
         return this;
      }

      public OctetKeyPair.Builder notBeforeTime(Date nbf) {
         this.nbf = nbf;
         return this;
      }

      public OctetKeyPair.Builder issueTime(Date iat) {
         this.iat = iat;
         return this;
      }

      public OctetKeyPair.Builder keyRevocation(KeyRevocation revocation) {
         this.revocation = revocation;
         return this;
      }

      public OctetKeyPair.Builder keyStore(KeyStore keyStore) {
         this.ks = keyStore;
         return this;
      }

      public OctetKeyPair build() {
         try {
            return this.d == null
               ? new OctetKeyPair(
                  this.crv,
                  this.x,
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
               )
               : new OctetKeyPair(
                  this.crv,
                  this.x,
                  this.d,
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
