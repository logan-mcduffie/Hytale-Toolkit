package com.nimbusds.jose.jwk;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONArrayUtils;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jose.util.X509CertChainUtils;
import com.nimbusds.jose.util.X509CertUtils;
import com.nimbusds.jwt.util.DateUtils;
import java.io.Serializable;
import java.net.URI;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.ECParameterSpec;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class JWK implements Serializable {
   private static final long serialVersionUID = 1L;
   public static final String MIME_TYPE = "application/jwk+json; charset=UTF-8";
   private final KeyType kty;
   private final KeyUse use;
   private final Set<KeyOperation> ops;
   private final Algorithm alg;
   private final String kid;
   private final URI x5u;
   @Deprecated
   private final Base64URL x5t;
   private final Base64URL x5t256;
   private final List<Base64> x5c;
   private final Date exp;
   private final Date nbf;
   private final Date iat;
   private final KeyRevocation revocation;
   private final List<X509Certificate> parsedX5c;
   private final KeyStore keyStore;

   @Deprecated
   protected JWK(
      KeyType kty, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, KeyStore ks
   ) {
      this(kty, use, ops, alg, kid, x5u, x5t, x5t256, x5c, null, null, null, ks);
   }

   @Deprecated
   protected JWK(
      KeyType kty,
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
      this(kty, use, ops, alg, kid, x5u, x5t, x5t256, x5c, exp, nbf, iat, null, ks);
   }

   protected JWK(
      KeyType kty,
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
      this.kty = Objects.requireNonNull(kty, "The key type \"kty\" parameter must not be null");
      if (!KeyUseAndOpsConsistency.areConsistent(use, ops)) {
         throw new IllegalArgumentException("The key use \"use\" and key options \"key_ops\" parameters are not consistent, see RFC 7517, section 4.3");
      } else {
         this.use = use;
         this.ops = ops;
         this.alg = alg;
         this.kid = kid;
         this.x5u = x5u;
         this.x5t = x5t;
         this.x5t256 = x5t256;
         if (x5c != null && x5c.isEmpty()) {
            throw new IllegalArgumentException("The X.509 certificate chain \"x5c\" must not be empty");
         } else {
            this.x5c = x5c;

            try {
               this.parsedX5c = X509CertChainUtils.parse(x5c);
            } catch (ParseException var16) {
               throw new IllegalArgumentException("Invalid X.509 certificate chain \"x5c\": " + var16.getMessage(), var16);
            }

            this.exp = exp;
            this.nbf = nbf;
            this.iat = iat;
            this.revocation = revocation;
            this.keyStore = ks;
         }
      }
   }

   public KeyType getKeyType() {
      return this.kty;
   }

   public KeyUse getKeyUse() {
      return this.use;
   }

   public Set<KeyOperation> getKeyOperations() {
      return this.ops;
   }

   public Algorithm getAlgorithm() {
      return this.alg;
   }

   public String getKeyID() {
      return this.kid;
   }

   public URI getX509CertURL() {
      return this.x5u;
   }

   @Deprecated
   public Base64URL getX509CertThumbprint() {
      return this.x5t;
   }

   public Base64URL getX509CertSHA256Thumbprint() {
      return this.x5t256;
   }

   public List<Base64> getX509CertChain() {
      return this.x5c == null ? null : Collections.unmodifiableList(this.x5c);
   }

   public List<X509Certificate> getParsedX509CertChain() {
      return this.parsedX5c == null ? null : Collections.unmodifiableList(this.parsedX5c);
   }

   public Date getExpirationTime() {
      return this.exp;
   }

   public Date getNotBeforeTime() {
      return this.nbf;
   }

   public Date getIssueTime() {
      return this.iat;
   }

   public KeyRevocation getKeyRevocation() {
      return this.revocation;
   }

   public KeyStore getKeyStore() {
      return this.keyStore;
   }

   public abstract LinkedHashMap<String, ?> getRequiredParams();

   public Base64URL computeThumbprint() throws JOSEException {
      return this.computeThumbprint("SHA-256");
   }

   public Base64URL computeThumbprint(String hashAlg) throws JOSEException {
      return ThumbprintUtils.compute(hashAlg, this);
   }

   public ThumbprintURI computeThumbprintURI() throws JOSEException {
      return new ThumbprintURI("sha-256", this.computeThumbprint("SHA-256"));
   }

   public abstract boolean isPrivate();

   public abstract JWK toPublicJWK();

   public abstract JWK toRevokedJWK(KeyRevocation var1);

   public abstract int size();

   public RSAKey toRSAKey() {
      return (RSAKey)this;
   }

   public ECKey toECKey() {
      return (ECKey)this;
   }

   public OctetSequenceKey toOctetSequenceKey() {
      return (OctetSequenceKey)this;
   }

   public OctetKeyPair toOctetKeyPair() {
      return (OctetKeyPair)this;
   }

   public Map<String, Object> toJSONObject() {
      Map<String, Object> o = JSONObjectUtils.newJSONObject();
      o.put("kty", this.kty.getValue());
      if (this.use != null) {
         o.put("use", this.use.identifier());
      }

      if (this.ops != null) {
         List<Object> stringValues = JSONArrayUtils.newJSONArray();

         for (KeyOperation op : this.ops) {
            stringValues.add(op.identifier());
         }

         o.put("key_ops", stringValues);
      }

      if (this.alg != null) {
         o.put("alg", this.alg.getName());
      }

      if (this.kid != null) {
         o.put("kid", this.kid);
      }

      if (this.x5u != null) {
         o.put("x5u", this.x5u.toString());
      }

      if (this.x5t != null) {
         o.put("x5t", this.x5t.toString());
      }

      if (this.x5t256 != null) {
         o.put("x5t#S256", this.x5t256.toString());
      }

      if (this.x5c != null) {
         List<Object> stringValues = JSONArrayUtils.newJSONArray();

         for (Base64 base64 : this.x5c) {
            stringValues.add(base64.toString());
         }

         o.put("x5c", stringValues);
      }

      if (this.exp != null) {
         o.put("exp", DateUtils.toSecondsSinceEpoch(this.exp));
      }

      if (this.nbf != null) {
         o.put("nbf", DateUtils.toSecondsSinceEpoch(this.nbf));
      }

      if (this.iat != null) {
         o.put("iat", DateUtils.toSecondsSinceEpoch(this.iat));
      }

      if (this.revocation != null) {
         o.put("revoked", this.revocation.toJSONObject());
      }

      return o;
   }

   public String toJSONString() {
      return JSONObjectUtils.toJSONString(this.toJSONObject());
   }

   @Override
   public String toString() {
      return JSONObjectUtils.toJSONString(this.toJSONObject());
   }

   public static JWK parse(String s) throws ParseException {
      return parse(JSONObjectUtils.parse(s));
   }

   public static JWK parse(Map<String, Object> jsonObject) throws ParseException {
      String ktyString = JSONObjectUtils.getString(jsonObject, "kty");
      if (ktyString == null) {
         throw new ParseException("Missing key type \"kty\" parameter", 0);
      } else {
         KeyType kty = KeyType.parse(ktyString);
         if (kty == KeyType.EC) {
            return ECKey.parse(jsonObject);
         } else if (kty == KeyType.RSA) {
            return RSAKey.parse(jsonObject);
         } else if (kty == KeyType.OCT) {
            return OctetSequenceKey.parse(jsonObject);
         } else if (kty == KeyType.OKP) {
            return OctetKeyPair.parse(jsonObject);
         } else {
            throw new ParseException("Unsupported key type \"kty\" parameter: " + kty, 0);
         }
      }
   }

   public static JWK parse(X509Certificate cert) throws JOSEException {
      if (cert.getPublicKey() instanceof RSAPublicKey) {
         return RSAKey.parse(cert);
      } else if (cert.getPublicKey() instanceof ECPublicKey) {
         return ECKey.parse(cert);
      } else {
         throw new JOSEException("Unsupported public key algorithm: " + cert.getPublicKey().getAlgorithm());
      }
   }

   public static JWK parseFromPEMEncodedX509Cert(String pemEncodedCert) throws JOSEException {
      X509Certificate cert = X509CertUtils.parse(pemEncodedCert);
      if (cert == null) {
         throw new JOSEException("Couldn't parse PEM-encoded X.509 certificate");
      } else {
         return parse(cert);
      }
   }

   public static JWK load(KeyStore keyStore, String alias, char[] pin) throws KeyStoreException, JOSEException {
      Certificate cert = keyStore.getCertificate(alias);
      if (cert == null) {
         return OctetSequenceKey.load(keyStore, alias, pin);
      } else if (cert.getPublicKey() instanceof RSAPublicKey) {
         return RSAKey.load(keyStore, alias, pin);
      } else if (cert.getPublicKey() instanceof ECPublicKey) {
         return ECKey.load(keyStore, alias, pin);
      } else {
         throw new JOSEException("Unsupported public key algorithm: " + cert.getPublicKey().getAlgorithm());
      }
   }

   public static JWK parseFromPEMEncodedObjects(String pemEncodedObjects) throws JOSEException {
      List<KeyPair> keys = PEMEncodedKeyParser.parseKeys(pemEncodedObjects);
      if (keys.isEmpty()) {
         throw new JOSEException("No PEM-encoded keys found");
      } else {
         KeyPair pair = mergeKeyPairs(keys);
         PublicKey publicKey = pair.getPublic();
         PrivateKey privateKey = pair.getPrivate();
         if (publicKey == null) {
            throw new JOSEException("Missing PEM-encoded public key to construct JWK");
         } else if (publicKey instanceof ECPublicKey) {
            ECPublicKey ecPubKey = (ECPublicKey)publicKey;
            ECParameterSpec pubParams = ecPubKey.getParams();
            if (privateKey instanceof ECPrivateKey) {
               validateEcCurves(ecPubKey, (ECPrivateKey)privateKey);
            }

            if (privateKey != null && !(privateKey instanceof ECPrivateKey)) {
               throw new JOSEException("Unsupported " + KeyType.EC.getValue() + " private key type: " + privateKey);
            } else {
               Curve curve = Curve.forECParameterSpec(pubParams);
               ECKey.Builder builder = new ECKey.Builder(curve, (ECPublicKey)publicKey);
               if (privateKey != null) {
                  builder.privateKey((ECPrivateKey)privateKey);
               }

               return builder.build();
            }
         } else if (publicKey instanceof RSAPublicKey) {
            RSAKey.Builder builder = new RSAKey.Builder((RSAPublicKey)publicKey);
            if (privateKey instanceof RSAPrivateKey) {
               builder.privateKey((RSAPrivateKey)privateKey);
            } else if (privateKey != null) {
               throw new JOSEException("Unsupported " + KeyType.RSA.getValue() + " private key type: " + privateKey);
            }

            return builder.build();
         } else {
            throw new JOSEException("Unsupported algorithm of PEM-encoded key: " + publicKey.getAlgorithm());
         }
      }
   }

   private static void validateEcCurves(ECPublicKey publicKey, ECPrivateKey privateKey) throws JOSEException {
      ECParameterSpec pubParams = publicKey.getParams();
      ECParameterSpec privParams = privateKey.getParams();
      if (!pubParams.getCurve().equals(privParams.getCurve())) {
         throw new JOSEException("Public/private " + KeyType.EC.getValue() + " key curve mismatch: " + publicKey);
      } else if (pubParams.getCofactor() != privParams.getCofactor()) {
         throw new JOSEException("Public/private " + KeyType.EC.getValue() + " key cofactor mismatch: " + publicKey);
      } else if (!pubParams.getGenerator().equals(privParams.getGenerator())) {
         throw new JOSEException("Public/private " + KeyType.EC.getValue() + " key generator mismatch: " + publicKey);
      } else if (!pubParams.getOrder().equals(privParams.getOrder())) {
         throw new JOSEException("Public/private " + KeyType.EC.getValue() + " key order mismatch: " + publicKey);
      }
   }

   private static KeyPair mergeKeyPairs(List<KeyPair> keys) throws JOSEException {
      KeyPair pair;
      if (keys.size() == 1) {
         pair = keys.get(0);
      } else {
         if (keys.size() != 2) {
            throw new JOSEException("Expected key or pair of PEM-encoded keys");
         }

         pair = twoKeysToKeyPair(keys);
      }

      return pair;
   }

   private static KeyPair twoKeysToKeyPair(List<? extends KeyPair> keys) throws JOSEException {
      KeyPair key1 = keys.get(0);
      KeyPair key2 = keys.get(1);
      if (key1.getPublic() != null && key2.getPrivate() != null) {
         return new KeyPair(key1.getPublic(), key2.getPrivate());
      } else if (key1.getPrivate() != null && key2.getPublic() != null) {
         return new KeyPair(key2.getPublic(), key1.getPrivate());
      } else {
         throw new JOSEException("Not a public/private key pair");
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof JWK)) {
         return false;
      } else {
         JWK jwk = (JWK)o;
         return Objects.equals(this.kty, jwk.kty)
            && Objects.equals(this.use, jwk.use)
            && Objects.equals(this.ops, jwk.ops)
            && Objects.equals(this.alg, jwk.alg)
            && Objects.equals(this.kid, jwk.kid)
            && Objects.equals(this.x5u, jwk.x5u)
            && Objects.equals(this.x5t, jwk.x5t)
            && Objects.equals(this.x5t256, jwk.x5t256)
            && Objects.equals(this.x5c, jwk.x5c)
            && Objects.equals(this.exp, jwk.exp)
            && Objects.equals(this.nbf, jwk.nbf)
            && Objects.equals(this.iat, jwk.iat)
            && Objects.equals(this.revocation, jwk.revocation)
            && Objects.equals(this.keyStore, jwk.keyStore);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(
         this.kty,
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
         this.keyStore
      );
   }
}
