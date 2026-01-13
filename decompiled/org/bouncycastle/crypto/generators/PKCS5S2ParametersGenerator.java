package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.util.DigestFactory;

public class PKCS5S2ParametersGenerator extends PBEParametersGenerator {
   private Mac hMac;
   private byte[] state;

   public PKCS5S2ParametersGenerator() {
      this(DigestFactory.createSHA1());
   }

   public PKCS5S2ParametersGenerator(Digest var1) {
      this.hMac = new HMac(var1);
      this.state = new byte[this.hMac.getMacSize()];
   }

   private void F(byte[] var1, int var2, byte[] var3, byte[] var4, int var5) {
      if (var2 == 0) {
         throw new IllegalArgumentException("iteration count must be at least 1.");
      } else {
         if (var1 != null) {
            this.hMac.update(var1, 0, var1.length);
         }

         this.hMac.update(var3, 0, var3.length);
         this.hMac.doFinal(this.state, 0);
         System.arraycopy(this.state, 0, var4, var5, this.state.length);

         for (int var6 = 1; var6 < var2; var6++) {
            this.hMac.update(this.state, 0, this.state.length);
            this.hMac.doFinal(this.state, 0);

            for (int var7 = 0; var7 != this.state.length; var7++) {
               var4[var5 + var7] = (byte)(var4[var5 + var7] ^ this.state[var7]);
            }
         }
      }
   }

   private byte[] generateDerivedKey(int var1) {
      int var2 = this.hMac.getMacSize();
      int var3 = (var1 + var2 - 1) / var2;
      byte[] var4 = new byte[4];
      byte[] var5 = new byte[var3 * var2];
      int var6 = 0;
      KeyParameter var7 = new KeyParameter(this.password);
      this.hMac.init(var7);

      for (int var8 = 1; var8 <= var3; var8++) {
         int var9 = 3;

         while (++var4[var9] == 0) {
            var9--;
         }

         this.F(this.salt, this.iterationCount, var4, var5, var6);
         var6 += var2;
      }

      return var5;
   }

   @Override
   public CipherParameters generateDerivedParameters(int var1) {
      var1 /= 8;
      byte[] var2 = this.generateDerivedKey(var1);
      return new KeyParameter(var2, 0, var1);
   }

   @Override
   public CipherParameters generateDerivedParameters(int var1, int var2) {
      var1 /= 8;
      var2 /= 8;
      byte[] var3 = this.generateDerivedKey(var1 + var2);
      return new ParametersWithIV(new KeyParameter(var3, 0, var1), var3, var1, var2);
   }

   @Override
   public CipherParameters generateDerivedMacParameters(int var1) {
      return this.generateDerivedParameters(var1);
   }
}
