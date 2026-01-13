package org.bouncycastle.math.ec;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Hashtable;
import org.bouncycastle.crypto.CryptoServicesRegistrar;

public abstract class ECPoint {
   protected static final ECFieldElement[] EMPTY_ZS = new ECFieldElement[0];
   protected ECCurve curve;
   protected ECFieldElement x;
   protected ECFieldElement y;
   protected ECFieldElement[] zs;
   protected Hashtable preCompTable = null;

   protected static ECFieldElement[] getInitialZCoords(ECCurve var0) {
      int var1 = null == var0 ? 0 : var0.getCoordinateSystem();
      switch (var1) {
         case 0:
         case 5:
            return EMPTY_ZS;
         default:
            ECFieldElement var2 = var0.fromBigInteger(ECConstants.ONE);
            switch (var1) {
               case 1:
               case 2:
               case 6:
                  return new ECFieldElement[]{var2};
               case 3:
                  return new ECFieldElement[]{var2, var2, var2};
               case 4:
                  return new ECFieldElement[]{var2, var0.getA()};
               case 5:
               default:
                  throw new IllegalArgumentException("unknown coordinate system");
            }
      }
   }

   protected ECPoint(ECCurve var1, ECFieldElement var2, ECFieldElement var3) {
      this(var1, var2, var3, getInitialZCoords(var1));
   }

   protected ECPoint(ECCurve var1, ECFieldElement var2, ECFieldElement var3, ECFieldElement[] var4) {
      this.curve = var1;
      this.x = var2;
      this.y = var3;
      this.zs = var4;
   }

   protected abstract boolean satisfiesCurveEquation();

   protected boolean satisfiesOrder() {
      if (ECConstants.ONE.equals(this.curve.getCofactor())) {
         return true;
      } else {
         BigInteger var1 = this.curve.getOrder();
         return var1 == null || ECAlgorithms.referenceMultiply(this, var1).isInfinity();
      }
   }

   public final ECPoint getDetachedPoint() {
      return this.normalize().detach();
   }

   public ECCurve getCurve() {
      return this.curve;
   }

   protected abstract ECPoint detach();

   protected int getCurveCoordinateSystem() {
      return null == this.curve ? 0 : this.curve.getCoordinateSystem();
   }

   public ECFieldElement getAffineXCoord() {
      this.checkNormalized();
      return this.getXCoord();
   }

   public ECFieldElement getAffineYCoord() {
      this.checkNormalized();
      return this.getYCoord();
   }

   public ECFieldElement getXCoord() {
      return this.x;
   }

   public ECFieldElement getYCoord() {
      return this.y;
   }

   public ECFieldElement getZCoord(int var1) {
      return var1 >= 0 && var1 < this.zs.length ? this.zs[var1] : null;
   }

   public ECFieldElement[] getZCoords() {
      int var1 = this.zs.length;
      if (var1 == 0) {
         return EMPTY_ZS;
      } else {
         ECFieldElement[] var2 = new ECFieldElement[var1];
         System.arraycopy(this.zs, 0, var2, 0, var1);
         return var2;
      }
   }

   public final ECFieldElement getRawXCoord() {
      return this.x;
   }

   public final ECFieldElement getRawYCoord() {
      return this.y;
   }

   protected final ECFieldElement[] getRawZCoords() {
      return this.zs;
   }

   protected void checkNormalized() {
      if (!this.isNormalized()) {
         throw new IllegalStateException("point not in normal form");
      }
   }

   public boolean isNormalized() {
      int var1 = this.getCurveCoordinateSystem();
      return var1 == 0 || var1 == 5 || this.isInfinity() || this.zs[0].isOne();
   }

   public ECPoint normalize() {
      if (this.isInfinity()) {
         return this;
      } else {
         switch (this.getCurveCoordinateSystem()) {
            case 0:
            case 5:
               return this;
            default:
               ECFieldElement var1 = this.getZCoord(0);
               if (var1.isOne()) {
                  return this;
               } else if (null == this.curve) {
                  throw new IllegalStateException("Detached points must be in affine coordinates");
               } else {
                  SecureRandom var2 = CryptoServicesRegistrar.getSecureRandom();
                  ECFieldElement var3 = this.curve.randomFieldElementMult(var2);
                  ECFieldElement var4 = var1.multiply(var3).invert().multiply(var3);
                  return this.normalize(var4);
               }
         }
      }
   }

   ECPoint normalize(ECFieldElement var1) {
      switch (this.getCurveCoordinateSystem()) {
         case 1:
         case 6:
            return this.createScaledPoint(var1, var1);
         case 2:
         case 3:
         case 4:
            ECFieldElement var2 = var1.square();
            ECFieldElement var3 = var2.multiply(var1);
            return this.createScaledPoint(var2, var3);
         case 5:
         default:
            throw new IllegalStateException("not a projective coordinate system");
      }
   }

   protected ECPoint createScaledPoint(ECFieldElement var1, ECFieldElement var2) {
      return this.getCurve().createRawPoint(this.getRawXCoord().multiply(var1), this.getRawYCoord().multiply(var2));
   }

   public boolean isInfinity() {
      return this.x == null || this.y == null || this.zs.length > 0 && this.zs[0].isZero();
   }

   public boolean isValid() {
      return this.implIsValid(false, true);
   }

   boolean isValidPartial() {
      return this.implIsValid(false, false);
   }

   boolean implIsValid(final boolean var1, final boolean var2) {
      if (this.isInfinity()) {
         return true;
      } else {
         ValidityPrecompInfo var3 = (ValidityPrecompInfo)this.getCurve().precompute(this, "bc_validity", new PreCompCallback() {
            @Override
            public PreCompInfo precompute(PreCompInfo var1x) {
               ValidityPrecompInfo var2x = var1x instanceof ValidityPrecompInfo ? (ValidityPrecompInfo)var1x : null;
               if (var2x == null) {
                  var2x = new ValidityPrecompInfo();
               }

               if (var2x.hasFailed()) {
                  return var2x;
               } else {
                  if (!var2x.hasCurveEquationPassed()) {
                     if (!var1 && !ECPoint.this.satisfiesCurveEquation()) {
                        var2x.reportFailed();
                        return var2x;
                     }

                     var2x.reportCurveEquationPassed();
                  }

                  if (var2 && !var2x.hasOrderPassed()) {
                     if (!ECPoint.this.satisfiesOrder()) {
                        var2x.reportFailed();
                        return var2x;
                     }

                     var2x.reportOrderPassed();
                  }

                  return var2x;
               }
            }
         });
         return !var3.hasFailed();
      }
   }

   public ECPoint scaleX(ECFieldElement var1) {
      return this.isInfinity() ? this : this.getCurve().createRawPoint(this.getRawXCoord().multiply(var1), this.getRawYCoord(), this.getRawZCoords());
   }

