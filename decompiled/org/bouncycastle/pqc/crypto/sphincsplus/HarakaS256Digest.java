package org.bouncycastle.pqc.crypto.sphincsplus;

import org.bouncycastle.crypto.Digest;

class HarakaS256Digest extends HarakaSBase implements Digest {
   public HarakaS256Digest(HarakaSXof var1) {
      this.haraka256_rc = var1.haraka256_rc;
   }

   @Override
   public String getAlgorithmName() {
      return "HarakaS-256";
   }

   @Override
   public int getDigestSize() {
      return 32;
   }

   @Override
   public void update(byte var1) {
      if (this.off > 31) {
         throw new IllegalArgumentException("total input cannot be more than 32 bytes");
      } else {
         this.buffer[this.off++] = var1;
      }
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      if (this.off > 32 - var3) {
         throw new IllegalArgumentException("total input cannot be more than 32 bytes");
      } else {
         System.arraycopy(var1, var2, this.buffer, this.off, var3);
         this.off += var3;
      }
   }

   @Override
   public int doFinal(byte[] var1, int var2) {
      byte[] var3 = new byte[32];
      this.haraka256Perm(var3);
      xor(var3, 0, this.buffer, 0, var1, var2, 32);
      this.reset();
      return var1.length;
   }

   @Override
   public void reset() {
      super.reset();
   }
}
