package com.nimbusds.jose.jwk.gen;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.util.Objects;

public class ECKeyGenerator extends JWKGenerator<ECKey> {
   private final Curve crv;

   public ECKeyGenerator(Curve crv) {
      this.crv = Objects.requireNonNull(crv);
   }

   public ECKey generate() throws JOSEException {
      ECParameterSpec ecSpec = this.crv.toECParameterSpec();

      KeyPairGenerator generator;
      try {
         if (this.keyStore != null) {
            generator = KeyPairGenerator.getInstance("EC", this.keyStore.getProvider());
         } else if (this.provider != null) {
            generator = KeyPairGenerator.getInstance("EC", this.provider);
         } else {
            generator = KeyPairGenerator.getInstance("EC");
         }

         if (this.secureRandom != null) {
            generator.initialize(ecSpec, this.secureRandom);
         } else {
            generator.initialize(ecSpec);
         }
      } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException var5) {
         throw new JOSEException(var5.getMessage(), var5);
      }

      KeyPair kp = generator.generateKeyPair();
      ECKey.Builder builder = new ECKey.Builder(this.crv, (ECPublicKey)kp.getPublic())
         .privateKey(kp.getPrivate())
         .keyUse(this.use)
         .keyOperations(this.ops)
         .algorithm(this.alg)
         .expirationTime(this.exp)
         .notBeforeTime(this.nbf)
         .issueTime(this.iat)
         .keyStore(this.keyStore);
      if (this.tprKid) {
         builder.keyIDFromThumbprint();
      } else {
         builder.keyID(this.kid);
      }

      return builder.build();
   }
}
