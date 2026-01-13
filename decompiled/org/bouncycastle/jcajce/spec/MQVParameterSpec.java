package org.bouncycastle.jcajce.spec;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.util.Arrays;

public class MQVParameterSpec implements AlgorithmParameterSpec {
   private final PublicKey ephemeralPublicKey;
   private final PrivateKey ephemeralPrivateKey;
   private final PublicKey otherPartyEphemeralKey;
   private final byte[] userKeyingMaterial;

   public MQVParameterSpec(PublicKey var1, PrivateKey var2, PublicKey var3, byte[] var4) {
      this.ephemeralPublicKey = var1;
      this.ephemeralPrivateKey = var2;
      this.otherPartyEphemeralKey = var3;
      this.userKeyingMaterial = Arrays.clone(var4);
   }

   public MQVParameterSpec(PublicKey var1, PrivateKey var2, PublicKey var3) {
      this(var1, var2, var3, null);
   }

   public MQVParameterSpec(KeyPair var1, PublicKey var2, byte[] var3) {
      this(var1.getPublic(), var1.getPrivate(), var2, var3);
   }

   public MQVParameterSpec(PrivateKey var1, PublicKey var2, byte[] var3) {
      this(null, var1, var2, var3);
   }

   public MQVParameterSpec(KeyPair var1, PublicKey var2) {
      this(var1.getPublic(), var1.getPrivate(), var2, null);
   }

   public MQVParameterSpec(PrivateKey var1, PublicKey var2) {
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
