package org.bouncycastle.pqc.jcajce.provider.frodo;

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
import org.bouncycastle.pqc.crypto.frodo.FrodoKEMExtractor;
import org.bouncycastle.pqc.crypto.frodo.FrodoKEMGenerator;
import org.bouncycastle.util.Arrays;

public class FrodoKeyGeneratorSpi extends KeyGeneratorSpi {
   private KEMGenerateSpec genSpec;
   private SecureRandom random;
   private KEMExtractSpec extSpec;

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
      } else {
         if (!(var1 instanceof KEMExtractSpec)) {
            throw new InvalidAlgorithmParameterException("unknown spec");
         }

         this.genSpec = null;
         this.extSpec = (KEMExtractSpec)var1;
      }
   }

   @Override
   protected void engineInit(int var1, SecureRandom var2) {
      throw new UnsupportedOperationException("Operation not supported");
   }

   @Override
   protected SecretKey engineGenerateKey() {
      if (this.genSpec != null) {
         BCFrodoPublicKey var7 = (BCFrodoPublicKey)this.genSpec.getPublicKey();
         FrodoKEMGenerator var8 = new FrodoKEMGenerator(this.random);
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
         BCFrodoPrivateKey var1 = (BCFrodoPrivateKey)this.extSpec.getPrivateKey();
         FrodoKEMExtractor var2 = new FrodoKEMExtractor(var1.getKeyParams());
         byte[] var3 = this.extSpec.getEncapsulation();
         byte[] var4 = var2.extractSecret(var3);
         SecretKeyWithEncapsulation var5 = new SecretKeyWithEncapsulation(new SecretKeySpec(var4, this.extSpec.getKeyAlgorithmName()), var3);
         Arrays.clear(var4);
         return var5;
      }
   }
}
