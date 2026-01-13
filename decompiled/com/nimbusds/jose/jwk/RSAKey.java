package com.nimbusds.jose.jwk;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.shaded.jcip.Immutable;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.ByteUtils;
import com.nimbusds.jose.util.IntegerOverflowException;
import com.nimbusds.jose.util.JSONArrayUtils;
import com.nimbusds.jose.util.JSONObjectUtils;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.URI;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAMultiPrimePrivateCrtKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAMultiPrimePrivateCrtKeySpec;
import java.security.spec.RSAOtherPrimeInfo;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Immutable
public final class RSAKey extends JWK implements AsymmetricJWK {
   private static final long serialVersionUID = 1L;
   private final Base64URL n;
   private final Base64URL e;
   private final Base64URL d;
   private final Base64URL p;
   private final Base64URL q;
   private final Base64URL dp;
   private final Base64URL dq;
   private final Base64URL qi;
   private final List<RSAKey.OtherPrimesInfo> oth;
   private final PrivateKey privateKey;

   @Deprecated
   public RSAKey(
      Base64URL n,
      Base64URL e,
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
      this(n, e, null, null, null, null, null, null, null, null, use, ops, alg, kid, x5u, x5t, x5t256, x5c, null, null, null, ks);
   }

   @Deprecated
   public RSAKey(
      Base64URL n,
      Base64URL e,
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
      this(n, e, null, null, null, null, null, null, null, null, use, ops, alg, kid, x5u, x5t, x5t256, x5c, exp, nbf, iat, ks);
   }

   public RSAKey(
      Base64URL n,
      Base64URL e,
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
      this(n, e, null, null, null, null, null, null, null, null, use, ops, alg, kid, x5u, x5t, x5t256, x5c, exp, nbf, iat, revocation, ks);
   }

   @Deprecated
   public RSAKey(
      Base64URL n,
      Base64URL e,
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
      this(n, e, d, null, null, null, null, null, null, null, use, ops, alg, kid, x5u, x5t, x5t256, x5c, null, null, null, ks);
   }

   @Deprecated
   public RSAKey(
      Base64URL n,
      Base64URL e,
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
      this(n, e, d, use, ops, alg, kid, x5u, x5t, x5t256, x5c, exp, nbf, iat, null, ks);
   }

   public RSAKey(
      Base64URL n,
      Base64URL e,
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
      this(n, e, d, null, null, null, null, null, null, null, use, ops, alg, kid, x5u, x5t, x5t256, x5c, exp, nbf, iat, revocation, ks);
      Objects.requireNonNull(d, "The private exponent must not be null");
   }

   @Deprecated
   public RSAKey(
      Base64URL n,
      Base64URL e,
      Base64URL p,
      Base64URL q,
      Base64URL dp,
      Base64URL dq,
      Base64URL qi,
      List<RSAKey.OtherPrimesInfo> oth,
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
      this(n, e, null, p, q, dp, dq, qi, oth, null, use, ops, alg, kid, x5u, x5t, x5t256, x5c, null, null, null, ks);
   }

   @Deprecated
   public RSAKey(
      Base64URL n,
      Base64URL e,
      Base64URL p,
      Base64URL q,
      Base64URL dp,
      Base64URL dq,
      Base64URL qi,
      List<RSAKey.OtherPrimesInfo> oth,
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
      this(n, e, p, q, dp, dq, qi, oth, use, ops, alg, kid, x5u, x5t, x5t256, x5c, exp, nbf, iat, null, ks);
   }

   public RSAKey(
      Base64URL n,
      Base64URL e,
      Base64URL p,
      Base64URL q,
      Base64URL dp,
      Base64URL dq,
      Base64URL qi,
      List<RSAKey.OtherPrimesInfo> oth,
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
      this(n, e, null, p, q, dp, dq, qi, oth, null, use, ops, alg, kid, x5u, x5t, x5t256, x5c, exp, nbf, iat, revocation, ks);
      Objects.requireNonNull(p, "The first prime factor must not be null");
      Objects.requireNonNull(q, "The second prime factor must not be null");
      Objects.requireNonNull(dp, "The first factor CRT exponent must not be null");
      Objects.requireNonNull(dq, "The second factor CRT exponent must not be null");
      Objects.requireNonNull(qi, "The first CRT coefficient must not be null");
   }