   public ECPoint scaleXNegateY(ECFieldElement var1) {
      return this.isInfinity() ? this : this.getCurve().createRawPoint(this.getRawXCoord().multiply(var1), this.getRawYCoord().negate(), this.getRawZCoords());
   }

   public ECPoint scaleY(ECFieldElement var1) {
      return this.isInfinity() ? this : this.getCurve().createRawPoint(this.getRawXCoord(), this.getRawYCoord().multiply(var1), this.getRawZCoords());
   }

   public ECPoint scaleYNegateX(ECFieldElement var1) {
      return this.isInfinity() ? this : this.getCurve().createRawPoint(this.getRawXCoord().negate(), this.getRawYCoord().multiply(var1), this.getRawZCoords());
   }

   public boolean equals(ECPoint var1) {
      if (null == var1) {
         return false;
      } else {
         ECCurve var2 = this.getCurve();
         ECCurve var3 = var1.getCurve();
         boolean var4 = null == var2;
         boolean var5 = null == var3;
         boolean var6 = this.isInfinity();
         boolean var7 = var1.isInfinity();
         if (!var6 && !var7) {
            ECPoint var8 = this;
            ECPoint var9 = var1;
            if (!var4 || !var5) {
               if (var4) {
                  var9 = var1.normalize();
               } else if (var5) {
                  var8 = this.normalize();
               } else {
                  if (!var2.equals(var3)) {
                     return false;
                  }

                  ECPoint[] var10 = new ECPoint[]{this, var2.importPoint(var1)};
                  var2.normalizeAll(var10);
                  var8 = var10[0];
                  var9 = var10[1];
               }
            }

            return var8.getXCoord().equals(var9.getXCoord()) && var8.getYCoord().equals(var9.getYCoord());
         } else {
            return var6 && var7 && (var4 || var5 || var2.equals(var3));
         }
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else {
         return !(var1 instanceof ECPoint) ? false : this.equals((ECPoint)var1);
      }
   }

   @Override
   public int hashCode() {
      ECCurve var1 = this.getCurve();
      int var2 = null == var1 ? 0 : ~var1.hashCode();
      if (!this.isInfinity()) {
         ECPoint var3 = this.normalize();
         var2 ^= var3.getXCoord().hashCode() * 17;
         var2 ^= var3.getYCoord().hashCode() * 257;
      }

      return var2;
   }

   @Override
   public String toString() {
      if (this.isInfinity()) {
         return "INF";
      } else {
         StringBuilder var1 = new StringBuilder();
         var1.append('(');
         var1.append(this.getRawXCoord());
         var1.append(',');
         var1.append(this.getRawYCoord());

         for (int var2 = 0; var2 < this.zs.length; var2++) {
            var1.append(',');
            var1.append(this.zs[var2]);
         }

         var1.append(')');
         return var1.toString();
      }
   }

   public byte[] getEncoded(boolean var1) {
      if (this.isInfinity()) {
         return new byte[1];
      } else {
         ECPoint var2 = this.normalize();
         byte[] var3 = var2.getXCoord().getEncoded();
         if (var1) {
            byte[] var6 = new byte[var3.length + 1];
            var6[0] = (byte)(var2.getCompressionYTilde() ? 3 : 2);
            System.arraycopy(var3, 0, var6, 1, var3.length);
            return var6;
         } else {
            byte[] var4 = var2.getYCoord().getEncoded();
            byte[] var5 = new byte[var3.length + var4.length + 1];
            var5[0] = 4;
            System.arraycopy(var3, 0, var5, 1, var3.length);
            System.arraycopy(var4, 0, var5, var3.length + 1, var4.length);
            return var5;
         }
      }
   }

   public int getEncodedLength(boolean var1) {
      if (this.isInfinity()) {
         return 1;
      } else {
         return var1 ? 1 + this.getXCoord().getEncodedLength() : 1 + this.getXCoord().getEncodedLength() + this.getYCoord().getEncodedLength();
      }
   }

   public void encodeTo(boolean var1, byte[] var2, int var3) {
      if (this.isInfinity()) {
         var2[var3] = 0;
      } else {
         ECPoint var4 = this.normalize();
         ECFieldElement var5 = var4.getXCoord();
         ECFieldElement var6 = var4.getYCoord();
         if (var1) {
            var2[var3] = (byte)(var4.getCompressionYTilde() ? 3 : 2);
            var5.encodeTo(var2, var3 + 1);
         } else {
            var2[var3] = 4;
            var5.encodeTo(var2, var3 + 1);
            var6.encodeTo(var2, var3 + 1 + var5.getEncodedLength());
         }
      }
   }

   protected abstract boolean getCompressionYTilde();

   public abstract ECPoint add(ECPoint var1);

   public abstract ECPoint negate();

   public abstract ECPoint subtract(ECPoint var1);

   public ECPoint timesPow2(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("'e' cannot be negative");
      } else {
         ECPoint var2 = this;

         while (--var1 >= 0) {
            var2 = var2.twice();
         }

         return var2;
      }
   }

   public abstract ECPoint twice();

   public ECPoint twicePlus(ECPoint var1) {
      return this.twice().add(var1);
   }

   public ECPoint threeTimes() {
      return this.twicePlus(this);
   }

   public ECPoint multiply(BigInteger var1) {
      return this.getCurve().getMultiplier().multiply(this, var1);
   }

   public abstract static class AbstractF2m extends ECPoint {
      protected AbstractF2m(ECCurve var1, ECFieldElement var2, ECFieldElement var3) {
         super(var1, var2, var3);
      }

      protected AbstractF2m(ECCurve var1, ECFieldElement var2, ECFieldElement var3, ECFieldElement[] var4) {
         super(var1, var2, var3, var4);
      }

      @Override
      protected boolean satisfiesCurveEquation() {
         ECCurve var1 = this.getCurve();
         ECFieldElement var2 = this.x;
         ECFieldElement var3 = var1.getA();
         ECFieldElement var4 = var1.getB();
         int var5 = var1.getCoordinateSystem();
         if (var5 == 6) {
            ECFieldElement var14 = this.zs[0];
            boolean var15 = var14.isOne();
            if (var2.isZero()) {
               ECFieldElement var18 = this.y;
               ECFieldElement var20 = var18.square();
               ECFieldElement var23 = var4;
               if (!var15) {
                  var23 = var4.multiply(var14.square());
               }

               return var20.equals(var23);
            } else {
               ECFieldElement var17 = this.y;
               ECFieldElement var19 = var2.square();
               ECFieldElement var11;
               ECFieldElement var21;
               if (var15) {
                  var21 = var17.square().add(var17).add(var3);
                  var11 = var19.square().add(var4);
               } else {
                  ECFieldElement var12 = var14.square();
                  ECFieldElement var13 = var12.square();
                  var21 = var17.add(var14).multiplyPlusProduct(var17, var3, var12);
                  var11 = var19.squarePlusProduct(var4, var13);
               }

               var21 = var21.multiply(var19);
               return var21.equals(var11);
            }
         } else {
            ECFieldElement var6 = this.y;
            ECFieldElement var7 = var6.add(var2).multiply(var6);
            switch (var5) {
               case 1:
                  ECFieldElement var8 = this.zs[0];
                  if (!var8.isOne()) {
                     ECFieldElement var9 = var8.square();
                     ECFieldElement var10 = var8.multiply(var9);
                     var7 = var7.multiply(var8);
                     var3 = var3.multiply(var8);
                     var4 = var4.multiply(var10);
                  }
               case 0:
                  ECFieldElement var16 = var2.add(var3).multiply(var2.square()).add(var4);
                  return var7.equals(var16);
               default:
                  throw new IllegalStateException("unsupported coordinate system");
            }
         }
      }

