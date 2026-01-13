package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class RC5Parameters implements CipherParameters {
   private byte[] key;
   private int rounds;

   public RC5Parameters(byte[] var1, int var2) {
      if (var1.length > 255) {
         throw new IllegalArgumentException("RC5 key length can be no greater than 255");
      } else {
         this.key = new byte[var1.length];
         this.rounds = var2;
         System.arraycopy(var1, 0, this.key, 0, var1.length);
      }
   }

   public byte[] getKey() {
      return this.key;
   }

   public int getRounds() {
      return this.rounds;
   }
}
