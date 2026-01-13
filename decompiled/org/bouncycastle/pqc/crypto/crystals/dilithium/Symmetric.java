package org.bouncycastle.pqc.crypto.crystals.dilithium;

import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.SICBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

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

   @Deprecated
   static class AesSymmetric extends Symmetric {
      private final StreamCipher cipher = SICBlockCipher.newInstance(AESEngine.newInstance());

      AesSymmetric() {
         super(64, 64);
      }

      private void aes128(byte[] var1, int var2, int var3) {
         byte[] var4 = new byte[var3];
         this.cipher.processBytes(var4, 0, var3, var1, var2);
      }

      private void streamInit(byte[] var1, short var2) {
         byte[] var3 = new byte[12];
         var3[0] = (byte)var2;
         var3[1] = (byte)(var2 >> 8);
         ParametersWithIV var4 = new ParametersWithIV(new KeyParameter(var1, 0, 32), var3);
         this.cipher.init(true, var4);
      }

      @Override
      void stream128init(byte[] var1, short var2) {
         this.streamInit(var1, var2);
      }

      @Override
      void stream256init(byte[] var1, short var2) {
         this.streamInit(var1, var2);
      }

      @Override
      void stream128squeezeBlocks(byte[] var1, int var2, int var3) {
         this.aes128(var1, var2, var3);
      }

      @Override
      void stream256squeezeBlocks(byte[] var1, int var2, int var3) {
         this.aes128(var1, var2, var3);
      }
   }

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
