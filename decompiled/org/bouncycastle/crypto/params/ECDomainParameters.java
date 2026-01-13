package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class ECDomainParameters implements ECConstants {
   private final ECCurve curve;
   private final byte[] seed;
   private final ECPoint G;
   private final BigInteger n;
   private final BigInteger h;
   private BigInteger hInv = null;

   public ECDomainParameters(X9ECParameters var1) {
      this(var1.getCurve(), var1.getG(), var1.getN(), var1.getH(), var1.getSeed());
   }

   public ECDomainParameters(ECCurve var1, ECPoint var2, BigInteger var3) {
      this(var1, var2, var3, ONE, null);
   }

   public ECDomainParameters(ECCurve var1, ECPoint var2, BigInteger var3, BigInteger var4) {
      this(var1, var2, var3, var4, null);
   }

   public ECDomainParameters(ECCurve var1, ECPoint var2, BigInteger var3, BigInteger var4, byte[] var5) {
      if (var1 == null) {
         throw new NullPointerException("curve");
      } else if (var3 == null) {
         throw new NullPointerException("n");
      } else {
         this.curve = var1;
         this.G = validatePublicPoint(var1, var2);
         this.n = var3;
         this.h = var4;
         this.seed = Arrays.clone(var5);
      }
   }

   public ECCurve getCurve() {
      return this.curve;
   }

   public ECPoint getG() {
      return this.G;
   }

   public BigInteger getN() {
      return this.n;
   }

   public BigInteger getH() {
      return this.h;
   }

   public synchronized BigInteger getHInv() {
      if (this.hInv == null) {
         this.hInv = BigIntegers.modOddInverseVar(this.n, this.h);
      }

      return this.hInv;
   }

   public byte[] getSeed() {
      return Arrays.clone(this.seed);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ECDomainParameters)) {
         return false;
      } else {
         ECDomainParameters var2 = (ECDomainParameters)var1;
         return this.curve.equals(var2.curve) && this.G.equals(var2.G) && this.n.equals(var2.n);
      }
   }

   @Override
   public int hashCode() {
      int var1 = 4;
      var1 *= 257;
      var1 ^= this.curve.hashCode();
      var1 *= 257;
      var1 ^= this.G.hashCode();
      var1 *= 257;
      return var1 ^ this.n.hashCode();
   }

   public BigInteger validatePrivateScalar(BigInteger var1) {
      if (null == var1) {
         throw new NullPointerException("Scalar cannot be null");
      } else if (var1.compareTo(ECConstants.ONE) >= 0 && var1.compareTo(this.getN()) < 0) {
         return var1;
      } else {
         throw new IllegalArgumentException("Scalar is not in the interval [1, n - 1]");
      }
   }

   public ECPoint validatePublicPoint(ECPoint var1) {
      return validatePublicPoint(this.getCurve(), var1);
   }

   static ECPoint validatePublicPoint(ECCurve var0, ECPoint var1) {
      if (null == var1) {
         throw new NullPointerException("Point cannot be null");
      } else {
         var1 = ECAlgorithms.importPoint(var0, var1).normalize();
         if (var1.isInfinity()) {
            throw new IllegalArgumentException("Point at infinity");
         } else if (!var1.isValid()) {
            throw new IllegalArgumentException("Point not on curve");
         } else {
            return var1;
         }
      }
   }
}
