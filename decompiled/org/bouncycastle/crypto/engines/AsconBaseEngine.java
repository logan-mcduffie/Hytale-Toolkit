package org.bouncycastle.crypto.engines;

abstract class AsconBaseEngine extends AEADBaseEngine {
   protected int nr;
   protected long K0;
   protected long K1;
   protected long N0;
   protected long N1;
   protected long ASCON_IV;
   AsconPermutationFriend.AsconPermutation p = new AsconPermutationFriend.AsconPermutation();
   protected long dsep;

   protected abstract long pad(int var1);

   protected abstract long loadBytes(byte[] var1, int var2);

   protected abstract void setBytes(long var1, byte[] var3, int var4);

   protected abstract void ascon_aeadinit();

   @Override
   protected void finishAAD(AEADBaseEngine.State var1, boolean var2) {
      switch (this.m_state.ord) {
         case 2:
         case 6:
            this.processFinalAAD();
            this.p.p(this.nr);
         default:
            this.p.x4 = this.p.x4 ^ this.dsep;
            this.m_aadPos = 0;
            this.m_state = var1;
      }
   }

   protected abstract void processFinalDecrypt(byte[] var1, int var2, byte[] var3, int var4);

   protected abstract void processFinalEncrypt(byte[] var1, int var2, byte[] var3, int var4);

   @Override
   protected void processBufferAAD(byte[] var1, int var2) {
      this.p.x0 = this.p.x0 ^ this.loadBytes(var1, var2);
      if (this.BlockSize == 16) {
         this.p.x1 = this.p.x1 ^ this.loadBytes(var1, 8 + var2);
      }

      this.p.p(this.nr);
   }

   @Override
   protected void processFinalBlock(byte[] var1, int var2) {
      if (this.forEncryption) {
         this.processFinalEncrypt(this.m_buf, this.m_bufPos, var1, var2);
      } else {
         this.processFinalDecrypt(this.m_buf, this.m_bufPos, var1, var2);
      }

      this.setBytes(this.p.x3, this.mac, 0);
      this.setBytes(this.p.x4, this.mac, 8);
   }

   @Override
   protected void processBufferDecrypt(byte[] var1, int var2, byte[] var3, int var4) {
      long var5 = this.loadBytes(var1, var2);
      this.setBytes(this.p.x0 ^ var5, var3, var4);
      this.p.x0 = var5;
      if (this.BlockSize == 16) {
         long var7 = this.loadBytes(var1, var2 + 8);
         this.setBytes(this.p.x1 ^ var7, var3, var4 + 8);
         this.p.x1 = var7;
      }

      this.p.p(this.nr);
   }

   @Override
   protected void processBufferEncrypt(byte[] var1, int var2, byte[] var3, int var4) {
      this.p.x0 = this.p.x0 ^ this.loadBytes(var1, var2);
      this.setBytes(this.p.x0, var3, var4);
      if (this.BlockSize == 16) {
         this.p.x1 = this.p.x1 ^ this.loadBytes(var1, var2 + 8);
         this.setBytes(this.p.x1, var3, var4 + 8);
      }

      this.p.p(this.nr);
   }

   @Override
   protected void reset(boolean var1) {
      super.reset(var1);
      this.ascon_aeadinit();
   }

   public abstract String getAlgorithmVersion();
}
