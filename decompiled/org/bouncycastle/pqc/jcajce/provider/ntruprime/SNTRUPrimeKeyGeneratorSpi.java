package org.bouncycastle.pqc.jcajce.provider.ntruprime;

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
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimeKEMExtractor;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimeKEMGenerator;
import org.bouncycastle.util.Arrays;

public class SNTRUPrimeKeyGeneratorSpi extends KeyGeneratorSpi {
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
         BCSNTRUPrimePublicKey var7 = (BCSNTRUPrimePublicKey)this.genSpec.getPublicKey();
         SNTRUPrimeKEMGenerator var8 = new SNTRUPrimeKEMGenerator(this.random);
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
         BCSNTRUPrimePrivateKey var1 = (BCSNTRUPrimePrivateKey)this.extSpec.getPrivateKey();
         SNTRUPrimeKEMExtractor var2 = new SNTRUPrimeKEMExtractor(var1.getKeyParams());
         byte[] var3 = this.extSpec.getEncapsulation();
         byte[] var4 = var2.extractSecret(var3);
         SecretKeyWithEncapsulation var5 = new SecretKeyWithEncapsulation(new SecretKeySpec(var4, this.extSpec.getKeyAlgorithmName()), var3);
         Arrays.clear(var4);
         return var5;
      }
   }
}