      @Override
      protected boolean satisfiesOrder() {
         BigInteger var1 = this.curve.getCofactor();
         if (ECConstants.TWO.equals(var1)) {
            ECPoint var7 = this.normalize();
            ECFieldElement var8 = var7.getAffineXCoord();
            return 0 != ((ECFieldElement.AbstractF2m)var8).trace();
         } else if (ECConstants.FOUR.equals(var1)) {
            ECPoint var2 = this.normalize();
            ECFieldElement var3 = var2.getAffineXCoord();
            ECFieldElement var4 = ((ECCurve.AbstractF2m)this.curve).solveQuadraticEquation(var3.add(this.curve.getA()));
            if (null == var4) {
               return false;
            } else {
               ECFieldElement var5 = var2.getAffineYCoord();
               ECFieldElement var6 = var3.multiply(var4).add(var5);
               return 0 == ((ECFieldElement.AbstractF2m)var6).trace();
            }
         } else {
            return super.satisfiesOrder();
         }
      }

      @Override
      public ECPoint scaleX(ECFieldElement var1) {
         if (this.isInfinity()) {
            return this;
         } else {
            int var2 = this.getCurveCoordinateSystem();
            switch (var2) {
               case 5:
                  ECFieldElement var9 = this.getRawXCoord();
                  ECFieldElement var10 = this.getRawYCoord();
                  ECFieldElement var11 = var9.multiply(var1);
                  ECFieldElement var12 = var10.add(var9).divide(var1).add(var11);
                  return this.getCurve().createRawPoint(var9, var12, this.getRawZCoords());
               case 6:
                  ECFieldElement var3 = this.getRawXCoord();
                  ECFieldElement var4 = this.getRawYCoord();
                  ECFieldElement var5 = this.getRawZCoords()[0];
                  ECFieldElement var6 = var3.multiply(var1.square());
                  ECFieldElement var7 = var4.add(var3).add(var6);
                  ECFieldElement var8 = var5.multiply(var1);
                  return this.getCurve().createRawPoint(var6, var7, new ECFieldElement[]{var8});
               default:
                  return super.scaleX(var1);
            }
         }
      }

      @Override
      public ECPoint scaleXNegateY(ECFieldElement var1) {
         return this.scaleX(var1);
      }

      @Override
      public ECPoint scaleY(ECFieldElement var1) {
         if (this.isInfinity()) {
            return this;
         } else {
            int var2 = this.getCurveCoordinateSystem();
            switch (var2) {
               case 5:
               case 6:
                  ECFieldElement var3 = this.getRawXCoord();
                  ECFieldElement var4 = this.getRawYCoord();
                  ECFieldElement var5 = var4.add(var3).multiply(var1).add(var3);
                  return this.getCurve().createRawPoint(var3, var5, this.getRawZCoords());
               default:
                  return super.scaleY(var1);
            }
         }
      }

      @Override
      public ECPoint scaleYNegateX(ECFieldElement var1) {
         return this.scaleY(var1);
      }

      @Override
      public ECPoint subtract(ECPoint var1) {
         return (ECPoint)(var1.isInfinity() ? this : this.add(var1.negate()));
      }

      public ECPoint.AbstractF2m tau() {
         if (this.isInfinity()) {
            return this;
         } else {
            ECCurve var1 = this.getCurve();
            int var2 = var1.getCoordinateSystem();
            ECFieldElement var3 = this.x;
            switch (var2) {
               case 0:
               case 5:
                  ECFieldElement var6 = this.y;
                  return (ECPoint.AbstractF2m)var1.createRawPoint(var3.square(), var6.square());
               case 1:
               case 6:
                  ECFieldElement var4 = this.y;
                  ECFieldElement var5 = this.zs[0];
                  return (ECPoint.AbstractF2m)var1.createRawPoint(var3.square(), var4.square(), new ECFieldElement[]{var5.square()});
               case 2:
               case 3:
               case 4:
               default:
                  throw new IllegalStateException("unsupported coordinate system");
            }
         }
      }

      public ECPoint.AbstractF2m tauPow(int var1) {
         if (this.isInfinity()) {
            return this;
         } else {
            ECCurve var2 = this.getCurve();
            int var3 = var2.getCoordinateSystem();
            ECFieldElement var4 = this.x;
            switch (var3) {
               case 0:
               case 5:
                  ECFieldElement var7 = this.y;
                  return (ECPoint.AbstractF2m)var2.createRawPoint(var4.squarePow(var1), var7.squarePow(var1));
               case 1:
               case 6:
                  ECFieldElement var5 = this.y;
                  ECFieldElement var6 = this.zs[0];
                  return (ECPoint.AbstractF2m)var2.createRawPoint(var4.squarePow(var1), var5.squarePow(var1), new ECFieldElement[]{var6.squarePow(var1)});
               case 2:
               case 3:
               case 4:
               default:
                  throw new IllegalStateException("unsupported coordinate system");
            }
         }
      }
   }

   public abstract static class AbstractFp extends ECPoint {
      protected AbstractFp(ECCurve var1, ECFieldElement var2, ECFieldElement var3) {
         super(var1, var2, var3);
      }

      protected AbstractFp(ECCurve var1, ECFieldElement var2, ECFieldElement var3, ECFieldElement[] var4) {
         super(var1, var2, var3, var4);
      }

      @Override
      protected boolean getCompressionYTilde() {
         return this.getAffineYCoord().testBitZero();
      }

