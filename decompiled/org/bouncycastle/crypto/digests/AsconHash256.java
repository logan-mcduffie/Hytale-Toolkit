package org.bouncycastle.crypto.digests;

import org.bouncycastle.util.Pack;

public class AsconHash256 extends AsconBaseDigest {
   public AsconHash256() {
      this.algorithmName = "Ascon-Hash256";
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
      this.p.set(-7269279749984954751L, 5459383224871899602L, -5880230600644446182L, 4359436768738168243L, 1899470422303676269L);
   }
}
