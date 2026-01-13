package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.io.LimitedBuffer;

public class Prehash implements Digest {
   private final String algorithmName;
   private final LimitedBuffer buf;

   public static Prehash forDigest(Digest var0) {
      return new Prehash(var0);
   }

   private Prehash(Digest var1) {
      this.algorithmName = var1.getAlgorithmName();
      this.buf = new LimitedBuffer(var1.getDigestSize());
   }

   @Override
   public String getAlgorithmName() {
      return this.algorithmName;
   }

   @Override
   public int getDigestSize() {
      return this.buf.limit();
   }

   @Override
   public void update(byte var1) {
      this.buf.write(var1);
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      this.buf.write(var1, var2, var3);
   }

   @Override
   public int doFinal(byte[] var1, int var2) {
      int var3;
      try {
         if (this.getDigestSize() != this.buf.size()) {
            throw new IllegalStateException("Incorrect prehash size");
         }

         var3 = this.buf.copyTo(var1, var2);
      } finally {
         this.reset();
      }

      return var3;
   }

   @Override
   public void reset() {
      this.buf.reset();
   }
}
