package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.util.Pack;

public class AsconCXof128 extends AsconXofBase {
   private final long z0;
   private final long z1;
   private final long z2;
   private final long z3;
   private final long z4;

   public AsconCXof128() {
      this(new byte[0], 0, 0);
   }

   public AsconCXof128(byte[] var1) {
      this(var1, 0, var1.length);
   }

   public AsconCXof128(byte[] var1, int var2, int var3) {
      this.algorithmName = "Ascon-CXOF128";
      this.ensureSufficientInputBuffer(var1, var2, var3);
      if (var3 > 256) {
         throw new DataLengthException("customized string is too long");
      } else {
         this.initState(var1, var2, var3);
         this.z0 = this.p.x0;
         this.z1 = this.p.x1;
         this.z2 = this.p.x2;
         this.z3 = this.p.x3;
         this.z4 = this.p.x4;
      }
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
      this.p.set(this.z0, this.z1, this.z2, this.z3, this.z4);
   }

   private void initState(byte[] var1, int var2, int var3) {
      if (var3 == 0) {
         this.p.set(5768210384618244584L, 6623958265790276749L, 4252419465292010770L, 1238191464582506891L, 56353695744608240L);
      } else {
         this.p.set(7445901275803737603L, 4886737088792722364L, -1616759365661982283L, 3076320316797452470L, -8124743304765850554L);
         this.p.x0 ^= (long)var3 << 3;
         this.p.p(12);
         this.update(var1, var2, var3);
         this.padAndAbsorb();
      }

      super.reset();
   }
}
