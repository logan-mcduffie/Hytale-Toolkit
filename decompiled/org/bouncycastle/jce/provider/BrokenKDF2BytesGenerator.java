package org.bouncycastle.jce.provider;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KDFParameters;

public class BrokenKDF2BytesGenerator implements DerivationFunction {
   private Digest digest;
   private byte[] shared;
   private byte[] iv;

   public BrokenKDF2BytesGenerator(Digest var1) {
      this.digest = var1;
   }

   @Override
   public void init(DerivationParameters var1) {
      if (!(var1 instanceof KDFParameters)) {
         throw new IllegalArgumentException("KDF parameters required for generator");
      } else {
         KDFParameters var2 = (KDFParameters)var1;
         this.shared = var2.getSharedSecret();
         this.iv = var2.getIV();
      }
   }

   public Digest getDigest() {
      return this.digest;
   }

   @Override
   public int generateBytes(byte[] var1, int var2, int var3) throws DataLengthException, IllegalArgumentException {
      if (var1.length - var3 < var2) {
         throw new OutputLengthException("output buffer too small");
      } else {
         long var4 = var3 * 8L;
         if (var4 > this.digest.getDigestSize() * 8L * 2147483648L) {
            throw new IllegalArgumentException("Output length too large");
         } else {
            int var6 = (int)(var4 / this.digest.getDigestSize());
            Object var7 = null;
            var7 = new byte[this.digest.getDigestSize()];

            for (int var8 = 1; var8 <= var6; var8++) {
               this.digest.update(this.shared, 0, this.shared.length);
               this.digest.update((byte)(var8 & 0xFF));
               this.digest.update((byte)(var8 >> 8 & 0xFF));
               this.digest.update((byte)(var8 >> 16 & 0xFF));
               this.digest.update((byte)(var8 >> 24 & 0xFF));
               this.digest.update(this.iv, 0, this.iv.length);
               this.digest.doFinal((byte[])var7, 0);
               if (var3 - var2 > ((Object[])var7).length) {
                  System.arraycopy(var7, 0, var1, var2, ((Object[])var7).length);
                  var2 += ((Object[])var7).length;
               } else {
                  System.arraycopy(var7, 0, var1, var2, var3 - var2);
               }
            }

            this.digest.reset();
            return var3;
         }
      }
   }
}
