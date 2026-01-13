package org.bouncycastle.crypto.util;

import java.math.BigInteger;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

class SSHBuffer {
   private final byte[] buffer;
   private int pos = 0;

   public SSHBuffer(byte[] var1, byte[] var2) {
      this.buffer = var2;

      for (int var3 = 0; var3 != var1.length; var3++) {
         if (var1[var3] != var2[var3]) {
            throw new IllegalArgumentException("magic-number incorrect");
         }
      }

      this.pos += var1.length;
   }

   public SSHBuffer(byte[] var1) {
      this.buffer = var1;
   }

   public int readU32() {
      if (this.pos > this.buffer.length - 4) {
         throw new IllegalArgumentException("4 bytes for U32 exceeds buffer.");
      } else {
         int var1 = (this.buffer[this.pos++] & 255) << 24;
         var1 |= (this.buffer[this.pos++] & 255) << 16;
         var1 |= (this.buffer[this.pos++] & 255) << 8;
         return var1 | this.buffer[this.pos++] & 0xFF;
      }
   }

   public String readString() {
      return Strings.fromByteArray(this.readBlock());
   }

   public byte[] readBlock() {
      int var1 = this.readU32();
      if (var1 == 0) {
         return new byte[0];
      } else if (this.pos > this.buffer.length - var1) {
         throw new IllegalArgumentException("not enough data for block");
      } else {
         int var2 = this.pos;
         this.pos += var1;
         return Arrays.copyOfRange(this.buffer, var2, this.pos);
      }
   }

   public void skipBlock() {
      int var1 = this.readU32();
      if (this.pos > this.buffer.length - var1) {
         throw new IllegalArgumentException("not enough data for block");
      } else {
         this.pos += var1;
      }
   }

   public byte[] readPaddedBlock() {
      return this.readPaddedBlock(8);
   }

   public byte[] readPaddedBlock(int var1) {
      int var2 = this.readU32();
      if (var2 == 0) {
         return new byte[0];
      } else if (this.pos > this.buffer.length - var2) {
         throw new IllegalArgumentException("not enough data for block");
      } else {
         int var3 = var2 % var1;
         if (0 != var3) {
            throw new IllegalArgumentException("missing padding");
         } else {
            int var4 = this.pos;
            this.pos += var2;
            int var5 = this.pos;
            if (var2 > 0) {
               int var6 = this.buffer[this.pos - 1] & 255;
               if (0 < var6 && var6 < var1) {
                  int var7 = var6;
                  var5 -= var6;
                  int var8 = 1;

                  for (int var9 = var5; var8 <= var7; var9++) {
                     if (var8 != (this.buffer[var9] & 255)) {
                        throw new IllegalArgumentException("incorrect padding");
                     }

                     var8++;
                  }
               }
            }

            return Arrays.copyOfRange(this.buffer, var4, var5);
         }
      }
   }

   public BigInteger readBigNumPositive() {
      int var1 = this.readU32();
      if (this.pos + var1 > this.buffer.length) {
         throw new IllegalArgumentException("not enough data for big num");
      } else {
         int var2 = this.pos;
         this.pos += var1;
         byte[] var3 = Arrays.copyOfRange(this.buffer, var2, this.pos);
         return new BigInteger(1, var3);
      }
   }

   public byte[] getBuffer() {
      return Arrays.clone(this.buffer);
   }

   public boolean hasRemaining() {
      return this.pos < this.buffer.length;
   }
}
