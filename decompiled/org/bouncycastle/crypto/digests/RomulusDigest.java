package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.engines.RomulusEngine;
import org.bouncycastle.util.Arrays;

public class RomulusDigest extends BufferBaseDigest {
   private final byte[] h = new byte[16];
   private final byte[] g = new byte[16];

   public RomulusDigest() {
      super(BufferBaseDigest.ProcessingBufferType.Immediate, 32);
      this.DigestSize = 32;
      this.algorithmName = "Romulus Hash";
   }

   @Override
   protected void processBytes(byte[] var1, int var2) {
      RomulusEngine.hirose_128_128_256(RomulusDigest.Friend.INSTANCE, this.h, this.g, var1, var2);
   }

   @Override
   protected void finish(byte[] var1, int var2) {
      Arrays.fill(this.m_buf, this.m_bufPos, 31, (byte)0);
      this.m_buf[31] = (byte)(this.m_bufPos & 31);
      this.h[0] = (byte)(this.h[0] ^ 2);
      RomulusEngine.hirose_128_128_256(RomulusDigest.Friend.INSTANCE, this.h, this.g, this.m_buf, 0);
      System.arraycopy(this.h, 0, var1, var2, 16);
      System.arraycopy(this.g, 0, var1, 16 + var2, 16);
   }

   @Override
   public void reset() {
      super.reset();
      Arrays.clear(this.h);
      Arrays.clear(this.g);
   }

   public static class Friend {
      private static final RomulusDigest.Friend INSTANCE = new RomulusDigest.Friend();

      private Friend() {
      }
   }
}
