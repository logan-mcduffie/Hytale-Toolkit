package com.nimbusds.jose.jwk;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.shaded.jcip.Immutable;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.X509CertUtils;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Immutable
public class JWKMatcher {
   private final Set<KeyType> types;
   private final Set<KeyUse> uses;
   private final Set<KeyOperation> ops;
   private final Set<Algorithm> algs;
   private final Set<String> ids;
   private final boolean withUseOnly;
   private final boolean withIDOnly;
   private final boolean privateOnly;
   private final boolean publicOnly;
   private final boolean nonRevokedOnly;
   private final boolean revokedOnly;
   private final int minSizeBits;
   private final int maxSizeBits;
   private final Set<Integer> sizesBits;
   private final Set<Curve> curves;
   private final Set<Base64URL> x5tS256s;
   private final boolean withX5COnly;

   @Deprecated
   public JWKMatcher(Set<KeyType> types, Set<KeyUse> uses, Set<KeyOperation> ops, Set<Algorithm> algs, Set<String> ids, boolean privateOnly, boolean publicOnly) {
      this(types, uses, ops, algs, ids, privateOnly, publicOnly, 0, 0);
   }

   @Deprecated
   public JWKMatcher(
      Set<KeyType> types,
      Set<KeyUse> uses,
      Set<KeyOperation> ops,
      Set<Algorithm> algs,
      Set<String> ids,
      boolean privateOnly,
      boolean publicOnly,
      int minSizeBits,
      int maxSizeBits
   ) {
      this(types, uses, ops, algs, ids, privateOnly, publicOnly, minSizeBits, maxSizeBits, null);
   }

   @Deprecated
   public JWKMatcher(
      Set<KeyType> types,
      Set<KeyUse> uses,
      Set<KeyOperation> ops,
      Set<Algorithm> algs,
      Set<String> ids,
      boolean privateOnly,
      boolean publicOnly,
      int minSizeBits,
      int maxSizeBits,
      Set<Curve> curves
   ) {
      this(types, uses, ops, algs, ids, privateOnly, publicOnly, minSizeBits, maxSizeBits, null, curves);
   }

   @Deprecated
   public JWKMatcher(
      Set<KeyType> types,
      Set<KeyUse> uses,
      Set<KeyOperation> ops,
      Set<Algorithm> algs,
      Set<String> ids,
      boolean privateOnly,
      boolean publicOnly,
      int minSizeBits,
      int maxSizeBits,
      Set<Integer> sizesBits,
      Set<Curve> curves
   ) {
      this(types, uses, ops, algs, ids, false, false, privateOnly, publicOnly, minSizeBits, maxSizeBits, sizesBits, curves);
   }

   @Deprecated
   public JWKMatcher(
      Set<KeyType> types,
      Set<KeyUse> uses,
      Set<KeyOperation> ops,
      Set<Algorithm> algs,
      Set<String> ids,
      boolean withUseOnly,
      boolean withIDOnly,
      boolean privateOnly,
      boolean publicOnly,
      int minSizeBits,
      int maxSizeBits,
      Set<Integer> sizesBits,
      Set<Curve> curves
   ) {
      this(types, uses, ops, algs, ids, withUseOnly, withIDOnly, privateOnly, publicOnly, minSizeBits, maxSizeBits, sizesBits, curves, null);
   }

   @Deprecated
   public JWKMatcher(
      Set<KeyType> types,
      Set<KeyUse> uses,
      Set<KeyOperation> ops,
      Set<Algorithm> algs,
      Set<String> ids,
      boolean withUseOnly,
      boolean withIDOnly,
      boolean privateOnly,
      boolean publicOnly,
      int minSizeBits,
      int maxSizeBits,
      Set<Integer> sizesBits,
      Set<Curve> curves,
      Set<Base64URL> x5tS256s
   ) {
      this(types, uses, ops, algs, ids, withUseOnly, withIDOnly, privateOnly, publicOnly, minSizeBits, maxSizeBits, sizesBits, curves, x5tS256s, false);
   }

   @Deprecated
   public JWKMatcher(
      Set<KeyType> types,
      Set<KeyUse> uses,
      Set<KeyOperation> ops,
      Set<Algorithm> algs,
      Set<String> ids,
      boolean withUseOnly,
      boolean withIDOnly,
      boolean privateOnly,
      boolean publicOnly,
      int minSizeBits,
      int maxSizeBits,
      Set<Integer> sizesBits,
      Set<Curve> curves,
      Set<Base64URL> x5tS256s,
      boolean withX5COnly
   ) {
      this(
         types,
         uses,
         ops,
         algs,
         ids,
         withUseOnly,
         withIDOnly,
         privateOnly,
         publicOnly,
         false,
         false,
         minSizeBits,
         maxSizeBits,
         sizesBits,
         curves,
         x5tS256s,
         withX5COnly
      );
   }

