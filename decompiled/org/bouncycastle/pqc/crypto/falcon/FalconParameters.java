package org.bouncycastle.pqc.crypto.falcon;

import org.bouncycastle.crypto.CipherParameters;

public class FalconParameters implements CipherParameters {
   public static final FalconParameters falcon_512 = new FalconParameters("falcon-512", 9, 40);
   public static final FalconParameters falcon_1024 = new FalconParameters("falcon-1024", 10, 40);
   private final String name;
   private final int logn;
   private final int nonce_length;

   private FalconParameters(String var1, int var2, int var3) {
      if (var2 >= 1 && var2 <= 10) {
         this.name = var1;
         this.logn = var2;
         this.nonce_length = var3;
      } else {
         throw new IllegalArgumentException("Log N degree should be between 1 and 10");
      }
   }

   public int getLogN() {
      return this.logn;
   }

   int getNonceLength() {
      return this.nonce_length;
   }

   public String getName() {
      return this.name;
   }
}
