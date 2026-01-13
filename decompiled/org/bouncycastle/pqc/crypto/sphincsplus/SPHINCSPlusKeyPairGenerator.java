package org.bouncycastle.pqc.crypto.sphincsplus;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class SPHINCSPlusKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private SecureRandom random;
   private SPHINCSPlusParameters parameters;

   @Override
   public void init(KeyGenerationParameters var1) {
      this.random = var1.getRandom();
      this.parameters = ((SPHINCSPlusKeyGenerationParameters)var1).getParameters();
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      SPHINCSPlusEngine var1 = this.parameters.getEngine();
      byte[] var2;
      SK var3;
      if (var1 instanceof SPHINCSPlusEngine.HarakaSEngine) {
         byte[] var4 = this.sec_rand(var1.N * 3);
         byte[] var5 = new byte[var1.N];
         byte[] var6 = new byte[var1.N];
         var2 = new byte[var1.N];
         System.arraycopy(var4, 0, var5, 0, var1.N);
         System.arraycopy(var4, var1.N, var6, 0, var1.N);
         System.arraycopy(var4, var1.N << 1, var2, 0, var1.N);
         var3 = new SK(var5, var6);
      } else {
         var3 = new SK(this.sec_rand(var1.N), this.sec_rand(var1.N));
         var2 = this.sec_rand(var1.N);
      }

      var1.init(var2);
      PK var7 = new PK(var2, (new HT(var1, var3.seed, var2)).htPubKey);
      return new AsymmetricCipherKeyPair(
         (AsymmetricKeyParameter)(new SPHINCSPlusPublicKeyParameters(this.parameters, var7)),
         (AsymmetricKeyParameter)(new SPHINCSPlusPrivateKeyParameters(this.parameters, var3, var7))
      );
   }

   private byte[] sec_rand(int var1) {
      byte[] var2 = new byte[var1];
      this.random.nextBytes(var2);
      return var2;
   }
}
