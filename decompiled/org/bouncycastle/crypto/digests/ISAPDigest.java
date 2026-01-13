package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.engines.AsconPermutationFriend;
import org.bouncycastle.util.Pack;

public class ISAPDigest extends BufferBaseDigest {
   private final AsconPermutationFriend.AsconPermutation p = AsconPermutationFriend.getAsconPermutation(ISAPDigest.Friend.INSTANCE);

   public ISAPDigest() {
      super(BufferBaseDigest.ProcessingBufferType.Immediate, 8);
      this.DigestSize = 32;
      this.algorithmName = "ISAP Hash";
      this.reset();
   }

   @Override
   protected void processBytes(byte[] var1, int var2) {
      this.p.x0 = this.p.x0 ^ Pack.bigEndianToLong(var1, var2);
      this.p.p(12);
   }

   @Override
   protected void finish(byte[] var1, int var2) {
      this.p.x0 = this.p.x0 ^ 128L << (7 - this.m_bufPos << 3);

      while (this.m_bufPos > 0) {
         this.p.x0 = this.p.x0 ^ (this.m_buf[--this.m_bufPos] & 255L) << (7 - this.m_bufPos << 3);
      }

      for (int var3 = 0; var3 < 4; var3++) {
         this.p.p(12);
         Pack.longToBigEndian(this.p.x0, var1, var2);
         var2 += 8;
      }
   }

   @Override
   public void reset() {
      super.reset();
      this.p.set(-1255492011513352131L, -8380609354527731710L, -5437372128236807582L, 4834782570098516968L, 3787428097924915520L);
   }

   public static class Friend {
      private static final ISAPDigest.Friend INSTANCE = new ISAPDigest.Friend();

      private Friend() {
      }

      static ISAPDigest.Friend getFriend(AsconBaseDigest.Friend var0) {
         if (null == var0) {
            throw new NullPointerException("This method is only for use by AsconBaseDigest");
         } else {
            return INSTANCE;
         }
      }
   }
}
