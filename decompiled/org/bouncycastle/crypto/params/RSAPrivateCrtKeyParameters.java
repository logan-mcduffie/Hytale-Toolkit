package org.bouncycastle.crypto.params;

import java.math.BigInteger;

public class RSAPrivateCrtKeyParameters extends RSAKeyParameters {
   private BigInteger e;
   private BigInteger p;
   private BigInteger q;
   private BigInteger dP;
   private BigInteger dQ;
   private BigInteger qInv;

   public RSAPrivateCrtKeyParameters(
      BigInteger var1, BigInteger var2, BigInteger var3, BigInteger var4, BigInteger var5, BigInteger var6, BigInteger var7, BigInteger var8
   ) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, false);
   }

   public RSAPrivateCrtKeyParameters(
      BigInteger var1, BigInteger var2, BigInteger var3, BigInteger var4, BigInteger var5, BigInteger var6, BigInteger var7, BigInteger var8, boolean var9
   ) {
      super(true, var1, var3, var9);
      this.e = var2;
      this.p = var4;
      this.q = var5;
      this.dP = var6;
      this.dQ = var7;
      this.qInv = var8;
   }

   public BigInteger getPublicExponent() {
      return this.e;
   }

   public BigInteger getP() {
      return this.p;
   }

   public BigInteger getQ() {
      return this.q;
   }

   public BigInteger getDP() {
      return this.dP;
   }

   public BigInteger getDQ() {
      return this.dQ;
   }

   public BigInteger getQInv() {
      return this.qInv;
   }
}