   @Deprecated
   public RSAKey(
      Base64URL n,
      Base64URL e,
      Base64URL d,
      Base64URL p,
      Base64URL q,
      Base64URL dp,
      Base64URL dq,
      Base64URL qi,
      List<RSAKey.OtherPrimesInfo> oth,
      KeyUse use,
      Set<KeyOperation> ops,
      Algorithm alg,
      String kid,
      URI x5u,
      Base64URL x5t,
      Base64URL x5t256,
      List<Base64> x5c
   ) {
      this(n, e, d, p, q, dp, dq, qi, oth, null, use, ops, alg, kid, x5u, x5t, x5t256, x5c, null);
   }

   @Deprecated
   public RSAKey(
      Base64URL n,
      Base64URL e,
      Base64URL d,
      Base64URL p,
      Base64URL q,
      Base64URL dp,
      Base64URL dq,
      Base64URL qi,
      List<RSAKey.OtherPrimesInfo> oth,
      PrivateKey prv,
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
      this(n, e, d, p, q, dp, dq, qi, oth, prv, use, ops, alg, kid, x5u, x5t, x5t256, x5c, null, null, null, ks);
   }

   @Deprecated
   public RSAKey(
      Base64URL n,
      Base64URL e,
      Base64URL d,
      Base64URL p,
      Base64URL q,
      Base64URL dp,
      Base64URL dq,
      Base64URL qi,
      List<RSAKey.OtherPrimesInfo> oth,
      PrivateKey prv,
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
      this(n, e, d, p, q, dp, dq, qi, oth, prv, use, ops, alg, kid, x5u, x5t, x5t256, x5c, exp, nbf, iat, null, ks);
   }

   public RSAKey(
      Base64URL n,
      Base64URL e,
      Base64URL d,
      Base64URL p,
      Base64URL q,
      Base64URL dp,
      Base64URL dq,
      Base64URL qi,
      List<RSAKey.OtherPrimesInfo> oth,
      PrivateKey prv,
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
      super(KeyType.RSA, use, ops, alg, kid, x5u, x5t, x5t256, x5c, exp, nbf, iat, revocation, ks);
      this.n = Objects.requireNonNull(n, "The modulus value must not be null");
      this.e = Objects.requireNonNull(e, "The public exponent value must not be null");
      if (this.getParsedX509CertChain() != null && !this.matches(this.getParsedX509CertChain().get(0))) {
         throw new IllegalArgumentException(
            "The public subject key info of the first X.509 certificate in the chain must match the JWK type and public parameters"
         );
      } else {
         this.d = d;
         if (p != null && q != null && dp != null && dq != null && qi != null) {
            this.p = p;
            this.q = q;
            this.dp = dp;
            this.dq = dq;
            this.qi = qi;
            if (oth != null) {
               this.oth = Collections.unmodifiableList(oth);
            } else {
               this.oth = Collections.emptyList();
            }
         } else if (p == null && q == null && dp == null && dq == null && qi == null && oth == null) {
            this.p = null;
            this.q = null;
            this.dp = null;
            this.dq = null;
            this.qi = null;
            this.oth = Collections.emptyList();
         } else {
            if (p != null || q != null || dp != null || dq != null || qi != null) {
               Objects.requireNonNull(p, "Incomplete second private (CRT) representation: The first prime factor must not be null");
               Objects.requireNonNull(q, "Incomplete second private (CRT) representation: The second prime factor must not be null");
               Objects.requireNonNull(dp, "Incomplete second private (CRT) representation: The first factor CRT exponent must not be null");
               Objects.requireNonNull(dq, "Incomplete second private (CRT) representation: The second factor CRT exponent must not be null");
               throw new IllegalArgumentException("Incomplete second private (CRT) representation: The first CRT coefficient must not be null");
            }

            this.p = null;
            this.q = null;
            this.dp = null;
            this.dq = null;
            this.qi = null;
            this.oth = Collections.emptyList();
         }

         this.privateKey = prv;
      }
   }

   @Deprecated
   public RSAKey(
      RSAPublicKey pub, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, KeyStore ks
   ) {
      this(pub, use, ops, alg, kid, x5u, x5t, x5t256, x5c, null, null, null, ks);
   }

   @Deprecated
   public RSAKey(
      RSAPublicKey pub,
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
      this(pub, use, ops, alg, kid, x5u, x5t, x5t256, x5c, exp, nbf, iat, null, ks);
   }

   public RSAKey(
      RSAPublicKey pub,
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
      this(
         Base64URL.encode(pub.getModulus()),
         Base64URL.encode(pub.getPublicExponent()),
         use,
         ops,
         alg,
         kid,
         x5u,
         x5t,
         x5t256,
         x5c,
         exp,
         nbf,
         iat,
         revocation,
         ks
      );
   }

