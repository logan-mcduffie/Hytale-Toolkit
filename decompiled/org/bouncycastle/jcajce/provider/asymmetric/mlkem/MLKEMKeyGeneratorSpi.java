package org.bouncycastle.jcajce.provider.asymmetric.mlkem;

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
import org.bouncycastle.jcajce.spec.MLKEMParameterSpec;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMExtractor;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMGenerator;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;
import org.bouncycastle.pqc.jcajce.provider.util.KdfUtil;
import org.bouncycastle.util.Arrays;

public class MLKEMKeyGeneratorSpi extends KeyGeneratorSpi {
   private KEMGenerateSpec genSpec;
   private SecureRandom random;
   private KEMExtractSpec extSpec;
   private MLKEMParameters kyberParameters;

   public MLKEMKeyGeneratorSpi() {
      this(null);
   }

   protected MLKEMKeyGeneratorSpi(MLKEMParameters var1) {
      this.kyberParameters = var1;
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
         if (this.kyberParameters != null) {
            String var3 = MLKEMParameterSpec.fromName(this.kyberParameters.getName()).getName();
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
         if (this.kyberParameters != null) {
            String var4 = MLKEMParameterSpec.fromName(this.kyberParameters.getName()).getName();
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
         BCMLKEMPublicKey var9 = (BCMLKEMPublicKey)this.genSpec.getPublicKey();
         MLKEMGenerator var10 = new MLKEMGenerator(this.random);
         SecretWithEncapsulation var11 = var10.generateEncapsulated(var9.getKeyParams());
         byte[] var12 = var11.getSecret();
         byte[] var13 = KdfUtil.makeKeyBytes(this.genSpec, var12);
         Arrays.clear(var12);
         SecretKeyWithEncapsulation var14 = new SecretKeyWithEncapsulation(
            new SecretKeySpec(var13, this.genSpec.getKeyAlgorithmName()), var11.getEncapsulation()
         );

         try {
            var11.destroy();
            return var14;
         } catch (DestroyFailedException var8) {
            throw new IllegalStateException("key cleanup failed");
         }
      } else {
         BCMLKEMPrivateKey var1 = (BCMLKEMPrivateKey)this.extSpec.getPrivateKey();
         MLKEMExtractor var2 = new MLKEMExtractor(var1.getKeyParams());
         byte[] var3 = this.extSpec.getEncapsulation();
         byte[] var4 = var2.extractSecret(var3);
         byte[] var5 = KdfUtil.makeKeyBytes(this.extSpec, var4);
         Arrays.clear(var4);
         SecretKeyWithEncapsulation var6 = new SecretKeyWithEncapsulation(new SecretKeySpec(var5, this.extSpec.getKeyAlgorithmName()), var3);
         Arrays.clear(var5);
         return var6;
      }
   }

   public static class MLKEM1024 extends MLKEMKeyGeneratorSpi {
      public MLKEM1024() {
         super(MLKEMParameters.ml_kem_1024);
      }
   }

   public static class MLKEM512 extends MLKEMKeyGeneratorSpi {
      public MLKEM512() {
         super(MLKEMParameters.ml_kem_512);
      }
   }

   public static class MLKEM768 extends MLKEMKeyGeneratorSpi {
      public MLKEM768() {
         super(MLKEMParameters.ml_kem_768);
      }
   }
}
