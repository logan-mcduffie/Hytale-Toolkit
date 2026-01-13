package org.bouncycastle.crypto.params;

import java.math.BigInteger;

public class CramerShoupPrivateKeyParameters extends CramerShoupKeyParameters {
   private BigInteger x1;
   private BigInteger x2;
   private BigInteger y1;
   private BigInteger y2;
   private BigInteger z;
   private CramerShoupPublicKeyParameters pk;

   public CramerShoupPrivateKeyParameters(CramerShoupParameters var1, BigInteger var2, BigInteger var3, BigInteger var4, BigInteger var5, BigInteger var6) {
      super(true, var1);
      this.x1 = var2;
      this.x2 = var3;
      this.y1 = var4;
      this.y2 = var5;
      this.z = var6;
   }

   public BigInteger getX1() {
      return this.x1;
   }

   public BigInteger getX2() {
      return this.x2;
   }

   public BigInteger getY1() {
      return this.y1;
   }

   public BigInteger getY2() {
      return this.y2;
   }

   public BigInteger getZ() {
      return this.z;
   }

   public void setPk(CramerShoupPublicKeyParameters var1) {
      this.pk = var1;
   }

   public CramerShoupPublicKeyParameters getPk() {
      return this.pk;
   }

   @Override
   public int hashCode() {
      return this.x1.hashCode() ^ this.x2.hashCode() ^ this.y1.hashCode() ^ this.y2.hashCode() ^ this.z.hashCode() ^ super.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof CramerShoupPrivateKeyParameters)) {
         return false;
      } else {
         CramerShoupPrivateKeyParameters var2 = (CramerShoupPrivateKeyParameters)var1;
         return var2.getX1().equals(this.x1)
            && var2.getX2().equals(this.x2)
            && var2.getY1().equals(this.y1)
            && var2.getY2().equals(this.y2)
            && var2.getZ().equals(this.z)
            && super.equals(var1);
      }
   }
}
