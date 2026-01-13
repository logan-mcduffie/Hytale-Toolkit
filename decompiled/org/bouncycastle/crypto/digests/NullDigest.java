package org.bouncycastle.crypto.digests;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Arrays;

public class NullDigest implements Digest {
   private NullDigest.OpenByteArrayOutputStream bOut = new NullDigest.OpenByteArrayOutputStream();

   @Override
   public String getAlgorithmName() {
      return "NULL";
   }

   @Override
   public int getDigestSize() {
      return this.bOut.size();
   }

   @Override
   public void update(byte var1) {
      this.bOut.write(var1);
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      this.bOut.write(var1, var2, var3);
   }

   @Override
   public int doFinal(byte[] var1, int var2) {
      int var3 = this.bOut.size();
      this.bOut.copy(var1, var2);
      this.reset();
      return var3;
   }

   @Override
   public void reset() {
      this.bOut.reset();
   }

   private static class OpenByteArrayOutputStream extends ByteArrayOutputStream {
      private OpenByteArrayOutputStream() {
      }

      @Override
      public void reset() {
         super.reset();
         Arrays.clear(this.buf);
      }

      void copy(byte[] var1, int var2) {
         System.arraycopy(this.buf, 0, var1, var2, this.size());
      }
   }
}
