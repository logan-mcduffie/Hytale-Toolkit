package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.macs.GOST28147Mac;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.ParametersWithUKM;
import org.bouncycastle.util.Arrays;

public class GOST28147WrapEngine implements Wrapper {
   private GOST28147Engine cipher = new GOST28147Engine();
   private GOST28147Mac mac = new GOST28147Mac();

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (var2 instanceof ParametersWithRandom) {
         ParametersWithRandom var3 = (ParametersWithRandom)var2;
         var2 = var3.getParameters();
      }

      ParametersWithUKM var4 = (ParametersWithUKM)var2;
      this.cipher.init(var1, var4.getParameters());
      this.mac.init(new ParametersWithIV(var4.getParameters(), var4.getUKM()));
   }

   @Override
   public String getAlgorithmName() {
      return "GOST28147Wrap";
   }

   @Override
   public byte[] wrap(byte[] var1, int var2, int var3) {
      this.mac.update(var1, var2, var3);
      byte[] var4 = new byte[var3 + this.mac.getMacSize()];
      this.cipher.processBlock(var1, var2, var4, 0);
      this.cipher.processBlock(var1, var2 + 8, var4, 8);
      this.cipher.processBlock(var1, var2 + 16, var4, 16);
      this.cipher.processBlock(var1, var2 + 24, var4, 24);
      this.mac.doFinal(var4, var3);
      return var4;
   }

   @Override
   public byte[] unwrap(byte[] var1, int var2, int var3) throws InvalidCipherTextException {
      byte[] var4 = new byte[var3 - this.mac.getMacSize()];
      this.cipher.processBlock(var1, var2, var4, 0);
      this.cipher.processBlock(var1, var2 + 8, var4, 8);
      this.cipher.processBlock(var1, var2 + 16, var4, 16);
      this.cipher.processBlock(var1, var2 + 24, var4, 24);
      byte[] var5 = new byte[this.mac.getMacSize()];
      this.mac.update(var4, 0, var4.length);
      this.mac.doFinal(var5, 0);
      byte[] var6 = new byte[this.mac.getMacSize()];
      System.arraycopy(var1, var2 + var3 - 4, var6, 0, this.mac.getMacSize());
      if (!Arrays.constantTimeAreEqual(var5, var6)) {
         throw new IllegalStateException("mac mismatch");
      } else {
         return var4;
      }
   }
}