      @Override
      protected boolean satisfiesCurveEquation() {
         ECFieldElement var1 = this.x;
         ECFieldElement var2 = this.y;
         ECFieldElement var3 = this.curve.getA();
         ECFieldElement var4 = this.curve.getB();
         ECFieldElement var5 = var2.square();
         switch (this.getCurveCoordinateSystem()) {
            case 0:
               break;
            case 1:
               ECFieldElement var10 = this.zs[0];
               if (!var10.isOne()) {
                  ECFieldElement var12 = var10.square();
                  ECFieldElement var13 = var10.multiply(var12);
                  var5 = var5.multiply(var10);
                  var3 = var3.multiply(var12);
                  var4 = var4.multiply(var13);
               }
               break;
            case 2:
            case 3:
            case 4:
               ECFieldElement var6 = this.zs[0];
               if (!var6.isOne()) {
                  ECFieldElement var7 = var6.square();
                  ECFieldElement var8 = var7.square();
                  ECFieldElement var9 = var7.multiply(var8);
                  var3 = var3.multiply(var8);
                  var4 = var4.multiply(var9);
               }
               break;
            default:
               throw new IllegalStateException("unsupported coordinate system");
         }

         ECFieldElement var11 = var1.square().add(var3).multiply(var1).add(var4);
         return var5.equals(var11);
      }

      @Override
      public ECPoint subtract(ECPoint var1) {
         return (ECPoint)(var1.isInfinity() ? this : this.add(var1.negate()));
      }
   }

   public static class F2m extends ECPoint.AbstractF2m {
      F2m(ECCurve var1, ECFieldElement var2, ECFieldElement var3) {
         super(var1, var2, var3);
      }

      F2m(ECCurve var1, ECFieldElement var2, ECFieldElement var3, ECFieldElement[] var4) {
         super(var1, var2, var3, var4);
      }

      @Override
      protected ECPoint detach() {
         return new ECPoint.F2m(null, this.getAffineXCoord(), this.getAffineYCoord());
      }

      @Override
      public ECFieldElement getYCoord() {
         int var1 = this.getCurveCoordinateSystem();
         switch (var1) {
            case 5:
            case 6:
               ECFieldElement var2 = this.x;
               ECFieldElement var3 = this.y;
               if (!this.isInfinity() && !var2.isZero()) {
                  ECFieldElement var4 = var3.add(var2).multiply(var2);
                  if (6 == var1) {
                     ECFieldElement var5 = this.zs[0];
                     if (!var5.isOne()) {
                        var4 = var4.divide(var5);
                     }
                  }

                  return var4;
               }

               return var3;
            default:
               return this.y;
         }
      }

      @Override
      protected boolean getCompressionYTilde() {
         ECFieldElement var1 = this.getRawXCoord();
         if (var1.isZero()) {
            return false;
         } else {
            ECFieldElement var2 = this.getRawYCoord();
            switch (this.getCurveCoordinateSystem()) {
               case 5:
               case 6:
                  return var2.testBitZero() != var1.testBitZero();
               default:
                  return var2.divide(var1).testBitZero();
            }
         }
      }

      @Override
      public ECPoint add(ECPoint var1) {
         if (this.isInfinity()) {
            return var1;
         } else if (var1.isInfinity()) {
            return this;
         } else {
            ECCurve var2 = this.getCurve();
            int var3 = var2.getCoordinateSystem();
            ECFieldElement var4 = this.x;
            ECFieldElement var5 = var1.x;
            switch (var3) {
               case 0:
                  ECFieldElement var28 = this.y;
                  ECFieldElement var30 = var1.y;
                  ECFieldElement var32 = var4.add(var5);
                  ECFieldElement var34 = var28.add(var30);
                  if (var32.isZero()) {
                     if (var34.isZero()) {
                        return this.twice();
                     }

                     return var2.getInfinity();
                  }

                  ECFieldElement var36 = var34.divide(var32);
                  ECFieldElement var38 = var36.square().add(var36).add(var32).add(var2.getA());
                  ECFieldElement var40 = var36.multiply(var4.add(var38)).add(var38).add(var28);
                  return new ECPoint.F2m(var2, var38, var40);
               case 1:
                  ECFieldElement var27 = this.y;
                  ECFieldElement var29 = this.zs[0];
                  ECFieldElement var31 = var1.y;
                  ECFieldElement var33 = var1.zs[0];
                  boolean var35 = var33.isOne();
                  ECFieldElement var37 = var29.multiply(var31);
                  ECFieldElement var39 = var35 ? var27 : var27.multiply(var33);
                  ECFieldElement var41 = var37.add(var39);
                  ECFieldElement var42 = var29.multiply(var5);
                  ECFieldElement var43 = var35 ? var4 : var4.multiply(var33);
                  ECFieldElement var44 = var42.add(var43);
                  if (var44.isZero()) {
                     if (var41.isZero()) {
                        return this.twice();
                     }

                     return var2.getInfinity();
                  }

                  ECFieldElement var46 = var44.square();
                  ECFieldElement var47 = var46.multiply(var44);
                  ECFieldElement var48 = var35 ? var29 : var29.multiply(var33);
                  ECFieldElement var49 = var41.add(var44);
                  ECFieldElement var51 = var49.multiplyPlusProduct(var41, var46, var2.getA()).multiply(var48).add(var47);
                  ECFieldElement var53 = var44.multiply(var51);
                  ECFieldElement var54 = var35 ? var46 : var46.multiply(var33);
                  ECFieldElement var55 = var41.multiplyPlusProduct(var4, var44, var27).multiplyPlusProduct(var54, var49, var51);
                  ECFieldElement var56 = var47.multiply(var48);
                  return new ECPoint.F2m(var2, var53, var55, new ECFieldElement[]{var56});
               case 6:
                  if (var4.isZero()) {
                     if (var5.isZero()) {
                        return var2.getInfinity();
                     }

                     return var1.add(this);
                  } else {
                     ECFieldElement var6 = this.y;
                     ECFieldElement var7 = this.zs[0];
                     ECFieldElement var8 = var1.y;
                     ECFieldElement var9 = var1.zs[0];
                     boolean var10 = var7.isOne();
                     ECFieldElement var11 = var5;
                     ECFieldElement var12 = var8;
                     if (!var10) {
                        var11 = var5.multiply(var7);
                        var12 = var8.multiply(var7);
                     }

                     boolean var13 = var9.isOne();
                     ECFieldElement var14 = var4;
                     ECFieldElement var15 = var6;
                     if (!var13) {
                        var14 = var4.multiply(var9);
                        var15 = var6.multiply(var9);
                     }

                     ECFieldElement var16 = var15.add(var12);
                     ECFieldElement var17 = var14.add(var11);
                     if (var17.isZero()) {
                        if (var16.isZero()) {
                           return this.twice();
                        }

                        return var2.getInfinity();
                     } else {
                        ECFieldElement var18;
                        ECFieldElement var19;
                        ECFieldElement var20;
                        if (var5.isZero()) {
                           ECPoint var21 = this.normalize();
                           var4 = var21.getXCoord();
                           ECFieldElement var22 = var21.getYCoord();
                           ECFieldElement var24 = var22.add(var8).divide(var4);
                           var18 = var24.square().add(var24).add(var4).add(var2.getA());
                           if (var18.isZero()) {
                              return new ECPoint.F2m(var2, var18, var2.getB().sqrt());
                           }

                           ECFieldElement var25 = var24.multiply(var4.add(var18)).add(var18).add(var22);
                           var19 = var25.divide(var18).add(var18);
                           var20 = var2.fromBigInteger(ECConstants.ONE);
                        } else {
                           var17 = var17.square();
                           ECFieldElement var50 = var16.multiply(var14);
                           ECFieldElement var52 = var16.multiply(var11);
                           var18 = var50.multiply(var52);
                           if (var18.isZero()) {
                              return new ECPoint.F2m(var2, var18, var2.getB().sqrt());
                           }

                           ECFieldElement var23 = var16.multiply(var17);
                           if (!var13) {
                              var23 = var23.multiply(var9);
                           }

                           var19 = var52.add(var17).squarePlusProduct(var23, var6.add(var7));
                           var20 = var23;
                           if (!var10) {
                              var20 = var23.multiply(var7);
                           }
                        }

                        return new ECPoint.F2m(var2, var18, var19, new ECFieldElement[]{var20});
                     }
                  }
               default:
                  throw new IllegalStateException("unsupported coordinate system");
            }
         }
      }

