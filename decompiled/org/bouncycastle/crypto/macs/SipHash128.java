package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.util.Pack;

public class SipHash128 extends SipHash {
   public SipHash128() {
   }

   public SipHash128(int var1, int var2) {
      super(var1, var2);
   }

   @Override
   public String getAlgorithmName() {
      return "SipHash128-" + this.c + "-" + this.d;
   }

   @Override
   public int getMacSize() {
      return 16;
   }

   @Override
   public long doFinal() throws DataLengthException, IllegalStateException {
      throw new UnsupportedOperationException("doFinal() is not supported");
   }

   @Override
   public int doFinal(byte[] var1, int var2) throws DataLengthException, IllegalStateException {
      this.m = this.m >>> (7 - this.wordPos << 3);
      this.m >>>= 8;
      this.m = this.m | ((this.wordCount << 3) + this.wordPos & 255L) << 56;
      this.processMessageWord();
      this.v2 ^= 238L;
      this.applySipRounds(this.d);
      long var3 = this.v0 ^ this.v1 ^ this.v2 ^ this.v3;
      this.v1 ^= 221L;
      this.applySipRounds(this.d);
      long var5 = this.v0 ^ this.v1 ^ this.v2 ^ this.v3;
      this.reset();
      Pack.longToLittleEndian(var3, var1, var2);
      Pack.longToLittleEndian(var5, var1, var2 + 8);
      return 16;
   }

   @Override
   public void reset() {
      super.reset();
      this.v1 ^= 238L;
   }
}
