package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.util.Properties;

public class DHParameters implements CipherParameters {
   private static final int DEFAULT_MINIMUM_LENGTH = 160;
   private BigInteger g;
   private BigInteger p;
   private BigInteger q;
   private BigInteger j;
   private int m;
   private int l;
   private DHValidationParameters validation;

   private static int getDefaultMParam(int var0) {
      if (var0 == 0) {
         return 160;
      } else {
         return var0 < 160 ? var0 : 160;
      }
   }

   public DHParameters(BigInteger var1, BigInteger var2) {
      this(var1, var2, null, 0);
   }

   public DHParameters(BigInteger var1, BigInteger var2, BigInteger var3) {
      this(var1, var2, var3, 0);
   }

   public DHParameters(BigInteger var1, BigInteger var2, BigInteger var3, int var4) {
      this(var1, var2, var3, getDefaultMParam(var4), var4, null, null);
   }

   public DHParameters(BigInteger var1, BigInteger var2, BigInteger var3, int var4, int var5) {
      this(var1, var2, var3, var4, var5, null, null);
   }

   public DHParameters(BigInteger var1, BigInteger var2, BigInteger var3, BigInteger var4, DHValidationParameters var5) {
      this(var1, var2, var3, 160, 0, var4, var5);
   }

   public DHParameters(BigInteger var1, BigInteger var2, BigInteger var3, int var4, int var5, BigInteger var6, DHValidationParameters var7) {
      if (var5 != 0) {
         if (var5 > var1.bitLength()) {
            throw new IllegalArgumentException("when l value specified, it must satisfy 2^(l-1) <= p");
         }

         if (var5 < var4) {
            throw new IllegalArgumentException("when l value specified, it may not be less than m value");
         }
      }

      if (var4 > var1.bitLength() && !Properties.isOverrideSet("org.bouncycastle.dh.allow_unsafe_p_value")) {
         throw new IllegalArgumentException("unsafe p value so small specific l required");
      } else {
         this.g = var2;
         this.p = var1;
         this.q = var3;
         this.m = var4;
         this.l = var5;
         this.j = var6;
         this.validation = var7;
      }
   }

   public BigInteger getP() {
      return this.p;
   }

   public BigInteger getG() {
      return this.g;
   }

   public BigInteger getQ() {
      return this.q;
   }

   public BigInteger getJ() {
      return this.j;
   }

   public int getM() {
      return this.m;
   }

   public int getL() {
      return this.l;
   }

   public DHValidationParameters getValidationParameters() {
      return this.validation;
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof DHParameters)) {
         return false;
      } else {
         DHParameters var2 = (DHParameters)var1;
         if (this.getQ() != null) {
            if (!this.getQ().equals(var2.getQ())) {
               return false;
            }
         } else if (var2.getQ() != null) {
            return false;
         }

         return var2.getP().equals(this.p) && var2.getG().equals(this.g);
      }
   }

   @Override
   public int hashCode() {
      return this.getP().hashCode() ^ this.getG().hashCode() ^ (this.getQ() != null ? this.getQ().hashCode() : 0);
   }
}