      @Override
      public ECPoint twice() {
         if (this.isInfinity()) {
            return this;
         } else {
            ECCurve var1 = this.getCurve();
            ECFieldElement var2 = this.x;
            if (var2.isZero()) {
               return var1.getInfinity();
            } else {
               int var3 = var1.getCoordinateSystem();
               switch (var3) {
                  case 0:
                     ECFieldElement var19 = this.y;
                     ECFieldElement var21 = var19.divide(var2).add(var2);
                     ECFieldElement var23 = var21.square().add(var21).add(var1.getA());
                     ECFieldElement var25 = var2.squarePlusProduct(var23, var21.addOne());
                     return new ECPoint.F2m(var1, var23, var25);
                  case 1:
                     ECFieldElement var18 = this.y;
                     ECFieldElement var20 = this.zs[0];
                     boolean var22 = var20.isOne();
                     ECFieldElement var24 = var22 ? var2 : var2.multiply(var20);
                     ECFieldElement var26 = var22 ? var18 : var18.multiply(var20);
                     ECFieldElement var27 = var2.square();
                     ECFieldElement var28 = var27.add(var26);
                     ECFieldElement var29 = var24.square();
                     ECFieldElement var30 = var28.add(var24);
                     ECFieldElement var31 = var30.multiplyPlusProduct(var28, var29, var1.getA());
                     ECFieldElement var32 = var24.multiply(var31);
                     ECFieldElement var34 = var27.square().multiplyPlusProduct(var24, var31, var30);
                     ECFieldElement var35 = var24.multiply(var29);
                     return new ECPoint.F2m(var1, var32, var34, new ECFieldElement[]{var35});
                  case 6:
                     ECFieldElement var4 = this.y;
                     ECFieldElement var5 = this.zs[0];
                     boolean var6 = var5.isOne();
                     ECFieldElement var7 = var6 ? var4 : var4.multiply(var5);
                     ECFieldElement var8 = var6 ? var5 : var5.square();
                     ECFieldElement var9 = var1.getA();
                     ECFieldElement var10 = var6 ? var9 : var9.multiply(var8);
                     ECFieldElement var11 = var4.square().add(var7).add(var10);
                     if (var11.isZero()) {
                        return new ECPoint.F2m(var1, var11, var1.getB().sqrt());
                     }

                     ECFieldElement var12 = var11.square();
                     ECFieldElement var13 = var6 ? var11 : var11.multiply(var8);
                     ECFieldElement var14 = var1.getB();
                     ECFieldElement var15;
                     if (var14.bitLength() < var1.getFieldSize() >> 1) {
                        ECFieldElement var16 = var4.add(var2).square();
                        ECFieldElement var17;
                        if (var14.isOne()) {
                           var17 = var10.add(var8).square();
                        } else {
                           var17 = var10.squarePlusProduct(var14, var8.square());
                        }

                        var15 = var16.add(var11).add(var8).multiply(var16).add(var17).add(var12);
                        if (var9.isZero()) {
                           var15 = var15.add(var13);
                        } else if (!var9.isOne()) {
                           var15 = var15.add(var9.addOne().multiply(var13));
                        }
                     } else {
                        ECFieldElement var33 = var6 ? var2 : var2.multiply(var5);
                        var15 = var33.squarePlusProduct(var11, var7).add(var12).add(var13);
                     }

                     return new ECPoint.F2m(var1, var12, var15, new ECFieldElement[]{var13});
                  default:
                     throw new IllegalStateException("unsupported coordinate system");
               }
            }
         }
      }

      @Override
      public ECPoint twicePlus(ECPoint var1) {
         if (this.isInfinity()) {
            return var1;
         } else if (var1.isInfinity()) {
            return this.twice();
         } else {
            ECCurve var2 = this.getCurve();
            ECFieldElement var3 = this.x;
            if (var3.isZero()) {
               return var1;
            } else {
               int var4 = var2.getCoordinateSystem();
               switch (var4) {
                  case 6:
                     ECFieldElement var5 = var1.x;
                     ECFieldElement var6 = var1.zs[0];
                     if (!var5.isZero() && var6.isOne()) {
                        ECFieldElement var7 = this.y;
                        ECFieldElement var8 = this.zs[0];
                        ECFieldElement var9 = var1.y;
                        ECFieldElement var10 = var3.square();
                        ECFieldElement var11 = var7.square();
                        ECFieldElement var12 = var8.square();
                        ECFieldElement var13 = var7.multiply(var8);
                        ECFieldElement var14 = var2.getA().multiply(var12).add(var11).add(var13);
                        ECFieldElement var15 = var9.addOne();
                        ECFieldElement var16 = var2.getA().add(var15).multiply(var12).add(var11).multiplyPlusProduct(var14, var10, var12);
                        ECFieldElement var17 = var5.multiply(var12);
                        ECFieldElement var18 = var17.add(var14).square();
                        if (var18.isZero()) {
                           if (var16.isZero()) {
                              return var1.twice();
                           }

                           return var2.getInfinity();
                        }

                        if (var16.isZero()) {
                           return new ECPoint.F2m(var2, var16, var2.getB().sqrt());
                        }

                        ECFieldElement var19 = var16.square().multiply(var17);
                        ECFieldElement var20 = var16.multiply(var18).multiply(var12);
                        ECFieldElement var21 = var16.add(var18).square().multiplyPlusProduct(var14, var15, var20);
                        return new ECPoint.F2m(var2, var19, var21, new ECFieldElement[]{var20});
                     }

                     return this.twice().add(var1);
                  default:
                     return this.twice().add(var1);
               }
            }
         }
      }

