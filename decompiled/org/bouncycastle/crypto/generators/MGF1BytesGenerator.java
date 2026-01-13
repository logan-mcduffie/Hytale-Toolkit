package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.MGFParameters;

public class MGF1BytesGenerator implements DerivationFunction {
   private Digest digest;
   private byte[] seed;
   private int hLen;

   public MGF1BytesGenerator(Digest var1) {
      this.digest = var1;
      this.hLen = var1.getDigestSize();
   }

   @Override
   public void init(DerivationParameters var1) {
      if (!(var1 instanceof MGFParameters)) {
         throw new IllegalArgumentException("MGF parameters required for MGF1Generator");
      } else {
         MGFParameters var2 = (MGFParameters)var1;
         this.seed = var2.getSeed();
      }
   }

   public Digest getDigest() {
      return this.digest;
   }

   private void ItoOSP(int var1, byte[] var2) {
      var2[0] = (byte)(var1 >>> 24);
      var2[1] = (byte)(var1 >>> 16);
      var2[2] = (byte)(var1 >>> 8);
      var2[3] = (byte)(var1 >>> 0);
   }

   @Override
   public int generateBytes(byte[] var1, int var2, int var3) throws DataLengthException, IllegalArgumentException {
      if (var1.length - var3 < var2) {
         throw new OutputLengthException("output buffer too small");
      } else {
         byte[] var4 = new byte[this.hLen];
         byte[] var5 = new byte[4];
         int var6 = 0;
         this.digest.reset();
         if (var3 > this.hLen) {
            do {
               this.ItoOSP(var6, var5);
               this.digest.update(this.seed, 0, this.seed.length);
               this.digest.update(var5, 0, var5.length);
               this.digest.doFinal(var4, 0);
               System.arraycopy(var4, 0, var1, var2 + var6 * this.hLen, this.hLen);
            } while (++var6 < var3 / this.hLen);
         }

         if (var6 * this.hLen < var3) {
            this.ItoOSP(var6, var5);
            this.digest.update(this.seed, 0, this.seed.length);
            this.digest.update(var5, 0, var5.length);
            this.digest.doFinal(var4, 0);
            System.arraycopy(var4, 0, var1, var2 + var6 * this.hLen, var3 - var6 * this.hLen);
         }

         return var3;
      }
   }
}