   public JWKMatcher(
      Set<KeyType> types,
      Set<KeyUse> uses,
      Set<KeyOperation> ops,
      Set<Algorithm> algs,
      Set<String> ids,
      boolean withUseOnly,
      boolean withIDOnly,
      boolean privateOnly,
      boolean publicOnly,
      boolean nonRevokedOnly,
      boolean revokedOnly,
      int minSizeBits,
      int maxSizeBits,
      Set<Integer> sizesBits,
      Set<Curve> curves,
      Set<Base64URL> x5tS256s,
      boolean withX5COnly
   ) {
      this.types = types;
      this.uses = uses;
      this.ops = ops;
      this.algs = algs;
      this.ids = ids;
      this.withUseOnly = withUseOnly;
      this.withIDOnly = withIDOnly;
      this.privateOnly = privateOnly;
      this.publicOnly = publicOnly;
      this.nonRevokedOnly = nonRevokedOnly;
      this.revokedOnly = revokedOnly;
      this.minSizeBits = minSizeBits;
      this.maxSizeBits = maxSizeBits;
      this.sizesBits = sizesBits;
      this.curves = curves;
      this.x5tS256s = x5tS256s;
      this.withX5COnly = withX5COnly;
   }

   public static JWKMatcher forJWEHeader(JWEHeader jweHeader) {
      return new JWKMatcher.Builder()
         .keyType(KeyType.forAlgorithm(jweHeader.getAlgorithm()))
         .keyID(jweHeader.getKeyID())
         .keyUses(KeyUse.ENCRYPTION, null)
         .algorithms(jweHeader.getAlgorithm(), null)
         .build();
   }

   public static JWKMatcher forJWSHeader(JWSHeader jwsHeader) {
      JWSAlgorithm algorithm = jwsHeader.getAlgorithm();
      if (JWSAlgorithm.Family.RSA.contains(algorithm) || JWSAlgorithm.Family.EC.contains(algorithm)) {
         return new JWKMatcher.Builder()
            .keyType(KeyType.forAlgorithm(algorithm))
            .keyID(jwsHeader.getKeyID())
            .keyUses(KeyUse.SIGNATURE, null)
            .algorithms(algorithm, null)
            .x509CertSHA256Thumbprint(jwsHeader.getX509CertSHA256Thumbprint())
            .build();
      } else if (JWSAlgorithm.Family.HMAC_SHA.contains(algorithm)) {
         return new JWKMatcher.Builder()
            .keyType(KeyType.forAlgorithm(algorithm))
            .keyID(jwsHeader.getKeyID())
            .privateOnly(true)
            .algorithms(algorithm, null)
            .build();
      } else {
         return JWSAlgorithm.Family.ED.contains(algorithm)
            ? new JWKMatcher.Builder()
               .keyType(KeyType.forAlgorithm(algorithm))
               .keyID(jwsHeader.getKeyID())
               .keyUses(KeyUse.SIGNATURE, null)
               .algorithms(algorithm, null)
               .curves(Curve.forJWSAlgorithm(algorithm))
               .build()
            : null;
      }
   }

   public Set<KeyType> getKeyTypes() {
      return this.types;
   }

   public Set<KeyUse> getKeyUses() {
      return this.uses;
   }

   public Set<KeyOperation> getKeyOperations() {
      return this.ops;
   }

   public Set<Algorithm> getAlgorithms() {
      return this.algs;
   }

   public Set<String> getKeyIDs() {
      return this.ids;
   }

   @Deprecated
   public boolean hasKeyUse() {
      return this.isWithKeyUseOnly();
   }

   public boolean isWithKeyUseOnly() {
      return this.withUseOnly;
   }

   @Deprecated
   public boolean hasKeyID() {
      return this.isWithKeyIDOnly();
   }

   public boolean isWithKeyIDOnly() {
      return this.withIDOnly;
   }

   public boolean isPrivateOnly() {
      return this.privateOnly;
   }

   public boolean isPublicOnly() {
      return this.publicOnly;
   }

   public boolean isNonRevokedOnly() {
      return this.nonRevokedOnly;
   }

   public boolean isRevokedOnly() {
      return this.revokedOnly;
   }