      @Override
      public ECPoint negate() {
         if (this.isInfinity()) {
            return this;
         } else {
            ECFieldElement var1 = this.x;
            if (var1.isZero()) {
               return this;
            } else {
               switch (this.getCurveCoordinateSystem()) {
                  case 0:
                     ECFieldElement var6 = this.y;
                     return new ECPoint.F2m(this.curve, var1, var6.add(var1));
                  case 1:
                     ECFieldElement var5 = this.y;
                     ECFieldElement var7 = this.zs[0];
                     return new ECPoint.F2m(this.curve, var1, var5.add(var1), new ECFieldElement[]{var7});
                  case 2:
                  case 3:
                  case 4:
                  default:
                     throw new IllegalStateException("unsupported coordinate system");
                  case 5:
                     ECFieldElement var4 = this.y;
                     return new ECPoint.F2m(this.curve, var1, var4.addOne());
                  case 6:
                     ECFieldElement var2 = this.y;
                     ECFieldElement var3 = this.zs[0];
                     return new ECPoint.F2m(this.curve, var1, var2.add(var3), new ECFieldElement[]{var3});
               }
            }
         }
      }
   }

   public static class Fp extends ECPoint.AbstractFp {
      Fp(ECCurve var1, ECFieldElement var2, ECFieldElement var3) {
         super(var1, var2, var3);
      }

      Fp(ECCurve var1, ECFieldElement var2, ECFieldElement var3, ECFieldElement[] var4) {
         super(var1, var2, var3, var4);
      }

      @Override
      protected ECPoint detach() {
         return new ECPoint.Fp(null, this.getAffineXCoord(), this.getAffineYCoord());
      }

      @Override
      public ECFieldElement getZCoord(int var1) {
         return var1 == 1 && 4 == this.getCurveCoordinateSystem() ? this.getJacobianModifiedW() : super.getZCoord(var1);
      }

      @Override
      public ECPoint add(ECPoint var1) {
         if (this.isInfinity()) {
            return var1;
         } else if (var1.isInfinity()) {
            return this;
         } else if (this == var1) {
            return this.twice();
         } else {
            ECCurve var2 = this.getCurve();
            int var3 = var2.getCoordinateSystem();
            ECFieldElement var4 = this.x;
            ECFieldElement var5 = this.y;
            ECFieldElement var6 = var1.x;
            ECFieldElement var7 = var1.y;
            switch (var3) {
               case 0:
                  ECFieldElement var28 = var6.subtract(var4);
                  ECFieldElement var30 = var7.subtract(var5);
                  if (var28.isZero()) {
                     if (var30.isZero()) {
                        return this.twice();
                     }

                     return var2.getInfinity();
                  }

                  ECFieldElement var32 = var30.divide(var28);
                  ECFieldElement var34 = var32.square().subtract(var4).subtract(var6);
                  ECFieldElement var36 = var32.multiply(var4.subtract(var34)).subtract(var5);
                  return new ECPoint.Fp(var2, var34, var36);
               case 1:
                  ECFieldElement var27 = this.zs[0];
                  ECFieldElement var29 = var1.zs[0];
                  boolean var31 = var27.isOne();
                  boolean var33 = var29.isOne();
                  ECFieldElement var35 = var31 ? var7 : var7.multiply(var27);
                  ECFieldElement var37 = var33 ? var5 : var5.multiply(var29);
                  ECFieldElement var38 = var35.subtract(var37);
                  ECFieldElement var41 = var31 ? var6 : var6.multiply(var27);
                  ECFieldElement var44 = var33 ? var4 : var4.multiply(var29);
                  ECFieldElement var46 = var41.subtract(var44);
                  if (var46.isZero()) {
                     if (var38.isZero()) {
                        return this.twice();
                     }

                     return var2.getInfinity();
                  }

                  ECFieldElement var49 = var31 ? var29 : (var33 ? var27 : var27.multiply(var29));
                  ECFieldElement var51 = var46.square();
                  ECFieldElement var53 = var51.multiply(var46);
                  ECFieldElement var54 = var51.multiply(var44);
                  ECFieldElement var56 = var38.square().multiply(var49).subtract(var53).subtract(this.two(var54));
                  ECFieldElement var57 = var46.multiply(var56);
                  ECFieldElement var58 = var54.subtract(var56).multiplyMinusProduct(var38, var37, var53);
                  ECFieldElement var59 = var53.multiply(var49);
                  return new ECPoint.Fp(var2, var57, var58, new ECFieldElement[]{var59});
               case 2:
               case 4:
                  ECFieldElement var8 = this.zs[0];
                  ECFieldElement var9 = var1.zs[0];
                  boolean var10 = var8.isOne();
                  ECFieldElement var14 = null;
                  ECFieldElement var11;
                  ECFieldElement var12;
                  ECFieldElement var13;
                  if (!var10 && var8.equals(var9)) {
                     ECFieldElement var39 = var4.subtract(var6);
                     ECFieldElement var42 = var5.subtract(var7);
                     if (var39.isZero()) {
                        if (var42.isZero()) {
                           return this.twice();
                        }

                        return var2.getInfinity();
                     }

                     ECFieldElement var45 = var39.square();
                     ECFieldElement var48 = var4.multiply(var45);
                     ECFieldElement var50 = var6.multiply(var45);
                     ECFieldElement var52 = var48.subtract(var50).multiply(var5);
                     var11 = var42.square().subtract(var48).subtract(var50);
                     var12 = var48.subtract(var11).multiply(var42).subtract(var52);
                     var13 = var39.multiply(var8);
                  } else {
                     ECFieldElement var16;
                     ECFieldElement var17;
                     if (var10) {
                        var16 = var6;
                        var17 = var7;
                     } else {
                        ECFieldElement var15 = var8.square();
                        var16 = var15.multiply(var6);
                        ECFieldElement var18 = var15.multiply(var8);
                        var17 = var18.multiply(var7);
                     }

                     boolean var47 = var9.isOne();
                     ECFieldElement var20;
                     ECFieldElement var21;
                     if (var47) {
                        var20 = var4;
                        var21 = var5;
                     } else {
                        ECFieldElement var19 = var9.square();
                        var20 = var19.multiply(var4);
                        ECFieldElement var22 = var19.multiply(var9);
                        var21 = var22.multiply(var5);
                     }

                     ECFieldElement var55 = var20.subtract(var16);
                     ECFieldElement var23 = var21.subtract(var17);
                     if (var55.isZero()) {
                        if (var23.isZero()) {
                           return this.twice();
                        }

                        return var2.getInfinity();
                     }

                     ECFieldElement var24 = var55.square();
                     ECFieldElement var25 = var24.multiply(var55);
                     ECFieldElement var26 = var24.multiply(var20);
                     var11 = var23.square().add(var25).subtract(this.two(var26));
                     var12 = var26.subtract(var11).multiplyMinusProduct(var23, var25, var21);
                     var13 = var55;
                     if (!var10) {
                        var13 = var55.multiply(var8);
                     }

                     if (!var47) {
                        var13 = var13.multiply(var9);
                     }

                     if (var13 == var55) {
                        var14 = var24;
                     }
                  }

                  ECFieldElement[] var40;
                  if (var3 == 4) {
                     ECFieldElement var43 = this.calculateJacobianModifiedW(var13, var14);
                     var40 = new ECFieldElement[]{var13, var43};
                  } else {
                     var40 = new ECFieldElement[]{var13};
                  }

                  return new ECPoint.Fp(var2, var11, var12, var40);
               case 3:
               default:
                  throw new IllegalStateException("unsupported coordinate system");
            }
         }
      }