   @Deprecated
   public RSAKey(
      RSAPublicKey pub,
      RSAPrivateKey priv,
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
      this(pub, priv, use, ops, alg, kid, x5u, x5t, x5t256, x5c, null, null, null, ks);
   }

   @Deprecated
   public RSAKey(
      RSAPublicKey pub,
      RSAPrivateKey priv,
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
      this(pub, priv, use, ops, alg, kid, x5u, x5t, x5t256, x5c, exp, nbf, iat, null, ks);
   }

   public RSAKey(
      RSAPublicKey pub,
      RSAPrivateKey priv,
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
      this(
         Base64URL.encode(pub.getModulus()),
         Base64URL.encode(pub.getPublicExponent()),
         Base64URL.encode(priv.getPrivateExponent()),
         use,
         ops,
         alg,
         kid,
         x5u,
         x5t,
         x5t256,
         x5c,
         exp,
         nbf,
         iat,
         revocation,
         ks
      );
   }

   @Deprecated
   public RSAKey(
      RSAPublicKey pub,
      RSAPrivateCrtKey priv,
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
      this(pub, priv, use, ops, alg, kid, x5u, x5t, x5t256, x5c, null, null, null, ks);
   }

   @Deprecated
   public RSAKey(
      RSAPublicKey pub,
      RSAPrivateCrtKey priv,
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
      this(
         Base64URL.encode(pub.getModulus()),
         Base64URL.encode(pub.getPublicExponent()),
         Base64URL.encode(priv.getPrivateExponent()),
         Base64URL.encode(priv.getPrimeP()),
         Base64URL.encode(priv.getPrimeQ()),
         Base64URL.encode(priv.getPrimeExponentP()),
         Base64URL.encode(priv.getPrimeExponentQ()),
         Base64URL.encode(priv.getCrtCoefficient()),
         null,
         null,
         use,
         ops,
         alg,
         kid,
         x5u,
         x5t,
         x5t256,
         x5c,
         exp,
         nbf,
         iat,
         ks
      );
   }

   public RSAKey(
      RSAPublicKey pub,
      RSAPrivateCrtKey priv,
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
      this(
         Base64URL.encode(pub.getModulus()),
         Base64URL.encode(pub.getPublicExponent()),
         Base64URL.encode(priv.getPrivateExponent()),
         Base64URL.encode(priv.getPrimeP()),
         Base64URL.encode(priv.getPrimeQ()),
         Base64URL.encode(priv.getPrimeExponentP()),
         Base64URL.encode(priv.getPrimeExponentQ()),
         Base64URL.encode(priv.getCrtCoefficient()),
         null,
         null,
         use,
         ops,
         alg,
         kid,
         x5u,
         x5t,
         x5t256,
         x5c,
         exp,
         nbf,
         iat,
         revocation,
         ks
      );
   }

   @Deprecated
   public RSAKey(
      RSAPublicKey pub,
      RSAMultiPrimePrivateCrtKey priv,
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
      this(pub, priv, use, ops, alg, kid, x5u, x5t, x5t256, x5c, null, null, null, ks);
   }

   @Deprecated
   public RSAKey(
      RSAPublicKey pub,
      RSAMultiPrimePrivateCrtKey priv,
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
      this(
         Base64URL.encode(pub.getModulus()),
         Base64URL.encode(pub.getPublicExponent()),
         Base64URL.encode(priv.getPrivateExponent()),
         Base64URL.encode(priv.getPrimeP()),
         Base64URL.encode(priv.getPrimeQ()),
         Base64URL.encode(priv.getPrimeExponentP()),
         Base64URL.encode(priv.getPrimeExponentQ()),
         Base64URL.encode(priv.getCrtCoefficient()),
         RSAKey.OtherPrimesInfo.toList(priv.getOtherPrimeInfo()),
         null,
         use,
         ops,
         alg,
         kid,
         x5u,
         x5t,
         x5t256,
         x5c,
         exp,
         nbf,
         iat,
         ks
      );
   }

   public RSAKey(
      RSAPublicKey pub,
      RSAMultiPrimePrivateCrtKey priv,
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
      this(
         Base64URL.encode(pub.getModulus()),
         Base64URL.encode(pub.getPublicExponent()),
         Base64URL.encode(priv.getPrivateExponent()),
         Base64URL.encode(priv.getPrimeP()),
         Base64URL.encode(priv.getPrimeQ()),
         Base64URL.encode(priv.getPrimeExponentP()),
         Base64URL.encode(priv.getPrimeExponentQ()),
         Base64URL.encode(priv.getCrtCoefficient()),
         RSAKey.OtherPrimesInfo.toList(priv.getOtherPrimeInfo()),
         null,
         use,
         ops,
         alg,
         kid,
         x5u,
         x5t,
         x5t256,
         x5c,
         exp,
         nbf,
         iat,
         revocation,
         ks
      );
   }

