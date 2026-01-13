package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.Xof;

abstract class AsconXofBase extends AsconBaseDigest implements Xof {
   private boolean m_squeezing;
   private final byte[] buffer = new byte[this.BlockSize];
   private int bytesInBuffer;

   @Override
   public void update(byte var1) {
      this.ensureNoAbsorbWhileSqueezing(this.m_squeezing);
      super.update(var1);
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      this.ensureNoAbsorbWhileSqueezing(this.m_squeezing);
      super.update(var1, var2, var3);
   }

   @Override
   public int doOutput(byte[] var1, int var2, int var3) {
      this.ensureSufficientOutputBuffer(var1, var2, var3);
      int var4 = 0;
      if (this.bytesInBuffer != 0) {
         int var5 = this.BlockSize - this.bytesInBuffer;
         int var6 = Math.min(var3, this.bytesInBuffer);
         System.arraycopy(this.buffer, var5, var1, var2, var6);
         this.bytesInBuffer -= var6;
         var4 += var6;
      }

      int var7 = var3 - var4;
      if (var7 >= this.BlockSize) {
         int var8 = var7 - var7 % this.BlockSize;
         var4 += this.hash(var1, var2 + var4, var8);
      }

      if (var4 < var3) {
         this.hash(this.buffer, 0, this.BlockSize);
         int var9 = var3 - var4;
         System.arraycopy(this.buffer, 0, var1, var2 + var4, var9);
         this.bytesInBuffer = this.buffer.length - var9;
         var4 += var9;
      }

      return var4;
   }

   @Override
   public int doFinal(byte[] var1, int var2, int var3) {
      int var4 = this.doOutput(var1, var2, var3);
      this.reset();
      return var4;
   }

   @Override
   public void reset() {
      this.m_squeezing = false;
      this.bytesInBuffer = 0;
      super.reset();
   }

   @Override
   protected void padAndAbsorb() {
      if (!this.m_squeezing) {
         this.m_squeezing = true;
         super.padAndAbsorb();
      } else {
         this.p.p(this.ASCON_PB_ROUNDS);
      }
   }

   private void ensureNoAbsorbWhileSqueezing(boolean var1) {
      if (var1) {
         throw new IllegalStateException("attempt to absorb while squeezing");
      }
   }
}
