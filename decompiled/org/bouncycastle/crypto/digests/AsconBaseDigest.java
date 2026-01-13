package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.engines.AsconPermutationFriend;

abstract class AsconBaseDigest extends BufferBaseDigest {
   AsconPermutationFriend.AsconPermutation p;
   protected int ASCON_PB_ROUNDS = 12;

   protected AsconBaseDigest() {
      super(BufferBaseDigest.ProcessingBufferType.Immediate, 8);
      this.p = AsconPermutationFriend.getAsconPermutation(ISAPDigest.Friend.getFriend(AsconBaseDigest.Friend.INSTANCE));
      this.DigestSize = 32;
   }

   protected abstract long pad(int var1);

   protected abstract long loadBytes(byte[] var1, int var2);

   protected abstract long loadBytes(byte[] var1, int var2, int var3);

   protected abstract void setBytes(long var1, byte[] var3, int var4);

   protected abstract void setBytes(long var1, byte[] var3, int var4, int var5);

   @Override
   protected void processBytes(byte[] var1, int var2) {
      this.p.x0 = this.p.x0 ^ this.loadBytes(var1, var2);
      this.p.p(this.ASCON_PB_ROUNDS);
   }

   @Override
   protected void finish(byte[] var1, int var2) {
      this.padAndAbsorb();
      this.squeeze(var1, var2, this.DigestSize);
   }

   protected void padAndAbsorb() {
      this.p.x0 = this.p.x0 ^ this.loadBytes(this.m_buf, 0, this.m_bufPos) ^ this.pad(this.m_bufPos);
      this.p.p(12);
   }

   protected void squeeze(byte[] var1, int var2, int var3) {
      while (var3 > this.BlockSize) {
         this.setBytes(this.p.x0, var1, var2);
         this.p.p(this.ASCON_PB_ROUNDS);
         var2 += this.BlockSize;
         var3 -= this.BlockSize;
      }

      this.setBytes(this.p.x0, var1, var2, var3);
   }

   protected int hash(byte[] var1, int var2, int var3) {
      this.ensureSufficientOutputBuffer(var1, var2, var3);
      this.padAndAbsorb();
      this.squeeze(var1, var2, var3);
      return var3;
   }

   protected void ensureSufficientOutputBuffer(byte[] var1, int var2, int var3) {
      if (var2 + var3 > var1.length) {
         throw new OutputLengthException("output buffer is too short");
      }
   }

   public static class Friend {
      private static final AsconBaseDigest.Friend INSTANCE = new AsconBaseDigest.Friend();

      private Friend() {
      }
   }
}
