package org.bouncycastle.pqc.crypto.saber;

import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.SICBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

abstract class Symmetric {
   abstract void hash_h(byte[] var1, byte[] var2, int var3);

   abstract void hash_g(byte[] var1, byte[] var2);

   abstract void prf(byte[] var1, byte[] var2, int var3, int var4);

   static class AesSymmetric extends Symmetric {
      private final SHA256Digest sha256Digest = new SHA256Digest();
      private final SHA512Digest sha512Digest = new SHA512Digest();
      private final StreamCipher cipher = SICBlockCipher.newInstance(AESEngine.newInstance());

      @Override
      void hash_h(byte[] var1, byte[] var2, int var3) {
         this.sha256Digest.update(var2, 0, var2.length);
         this.sha256Digest.doFinal(var1, var3);
      }

      @Override
      void hash_g(byte[] var1, byte[] var2) {
         this.sha512Digest.update(var2, 0, var2.length);
         this.sha512Digest.doFinal(var1, 0);
      }

      @Override
      void prf(byte[] var1, byte[] var2, int var3, int var4) {
         ParametersWithIV var5 = new ParametersWithIV(new KeyParameter(var2, 0, var3), new byte[16]);
         this.cipher.init(true, var5);
         byte[] var6 = new byte[var4];
         this.cipher.processBytes(var6, 0, var4, var1, 0);
      }
   }

   static class ShakeSymmetric extends Symmetric {
      private final SHA3Digest sha3Digest256;
      private final SHA3Digest sha3Digest512;
      private final Xof shakeDigest = new SHAKEDigest(128);

      ShakeSymmetric() {
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
      void prf(byte[] var1, byte[] var2, int var3, int var4) {
         this.shakeDigest.reset();
         this.shakeDigest.update(var2, 0, var3);
         this.shakeDigest.doFinal(var1, 0, var4);
      }
   }
}