   @Deprecated
   public int getMinSize() {
      return this.getMinKeySize();
   }

   public int getMinKeySize() {
      return this.minSizeBits;
   }

   @Deprecated
   public int getMaxSize() {
      return this.getMaxKeySize();
   }

   public int getMaxKeySize() {
      return this.maxSizeBits;
   }

   public Set<Integer> getKeySizes() {
      return this.sizesBits;
   }

   public Set<Curve> getCurves() {
      return this.curves;
   }

   public Set<Base64URL> getX509CertSHA256Thumbprints() {
      return this.x5tS256s;
   }

   @Deprecated
   public boolean hasX509CertChain() {
      return this.isWithX509CertChainOnly();
   }

   public boolean isWithX509CertChainOnly() {
      return this.withX5COnly;
   }

   public boolean matches(JWK key) {
      if (this.withUseOnly && key.getKeyUse() == null) {
         return false;
      } else if (!this.withIDOnly || key.getKeyID() != null && !key.getKeyID().trim().isEmpty()) {
         if (this.privateOnly && !key.isPrivate()) {
            return false;
         } else if (this.publicOnly && key.isPrivate()) {
            return false;
         } else if (this.nonRevokedOnly && key.getKeyRevocation() != null) {
            return false;
         } else if (this.revokedOnly && key.getKeyRevocation() == null) {
            return false;
         } else if (this.types != null && !this.types.contains(key.getKeyType())) {
            return false;
         } else if (this.uses != null && !this.uses.contains(key.getKeyUse())) {
            return false;
         } else if (this.ops == null
            || this.ops.contains(null) && key.getKeyOperations() == null
            || key.getKeyOperations() != null && this.ops.containsAll(key.getKeyOperations())) {
            if (this.algs != null && !this.algs.contains(key.getAlgorithm())) {
               return false;
            } else if (this.ids != null && !this.ids.contains(key.getKeyID())) {
               return false;
            } else if (this.minSizeBits > 0 && key.size() < this.minSizeBits) {
               return false;
            } else if (this.maxSizeBits > 0 && key.size() > this.maxSizeBits) {
               return false;
            } else if (this.sizesBits != null && !this.sizesBits.contains(key.size())) {
               return false;
            } else {
               if (this.curves != null) {
                  if (!(key instanceof CurveBasedJWK)) {
                     return false;
                  }

                  CurveBasedJWK curveBasedJWK = (CurveBasedJWK)key;
                  if (!this.curves.contains(curveBasedJWK.getCurve())) {
                     return false;
                  }
               }

               if (this.x5tS256s != null) {
                  boolean matchingCertFound = false;
                  if (key.getX509CertChain() != null && !key.getX509CertChain().isEmpty()) {
                     try {
                        X509Certificate cert = X509CertUtils.parseWithException(key.getX509CertChain().get(0).decode());
                        matchingCertFound = this.x5tS256s.contains(X509CertUtils.computeSHA256Thumbprint(cert));
                     } catch (CertificateException var4) {
                     }
                  }

                  boolean matchingX5T256Found = this.x5tS256s.contains(key.getX509CertSHA256Thumbprint());
                  if (!matchingCertFound && !matchingX5T256Found) {
                     return false;
                  }
               }

               return !this.withX5COnly ? true : key.getX509CertChain() != null && !key.getX509CertChain().isEmpty();
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      append(sb, "kty", this.types);
      append(sb, "use", this.uses);
      append(sb, "key_ops", this.ops);
      append(sb, "alg", this.algs);
      append(sb, "kid", this.ids);
      if (this.withUseOnly) {
         sb.append("with_use_only=true ");
      }

      if (this.withIDOnly) {
         sb.append("with_id_only=true ");
      }

      if (this.privateOnly) {
         sb.append("private_only=true ");
      }

      if (this.publicOnly) {
         sb.append("public_only=true ");
      }

      if (this.nonRevokedOnly) {
         sb.append("non_revoked_only=true ");
      }

      if (this.revokedOnly) {
         sb.append("revoked_only=true ");
      }

      if (this.minSizeBits > 0) {
         sb.append("min_size=" + this.minSizeBits + " ");
      }

      if (this.maxSizeBits > 0) {
         sb.append("max_size=" + this.maxSizeBits + " ");
      }

      append(sb, "size", this.sizesBits);
      append(sb, "crv", this.curves);
      append(sb, "x5t#S256", this.x5tS256s);
      if (this.withX5COnly) {
         sb.append("with_x5c_only=true");
      }

      return sb.toString().trim();
   }

   private static void append(StringBuilder sb, String key, Set<?> values) {
      if (values != null) {
         sb.append(key);
         sb.append('=');
         if (values.size() == 1) {
            Object value = values.iterator().next();
            if (value == null) {
               sb.append("ANY");
            } else {
               sb.append(value.toString().trim());
            }
         } else {
            sb.append(values.toString().trim());
         }

         sb.append(' ');
      }
   }

   public static class Builder {
      private Set<KeyType> types;
      private Set<KeyUse> uses;
      private Set<KeyOperation> ops;
      private Set<Algorithm> algs;
      private Set<String> ids;
      private boolean withUseOnly = false;
      private boolean withIDOnly = false;
      private boolean privateOnly = false;
      private boolean publicOnly = false;
      private boolean nonRevokedOnly = false;
      private boolean revokedOnly = false;
      private int minSizeBits = 0;
      private int maxSizeBits = 0;
      private Set<Integer> sizesBits;
      private Set<Curve> curves;
      private Set<Base64URL> x5tS256s;
      private boolean withX5COnly = false;

      public Builder() {
      }

      public Builder(JWKMatcher jwkMatcher) {
         this.types = jwkMatcher.getKeyTypes();
         this.uses = jwkMatcher.getKeyUses();
         this.ops = jwkMatcher.getKeyOperations();
         this.algs = jwkMatcher.getAlgorithms();
         this.ids = jwkMatcher.getKeyIDs();
         this.withUseOnly = jwkMatcher.isWithKeyUseOnly();
         this.withIDOnly = jwkMatcher.isWithKeyIDOnly();
         this.privateOnly = jwkMatcher.isPrivateOnly();
         this.publicOnly = jwkMatcher.isPublicOnly();
         this.nonRevokedOnly = jwkMatcher.isNonRevokedOnly();
         this.revokedOnly = jwkMatcher.isNonRevokedOnly();
         this.minSizeBits = jwkMatcher.getMinKeySize();
         this.maxSizeBits = jwkMatcher.getMaxKeySize();
         this.sizesBits = jwkMatcher.getKeySizes();
         this.curves = jwkMatcher.getCurves();
         this.x5tS256s = jwkMatcher.getX509CertSHA256Thumbprints();
         this.withX5COnly = jwkMatcher.isWithX509CertChainOnly();
      }

      public JWKMatcher.Builder keyType(KeyType kty) {
         if (kty == null) {
            this.types = null;
         } else {
            this.types = new HashSet<>(Collections.singletonList(kty));
         }

         return this;
      }

      public JWKMatcher.Builder keyTypes(KeyType... types) {
         this.keyTypes(new LinkedHashSet<>(Arrays.asList(types)));
         return this;
      }

      public JWKMatcher.Builder keyTypes(Set<KeyType> types) {
         this.types = types;
         return this;
      }

      public JWKMatcher.Builder keyUse(KeyUse use) {
         if (use == null) {
            this.uses = null;
         } else {
            this.uses = new HashSet<>(Collections.singletonList(use));
         }

         return this;
      }

      public JWKMatcher.Builder keyUses(KeyUse... uses) {
         this.keyUses(new LinkedHashSet<>(Arrays.asList(uses)));
         return this;
      }

      public JWKMatcher.Builder keyUses(Set<KeyUse> uses) {
         this.uses = uses;
         return this;
      }

      public JWKMatcher.Builder keyOperation(KeyOperation op) {
         if (op == null) {
            this.ops = null;
         } else {
            this.ops = new HashSet<>(Collections.singletonList(op));
         }

         return this;
      }

      public JWKMatcher.Builder keyOperations(KeyOperation... ops) {
         this.keyOperations(new LinkedHashSet<>(Arrays.asList(ops)));
         return this;
      }

      public JWKMatcher.Builder keyOperations(Set<KeyOperation> ops) {
         this.ops = ops;
         return this;
      }

      public JWKMatcher.Builder algorithm(Algorithm alg) {
         if (alg == null) {
            this.algs = null;
         } else {
            this.algs = new HashSet<>(Collections.singletonList(alg));
         }

         return this;
      }

      public JWKMatcher.Builder algorithms(Algorithm... algs) {
         this.algorithms(new LinkedHashSet<>(Arrays.asList(algs)));
         return this;
      }

      public JWKMatcher.Builder algorithms(Set<Algorithm> algs) {
         this.algs = algs;
         return this;
      }

      public JWKMatcher.Builder keyID(String id) {
         if (id == null) {
            this.ids = null;
         } else {
            this.ids = new HashSet<>(Collections.singletonList(id));
         }

         return this;
      }

      public JWKMatcher.Builder keyIDs(String... ids) {
         this.keyIDs(new LinkedHashSet<>(Arrays.asList(ids)));
         return this;
      }

      public JWKMatcher.Builder keyIDs(Set<String> ids) {
         this.ids = ids;
         return this;
      }

      @Deprecated
      public JWKMatcher.Builder hasKeyUse(boolean hasUse) {
         return this.withKeyUseOnly(hasUse);
      }

      public JWKMatcher.Builder withKeyUseOnly(boolean withUseOnly) {
         this.withUseOnly = withUseOnly;
         return this;
      }

      @Deprecated
      public JWKMatcher.Builder hasKeyID(boolean hasID) {
         return this.withKeyIDOnly(hasID);
      }

      public JWKMatcher.Builder withKeyIDOnly(boolean withIDOnly) {
         this.withIDOnly = withIDOnly;
         return this;
      }

      public JWKMatcher.Builder privateOnly(boolean privateOnly) {
         this.privateOnly = privateOnly;
         return this;
      }

      public JWKMatcher.Builder publicOnly(boolean publicOnly) {
         this.publicOnly = publicOnly;
         return this;
      }

      public JWKMatcher.Builder nonRevokedOnly(boolean nonRevokedOnly) {
         this.nonRevokedOnly = nonRevokedOnly;
         return this;
      }

      public JWKMatcher.Builder revokedOnly(boolean revokedOnly) {
         this.revokedOnly = revokedOnly;
         return this;
      }

      public JWKMatcher.Builder minKeySize(int minSizeBits) {
         this.minSizeBits = minSizeBits;
         return this;
      }

      public JWKMatcher.Builder maxKeySize(int maxSizeBits) {
         this.maxSizeBits = maxSizeBits;
         return this;
      }

      public JWKMatcher.Builder keySize(int keySizeBits) {
         if (keySizeBits <= 0) {
            this.sizesBits = null;
         } else {
            this.sizesBits = Collections.singleton(keySizeBits);
         }

         return this;
      }

      public JWKMatcher.Builder keySizes(int... keySizesBits) {
         Set<Integer> sizesSet = new LinkedHashSet<>();

         for (int keySize : keySizesBits) {
            sizesSet.add(keySize);
         }

         this.keySizes(sizesSet);
         return this;
      }

      public JWKMatcher.Builder keySizes(Set<Integer> keySizesBits) {
         this.sizesBits = keySizesBits;
         return this;
      }

      public JWKMatcher.Builder curve(Curve curve) {
         if (curve == null) {
            this.curves = null;
         } else {
            this.curves = Collections.singleton(curve);
         }

         return this;
      }

      public JWKMatcher.Builder curves(Curve... curves) {
         this.curves(new LinkedHashSet<>(Arrays.asList(curves)));
         return this;
      }

      public JWKMatcher.Builder curves(Set<Curve> curves) {
         this.curves = curves;
         return this;
      }

      public JWKMatcher.Builder x509CertSHA256Thumbprint(Base64URL x5tS256) {
         if (x5tS256 == null) {
            this.x5tS256s = null;
         } else {
            this.x5tS256s = Collections.singleton(x5tS256);
         }

         return this;
      }

      public JWKMatcher.Builder x509CertSHA256Thumbprints(Base64URL... x5tS256s) {
         return this.x509CertSHA256Thumbprints(new LinkedHashSet<>(Arrays.asList(x5tS256s)));
      }

      public JWKMatcher.Builder x509CertSHA256Thumbprints(Set<Base64URL> x5tS256s) {
         this.x5tS256s = x5tS256s;
         return this;
      }

      @Deprecated
      public JWKMatcher.Builder hasX509CertChain(boolean hasX5C) {
         return this.withX509CertChainOnly(hasX5C);
      }

      public JWKMatcher.Builder withX509CertChainOnly(boolean withX5CONly) {
         this.withX5COnly = withX5CONly;
         return this;
      }

      public JWKMatcher build() {
         return new JWKMatcher(
            this.types,
            this.uses,
            this.ops,
            this.algs,
            this.ids,
            this.withUseOnly,
            this.withIDOnly,
            this.privateOnly,
            this.publicOnly,
            this.nonRevokedOnly,
            this.revokedOnly,
            this.minSizeBits,
            this.maxSizeBits,
            this.sizesBits,
            this.curves,
            this.x5tS256s,
            this.withX5COnly
         );
      }
   }
}
