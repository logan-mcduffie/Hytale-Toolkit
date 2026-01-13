package org.bouncycastle.jcajce.spec;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.util.Arrays;

public class DHUParameterSpec implements AlgorithmParameterSpec {
   private final PublicKey ephemeralPublicKey;
   private final PrivateKey ephemeralPrivateKey;
   private final PublicKey otherPartyEphemeralKey;
   private final byte[] userKeyingMaterial;

   public DHUParameterSpec(PublicKey var1, PrivateKey var2, PublicKey var3, byte[] var4) {
      if (var2 == null) {
         throw new IllegalArgumentException("ephemeral private key cannot be null");
      } else if (var3 == null) {
         throw new IllegalArgumentException("other party ephemeral key cannot be null");
      } else {
         this.ephemeralPublicKey = var1;
         this.ephemeralPrivateKey = var2;
         this.otherPartyEphemeralKey = var3;
         this.userKeyingMaterial = Arrays.clone(var4);
      }
   }

   public DHUParameterSpec(PublicKey var1, PrivateKey var2, PublicKey var3) {
      this(var1, var2, var3, null);
   }

   public DHUParameterSpec(KeyPair var1, PublicKey var2, byte[] var3) {
      this(var1.getPublic(), var1.getPrivate(), var2, var3);
   }

   public DHUParameterSpec(PrivateKey var1, PublicKey var2, byte[] var3) {
      this(null, var1, var2, var3);
   }

   public DHUParameterSpec(KeyPair var1, PublicKey var2) {
      this(var1.getPublic(), var1.getPrivate(), var2, null);
   }

   public DHUParameterSpec(PrivateKey var1, PublicKey var2) {
      this(null, var1, var2, null);
   }

   public PrivateKey getEphemeralPrivateKey() {
      return this.ephemeralPrivateKey;
   }

   public PublicKey getEphemeralPublicKey() {
      return this.ephemeralPublicKey;
   }

   public PublicKey getOtherPartyEphemeralKey() {
      return this.otherPartyEphemeralKey;
   }

   public byte[] getUserKeyingMaterial() {
      return Arrays.clone(this.userKeyingMaterial);
   }
}
