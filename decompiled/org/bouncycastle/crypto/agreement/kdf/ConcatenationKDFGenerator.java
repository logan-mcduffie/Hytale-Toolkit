package org.bouncycastle.crypto.agreement.kdf;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KDFParameters;

public class ConcatenationKDFGenerator implements DerivationFunction {
   private Digest digest;
   private byte[] shared;
   private byte[] otherInfo;
   private int hLen;

   public ConcatenationKDFGenerator(Digest var1) {
      this.digest = var1;
      this.hLen = var1.getDigestSize();
   }

   @Override
   public void init(DerivationParameters var1) {
      if (var1 instanceof KDFParameters) {
         KDFParameters var2 = (KDFParameters)var1;
         this.shared = var2.getSharedSecret();
         this.otherInfo = var2.getIV();
      } else {
         throw new IllegalArgumentException("KDF parameters required for generator");
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
      if (var3 <= 0) {
         throw new IllegalArgumentException("len must be > 0");
      } else if (var1.length - var3 < var2) {
         throw new OutputLengthException("output buffer too small");
      } else {
         byte[] var4 = new byte[this.hLen];
         byte[] var5 = new byte[4];
         int var6 = 1;
         int var7 = 0;
         this.digest.reset();
         if (var3 > this.hLen) {
            do {
               this.ItoOSP(var6, var5);
               this.digest.update(var5, 0, var5.length);
               this.digest.update(this.shared, 0, this.shared.length);
               this.digest.update(this.otherInfo, 0, this.otherInfo.length);
               this.digest.doFinal(var4, 0);
               System.arraycopy(var4, 0, var1, var2 + var7, this.hLen);
               var7 += this.hLen;
            } while (var6++ < var3 / this.hLen);
         }

         if (var7 < var3) {
            this.ItoOSP(var6, var5);
            this.digest.update(var5, 0, var5.length);
            this.digest.update(this.shared, 0, this.shared.length);
            this.digest.update(this.otherInfo, 0, this.otherInfo.length);
            this.digest.doFinal(var4, 0);
            System.arraycopy(var4, 0, var1, var2 + var7, var3 - var7);
         }

         return var3;
      }
   }
}
