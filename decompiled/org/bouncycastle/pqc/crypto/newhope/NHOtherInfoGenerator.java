package org.bouncycastle.pqc.crypto.newhope;

import java.io.IOException;
import java.security.SecureRandom;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.util.DEROtherInfo;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.ExchangePair;

public class NHOtherInfoGenerator {
   protected final DEROtherInfo.Builder otherInfoBuilder;
   protected final SecureRandom random;
   protected boolean used = false;

   public NHOtherInfoGenerator(AlgorithmIdentifier var1, byte[] var2, byte[] var3, SecureRandom var4) {
      this.otherInfoBuilder = new DEROtherInfo.Builder(var1, var2, var3);
      this.random = var4;
   }

   private static byte[] getEncoded(NHPublicKeyParameters var0) {
      try {
         AlgorithmIdentifier var2 = new AlgorithmIdentifier(PQCObjectIdentifiers.newHope);
         SubjectPublicKeyInfo var1 = new SubjectPublicKeyInfo(var2, var0.getPubData());
         return var1.getEncoded();
      } catch (IOException var3) {
         return null;
      }
   }

   private static NHPublicKeyParameters getPublicKey(byte[] var0) {
      SubjectPublicKeyInfo var1 = SubjectPublicKeyInfo.getInstance(var0);
      return new NHPublicKeyParameters(var1.getPublicKeyData().getOctets());
   }

   public static class PartyU extends NHOtherInfoGenerator {
      private AsymmetricCipherKeyPair aKp;
      private NHAgreement agreement = new NHAgreement();

      public PartyU(AlgorithmIdentifier var1, byte[] var2, byte[] var3, SecureRandom var4) {
         super(var1, var2, var3, var4);
         NHKeyPairGenerator var5 = new NHKeyPairGenerator();
         var5.init(new KeyGenerationParameters(var4, 2048));
         this.aKp = var5.generateKeyPair();
         this.agreement.init(this.aKp.getPrivate());
      }

      public NHOtherInfoGenerator withSuppPubInfo(byte[] var1) {
         this.otherInfoBuilder.withSuppPubInfo(var1);
         return this;
      }

      public byte[] getSuppPrivInfoPartA() {
         return NHOtherInfoGenerator.getEncoded((NHPublicKeyParameters)this.aKp.getPublic());
      }

      public DEROtherInfo generate(byte[] var1) {
         if (this.used) {
            throw new IllegalStateException("builder already used");
         } else {
            this.used = true;
            this.otherInfoBuilder.withSuppPrivInfo(this.agreement.calculateAgreement(NHOtherInfoGenerator.getPublicKey(var1)));
            return this.otherInfoBuilder.build();
         }
      }
   }

   public static class PartyV extends NHOtherInfoGenerator {
      public PartyV(AlgorithmIdentifier var1, byte[] var2, byte[] var3, SecureRandom var4) {
         super(var1, var2, var3, var4);
      }

      public NHOtherInfoGenerator withSuppPubInfo(byte[] var1) {
         this.otherInfoBuilder.withSuppPubInfo(var1);
         return this;
      }

      public byte[] getSuppPrivInfoPartB(byte[] var1) {
         NHExchangePairGenerator var2 = new NHExchangePairGenerator(this.random);
         ExchangePair var3 = var2.generateExchange(NHOtherInfoGenerator.getPublicKey(var1));
         this.otherInfoBuilder.withSuppPrivInfo(var3.getSharedValue());
         return NHOtherInfoGenerator.getEncoded((NHPublicKeyParameters)var3.getPublicKey());
      }

      public DEROtherInfo generate() {
         if (this.used) {
            throw new IllegalStateException("builder already used");
         } else {
            this.used = true;
            return this.otherInfoBuilder.build();
         }
      }
   }
}