   @Deprecated
   public RSAKey(
      RSAPublicKey pub,
      PrivateKey priv,
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
      this(pub, priv, use, ops, alg, kid, x5u, x5t, x5t256, x5c, null, null, null, ks);
   }

   @Deprecated
   public RSAKey(
      RSAPublicKey pub,
      PrivateKey priv,
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
      this(pub, priv, use, ops, alg, kid, x5u, x5t, x5t256, x5c, exp, nbf, iat, null, ks);
   }

   public RSAKey(
      RSAPublicKey pub,
      PrivateKey priv,
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
      this(
         Base64URL.encode(pub.getModulus()),
         Base64URL.encode(pub.getPublicExponent()),
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         priv,
         use,
         ops,
         alg,
         kid,
         x5u,
         x5t,
         x5t256,
         x5c,
         exp,
         nbf,
         iat,
         revocation,
         ks
      );
   }

   public Base64URL getModulus() {
      return this.n;
   }

   public Base64URL getPublicExponent() {
      return this.e;
   }

   public Base64URL getPrivateExponent() {
      return this.d;
   }

   public Base64URL getFirstPrimeFactor() {
      return this.p;
   }

   public Base64URL getSecondPrimeFactor() {
      return this.q;
   }

   public Base64URL getFirstFactorCRTExponent() {
      return this.dp;
   }

   public Base64URL getSecondFactorCRTExponent() {
      return this.dq;
   }

   public Base64URL getFirstCRTCoefficient() {
      return this.qi;
   }

   public List<RSAKey.OtherPrimesInfo> getOtherPrimes() {
      return this.oth;
   }

   public RSAPublicKey toRSAPublicKey() throws JOSEException {
      BigInteger modulus = this.n.decodeToBigInteger();
      BigInteger exponent = this.e.decodeToBigInteger();
      RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);

