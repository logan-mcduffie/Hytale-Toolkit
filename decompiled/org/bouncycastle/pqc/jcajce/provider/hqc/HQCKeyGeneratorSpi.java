package org.bouncycastle.pqc.jcajce.provider.hqc;

import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.KeyGeneratorSpi;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.DestroyFailedException;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.jcajce.SecretKeyWithEncapsulation;
import org.bouncycastle.jcajce.spec.KEMExtractSpec;
import org.bouncycastle.jcajce.spec.KEMGenerateSpec;
import org.bouncycastle.pqc.crypto.hqc.HQCKEMExtractor;
import org.bouncycastle.pqc.crypto.hqc.HQCKEMGenerator;
import org.bouncycastle.pqc.crypto.hqc.HQCParameters;
import org.bouncycastle.pqc.jcajce.spec.HQCParameterSpec;
import org.bouncycastle.util.Arrays;

public class HQCKeyGeneratorSpi extends KeyGeneratorSpi {
   private KEMGenerateSpec genSpec;
   private SecureRandom random;
   private KEMExtractSpec extSpec;
   private HQCParameters hqcParameters;

   public HQCKeyGeneratorSpi() {
      this(null);
   }

   public HQCKeyGeneratorSpi(HQCParameters var1) {
      this.hqcParameters = var1;
   }

   @Override
   protected void engineInit(SecureRandom var1) {
      throw new UnsupportedOperationException("Operation not supported");
   }

   @Override
   protected void engineInit(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      this.random = var2;
      if (var1 instanceof KEMGenerateSpec) {
         this.genSpec = (KEMGenerateSpec)var1;
         this.extSpec = null;
         if (this.hqcParameters != null) {
            String var3 = HQCParameterSpec.fromName(this.hqcParameters.getName()).getName();
            if (!var3.equals(this.genSpec.getPublicKey().getAlgorithm())) {
               throw new InvalidAlgorithmParameterException("key generator locked to " + var3);
            }
         }
      } else {
         if (!(var1 instanceof KEMExtractSpec)) {
            throw new InvalidAlgorithmParameterException("unknown spec");
         }

         this.genSpec = null;
         this.extSpec = (KEMExtractSpec)var1;
         if (this.hqcParameters != null) {
            String var4 = HQCParameterSpec.fromName(this.hqcParameters.getName()).getName();
            if (!var4.equals(this.extSpec.getPrivateKey().getAlgorithm())) {
               throw new InvalidAlgorithmParameterException("key generator locked to " + var4);
            }
         }
      }
   }

   @Override
   protected void engineInit(int var1, SecureRandom var2) {
      throw new UnsupportedOperationException("Operation not supported");
   }

   @Override
   protected SecretKey engineGenerateKey() {
      if (this.genSpec != null) {
         BCHQCPublicKey var7 = (BCHQCPublicKey)this.genSpec.getPublicKey();
         HQCKEMGenerator var8 = new HQCKEMGenerator(this.random);
         SecretWithEncapsulation var9 = var8.generateEncapsulated(var7.getKeyParams());
         SecretKeyWithEncapsulation var10 = new SecretKeyWithEncapsulation(
            new SecretKeySpec(var9.getSecret(), this.genSpec.getKeyAlgorithmName()), var9.getEncapsulation()
         );

         try {
            var9.destroy();
            return var10;
         } catch (DestroyFailedException var6) {
            throw new IllegalStateException("key cleanup failed");
         }
      } else {
         BCHQCPrivateKey var1 = (BCHQCPrivateKey)this.extSpec.getPrivateKey();
         HQCKEMExtractor var2 = new HQCKEMExtractor(var1.getKeyParams());
         byte[] var3 = this.extSpec.getEncapsulation();
         byte[] var4 = var2.extractSecret(var3);
         SecretKeyWithEncapsulation var5 = new SecretKeyWithEncapsulation(new SecretKeySpec(var4, this.extSpec.getKeyAlgorithmName()), var3);
         Arrays.clear(var4);
         return var5;
      }
   }

   public static class HQC128 extends HQCKeyGeneratorSpi {
      public HQC128() {
         super(HQCParameters.hqc128);
      }
   }

   public static class HQC192 extends HQCKeyGeneratorSpi {
      public HQC192() {
         super(HQCParameters.hqc192);
      }
   }

   public static class HQC256 extends HQCKeyGeneratorSpi {
      public HQC256() {
         super(HQCParameters.hqc256);
      }
   }
}
