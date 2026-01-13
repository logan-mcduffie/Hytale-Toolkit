package org.bouncycastle.crypto.digests;

import org.bouncycastle.util.Pack;

public class AsconXof128 extends AsconXofBase {
   public AsconXof128() {
      this.algorithmName = "Ascon-XOF-128";
      this.reset();
   }

   @Override
   protected long pad(int var1) {
      return 1L << (var1 << 3);
   }

   @Override
   protected long loadBytes(byte[] var1, int var2) {
      return Pack.littleEndianToLong(var1, var2);
   }

   @Override
   protected long loadBytes(byte[] var1, int var2, int var3) {
      return Pack.littleEndianToLong(var1, var2, var3);
   }

   @Override
   protected void setBytes(long var1, byte[] var3, int var4) {
      Pack.longToLittleEndian(var1, var3, var4);
   }

   @Override
   protected void setBytes(long var1, byte[] var3, int var4, int var5) {
      Pack.longToLittleEndian(var1, var3, var4, var5);
   }

   @Override
   public void reset() {
      super.reset();
      this.p.set(-2701369817892108309L, -3711838248891385495L, -1778763697082575311L, 1072114354614917324L, -2282070310009238562L);
   }
}
