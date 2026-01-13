package org.bouncycastle.pqc.crypto.sphincsplus;

import org.bouncycastle.crypto.Digest;

class HarakaS512Digest extends HarakaSBase implements Digest {
   public HarakaS512Digest(HarakaSXof var1) {
      this.haraka512_rc = var1.haraka512_rc;
   }

   @Override
   public String getAlgorithmName() {
      return "HarakaS-512";
   }

   @Override
   public int getDigestSize() {
      return 32;
   }

   @Override
   public void update(byte var1) {
      if (this.off > 63) {
         throw new IllegalArgumentException("total input cannot be more than 64 bytes");
      } else {
         this.buffer[this.off++] = var1;
      }
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      if (this.off > 64 - var3) {
         throw new IllegalArgumentException("total input cannot be more than 64 bytes");
      } else {
         System.arraycopy(var1, var2, this.buffer, this.off, var3);
         this.off += var3;
      }
   }

   @Override
   public int doFinal(byte[] var1, int var2) {
      byte[] var3 = new byte[64];
      this.haraka512Perm(var3);
      xor(var3, 8, this.buffer, 8, var1, var2, 8);
      xor(var3, 24, this.buffer, 24, var1, var2 + 8, 16);
      xor(var3, 48, this.buffer, 48, var1, var2 + 24, 8);
      this.reset();
      return var3.length;
   }

   @Override
   public void reset() {
      super.reset();
   }
}
