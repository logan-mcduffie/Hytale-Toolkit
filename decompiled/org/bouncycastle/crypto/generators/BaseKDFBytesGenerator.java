package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.DigestDerivationFunction;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.ISO18033KDFParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.util.Pack;

public class BaseKDFBytesGenerator implements DigestDerivationFunction {
   private int counterStart;
   private Digest digest;
   private byte[] shared;
   private byte[] iv;

   protected BaseKDFBytesGenerator(int var1, Digest var2) {
      this.counterStart = var1;
      this.digest = var2;
   }

   @Override
   public void init(DerivationParameters var1) {
      if (var1 instanceof KDFParameters) {
         KDFParameters var2 = (KDFParameters)var1;
         this.shared = var2.getSharedSecret();
         this.iv = var2.getIV();
      } else {
         if (!(var1 instanceof ISO18033KDFParameters)) {
            throw new IllegalArgumentException("KDF parameters required for generator");
         }

         ISO18033KDFParameters var3 = (ISO18033KDFParameters)var1;
         this.shared = var3.getSeed();
         this.iv = null;
      }
   }

   @Override
   public Digest getDigest() {
      return this.digest;
   }

   @Override
   public int generateBytes(byte[] var1, int var2, int var3) throws DataLengthException, IllegalArgumentException {
      if (var1.length - var3 < var2) {
         throw new OutputLengthException("output buffer too small");
      } else {
         long var4 = var3;
         int var6 = this.digest.getDigestSize();
         if (var4 > 8589934591L) {
            throw new IllegalArgumentException("Output length too large");
         } else {
            int var7 = (int)((var4 + var6 - 1L) / var6);
            byte[] var8 = new byte[this.digest.getDigestSize()];
            byte[] var9 = new byte[4];
            Pack.intToBigEndian(this.counterStart, var9, 0);
            int var10 = this.counterStart & -256;

            for (int var11 = 0; var11 < var7; var11++) {
               this.digest.update(this.shared, 0, this.shared.length);
               this.digest.update(var9, 0, var9.length);
               if (this.iv != null) {
                  this.digest.update(this.iv, 0, this.iv.length);
               }

               this.digest.doFinal(var8, 0);
               if (var3 > var6) {
                  System.arraycopy(var8, 0, var1, var2, var6);
                  var2 += var6;
                  var3 -= var6;
               } else {
                  System.arraycopy(var8, 0, var1, var2, var3);
               }

               if (++var9[3] == 0) {
                  var10 += 256;
                  Pack.intToBigEndian(var10, var9, 0);
               }
            }

            this.digest.reset();
            return (int)var4;
         }
      }
   }
}
