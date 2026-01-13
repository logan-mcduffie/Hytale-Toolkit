package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;

public class GOST3410Parameters implements CipherParameters {
   private BigInteger p;
   private BigInteger q;
   private BigInteger a;
   private GOST3410ValidationParameters validation;

   public GOST3410Parameters(BigInteger var1, BigInteger var2, BigInteger var3) {
      this.p = var1;
      this.q = var2;
      this.a = var3;
   }

   public GOST3410Parameters(BigInteger var1, BigInteger var2, BigInteger var3, GOST3410ValidationParameters var4) {
      this.a = var3;
      this.p = var1;
      this.q = var2;
      this.validation = var4;
   }

   public BigInteger getP() {
      return this.p;
   }

   public BigInteger getQ() {
      return this.q;
   }

   public BigInteger getA() {
      return this.a;
   }

   public GOST3410ValidationParameters getValidationParameters() {
      return this.validation;
   }

   @Override
   public int hashCode() {
      return this.p.hashCode() ^ this.q.hashCode() ^ this.a.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof GOST3410Parameters)) {
         return false;
      } else {
         GOST3410Parameters var2 = (GOST3410Parameters)var1;
         return var2.getP().equals(this.p) && var2.getQ().equals(this.q) && var2.getA().equals(this.a);
      }
   }
}
