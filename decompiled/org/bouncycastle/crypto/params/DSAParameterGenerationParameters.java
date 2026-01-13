package org.bouncycastle.crypto.params;

import java.security.SecureRandom;

public class DSAParameterGenerationParameters {
   public static final int DIGITAL_SIGNATURE_USAGE = 1;
   public static final int KEY_ESTABLISHMENT_USAGE = 2;
   private final int l;
   private final int n;
   private final int usageIndex;
   private final int certainty;
   private final SecureRandom random;

   public DSAParameterGenerationParameters(int var1, int var2, int var3, SecureRandom var4) {
      this(var1, var2, var3, var4, -1);
   }

   public DSAParameterGenerationParameters(int var1, int var2, int var3, SecureRandom var4, int var5) {
      this.l = var1;
      this.n = var2;
      this.certainty = var3;
      this.usageIndex = var5;
      this.random = var4;
   }

   public int getL() {
      return this.l;
   }

   public int getN() {
      return this.n;
   }

   public int getCertainty() {
      return this.certainty;
   }

   public SecureRandom getRandom() {
      return this.random;
   }

   public int getUsageIndex() {
      return this.usageIndex;
   }
}
