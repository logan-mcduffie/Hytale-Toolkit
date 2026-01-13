package org.bouncycastle.crypto.agreement.kdf;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.DigestDerivationFunction;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class GSKKFDGenerator implements DigestDerivationFunction {
   private final Digest digest;
   private byte[] z;
   private int counter;
   private byte[] r;
   private byte[] buf;

   public GSKKFDGenerator(Digest var1) {
      this.digest = var1;
      this.buf = new byte[var1.getDigestSize()];
   }

   @Override
   public Digest getDigest() {
      return this.digest;
   }

   @Override
   public void init(DerivationParameters var1) {
      if (var1 instanceof GSKKDFParameters) {
         this.z = ((GSKKDFParameters)var1).getZ();
         this.counter = ((GSKKDFParameters)var1).getStartCounter();
         this.r = ((GSKKDFParameters)var1).getNonce();
      } else {
         throw new IllegalArgumentException("unkown parameters type");
      }
   }

   @Override
   public int generateBytes(byte[] var1, int var2, int var3) throws DataLengthException, IllegalArgumentException {
      if (var2 + var3 > var1.length) {
         throw new DataLengthException("output buffer too small");
      } else {
         this.digest.update(this.z, 0, this.z.length);
         byte[] var4 = Pack.intToBigEndian(this.counter++);
         this.digest.update(var4, 0, var4.length);
         if (this.r != null) {
            this.digest.update(this.r, 0, this.r.length);
         }

         this.digest.doFinal(this.buf, 0);
         System.arraycopy(this.buf, 0, var1, var2, var3);
         Arrays.clear(this.buf);
         return var3;
      }
   }
}