      try {
         KeyFactory factory = KeyFactory.getInstance("RSA");
         return (RSAPublicKey)factory.generatePublic(spec);
      } catch (InvalidKeySpecException | NoSuchAlgorithmException var5) {
         throw new JOSEException(var5.getMessage(), var5);
      }
   }

   public RSAPrivateKey toRSAPrivateKey() throws JOSEException {
      if (this.d == null) {
         return null;
      } else {
         BigInteger modulus = this.n.decodeToBigInteger();
         BigInteger privateExponent = this.d.decodeToBigInteger();
         RSAPrivateKeySpec spec;
         if (this.p == null) {
            spec = new RSAPrivateKeySpec(modulus, privateExponent);
         } else {
            BigInteger publicExponent = this.e.decodeToBigInteger();
            BigInteger primeP = this.p.decodeToBigInteger();
            BigInteger primeQ = this.q.decodeToBigInteger();
            BigInteger primeExponentP = this.dp.decodeToBigInteger();
            BigInteger primeExponentQ = this.dq.decodeToBigInteger();
            BigInteger crtCoefficient = this.qi.decodeToBigInteger();
            if (this.oth != null && !this.oth.isEmpty()) {
               RSAOtherPrimeInfo[] otherInfo = new RSAOtherPrimeInfo[this.oth.size()];

               for (int i = 0; i < this.oth.size(); i++) {
                  RSAKey.OtherPrimesInfo opi = this.oth.get(i);
                  BigInteger otherPrime = opi.getPrimeFactor().decodeToBigInteger();
                  BigInteger otherPrimeExponent = opi.getFactorCRTExponent().decodeToBigInteger();
                  BigInteger otherCrtCoefficient = opi.getFactorCRTCoefficient().decodeToBigInteger();
                  otherInfo[i] = new RSAOtherPrimeInfo(otherPrime, otherPrimeExponent, otherCrtCoefficient);
               }

               spec = new RSAMultiPrimePrivateCrtKeySpec(
                  modulus, publicExponent, privateExponent, primeP, primeQ, primeExponentP, primeExponentQ, crtCoefficient, otherInfo
               );
            } else {
               spec = new RSAPrivateCrtKeySpec(modulus, publicExponent, privateExponent, primeP, primeQ, primeExponentP, primeExponentQ, crtCoefficient);
            }
         }

         try {
            KeyFactory factory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey)factory.generatePrivate(spec);
         } catch (NoSuchAlgorithmException | InvalidKeySpecException var16) {
            throw new JOSEException(var16.getMessage(), var16);
         }
      }
   }

   @Override
   public PublicKey toPublicKey() throws JOSEException {
      return this.toRSAPublicKey();
   }

   @Override
   public PrivateKey toPrivateKey() throws JOSEException {
      PrivateKey prv = this.toRSAPrivateKey();
      return prv != null ? prv : this.privateKey;
   }

   @Override
   public KeyPair toKeyPair() throws JOSEException {
      return new KeyPair(this.toRSAPublicKey(), this.toPrivateKey());
   }

   public RSAKey toRevokedJWK(KeyRevocation keyRevocation) {
      if (this.getKeyRevocation() != null) {
         throw new IllegalStateException("Already revoked");
      } else {
         return new RSAKey.Builder(this).keyRevocation(Objects.requireNonNull(keyRevocation)).build();
      }
   }

   @Override
   public boolean matches(X509Certificate cert) {
      RSAPublicKey certRSAKey;
      try {
         certRSAKey = (RSAPublicKey)this.getParsedX509CertChain().get(0).getPublicKey();
      } catch (ClassCastException var4) {
         return false;
      }

      return !this.e.decodeToBigInteger().equals(certRSAKey.getPublicExponent()) ? false : this.n.decodeToBigInteger().equals(certRSAKey.getModulus());
   }

   @Override
   public LinkedHashMap<String, ?> getRequiredParams() {
      LinkedHashMap<String, String> requiredParams = new LinkedHashMap<>();
      requiredParams.put("e", this.e.toString());
      requiredParams.put("kty", this.getKeyType().getValue());
      requiredParams.put("n", this.n.toString());
      return requiredParams;
   }

   @Override
   public boolean isPrivate() {
      return this.d != null || this.p != null || this.privateKey != null;
   }

   @Override
   public int size() {
      try {
         return ByteUtils.safeBitLength(this.n.decode());
      } catch (IntegerOverflowException var2) {
         throw new ArithmeticException(var2.getMessage());
      }
   }

   public RSAKey toPublicJWK() {
      return new RSAKey(
         this.getModulus(),
         this.getPublicExponent(),
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

   @Override
   public Map<String, Object> toJSONObject() {
      Map<String, Object> o = super.toJSONObject();
      o.put("n", this.n.toString());
      o.put("e", this.e.toString());
      if (this.d != null) {
         o.put("d", this.d.toString());
      }

      if (this.p != null) {
         o.put("p", this.p.toString());
      }

      if (this.q != null) {
         o.put("q", this.q.toString());
      }

      if (this.dp != null) {
         o.put("dp", this.dp.toString());
      }

      if (this.dq != null) {
         o.put("dq", this.dq.toString());
      }

      if (this.qi != null) {
         o.put("qi", this.qi.toString());
      }

      if (this.oth != null && !this.oth.isEmpty()) {
         List<Object> a = JSONArrayUtils.newJSONArray();

         for (RSAKey.OtherPrimesInfo other : this.oth) {
            Map<String, Object> oo = JSONObjectUtils.newJSONObject();
            oo.put("r", other.r.toString());
            oo.put("d", other.d.toString());
            oo.put("t", other.t.toString());
            a.add(oo);
         }

         o.put("oth", a);
      }

      return o;
   }

   public static RSAKey parse(String s) throws ParseException {
      return parse(JSONObjectUtils.parse(s));
   }

   public static RSAKey parse(Map<String, Object> jsonObject) throws ParseException {
      if (!KeyType.RSA.equals(JWKMetadata.parseKeyType(jsonObject))) {
         throw new ParseException("The key type \"kty\" must be RSA", 0);
      } else {
         Base64URL n = JSONObjectUtils.getBase64URL(jsonObject, "n");
         Base64URL e = JSONObjectUtils.getBase64URL(jsonObject, "e");
         Base64URL d = JSONObjectUtils.getBase64URL(jsonObject, "d");
         Base64URL p = JSONObjectUtils.getBase64URL(jsonObject, "p");
         Base64URL q = JSONObjectUtils.getBase64URL(jsonObject, "q");
         Base64URL dp = JSONObjectUtils.getBase64URL(jsonObject, "dp");
         Base64URL dq = JSONObjectUtils.getBase64URL(jsonObject, "dq");
         Base64URL qi = JSONObjectUtils.getBase64URL(jsonObject, "qi");
         List<RSAKey.OtherPrimesInfo> oth = null;
         if (jsonObject.containsKey("oth")) {
            List<Object> arr = JSONObjectUtils.getJSONArray(jsonObject, "oth");
            if (arr != null) {
               oth = new ArrayList<>(arr.size());

               for (Object o : arr) {
                  if (o instanceof Map) {
                     Map<String, Object> otherJson = (Map<String, Object>)o;
                     Base64URL r = JSONObjectUtils.getBase64URL(otherJson, "r");
                     Base64URL odq = JSONObjectUtils.getBase64URL(otherJson, "dq");
                     Base64URL t = JSONObjectUtils.getBase64URL(otherJson, "t");

                     try {
                        oth.add(new RSAKey.OtherPrimesInfo(r, odq, t));
                     } catch (IllegalArgumentException var19) {
                        throw new ParseException(var19.getMessage(), 0);
                     }
                  }
               }
            }
         }

         try {
            return new RSAKey(
               n,
               e,
               d,
               p,
               q,
               dp,
               dq,
               qi,
               oth,
               null,
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
         } catch (Exception var18) {
            throw new ParseException(var18.getMessage(), 0);
         }
      }
   }

   public static RSAKey parse(X509Certificate cert) throws JOSEException {
      if (!(cert.getPublicKey() instanceof RSAPublicKey)) {
         throw new JOSEException("The public key of the X.509 certificate is not RSA");
      } else {
         RSAPublicKey publicKey = (RSAPublicKey)cert.getPublicKey();

         try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            return new RSAKey.Builder(publicKey)
               .keyUse(KeyUse.from(cert))
               .keyID(cert.getSerialNumber().toString(10))
               .x509CertChain(Collections.singletonList(Base64.encode(cert.getEncoded())))
               .x509CertSHA256Thumbprint(Base64URL.encode(sha256.digest(cert.getEncoded())))
               .expirationTime(cert.getNotAfter())
               .notBeforeTime(cert.getNotBefore())
               .build();
         } catch (NoSuchAlgorithmException var3) {
            throw new JOSEException("Couldn't encode x5t parameter: " + var3.getMessage(), var3);
         } catch (CertificateEncodingException var4) {
            throw new JOSEException("Couldn't encode x5c parameter: " + var4.getMessage(), var4);
         }
      }
   }

   public static RSAKey load(KeyStore keyStore, String alias, char[] pin) throws KeyStoreException, JOSEException {
      Certificate cert = keyStore.getCertificate(alias);
      if (!(cert instanceof X509Certificate)) {
         return null;
      } else {
         X509Certificate x509Cert = (X509Certificate)cert;
         if (!(x509Cert.getPublicKey() instanceof RSAPublicKey)) {
            throw new JOSEException("Couldn't load RSA JWK: The key algorithm is not RSA");
         } else {
            RSAKey rsaJWK = parse(x509Cert);
            rsaJWK = new RSAKey.Builder(rsaJWK).keyID(alias).keyStore(keyStore).build();

            Key key;
            try {
               key = keyStore.getKey(alias, pin);
            } catch (NoSuchAlgorithmException | UnrecoverableKeyException var8) {
               throw new JOSEException("Couldn't retrieve private RSA key (bad pin?): " + var8.getMessage(), var8);
            }

            if (key instanceof RSAPrivateKey) {
               return new RSAKey.Builder(rsaJWK).privateKey((RSAPrivateKey)key).build();
            } else {
               return key instanceof PrivateKey && "RSA".equalsIgnoreCase(key.getAlgorithm())
                  ? new RSAKey.Builder(rsaJWK).privateKey((PrivateKey)key).build()
                  : rsaJWK;
            }
         }
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof RSAKey)) {
         return false;
      } else if (!super.equals(o)) {
         return false;
      } else {
         RSAKey rsaKey = (RSAKey)o;
         return Objects.equals(this.n, rsaKey.n)
            && Objects.equals(this.e, rsaKey.e)
            && Objects.equals(this.d, rsaKey.d)
            && Objects.equals(this.p, rsaKey.p)
            && Objects.equals(this.q, rsaKey.q)
            && Objects.equals(this.dp, rsaKey.dp)
            && Objects.equals(this.dq, rsaKey.dq)
            && Objects.equals(this.qi, rsaKey.qi)
            && Objects.equals(this.oth, rsaKey.oth)
            && Objects.equals(this.privateKey, rsaKey.privateKey);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(super.hashCode(), this.n, this.e, this.d, this.p, this.q, this.dp, this.dq, this.qi, this.oth, this.privateKey);
   }

   public static class Builder {
      private final Base64URL n;
      private final Base64URL e;
      private Base64URL d;
      private Base64URL p;
      private Base64URL q;
      private Base64URL dp;
      private Base64URL dq;
      private Base64URL qi;
      private List<RSAKey.OtherPrimesInfo> oth;
      private PrivateKey priv;
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

      public Builder(Base64URL n, Base64URL e) {
         this.n = Objects.requireNonNull(n);
         this.e = Objects.requireNonNull(e);
      }

      public Builder(RSAPublicKey pub) {
         this.n = Base64URL.encode(pub.getModulus());
         this.e = Base64URL.encode(pub.getPublicExponent());
      }

      public Builder(RSAKey rsaJWK) {
         this.n = rsaJWK.n;
         this.e = rsaJWK.e;
         this.d = rsaJWK.d;
         this.p = rsaJWK.p;
         this.q = rsaJWK.q;
         this.dp = rsaJWK.dp;
         this.dq = rsaJWK.dq;
         this.qi = rsaJWK.qi;
         this.oth = rsaJWK.oth;
         this.priv = rsaJWK.privateKey;
         this.use = rsaJWK.getKeyUse();
         this.ops = rsaJWK.getKeyOperations();
         this.alg = rsaJWK.getAlgorithm();
         this.kid = rsaJWK.getKeyID();
         this.x5u = rsaJWK.getX509CertURL();
         this.x5t = rsaJWK.getX509CertThumbprint();
         this.x5t256 = rsaJWK.getX509CertSHA256Thumbprint();
         this.x5c = rsaJWK.getX509CertChain();
         this.exp = rsaJWK.getExpirationTime();
         this.nbf = rsaJWK.getNotBeforeTime();
         this.iat = rsaJWK.getIssueTime();
         this.revocation = rsaJWK.getKeyRevocation();
         this.ks = rsaJWK.getKeyStore();
      }

      public RSAKey.Builder privateExponent(Base64URL d) {
         this.d = d;
         return this;
      }

      public RSAKey.Builder privateKey(RSAPrivateKey priv) {
         if (priv instanceof RSAPrivateCrtKey) {
            return this.privateKey((RSAPrivateCrtKey)priv);
         } else if (priv instanceof RSAMultiPrimePrivateCrtKey) {
            return this.privateKey((RSAMultiPrimePrivateCrtKey)priv);
         } else {
            if (priv != null) {
               this.d = Base64URL.encode(priv.getPrivateExponent());
            } else {
               this.d = null;
            }

            return this;
         }
      }

      public RSAKey.Builder privateKey(PrivateKey priv) {
         if (priv instanceof RSAPrivateKey) {
            return this.privateKey((RSAPrivateKey)priv);
         } else if (priv != null && !"RSA".equalsIgnoreCase(priv.getAlgorithm())) {
            throw new IllegalArgumentException("The private key algorithm must be RSA");
         } else {
            this.priv = priv;
            return this;
         }
      }

      public RSAKey.Builder firstPrimeFactor(Base64URL p) {
         this.p = p;
         return this;
      }

      public RSAKey.Builder secondPrimeFactor(Base64URL q) {
         this.q = q;
         return this;
      }

      public RSAKey.Builder firstFactorCRTExponent(Base64URL dp) {
         this.dp = dp;
         return this;
      }

      public RSAKey.Builder secondFactorCRTExponent(Base64URL dq) {
         this.dq = dq;
         return this;
      }

      public RSAKey.Builder firstCRTCoefficient(Base64URL qi) {
         this.qi = qi;
         return this;
      }

      public RSAKey.Builder otherPrimes(List<RSAKey.OtherPrimesInfo> oth) {
         this.oth = oth;
         return this;
      }

      public RSAKey.Builder privateKey(RSAPrivateCrtKey priv) {
         if (priv != null) {
            this.d = Base64URL.encode(priv.getPrivateExponent());
            this.p = Base64URL.encode(priv.getPrimeP());
            this.q = Base64URL.encode(priv.getPrimeQ());
            this.dp = Base64URL.encode(priv.getPrimeExponentP());
            this.dq = Base64URL.encode(priv.getPrimeExponentQ());
            this.qi = Base64URL.encode(priv.getCrtCoefficient());
         } else {
            this.d = null;
            this.p = null;
            this.q = null;
            this.dp = null;
            this.dq = null;
            this.qi = null;
         }

         return this;
      }

      public RSAKey.Builder privateKey(RSAMultiPrimePrivateCrtKey priv) {
         if (priv != null) {
            this.d = Base64URL.encode(priv.getPrivateExponent());
            this.p = Base64URL.encode(priv.getPrimeP());
            this.q = Base64URL.encode(priv.getPrimeQ());
            this.dp = Base64URL.encode(priv.getPrimeExponentP());
            this.dq = Base64URL.encode(priv.getPrimeExponentQ());
            this.qi = Base64URL.encode(priv.getCrtCoefficient());
            this.oth = RSAKey.OtherPrimesInfo.toList(priv.getOtherPrimeInfo());
         } else {
            this.d = null;
            this.p = null;
            this.q = null;
            this.dp = null;
            this.dq = null;
            this.qi = null;
            this.oth = null;
         }

         return this;
      }

      public RSAKey.Builder keyUse(KeyUse use) {
         this.use = use;
         return this;
      }

      public RSAKey.Builder keyOperations(Set<KeyOperation> ops) {
         this.ops = ops;
         return this;
      }

      public RSAKey.Builder algorithm(Algorithm alg) {
         this.alg = alg;
         return this;
      }

      public RSAKey.Builder keyID(String kid) {
         this.kid = kid;
         return this;
      }

      public RSAKey.Builder keyIDFromThumbprint() throws JOSEException {
         return this.keyIDFromThumbprint("SHA-256");
      }

      public RSAKey.Builder keyIDFromThumbprint(String hashAlg) throws JOSEException {
         LinkedHashMap<String, Object> requiredParams = new LinkedHashMap<>();
         requiredParams.put("e", this.e.toString());
         requiredParams.put("kty", KeyType.RSA.getValue());
         requiredParams.put("n", this.n.toString());
         this.kid = ThumbprintUtils.compute(hashAlg, requiredParams).toString();
         return this;
      }

      public RSAKey.Builder x509CertURL(URI x5u) {
         this.x5u = x5u;
         return this;
      }

      @Deprecated
      public RSAKey.Builder x509CertThumbprint(Base64URL x5t) {
         this.x5t = x5t;
         return this;
      }

      public RSAKey.Builder x509CertSHA256Thumbprint(Base64URL x5t256) {
         this.x5t256 = x5t256;
         return this;
      }

      public RSAKey.Builder x509CertChain(List<Base64> x5c) {
         this.x5c = x5c;
         return this;
      }

      public RSAKey.Builder expirationTime(Date exp) {
         this.exp = exp;
         return this;
      }

      public RSAKey.Builder notBeforeTime(Date nbf) {
         this.nbf = nbf;
         return this;
      }

      public RSAKey.Builder issueTime(Date iat) {
         this.iat = iat;
         return this;
      }

      public RSAKey.Builder keyRevocation(KeyRevocation revocation) {
         this.revocation = revocation;
         return this;
      }

      public RSAKey.Builder keyStore(KeyStore keyStore) {
         this.ks = keyStore;
         return this;
      }

      public RSAKey build() {
         try {
            return new RSAKey(
               this.n,
               this.e,
               this.d,
               this.p,
               this.q,
               this.dp,
               this.dq,
               this.qi,
               this.oth,
               this.priv,
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

   @Immutable
   public static class OtherPrimesInfo implements Serializable {
      private static final long serialVersionUID = 1L;
      private final Base64URL r;
      private final Base64URL d;
      private final Base64URL t;

      public OtherPrimesInfo(Base64URL r, Base64URL d, Base64URL t) {
         this.r = Objects.requireNonNull(r);
         this.d = Objects.requireNonNull(d);
         this.t = Objects.requireNonNull(t);
      }

      public OtherPrimesInfo(RSAOtherPrimeInfo oth) {
         this.r = Base64URL.encode(oth.getPrime());
         this.d = Base64URL.encode(oth.getExponent());
         this.t = Base64URL.encode(oth.getCrtCoefficient());
      }

      public Base64URL getPrimeFactor() {
         return this.r;
      }

      public Base64URL getFactorCRTExponent() {
         return this.d;
      }

      public Base64URL getFactorCRTCoefficient() {
         return this.t;
      }

      public static List<RSAKey.OtherPrimesInfo> toList(RSAOtherPrimeInfo[] othArray) {
         List<RSAKey.OtherPrimesInfo> list = new ArrayList<>();
         if (othArray == null) {
            return list;
         } else {
            for (RSAOtherPrimeInfo oth : othArray) {
               list.add(new RSAKey.OtherPrimesInfo(oth));
            }

            return list;
         }
      }
   }
}
