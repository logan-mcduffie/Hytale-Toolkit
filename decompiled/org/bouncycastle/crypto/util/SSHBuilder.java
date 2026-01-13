package org.bouncycastle.crypto.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.util.Strings;

class SSHBuilder {
   private final ByteArrayOutputStream bos = new ByteArrayOutputStream();

   public void u32(int var1) {
      this.bos.write(var1 >>> 24 & 0xFF);
      this.bos.write(var1 >>> 16 & 0xFF);
      this.bos.write(var1 >>> 8 & 0xFF);
      this.bos.write(var1 & 0xFF);
   }

   public void writeBigNum(BigInteger var1) {
      this.writeBlock(var1.toByteArray());
   }

   public void writeBlock(byte[] var1) {
      this.u32(var1.length);

      try {
         this.bos.write(var1);
      } catch (IOException var3) {
         throw new IllegalStateException(var3.getMessage(), var3);
      }
   }

   public void writeBytes(byte[] var1) {
      try {
         this.bos.write(var1);
      } catch (IOException var3) {
         throw new IllegalStateException(var3.getMessage(), var3);
      }
   }

   public void writeString(String var1) {
      this.writeBlock(Strings.toByteArray(var1));
   }

   public byte[] getBytes() {
      return this.bos.toByteArray();
   }

   public byte[] getPaddedBytes() {
      return this.getPaddedBytes(8);
   }

   public byte[] getPaddedBytes(int var1) {
      int var2 = this.bos.size() % var1;
      if (0 != var2) {
         int var3 = var1 - var2;

         for (int var4 = 1; var4 <= var3; var4++) {
            this.bos.write(var4);
         }
      }

      return this.bos.toByteArray();
   }
}
