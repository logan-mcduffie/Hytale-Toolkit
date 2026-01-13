package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.util.Arrays;

public class KeyParameter implements CipherParameters {
   private byte[] key;

   public KeyParameter(byte[] var1) {
      this(var1, 0, var1.length);
   }

   public KeyParameter(byte[] var1, int var2, int var3) {
      this(var3);
      System.arraycopy(var1, var2, this.key, 0, var3);
   }

   private KeyParameter(int var1) {
      this.key = new byte[var1];
   }

   public void copyTo(byte[] var1, int var2, int var3) {
      if (this.key.length != var3) {
         throw new IllegalArgumentException("len");
      } else {
         System.arraycopy(this.key, 0, var1, var2, var3);
      }
   }

   public byte[] getKey() {
      return this.key;
   }

   public int getKeyLength() {
      return this.key.length;
   }

   public KeyParameter reverse() {
      KeyParameter var1 = new KeyParameter(this.key.length);
      Arrays.reverse(this.key, var1.key);
      return var1;
   }
}
