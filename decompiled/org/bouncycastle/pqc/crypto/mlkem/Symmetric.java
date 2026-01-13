package org.bouncycastle.pqc.crypto.mlkem;

import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;

abstract class Symmetric {
   final int xofBlockBytes;

   abstract void hash_h(byte[] var1, byte[] var2, int var3);

   abstract void hash_g(byte[] var1, byte[] var2);

   abstract void xofAbsorb(byte[] var1, byte var2, byte var3);

   abstract void xofSqueezeBlocks(byte[] var1, int var2, int var3);

   abstract void prf(byte[] var1, byte[] var2, byte var3);

   abstract void kdf(byte[] var1, byte[] var2);

   Symmetric(int var1) {
      this.xofBlockBytes = var1;
   }

   static class ShakeSymmetric extends Symmetric {
      private final SHAKEDigest xof = new SHAKEDigest(128);
      private final SHA3Digest sha3Digest512;
      private final SHA3Digest sha3Digest256;
      private final SHAKEDigest shakeDigest = new SHAKEDigest(256);

      ShakeSymmetric() {
         super(168);
         this.sha3Digest256 = new SHA3Digest(256);
         this.sha3Digest512 = new SHA3Digest(512);
      }

      @Override
      void hash_h(byte[] var1, byte[] var2, int var3) {
         this.sha3Digest256.update(var2, 0, var2.length);
         this.sha3Digest256.doFinal(var1, var3);
      }

      @Override
      void hash_g(byte[] var1, byte[] var2) {
         this.sha3Digest512.update(var2, 0, var2.length);
         this.sha3Digest512.doFinal(var1, 0);
      }

      @Override
      void xofAbsorb(byte[] var1, byte var2, byte var3) {
         this.xof.reset();
         byte[] var4 = new byte[var1.length + 2];
         System.arraycopy(var1, 0, var4, 0, var1.length);
         var4[var1.length] = var2;
         var4[var1.length + 1] = var3;
         this.xof.update(var4, 0, var1.length + 2);
      }

      @Override
      void xofSqueezeBlocks(byte[] var1, int var2, int var3) {
         this.xof.doOutput(var1, var2, var3);
      }

      @Override
      void prf(byte[] var1, byte[] var2, byte var3) {
         byte[] var4 = new byte[var2.length + 1];
         System.arraycopy(var2, 0, var4, 0, var2.length);
         var4[var2.length] = var3;
         this.shakeDigest.update(var4, 0, var4.length);
         this.shakeDigest.doFinal(var1, 0, var1.length);
      }

      @Override
      void kdf(byte[] var1, byte[] var2) {
         this.shakeDigest.update(var2, 0, var2.length);
         this.shakeDigest.doFinal(var1, 0, var1.length);
      }
   }
}
