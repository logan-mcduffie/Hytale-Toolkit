package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.Blake3Digest;
import org.bouncycastle.crypto.params.Blake3Parameters;
import org.bouncycastle.crypto.params.KeyParameter;

public class Blake3Mac implements Mac {
   private final Blake3Digest theDigest;

   public Blake3Mac(Blake3Digest var1) {
      this.theDigest = var1;
   }

   @Override
   public String getAlgorithmName() {
      return this.theDigest.getAlgorithmName() + "Mac";
   }

   @Override
   public void init(CipherParameters var1) {
      Object var2 = var1;
      if (var1 instanceof KeyParameter) {
         var2 = Blake3Parameters.key(((KeyParameter)var1).getKey());
      }

      if (!(var2 instanceof Blake3Parameters)) {
         throw new IllegalArgumentException("Invalid parameter passed to Blake3Mac init - " + var1.getClass().getName());
      } else {
         Blake3Parameters var3 = (Blake3Parameters)var2;
         if (var3.getKey() == null) {
            throw new IllegalArgumentException("Blake3Mac requires a key parameter.");
         } else {
            this.theDigest.init(var3);
         }
      }
   }

   @Override
   public int getMacSize() {
      return this.theDigest.getDigestSize();
   }

   @Override
   public void update(byte var1) {
      this.theDigest.update(var1);
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      this.theDigest.update(var1, var2, var3);
   }

   @Override
   public int doFinal(byte[] var1, int var2) {
      return this.theDigest.doFinal(var1, var2);
   }

   @Override
   public void reset() {
      this.theDigest.reset();
   }
}
