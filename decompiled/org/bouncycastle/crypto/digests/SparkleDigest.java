package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.engines.SparkleEngine;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

public class SparkleDigest extends BufferBaseDigest {
   private static final int RATE_WORDS = 4;
   private final int[] state;
   private final int SPARKLE_STEPS_SLIM;
   private final int SPARKLE_STEPS_BIG;
   private final int STATE_WORDS;

   public SparkleDigest(SparkleDigest.SparkleParameters var1) {
      super(BufferBaseDigest.ProcessingBufferType.Buffered, 16);
      switch (var1) {
         case ESCH256:
            this.algorithmName = "ESCH-256";
            this.DigestSize = 32;
            this.SPARKLE_STEPS_SLIM = 7;
            this.SPARKLE_STEPS_BIG = 11;
            this.STATE_WORDS = 12;
            break;
         case ESCH384:
            this.algorithmName = "ESCH-384";
            this.DigestSize = 48;
            this.SPARKLE_STEPS_SLIM = 8;
            this.SPARKLE_STEPS_BIG = 12;
            this.STATE_WORDS = 16;
            break;
         default:
            throw new IllegalArgumentException("Invalid definition of SCHWAEMM instance");
      }

      this.state = new int[this.STATE_WORDS];
   }

   @Override
   protected void processBytes(byte[] var1, int var2) {
      this.processBlock(var1, var2, this.SPARKLE_STEPS_SLIM);
   }

   @Override
   protected void finish(byte[] var1, int var2) {
      if (this.m_bufPos < this.BlockSize) {
         this.state[(this.STATE_WORDS >> 1) - 1] = this.state[(this.STATE_WORDS >> 1) - 1] ^ 16777216;
         this.m_buf[this.m_bufPos++] = -128;
         Arrays.fill(this.m_buf, this.m_bufPos, this.BlockSize, (byte)0);
      } else {
         this.state[(this.STATE_WORDS >> 1) - 1] = this.state[(this.STATE_WORDS >> 1) - 1] ^ 33554432;
      }

      this.processBlock(this.m_buf, 0, this.SPARKLE_STEPS_BIG);
      Pack.intToLittleEndian(this.state, 0, 4, var1, var2);
      if (this.STATE_WORDS == 16) {
         SparkleEngine.sparkle_opt16(SparkleDigest.Friend.INSTANCE, this.state, this.SPARKLE_STEPS_SLIM);
         Pack.intToLittleEndian(this.state, 0, 4, var1, var2 + 16);
         SparkleEngine.sparkle_opt16(SparkleDigest.Friend.INSTANCE, this.state, this.SPARKLE_STEPS_SLIM);
         Pack.intToLittleEndian(this.state, 0, 4, var1, var2 + 32);
      } else {
         SparkleEngine.sparkle_opt12(SparkleDigest.Friend.INSTANCE, this.state, this.SPARKLE_STEPS_SLIM);
         Pack.intToLittleEndian(this.state, 0, 4, var1, var2 + 16);
      }
   }

   @Override
   public void reset() {
      super.reset();
      Arrays.fill(this.state, 0);
   }

   private void processBlock(byte[] var1, int var2, int var3) {
      int var4 = Pack.littleEndianToInt(var1, var2);
      int var5 = Pack.littleEndianToInt(var1, var2 + 4);
      int var6 = Pack.littleEndianToInt(var1, var2 + 8);
      int var7 = Pack.littleEndianToInt(var1, var2 + 12);
      int var8 = ELL(var4 ^ var6);
      int var9 = ELL(var5 ^ var7);
      this.state[0] = this.state[0] ^ var4 ^ var9;
      this.state[1] = this.state[1] ^ var5 ^ var8;
      this.state[2] = this.state[2] ^ var6 ^ var9;
      this.state[3] = this.state[3] ^ var7 ^ var8;
      this.state[4] = this.state[4] ^ var9;
      this.state[5] = this.state[5] ^ var8;
      if (this.STATE_WORDS == 16) {
         this.state[6] = this.state[6] ^ var9;
         this.state[7] = this.state[7] ^ var8;
         SparkleEngine.sparkle_opt16(SparkleDigest.Friend.INSTANCE, this.state, var3);
      } else {
         SparkleEngine.sparkle_opt12(SparkleDigest.Friend.INSTANCE, this.state, var3);
      }
   }

   private static int ELL(int var0) {
      return Integers.rotateRight(var0, 16) ^ var0 & 65535;
   }

   public static class Friend {
      private static final SparkleDigest.Friend INSTANCE = new SparkleDigest.Friend();

      private Friend() {
      }
   }

   public static enum SparkleParameters {
      ESCH256,
      ESCH384;
   }
}
