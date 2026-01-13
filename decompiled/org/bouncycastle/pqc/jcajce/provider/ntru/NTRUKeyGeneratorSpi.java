package org.bouncycastle.pqc.jcajce.provider.ntru;

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
import org.bouncycastle.pqc.crypto.ntru.NTRUKEMExtractor;
import org.bouncycastle.pqc.crypto.ntru.NTRUKEMGenerator;
import org.bouncycastle.util.Arrays;

public class NTRUKeyGeneratorSpi extends KeyGeneratorSpi {
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
         BCNTRUPublicKey var9 = (BCNTRUPublicKey)this.genSpec.getPublicKey();
         NTRUKEMGenerator var10 = new NTRUKEMGenerator(this.random);
         SecretWithEncapsulation var11 = var10.generateEncapsulated(var9.getKeyParams());
         byte[] var12 = var11.getSecret();
         byte[] var13 = Arrays.copyOfRange(var12, 0, (this.genSpec.getKeySize() + 7) / 8);
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
         BCNTRUPrivateKey var1 = (BCNTRUPrivateKey)this.extSpec.getPrivateKey();
         NTRUKEMExtractor var2 = new NTRUKEMExtractor(var1.getKeyParams());
         byte[] var3 = this.extSpec.getEncapsulation();
         byte[] var4 = var2.extractSecret(var3);
         byte[] var5 = Arrays.copyOfRange(var4, 0, (this.extSpec.getKeySize() + 7) / 8);
         Arrays.clear(var4);
         SecretKeyWithEncapsulation var6 = new SecretKeyWithEncapsulation(new SecretKeySpec(var5, this.extSpec.getKeyAlgorithmName()), var3);
         Arrays.clear(var5);
         return var6;
      }
   }
}
