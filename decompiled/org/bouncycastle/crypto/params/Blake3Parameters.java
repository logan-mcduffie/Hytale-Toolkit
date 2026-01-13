package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.util.Arrays;

public class Blake3Parameters implements CipherParameters {
   private static final int KEYLEN = 32;
   private byte[] theKey;
   private byte[] theContext;

   public static Blake3Parameters context(byte[] var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("Invalid context");
      } else {
         Blake3Parameters var1 = new Blake3Parameters();
         var1.theContext = Arrays.clone(var0);
         return var1;
      }
   }

   public static Blake3Parameters key(byte[] var0) {
      if (var0 != null && var0.length == 32) {
         Blake3Parameters var1 = new Blake3Parameters();
         var1.theKey = Arrays.clone(var0);
         return var1;
      } else {
         throw new IllegalArgumentException("Invalid keyLength");
      }
   }

   public byte[] getKey() {
      return Arrays.clone(this.theKey);
   }

   public void clearKey() {
      Arrays.fill(this.theKey, (byte)0);
   }

   public byte[] getContext() {
      return Arrays.clone(this.theContext);
   }
}
