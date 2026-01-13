package com.nimbusds.jose.jwk.gen;

import com.google.crypto.tink.subtle.Ed25519Sign;
import com.google.crypto.tink.subtle.X25519;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.util.Base64URL;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class OctetKeyPairGenerator extends JWKGenerator<OctetKeyPair> {
   private final Curve crv;
   public static final Set<Curve> SUPPORTED_CURVES;

   public OctetKeyPairGenerator(Curve crv) {
      if (!SUPPORTED_CURVES.contains(Objects.requireNonNull(crv))) {
         throw new IllegalArgumentException("Curve not supported for OKP generation");
      } else {
         this.crv = crv;
      }
   }

   public OctetKeyPair generate() throws JOSEException {
      Base64URL privateKey;
      Base64URL publicKey;
      if (this.crv.equals(Curve.X25519)) {
         byte[] privateKeyBytes;
         byte[] publicKeyBytes;
         try {
            privateKeyBytes = X25519.generatePrivateKey();
            publicKeyBytes = X25519.publicFromPrivate(privateKeyBytes);
         } catch (InvalidKeyException var7) {
            throw new JOSEException(var7.getMessage(), var7);
         }

         privateKey = Base64URL.encode(privateKeyBytes);
         publicKey = Base64URL.encode(publicKeyBytes);
      } else {
         if (!this.crv.equals(Curve.Ed25519)) {
            throw new JOSEException("Curve not supported");
         }

         Ed25519Sign.KeyPair tinkKeyPair;
         try {
            if (this.secureRandom != null) {
               byte[] seed = new byte[32];
               this.secureRandom.nextBytes(seed);
               tinkKeyPair = Ed25519Sign.KeyPair.newKeyPairFromSeed(seed);
            } else {
               tinkKeyPair = Ed25519Sign.KeyPair.newKeyPair();
            }
         } catch (GeneralSecurityException var6) {
            throw new JOSEException(var6.getMessage(), var6);
         }

         privateKey = Base64URL.encode(tinkKeyPair.getPrivateKey());
         publicKey = Base64URL.encode(tinkKeyPair.getPublicKey());
      }

      OctetKeyPair.Builder builder = new OctetKeyPair.Builder(this.crv, publicKey)
         .d(privateKey)
         .keyUse(this.use)
         .keyOperations(this.ops)
         .algorithm(this.alg)
         .expirationTime(this.exp)
         .notBeforeTime(this.nbf)
         .issueTime(this.iat);
      if (this.tprKid) {
         builder.keyIDFromThumbprint();
      } else {
         builder.keyID(this.kid);
      }

      return builder.build();
   }

   static {
      Set<Curve> curves = new LinkedHashSet<>();
      curves.add(Curve.X25519);
      curves.add(Curve.Ed25519);
      SUPPORTED_CURVES = Collections.unmodifiableSet(curves);
   }
}
