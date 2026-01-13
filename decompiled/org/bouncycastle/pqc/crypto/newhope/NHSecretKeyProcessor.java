package org.bouncycastle.pqc.crypto.newhope;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.pqc.crypto.ExchangePair;
import org.bouncycastle.util.Arrays;

public class NHSecretKeyProcessor {
   private final Xof xof = new SHAKEDigest(256);

   private NHSecretKeyProcessor(byte[] var1, byte[] var2) {
      this.xof.update(var1, 0, var1.length);
      if (var2 != null) {
         this.xof.update(var2, 0, var2.length);
      }

      Arrays.fill(var1, (byte)0);
   }

   public byte[] processKey(byte[] var1) {
      byte[] var2 = new byte[var1.length];
      this.xof.doFinal(var2, 0, var2.length);
      xor(var1, var2);
      Arrays.fill(var2, (byte)0);
      return var1;
   }

   private static void xor(byte[] var0, byte[] var1) {
      for (int var2 = 0; var2 != var0.length; var2++) {
         var0[var2] ^= var1[var2];
      }
   }

   public static class PartyUBuilder {
      private final AsymmetricCipherKeyPair aKp;
      private final NHAgreement agreement = new NHAgreement();
      private byte[] sharedInfo = null;
      private boolean used = false;

      public PartyUBuilder(SecureRandom var1) {
         NHKeyPairGenerator var2 = new NHKeyPairGenerator();
         var2.init(new KeyGenerationParameters(var1, 2048));
         this.aKp = var2.generateKeyPair();
         this.agreement.init(this.aKp.getPrivate());
      }

      public NHSecretKeyProcessor.PartyUBuilder withSharedInfo(byte[] var1) {
         this.sharedInfo = Arrays.clone(var1);
         return this;
      }

      public byte[] getPartA() {
         return ((NHPublicKeyParameters)this.aKp.getPublic()).getPubData();
      }

      public NHSecretKeyProcessor build(byte[] var1) {
         if (this.used) {
            throw new IllegalStateException("builder already used");
         } else {
            this.used = true;
            return new NHSecretKeyProcessor(this.agreement.calculateAgreement(new NHPublicKeyParameters(var1)), this.sharedInfo);
         }
      }
   }

   public static class PartyVBuilder {
      protected final SecureRandom random;
      private byte[] sharedInfo = null;
      private byte[] sharedSecret = null;
      private boolean used = false;

      public PartyVBuilder(SecureRandom var1) {
         this.random = var1;
      }

      public NHSecretKeyProcessor.PartyVBuilder withSharedInfo(byte[] var1) {
         this.sharedInfo = Arrays.clone(var1);
         return this;
      }

      public byte[] getPartB(byte[] var1) {
         NHExchangePairGenerator var2 = new NHExchangePairGenerator(this.random);
         ExchangePair var3 = var2.generateExchange(new NHPublicKeyParameters(var1));
         this.sharedSecret = var3.getSharedValue();
         return ((NHPublicKeyParameters)var3.getPublicKey()).getPubData();
      }

      public NHSecretKeyProcessor build() {
         if (this.used) {
            throw new IllegalStateException("builder already used");
         } else {
            this.used = true;
            return new NHSecretKeyProcessor(this.sharedSecret, this.sharedInfo);
         }
      }
   }
}
