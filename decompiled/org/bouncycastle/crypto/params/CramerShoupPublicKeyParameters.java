package org.bouncycastle.crypto.params;

import java.math.BigInteger;

public class CramerShoupPublicKeyParameters extends CramerShoupKeyParameters {
   private BigInteger c;
   private BigInteger d;
   private BigInteger h;

   public CramerShoupPublicKeyParameters(CramerShoupParameters var1, BigInteger var2, BigInteger var3, BigInteger var4) {
      super(false, var1);
      this.c = var2;
      this.d = var3;
      this.h = var4;
   }

   public BigInteger getC() {
      return this.c;
   }

   public BigInteger getD() {
      return this.d;
   }

   public BigInteger getH() {
      return this.h;
   }

   @Override
   public int hashCode() {
      return this.c.hashCode() ^ this.d.hashCode() ^ this.h.hashCode() ^ super.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof CramerShoupPublicKeyParameters)) {
         return false;
      } else {
         CramerShoupPublicKeyParameters var2 = (CramerShoupPublicKeyParameters)var1;
         return var2.getC().equals(this.c) && var2.getD().equals(this.d) && var2.getH().equals(this.h) && super.equals(var1);
      }
   }
}
