package org.bouncycastle.pqc.crypto.mldsa;

import org.bouncycastle.crypto.digests.SHAKEDigest;

abstract class Symmetric {
   final int stream128BlockBytes;
   final int stream256BlockBytes;

   Symmetric(int var1, int var2) {
      this.stream128BlockBytes = var1;
      this.stream256BlockBytes = var2;
   }

   abstract void stream128init(byte[] var1, short var2);

   abstract void stream256init(byte[] var1, short var2);

   abstract void stream128squeezeBlocks(byte[] var1, int var2, int var3);

   abstract void stream256squeezeBlocks(byte[] var1, int var2, int var3);

   static class ShakeSymmetric extends Symmetric {
      private final SHAKEDigest digest128 = new SHAKEDigest(128);
      private final SHAKEDigest digest256 = new SHAKEDigest(256);

      ShakeSymmetric() {
         super(168, 136);
      }

      private void streamInit(SHAKEDigest var1, byte[] var2, short var3) {
         var1.reset();
         byte[] var4 = new byte[]{(byte)var3, (byte)(var3 >> 8)};
         var1.update(var2, 0, var2.length);
         var1.update(var4, 0, var4.length);
      }

      @Override
      void stream128init(byte[] var1, short var2) {
         this.streamInit(this.digest128, var1, var2);
      }

      @Override
      void stream256init(byte[] var1, short var2) {
         this.streamInit(this.digest256, var1, var2);
      }

      @Override
      void stream128squeezeBlocks(byte[] var1, int var2, int var3) {
         this.digest128.doOutput(var1, var2, var3);
      }

      @Override
      void stream256squeezeBlocks(byte[] var1, int var2, int var3) {
         this.digest256.doOutput(var1, var2, var3);
      }
   }
}
