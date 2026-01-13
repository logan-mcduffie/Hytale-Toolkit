package org.bouncycastle.pqc.crypto.ntru;

import org.bouncycastle.pqc.math.ntru.Polynomial;

class PolynomialPair {
   private final Polynomial a;
   private final Polynomial b;

   public PolynomialPair(Polynomial var1, Polynomial var2) {
      this.a = var1;
      this.b = var2;
   }

   public Polynomial f() {
      return this.a;
   }

   public Polynomial g() {
      return this.b;
   }

   public Polynomial r() {
      return this.a;
   }

   public Polynomial m() {
      return this.b;
   }
}