      @Override
      public ECPoint twice() {
         if (this.isInfinity()) {
            return this;
         } else {
            ECCurve var1 = this.getCurve();
            ECFieldElement var2 = this.y;
            if (var2.isZero()) {
               return var1.getInfinity();
            } else {
               int var3 = var1.getCoordinateSystem();
               ECFieldElement var4 = this.x;
               switch (var3) {
                  case 0:
                     ECFieldElement var20 = var4.square();
                     ECFieldElement var22 = this.three(var20).add(this.getCurve().getA()).divide(this.two(var2));
                     ECFieldElement var25 = var22.square().subtract(this.two(var4));
                     ECFieldElement var27 = var22.multiply(var4.subtract(var25)).subtract(var2);
                     return new ECPoint.Fp(var1, var25, var27);
                  case 1:
                     ECFieldElement var19 = this.zs[0];
                     boolean var21 = var19.isOne();
                     ECFieldElement var23 = var1.getA();
                     if (!var23.isZero() && !var21) {
                        var23 = var23.multiply(var19.square());
                     }

                     var23 = var23.add(this.three(var4.square()));
                     ECFieldElement var26 = var21 ? var2 : var2.multiply(var19);
                     ECFieldElement var28 = var21 ? var2.square() : var26.multiply(var2);
                     ECFieldElement var29 = var4.multiply(var28);
                     ECFieldElement var30 = this.four(var29);
                     ECFieldElement var31 = var23.square().subtract(this.two(var30));
                     ECFieldElement var34 = this.two(var26);
                     ECFieldElement var36 = var31.multiply(var34);
                     ECFieldElement var38 = this.two(var28);
                     ECFieldElement var16 = var30.subtract(var31).multiply(var23).subtract(this.two(var38.square()));
                     ECFieldElement var17 = var21 ? this.two(var38) : var34.square();
                     ECFieldElement var18 = this.two(var17).multiply(var26);
                     return new ECPoint.Fp(var1, var36, var16, new ECFieldElement[]{var18});
                  case 2:
                     ECFieldElement var5 = this.zs[0];
                     boolean var6 = var5.isOne();
                     ECFieldElement var7 = var2.square();
                     ECFieldElement var8 = var7.square();
                     ECFieldElement var9 = var1.getA();
                     ECFieldElement var10 = var9.negate();
                     ECFieldElement var11;
                     ECFieldElement var12;
                     if (var10.toBigInteger().equals(BigInteger.valueOf(3L))) {
                        ECFieldElement var13 = var6 ? var5 : var5.square();
                        var11 = this.three(var4.add(var13).multiply(var4.subtract(var13)));
                        var12 = this.four(var7.multiply(var4));
                     } else {
                        ECFieldElement var32 = var4.square();
                        var11 = this.three(var32);
                        if (var6) {
                           var11 = var11.add(var9);
                        } else if (!var9.isZero()) {
                           ECFieldElement var14 = var5.square();
                           ECFieldElement var15 = var14.square();
                           if (var10.bitLength() < var9.bitLength()) {
                              var11 = var11.subtract(var15.multiply(var10));
                           } else {
                              var11 = var11.add(var15.multiply(var9));
                           }
                        }

                        var12 = this.four(var4.multiply(var7));
                     }

                     ECFieldElement var33 = var11.square().subtract(this.two(var12));
                     ECFieldElement var35 = var12.subtract(var33).multiply(var11).subtract(this.eight(var8));
                     ECFieldElement var37 = this.two(var2);
                     if (!var6) {
                        var37 = var37.multiply(var5);
                     }

                     return new ECPoint.Fp(var1, var33, var35, new ECFieldElement[]{var37});
                  case 3:
                  default:
                     throw new IllegalStateException("unsupported coordinate system");
                  case 4:
                     return this.twiceJacobianModified(true);
               }
            }
         }
      }

      @Override
      public ECPoint twicePlus(ECPoint var1) {
         if (this == var1) {
            return this.threeTimes();
         } else if (this.isInfinity()) {
            return var1;
         } else if (var1.isInfinity()) {
            return this.twice();
         } else {
            ECFieldElement var2 = this.y;
            if (var2.isZero()) {
               return var1;
            } else {
               ECCurve var3 = this.getCurve();
               int var4 = var3.getCoordinateSystem();
               switch (var4) {
                  case 0:
                     ECFieldElement var5 = this.x;
                     ECFieldElement var6 = var1.x;
                     ECFieldElement var7 = var1.y;
                     ECFieldElement var8 = var6.subtract(var5);
                     ECFieldElement var9 = var7.subtract(var2);
                     if (var8.isZero()) {
                        if (var9.isZero()) {
                           return this.threeTimes();
                        }

                        return this;
                     } else {
                        ECFieldElement var10 = var8.square();
                        ECFieldElement var11 = var9.square();
                        ECFieldElement var12 = var10.multiply(this.two(var5).add(var6)).subtract(var11);
                        if (var12.isZero()) {
                           return var3.getInfinity();
                        }

                        ECFieldElement var13 = var12.multiply(var8);
                        ECFieldElement var14 = var13.invert();
                        ECFieldElement var15 = var12.multiply(var14).multiply(var9);
                        ECFieldElement var16 = this.two(var2).multiply(var10).multiply(var8).multiply(var14).subtract(var15);
                        ECFieldElement var17 = var16.subtract(var15).multiply(var15.add(var16)).add(var6);
                        ECFieldElement var18 = var5.subtract(var17).multiply(var16).subtract(var2);
                        return new ECPoint.Fp(var3, var17, var18);
                     }
                  case 4:
                     return this.twiceJacobianModified(false).add(var1);
                  default:
                     return this.twice().add(var1);
               }
            }
         }
      }

