package org.bouncycastle.operator;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.util.Arrays;

public class MacCaptureStream extends OutputStream {
   private final OutputStream cOut;
   private final byte[] mac;
   int macIndex = 0;

   public MacCaptureStream(OutputStream var1, int var2) {
      this.cOut = var1;
      this.mac = new byte[var2];
   }

   @Override
   public void write(byte[] var1, int var2, int var3) throws IOException {
      if (var3 >= this.mac.length) {
         this.cOut.write(this.mac, 0, this.macIndex);
         this.macIndex = this.mac.length;
         System.arraycopy(var1, var2 + var3 - this.mac.length, this.mac, 0, this.mac.length);
         this.cOut.write(var1, var2, var3 - this.mac.length);
      } else {
         for (int var4 = 0; var4 != var3; var4++) {
            this.write(var1[var2 + var4]);
         }
      }
   }

   @Override
   public void write(int var1) throws IOException {
      if (this.macIndex == this.mac.length) {
         byte var2 = this.mac[0];
         System.arraycopy(this.mac, 1, this.mac, 0, this.mac.length - 1);
         this.mac[this.mac.length - 1] = (byte)var1;
         this.cOut.write(var2);
      } else {
         this.mac[this.macIndex++] = (byte)var1;
      }
   }

   public byte[] getMac() {
      return Arrays.clone(this.mac);
   }
}
