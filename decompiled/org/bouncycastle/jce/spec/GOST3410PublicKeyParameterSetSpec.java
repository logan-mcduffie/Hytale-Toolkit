package org.bouncycastle.jce.spec;

import java.math.BigInteger;

public class GOST3410PublicKeyParameterSetSpec {
   private BigInteger p;
   private BigInteger q;
   private BigInteger a;

   public GOST3410PublicKeyParameterSetSpec(BigInteger var1, BigInteger var2, BigInteger var3) {
      this.p = var1;
      this.q = var2;
      this.a = var3;
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

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof GOST3410PublicKeyParameterSetSpec)) {
         return false;
      } else {
         GOST3410PublicKeyParameterSetSpec var2 = (GOST3410PublicKeyParameterSetSpec)var1;
         return this.a.equals(var2.a) && this.p.equals(var2.p) && this.q.equals(var2.q);
      }
   }

   @Override
   public int hashCode() {
      return this.a.hashCode() ^ this.p.hashCode() ^ this.q.hashCode();
   }
}