      @Override
      public ECPoint threeTimes() {
         if (this.isInfinity()) {
            return this;
         } else {
            ECFieldElement var1 = this.y;
            if (var1.isZero()) {
               return this;
            } else {
               ECCurve var2 = this.getCurve();
               int var3 = var2.getCoordinateSystem();
               switch (var3) {
                  case 0:
                     ECFieldElement var4 = this.x;
                     ECFieldElement var5 = this.two(var1);
                     ECFieldElement var6 = var5.square();
                     ECFieldElement var7 = this.three(var4.square()).add(this.getCurve().getA());
                     ECFieldElement var8 = var7.square();
                     ECFieldElement var9 = this.three(var4).multiply(var6).subtract(var8);
                     if (var9.isZero()) {
                        return this.getCurve().getInfinity();
                     }

                     ECFieldElement var10 = var9.multiply(var5);
                     ECFieldElement var11 = var10.invert();
                     ECFieldElement var12 = var9.multiply(var11).multiply(var7);
                     ECFieldElement var13 = var6.square().multiply(var11).subtract(var12);
                     ECFieldElement var14 = var13.subtract(var12).multiply(var12.add(var13)).add(var4);
                     ECFieldElement var15 = var4.subtract(var14).multiply(var13).subtract(var1);
                     return new ECPoint.Fp(var2, var14, var15);
                  case 4:
                     return this.twiceJacobianModified(false).add(this);
                  default:
                     return this.twice().add(this);
               }
            }
         }
      }

      @Override
      public ECPoint timesPow2(int var1) {
         if (var1 < 0) {
            throw new IllegalArgumentException("'e' cannot be negative");
         } else if (var1 != 0 && !this.isInfinity()) {
            if (var1 == 1) {
               return this.twice();
            } else {
               ECCurve var2 = this.getCurve();
               ECFieldElement var3 = this.y;
               if (var3.isZero()) {
                  return var2.getInfinity();
               } else {
                  int var4 = var2.getCoordinateSystem();
                  ECFieldElement var5 = var2.getA();
                  ECFieldElement var6 = this.x;
                  ECFieldElement var7 = this.zs.length < 1 ? var2.fromBigInteger(ECConstants.ONE) : this.zs[0];
                  if (!var7.isOne()) {
                     switch (var4) {
                        case 0:
                           break;
                        case 1:
                           ECFieldElement var8 = var7.square();
                           var6 = var6.multiply(var7);
                           var3 = var3.multiply(var8);
                           var5 = this.calculateJacobianModifiedW(var7, var8);
                           break;
                        case 2:
                           var5 = this.calculateJacobianModifiedW(var7, null);
                           break;
                        case 3:
                        default:
                           throw new IllegalStateException("unsupported coordinate system");
                        case 4:
                           var5 = this.getJacobianModifiedW();
                     }
                  }

                  for (int var18 = 0; var18 < var1; var18++) {
                     if (var3.isZero()) {
                        return var2.getInfinity();
                     }

                     ECFieldElement var9 = var6.square();
                     ECFieldElement var10 = this.three(var9);
                     ECFieldElement var11 = this.two(var3);
                     ECFieldElement var12 = var11.multiply(var3);
                     ECFieldElement var13 = this.two(var6.multiply(var12));
                     ECFieldElement var14 = var12.square();
                     ECFieldElement var15 = this.two(var14);
                     if (!var5.isZero()) {
                        var10 = var10.add(var5);
                        var5 = this.two(var15.multiply(var5));
                     }

                     var6 = var10.square().subtract(this.two(var13));
                     var3 = var10.multiply(var13.subtract(var6)).subtract(var15);
                     var7 = var7.isOne() ? var11 : var11.multiply(var7);
                  }

                  switch (var4) {
                     case 0:
                        ECFieldElement var19 = var7.invert();
                        ECFieldElement var20 = var19.square();
                        ECFieldElement var21 = var20.multiply(var19);
                        return new ECPoint.Fp(var2, var6.multiply(var20), var3.multiply(var21));
                     case 1:
                        var6 = var6.multiply(var7);
                        var7 = var7.multiply(var7.square());
                        return new ECPoint.Fp(var2, var6, var3, new ECFieldElement[]{var7});
                     case 2:
                        return new ECPoint.Fp(var2, var6, var3, new ECFieldElement[]{var7});
                     case 3:
                     default:
                        throw new IllegalStateException("unsupported coordinate system");
                     case 4:
                        return new ECPoint.Fp(var2, var6, var3, new ECFieldElement[]{var7, var5});
                  }
               }
            }
         } else {
            return this;
         }
      }

      protected ECFieldElement two(ECFieldElement var1) {
         return var1.add(var1);
      }

      protected ECFieldElement three(ECFieldElement var1) {
         return this.two(var1).add(var1);
      }

      protected ECFieldElement four(ECFieldElement var1) {
         return this.two(this.two(var1));
      }

      protected ECFieldElement eight(ECFieldElement var1) {
         return this.four(this.two(var1));
      }

      protected ECFieldElement doubleProductFromSquares(ECFieldElement var1, ECFieldElement var2, ECFieldElement var3, ECFieldElement var4) {
         return var1.add(var2).square().subtract(var3).subtract(var4);
      }

      @Override
      public ECPoint negate() {
         if (this.isInfinity()) {
            return this;
         } else {
            ECCurve var1 = this.getCurve();
            int var2 = var1.getCoordinateSystem();
            return 0 != var2 ? new ECPoint.Fp(var1, this.x, this.y.negate(), this.zs) : new ECPoint.Fp(var1, this.x, this.y.negate());
         }
      }

      protected ECFieldElement calculateJacobianModifiedW(ECFieldElement var1, ECFieldElement var2) {
         ECFieldElement var3 = this.getCurve().getA();
         if (!var3.isZero() && !var1.isOne()) {
            if (var2 == null) {
               var2 = var1.square();
            }

            ECFieldElement var4 = var2.square();
            ECFieldElement var5 = var3.negate();
            if (var5.bitLength() < var3.bitLength()) {
               var4 = var4.multiply(var5).negate();
            } else {
               var4 = var4.multiply(var3);
            }

            return var4;
         } else {
            return var3;
         }
      }

      protected ECFieldElement getJacobianModifiedW() {
         ECFieldElement var1 = this.zs[1];
         if (var1 == null) {
            this.zs[1] = var1 = this.calculateJacobianModifiedW(this.zs[0], null);
         }

         return var1;
      }

      protected ECPoint.Fp twiceJacobianModified(boolean var1) {
         ECFieldElement var2 = this.x;
         ECFieldElement var3 = this.y;
         ECFieldElement var4 = this.zs[0];
         ECFieldElement var5 = this.getJacobianModifiedW();
         ECFieldElement var6 = var2.square();
         ECFieldElement var7 = this.three(var6).add(var5);
         ECFieldElement var8 = this.two(var3);
         ECFieldElement var9 = var8.multiply(var3);
         ECFieldElement var10 = this.two(var2.multiply(var9));
         ECFieldElement var11 = var7.square().subtract(this.two(var10));
         ECFieldElement var12 = var9.square();
         ECFieldElement var13 = this.two(var12);
         ECFieldElement var14 = var7.multiply(var10.subtract(var11)).subtract(var13);
         ECFieldElement var15 = var1 ? this.two(var13.multiply(var5)) : null;
         ECFieldElement var16 = var4.isOne() ? var8 : var8.multiply(var4);
         return new ECPoint.Fp(this.getCurve(), var11, var14, new ECFieldElement[]{var16, var15});
      }
   }
}
