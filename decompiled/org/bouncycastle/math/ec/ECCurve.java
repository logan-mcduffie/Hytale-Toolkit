package org.bouncycastle.math.ec;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.math.Primes;
import org.bouncycastle.math.ec.endo.ECEndomorphism;
import org.bouncycastle.math.ec.endo.GLVEndomorphism;
import org.bouncycastle.math.field.FiniteField;
import org.bouncycastle.math.field.FiniteFields;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Properties;

public abstract class ECCurve {
   public static final int COORD_AFFINE = 0;
   public static final int COORD_HOMOGENEOUS = 1;
   public static final int COORD_JACOBIAN = 2;
   public static final int COORD_JACOBIAN_CHUDNOVSKY = 3;
   public static final int COORD_JACOBIAN_MODIFIED = 4;
   public static final int COORD_LAMBDA_AFFINE = 5;
   public static final int COORD_LAMBDA_PROJECTIVE = 6;
   public static final int COORD_SKEWED = 7;
   protected FiniteField field;
   protected ECFieldElement a;
   protected ECFieldElement b;
   protected BigInteger order;
   protected BigInteger cofactor;
   protected int coord = 0;
   protected ECEndomorphism endomorphism = null;
   protected ECMultiplier multiplier = null;

   public static int[] getAllCoordinateSystems() {
      return new int[]{0, 1, 2, 3, 4, 5, 6, 7};
   }

   protected ECCurve(FiniteField var1) {
      this.field = var1;
   }

   public abstract int getFieldSize();

   public abstract ECFieldElement fromBigInteger(BigInteger var1);

   public abstract boolean isValidFieldElement(BigInteger var1);

   public abstract ECFieldElement randomFieldElement(SecureRandom var1);

   public abstract ECFieldElement randomFieldElementMult(SecureRandom var1);

   public synchronized ECCurve.Config configure() {
      return new ECCurve.Config(this.coord, this.endomorphism, this.multiplier);
   }

   public int getFieldElementEncodingLength() {
      return (this.getFieldSize() + 7) / 8;
   }

   public int getAffinePointEncodingLength(boolean var1) {
      int var2 = this.getFieldElementEncodingLength();
      return var1 ? 1 + var2 : 1 + var2 * 2;
   }

   public ECPoint validatePoint(BigInteger var1, BigInteger var2) {
      ECPoint var3 = this.createPoint(var1, var2);
      if (!var3.isValid()) {
         throw new IllegalArgumentException("Invalid point coordinates");
      } else {
         return var3;
      }
   }

   public ECPoint createPoint(BigInteger var1, BigInteger var2) {
      return this.createRawPoint(this.fromBigInteger(var1), this.fromBigInteger(var2));
   }

   protected abstract ECCurve cloneCurve();

   protected abstract ECPoint createRawPoint(ECFieldElement var1, ECFieldElement var2);

   protected abstract ECPoint createRawPoint(ECFieldElement var1, ECFieldElement var2, ECFieldElement[] var3);

   protected ECMultiplier createDefaultMultiplier() {
      return (ECMultiplier)(this.endomorphism instanceof GLVEndomorphism
         ? new GLVMultiplier(this, (GLVEndomorphism)this.endomorphism)
         : new WNafL2RMultiplier());
   }

   public boolean supportsCoordinateSystem(int var1) {
      return var1 == 0;
   }

   public PreCompInfo getPreCompInfo(ECPoint var1, String var2) {
      this.checkPoint(var1);
      Hashtable var3;
      synchronized (var1) {
         var3 = var1.preCompTable;
      }

      if (null == var3) {
         return null;
      } else {
         synchronized (var3) {
            return (PreCompInfo)var3.get(var2);
         }
      }
   }

   public PreCompInfo precompute(ECPoint var1, String var2, PreCompCallback var3) {
      this.checkPoint(var1);
      Hashtable var4;
      synchronized (var1) {
         var4 = var1.preCompTable;
         if (null == var4) {
            var1.preCompTable = var4 = new Hashtable(4);
         }
      }

      synchronized (var4) {
         PreCompInfo var6 = (PreCompInfo)var4.get(var2);
         PreCompInfo var7 = var3.precompute(var6);
         if (var7 != var6) {
            var4.put(var2, var7);
         }

         return var7;
      }
   }

   public ECPoint importPoint(ECPoint var1) {
      if (this == var1.getCurve()) {
         return var1;
      } else if (var1.isInfinity()) {
         return this.getInfinity();
      } else {
         var1 = var1.normalize();
         return this.createPoint(var1.getXCoord().toBigInteger(), var1.getYCoord().toBigInteger());
      }
   }

   public void normalizeAll(ECPoint[] var1) {
      this.normalizeAll(var1, 0, var1.length, null);
   }

   public void normalizeAll(ECPoint[] var1, int var2, int var3, ECFieldElement var4) {
      this.checkPoints(var1, var2, var3);
      switch (this.getCoordinateSystem()) {
         case 0:
         case 5:
            if (var4 != null) {
               throw new IllegalArgumentException("'iso' not valid for affine coordinates");
            }

            return;
         default:
            ECFieldElement[] var5 = new ECFieldElement[var3];
            int[] var6 = new int[var3];
            int var7 = 0;
            int var8 = 0;

            for (; var8 < var3; var8++) {
               ECPoint var9 = var1[var2 + var8];
               if (null != var9 && (var4 != null || !var9.isNormalized())) {
                  var5[var7] = var9.getZCoord(0);
                  var6[var7++] = var2 + var8;
               }
            }

            if (var7 != 0) {
               ECAlgorithms.montgomeryTrick(var5, 0, var7, var4);

               for (int var10 = 0; var10 < var7; var10++) {
                  int var11 = var6[var10];
                  var1[var11] = var1[var11].normalize(var5[var10]);
               }
            }
      }
   }

   public abstract ECPoint getInfinity();

   public FiniteField getField() {
      return this.field;
   }

   public ECFieldElement getA() {
      return this.a;
   }

   public ECFieldElement getB() {
      return this.b;
   }

   public BigInteger getOrder() {
      return this.order;
   }

   public BigInteger getCofactor() {
      return this.cofactor;
   }

   public int getCoordinateSystem() {
      return this.coord;
   }

   protected abstract ECPoint decompressPoint(int var1, BigInteger var2);

   public ECEndomorphism getEndomorphism() {
      return this.endomorphism;
   }

   public ECMultiplier getMultiplier() {
      if (this.multiplier == null) {
         this.multiplier = this.createDefaultMultiplier();
      }

      return this.multiplier;
   }

   public ECPoint decodePoint(byte[] var1) {
      ECPoint var2 = null;
      int var3 = this.getFieldElementEncodingLength();
      byte var4 = var1[0];
      switch (var4) {
         case 0:
            if (var1.length != 1) {
               throw new IllegalArgumentException("Incorrect length for infinity encoding");
            }

            var2 = this.getInfinity();
            break;
         case 1:
         case 5:
         default:
            throw new IllegalArgumentException("Invalid point encoding 0x" + Integer.toString(var4, 16));
         case 2:
         case 3:
            if (var1.length != var3 + 1) {
               throw new IllegalArgumentException("Incorrect length for compressed encoding");
            }

            int var9 = var4 & 1;
            BigInteger var11 = BigIntegers.fromUnsignedByteArray(var1, 1, var3);
            var2 = this.decompressPoint(var9, var11);
            if (!var2.implIsValid(true, true)) {
               throw new IllegalArgumentException("Invalid point");
            }
            break;
         case 4:
            if (var1.length != 2 * var3 + 1) {
               throw new IllegalArgumentException("Incorrect length for uncompressed encoding");
            }

            BigInteger var8 = BigIntegers.fromUnsignedByteArray(var1, 1, var3);
            BigInteger var10 = BigIntegers.fromUnsignedByteArray(var1, 1 + var3, var3);
            var2 = this.validatePoint(var8, var10);
            break;
         case 6:
         case 7:
            if (var1.length != 2 * var3 + 1) {
               throw new IllegalArgumentException("Incorrect length for hybrid encoding");
            }

            BigInteger var5 = BigIntegers.fromUnsignedByteArray(var1, 1, var3);
            BigInteger var6 = BigIntegers.fromUnsignedByteArray(var1, 1 + var3, var3);
            if (var6.testBit(0) != (var4 == 7)) {
               throw new IllegalArgumentException("Inconsistent Y coordinate in hybrid encoding");
            }

            var2 = this.validatePoint(var5, var6);
      }

      if (var4 != 0 && var2.isInfinity()) {
         throw new IllegalArgumentException("Invalid infinity encoding");
      } else {
         return var2;
      }
   }

   public ECLookupTable createCacheSafeLookupTable(ECPoint[] var1, int var2, final int var3) {
      final int var4 = this.getFieldElementEncodingLength();
      final byte[] var5 = new byte[var3 * var4 * 2];
      int var6 = 0;

      for (int var7 = 0; var7 < var3; var7++) {
         ECPoint var8 = var1[var2 + var7];
         var8.getRawXCoord().encodeTo(var5, var6);
         var6 += var4;
         var8.getRawYCoord().encodeTo(var5, var6);
         var6 += var4;
      }

      return new AbstractECLookupTable() {
         @Override
         public int getSize() {
            return var3;
         }

         @Override
         public ECPoint lookup(int var1) {
            byte[] var2x = new byte[var4];
            byte[] var3x = new byte[var4];
            int var4x = 0;

            for (int var5x = 0; var5x < var3; var5x++) {
               int var6x = (var5x ^ var1) - 1 >> 31;

               for (int var7 = 0; var7 < var4; var7++) {
                  var2x[var7] = (byte)(var2x[var7] ^ var5[var4x + var7] & var6x);
                  var3x[var7] = (byte)(var3x[var7] ^ var5[var4x + var4 + var7] & var6x);
               }

               var4x += var4 * 2;
            }

            return this.createPoint(var2x, var3x);
         }

         @Override
         public ECPoint lookupVar(int var1) {
            byte[] var2x = new byte[var4];
            byte[] var3x = new byte[var4];
            int var4x = var1 * var4 * 2;

            for (int var5x = 0; var5x < var4; var5x++) {
               var2x[var5x] = var5[var4x + var5x];
               var3x[var5x] = var5[var4x + var4 + var5x];
            }

            return this.createPoint(var2x, var3x);
         }

         private ECPoint createPoint(byte[] var1, byte[] var2x) {
            return ECCurve.this.createRawPoint(ECCurve.this.fromBigInteger(new BigInteger(1, var1)), ECCurve.this.fromBigInteger(new BigInteger(1, var2x)));
         }
      };
   }

   protected void checkPoint(ECPoint var1) {
      if (null == var1 || this != var1.getCurve()) {
         throw new IllegalArgumentException("'point' must be non-null and on this curve");
      }
   }

   protected void checkPoints(ECPoint[] var1) {
      this.checkPoints(var1, 0, var1.length);
   }

   protected void checkPoints(ECPoint[] var1, int var2, int var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("'points' cannot be null");
      } else if (var2 >= 0 && var3 >= 0 && var2 <= var1.length - var3) {
         for (int var4 = 0; var4 < var3; var4++) {
            ECPoint var5 = var1[var2 + var4];
            if (null != var5 && this != var5.getCurve()) {
               throw new IllegalArgumentException("'points' entries must be null or on this curve");
            }
         }
      } else {
         throw new IllegalArgumentException("invalid range specified for 'points'");
      }
   }

   public boolean equals(ECCurve var1) {
      return this == var1
         || null != var1
            && this.getField().equals(var1.getField())
            && this.getA().toBigInteger().equals(var1.getA().toBigInteger())
            && this.getB().toBigInteger().equals(var1.getB().toBigInteger());
   }

   @Override
   public boolean equals(Object var1) {
      return this == var1 || var1 instanceof ECCurve && this.equals((ECCurve)var1);
   }

   @Override
   public int hashCode() {
      return this.getField().hashCode()
         ^ Integers.rotateLeft(this.getA().toBigInteger().hashCode(), 8)
         ^ Integers.rotateLeft(this.getB().toBigInteger().hashCode(), 16);
   }

   private static int getNumberOfIterations(int var0, int var1) {
      if (var0 >= 1536) {
         return var1 <= 100 ? 3 : (var1 <= 128 ? 4 : 4 + (var1 - 128 + 1) / 2);
      } else if (var0 >= 1024) {
         return var1 <= 100 ? 4 : (var1 <= 112 ? 5 : 5 + (var1 - 112 + 1) / 2);
      } else if (var0 >= 512) {
         return var1 <= 80 ? 5 : (var1 <= 100 ? 7 : 7 + (var1 - 100 + 1) / 2);
      } else {
         return var1 <= 80 ? 40 : 40 + (var1 - 80 + 1) / 2;
      }
   }

   public abstract static class AbstractF2m extends ECCurve {
      private BigInteger[] si = null;

      public static BigInteger inverse(int var0, int[] var1, BigInteger var2) {
         return new LongArray(var2).modInverse(var0, var1).toBigInteger();
      }

      private static FiniteField buildField(int var0, int var1, int var2, int var3) {
         if (var0 > Properties.asInteger("org.bouncycastle.ec.max_f2m_field_size", 1142)) {
            throw new IllegalArgumentException("field size out of range: " + var0);
         } else {
            int[] var4 = (var2 | var3) == 0 ? new int[]{0, var1, var0} : new int[]{0, var1, var2, var3, var0};
            return FiniteFields.getBinaryExtensionField(var4);
         }
      }

      protected AbstractF2m(int var1, int var2, int var3, int var4) {
         super(buildField(var1, var2, var3, var4));
         if (Properties.isOverrideSet("org.bouncycastle.ec.disable")) {
            throw new UnsupportedOperationException("F2M disabled by \"org.bouncycastle.ec.disable\"");
         } else if (Properties.isOverrideSet("org.bouncycastle.ec.disable_f2m")) {
            throw new UnsupportedOperationException("F2M disabled by \"org.bouncycastle.ec.disable_f2m\"");
         }
      }

      @Override
      public ECPoint createPoint(BigInteger var1, BigInteger var2) {
         ECFieldElement var3 = this.fromBigInteger(var1);
         ECFieldElement var4 = this.fromBigInteger(var2);
         int var5 = this.getCoordinateSystem();
         switch (var5) {
            case 5:
            case 6:
               if (var3.isZero()) {
                  if (!var4.square().equals(this.getB())) {
                     throw new IllegalArgumentException();
                  }
               } else {
                  var4 = var4.divide(var3).add(var3);
               }
            default:
               return this.createRawPoint(var3, var4);
         }
      }

      @Override
      public boolean isValidFieldElement(BigInteger var1) {
         return var1 != null && var1.signum() >= 0 && var1.bitLength() <= this.getFieldSize();
      }

      @Override
      public ECFieldElement randomFieldElement(SecureRandom var1) {
         int var2 = this.getFieldSize();
         return this.fromBigInteger(BigIntegers.createRandomBigInteger(var2, var1));
      }

      @Override
      public ECFieldElement randomFieldElementMult(SecureRandom var1) {
         int var2 = this.getFieldSize();
         ECFieldElement var3 = this.fromBigInteger(implRandomFieldElementMult(var1, var2));
         ECFieldElement var4 = this.fromBigInteger(implRandomFieldElementMult(var1, var2));
         return var3.multiply(var4);
      }

      @Override
      protected ECPoint decompressPoint(int var1, BigInteger var2) {
         ECFieldElement var3 = this.fromBigInteger(var2);
         ECFieldElement var4 = null;
         if (var3.isZero()) {
            var4 = this.getB().sqrt();
         } else {
            ECFieldElement var5 = var3.square().invert().multiply(this.getB()).add(this.getA()).add(var3);
            ECFieldElement var6 = this.solveQuadraticEquation(var5);
            if (var6 != null) {
               if (var6.testBitZero() != (var1 == 1)) {
                  var6 = var6.addOne();
               }

               switch (this.getCoordinateSystem()) {
                  case 5:
                  case 6:
                     var4 = var6.add(var3);
                     break;
                  default:
                     var4 = var6.multiply(var3);
               }
            }
         }

         if (var4 == null) {
            throw new IllegalArgumentException("Invalid point compression");
         } else {
            return this.createRawPoint(var3, var4);
         }
      }

      protected ECFieldElement solveQuadraticEquation(ECFieldElement var1) {
         ECFieldElement.AbstractF2m var2 = (ECFieldElement.AbstractF2m)var1;
         boolean var3 = var2.hasFastTrace();
         if (var3 && 0 != var2.trace()) {
            return null;
         } else {
            int var4 = this.getFieldSize();
            if (0 != (var4 & 1)) {
               ECFieldElement var13 = var2.halfTrace();
               return !var3 && !var13.square().add(var13).add(var1).isZero() ? null : var13;
            } else if (var1.isZero()) {
               return var1;
            } else {
               ECFieldElement var7 = this.fromBigInteger(ECConstants.ZERO);
               Random var8 = new Random();

               ECFieldElement var5;
               ECFieldElement var6;
               do {
                  ECFieldElement var9 = this.fromBigInteger(new BigInteger(var4, var8));
                  var6 = var7;
                  ECFieldElement var10 = var1;

                  for (int var11 = 1; var11 < var4; var11++) {
                     ECFieldElement var12 = var10.square();
                     var6 = var6.square().add(var12.multiply(var9));
                     var10 = var12.add(var1);
                  }

                  if (!var10.isZero()) {
                     return null;
                  }

                  var5 = var6.square().add(var6);
               } while (var5.isZero());

               return var6;
            }
         }
      }

      synchronized BigInteger[] getSi() {
         if (this.si == null) {
            this.si = Tnaf.getSi(this);
         }

         return this.si;
      }

      public boolean isKoblitz() {
         return this.order != null && this.cofactor != null && this.b.isOne() && (this.a.isZero() || this.a.isOne());
      }

      private static BigInteger implRandomFieldElementMult(SecureRandom var0, int var1) {
         BigInteger var2;
         do {
            var2 = BigIntegers.createRandomBigInteger(var1, var0);
         } while (var2.signum() <= 0);

         return var2;
      }
   }

   public abstract static class AbstractFp extends ECCurve {
      protected AbstractFp(BigInteger var1) {
         super(FiniteFields.getPrimeField(var1));
      }

      public BigInteger getQ() {
         return this.getField().getCharacteristic();
      }

      @Override
      public boolean isValidFieldElement(BigInteger var1) {
         return var1 != null && var1.signum() >= 0 && var1.compareTo(this.getQ()) < 0;
      }

      @Override
      public ECFieldElement randomFieldElement(SecureRandom var1) {
         BigInteger var2 = this.getQ();
         ECFieldElement var3 = this.fromBigInteger(implRandomFieldElement(var1, var2));
         ECFieldElement var4 = this.fromBigInteger(implRandomFieldElement(var1, var2));
         return var3.multiply(var4);
      }

      @Override
      public ECFieldElement randomFieldElementMult(SecureRandom var1) {
         BigInteger var2 = this.getQ();
         ECFieldElement var3 = this.fromBigInteger(implRandomFieldElementMult(var1, var2));
         ECFieldElement var4 = this.fromBigInteger(implRandomFieldElementMult(var1, var2));
         return var3.multiply(var4);
      }

      @Override
      protected ECPoint decompressPoint(int var1, BigInteger var2) {
         ECFieldElement var3 = this.fromBigInteger(var2);
         ECFieldElement var4 = var3.square().add(this.a).multiply(var3).add(this.b);
         ECFieldElement var5 = var4.sqrt();
         if (var5 == null) {
            throw new IllegalArgumentException("Invalid point compression");
         } else {
            if (var5.testBitZero() != (var1 == 1)) {
               var5 = var5.negate();
            }

            return this.createRawPoint(var3, var5);
         }
      }

      private static BigInteger implRandomFieldElement(SecureRandom var0, BigInteger var1) {
         BigInteger var2;
         do {
            var2 = BigIntegers.createRandomBigInteger(var1.bitLength(), var0);
         } while (var2.compareTo(var1) >= 0);

         return var2;
      }

      private static BigInteger implRandomFieldElementMult(SecureRandom var0, BigInteger var1) {
         BigInteger var2;
         do {
            var2 = BigIntegers.createRandomBigInteger(var1.bitLength(), var0);
         } while (var2.signum() <= 0 || var2.compareTo(var1) >= 0);

         return var2;
      }
   }

   public class Config {
      protected int coord;
      protected ECEndomorphism endomorphism;
      protected ECMultiplier multiplier;

      Config(int nullx, ECEndomorphism nullxx, ECMultiplier nullxxx) {
         this.coord = nullx;
         this.endomorphism = nullxx;
         this.multiplier = nullxxx;
      }

      public ECCurve.Config setCoordinateSystem(int var1) {
         this.coord = var1;
         return this;
      }

      public ECCurve.Config setEndomorphism(ECEndomorphism var1) {
         this.endomorphism = var1;
         return this;
      }

      public ECCurve.Config setMultiplier(ECMultiplier var1) {
         this.multiplier = var1;
         return this;
      }

      public ECCurve create() {
         if (!ECCurve.this.supportsCoordinateSystem(this.coord)) {
            throw new IllegalStateException("unsupported coordinate system");
         } else {
            ECCurve var1 = ECCurve.this.cloneCurve();
            if (var1 == ECCurve.this) {
               throw new IllegalStateException("implementation returned current curve");
            } else {
               synchronized (var1) {
                  var1.coord = this.coord;
                  var1.endomorphism = this.endomorphism;
                  var1.multiplier = this.multiplier;
                  return var1;
               }
            }
         }
      }
   }

   public static class F2m extends ECCurve.AbstractF2m {
      private static final int F2M_DEFAULT_COORDS = 6;
      private int m;
      private int k1;
      private int k2;
      private int k3;
      private ECPoint.F2m infinity;

      @Deprecated
      public F2m(int var1, int var2, BigInteger var3, BigInteger var4) {
         this(var1, var2, 0, 0, var3, var4, null, null);
      }

      public F2m(int var1, int var2, BigInteger var3, BigInteger var4, BigInteger var5, BigInteger var6) {
         this(var1, var2, 0, 0, var3, var4, var5, var6);
      }

      @Deprecated
      public F2m(int var1, int var2, int var3, int var4, BigInteger var5, BigInteger var6) {
         this(var1, var2, var3, var4, var5, var6, null, null);
      }

      public F2m(int var1, int var2, int var3, int var4, BigInteger var5, BigInteger var6, BigInteger var7, BigInteger var8) {
         super(var1, var2, var3, var4);
         this.m = var1;
         this.k1 = var2;
         this.k2 = var3;
         this.k3 = var4;
         this.order = var7;
         this.cofactor = var8;
         this.infinity = new ECPoint.F2m(this, null, null);
         this.a = this.fromBigInteger(var5);
         this.b = this.fromBigInteger(var6);
         this.coord = 6;
      }

      protected F2m(int var1, int var2, int var3, int var4, ECFieldElement var5, ECFieldElement var6, BigInteger var7, BigInteger var8) {
         super(var1, var2, var3, var4);
         this.m = var1;
         this.k1 = var2;
         this.k2 = var3;
         this.k3 = var4;
         this.order = var7;
         this.cofactor = var8;
         this.infinity = new ECPoint.F2m(this, null, null);
         this.a = var5;
         this.b = var6;
         this.coord = 6;
      }

      @Override
      protected ECCurve cloneCurve() {
         return new ECCurve.F2m(this.m, this.k1, this.k2, this.k3, this.a, this.b, this.order, this.cofactor);
      }

      @Override
      public boolean supportsCoordinateSystem(int var1) {
         switch (var1) {
            case 0:
            case 1:
            case 6:
               return true;
            default:
               return false;
         }
      }

      @Override
      protected ECMultiplier createDefaultMultiplier() {
         return (ECMultiplier)(this.isKoblitz() ? new WTauNafMultiplier() : super.createDefaultMultiplier());
      }

      @Override
      public int getFieldSize() {
         return this.m;
      }

      @Override
      public ECFieldElement fromBigInteger(BigInteger var1) {
         if (var1 != null && var1.signum() >= 0 && var1.bitLength() <= this.m) {
            int[] var2 = (this.k2 | this.k3) == 0 ? new int[]{this.k1} : new int[]{this.k1, this.k2, this.k3};
            return new ECFieldElement.F2m(this.m, var2, new LongArray(var1));
         } else {
            throw new IllegalArgumentException("x value invalid in F2m field element");
         }
      }

      @Override
      protected ECPoint createRawPoint(ECFieldElement var1, ECFieldElement var2) {
         return new ECPoint.F2m(this, var1, var2);
      }

      @Override
      protected ECPoint createRawPoint(ECFieldElement var1, ECFieldElement var2, ECFieldElement[] var3) {
         return new ECPoint.F2m(this, var1, var2, var3);
      }

      @Override
      public ECPoint getInfinity() {
         return this.infinity;
      }

      public int getM() {
         return this.m;
      }

      public boolean isTrinomial() {
         return this.k2 == 0 && this.k3 == 0;
      }

      public int getK1() {
         return this.k1;
      }

      public int getK2() {
         return this.k2;
      }

      public int getK3() {
         return this.k3;
      }

      @Override
      public ECLookupTable createCacheSafeLookupTable(ECPoint[] var1, int var2, final int var3) {
         final int var4 = this.m + 63 >>> 6;
         final int[] var5 = this.isTrinomial() ? new int[]{this.k1} : new int[]{this.k1, this.k2, this.k3};
         final long[] var6 = new long[var3 * var4 * 2];
         int var7 = 0;

         for (int var8 = 0; var8 < var3; var8++) {
            ECPoint var9 = var1[var2 + var8];
            ((ECFieldElement.F2m)var9.getRawXCoord()).x.copyTo(var6, var7);
            var7 += var4;
            ((ECFieldElement.F2m)var9.getRawYCoord()).x.copyTo(var6, var7);
            var7 += var4;
         }

         return new AbstractECLookupTable() {
            @Override
            public int getSize() {
               return var3;
            }

            @Override
            public ECPoint lookup(int var1) {
               long[] var2x = Nat.create64(var4);
               long[] var3x = Nat.create64(var4);
               int var4x = 0;

               for (int var5x = 0; var5x < var3; var5x++) {
                  long var6x = (var5x ^ var1) - 1 >> 31;

                  for (int var8 = 0; var8 < var4; var8++) {
                     var2x[var8] ^= var6[var4x + var8] & var6x;
                     var3x[var8] ^= var6[var4x + var4 + var8] & var6x;
                  }

                  var4x += var4 * 2;
               }

               return this.createPoint(var2x, var3x);
            }

            @Override
            public ECPoint lookupVar(int var1) {
               long[] var2x = Nat.create64(var4);
               long[] var3x = Nat.create64(var4);
               int var4x = var1 * var4 * 2;

               for (int var5x = 0; var5x < var4; var5x++) {
                  var2x[var5x] = var6[var4x + var5x];
                  var3x[var5x] = var6[var4x + var4 + var5x];
               }

               return this.createPoint(var2x, var3x);
            }

            private ECPoint createPoint(long[] var1, long[] var2x) {
               ECFieldElement.F2m var3x = new ECFieldElement.F2m(F2m.this.m, var5, new LongArray(var1));
               ECFieldElement.F2m var4x = new ECFieldElement.F2m(F2m.this.m, var5, new LongArray(var2x));
               return F2m.this.createRawPoint(var3x, var4x);
            }
         };
      }
   }

   public static class Fp extends ECCurve.AbstractFp {
      private static final int FP_DEFAULT_COORDS = 4;
      private static final Set<BigInteger> knownQs = Collections.synchronizedSet(new HashSet<>());
      private static final BigIntegers.Cache validatedQs = new BigIntegers.Cache();
      BigInteger q;
      BigInteger r;
      ECPoint.Fp infinity;

      @Deprecated
      public Fp(BigInteger var1, BigInteger var2, BigInteger var3) {
         this(var1, var2, var3, null, null);
      }

      public Fp(BigInteger var1, BigInteger var2, BigInteger var3, BigInteger var4, BigInteger var5) {
         this(var1, var2, var3, var4, var5, false);
      }

      public Fp(BigInteger var1, BigInteger var2, BigInteger var3, BigInteger var4, BigInteger var5, boolean var6) {
         super(var1);
         if (var6) {
            knownQs.add(var1);
         } else if (!knownQs.contains(var1) && !validatedQs.contains(var1)) {
            int var7 = Properties.asInteger("org.bouncycastle.ec.fp_max_size", 1042);
            int var8 = Properties.asInteger("org.bouncycastle.ec.fp_certainty", 100);
            int var9 = var1.bitLength();
            if (var7 < var9) {
               throw new IllegalArgumentException("Fp q value out of range");
            }

            if (Primes.hasAnySmallFactors(var1)
               || !Primes.isMRProbablePrime(var1, CryptoServicesRegistrar.getSecureRandom(), ECCurve.getNumberOfIterations(var9, var8))) {
               throw new IllegalArgumentException("Fp q value not prime");
            }

            validatedQs.add(var1);
         }

         this.q = var1;
         this.r = ECFieldElement.Fp.calculateResidue(var1);
         this.infinity = new ECPoint.Fp(this, null, null);
         this.a = this.fromBigInteger(var2);
         this.b = this.fromBigInteger(var3);
         this.order = var4;
         this.cofactor = var5;
         this.coord = 4;
      }

      protected Fp(BigInteger var1, BigInteger var2, ECFieldElement var3, ECFieldElement var4, BigInteger var5, BigInteger var6) {
         super(var1);
         this.q = var1;
         this.r = var2;
         this.infinity = new ECPoint.Fp(this, null, null);
         this.a = var3;
         this.b = var4;
         this.order = var5;
         this.cofactor = var6;
         this.coord = 4;
      }

      @Override
      protected ECCurve cloneCurve() {
         return new ECCurve.Fp(this.q, this.r, this.a, this.b, this.order, this.cofactor);
      }

      @Override
      public boolean supportsCoordinateSystem(int var1) {
         switch (var1) {
            case 0:
            case 1:
            case 2:
            case 4:
               return true;
            case 3:
            default:
               return false;
         }
      }

      @Override
      public BigInteger getQ() {
         return this.q;
      }

      @Override
      public int getFieldSize() {
         return this.q.bitLength();
      }

      @Override
      public ECFieldElement fromBigInteger(BigInteger var1) {
         if (var1 != null && var1.signum() >= 0 && var1.compareTo(this.q) < 0) {
            return new ECFieldElement.Fp(this.q, this.r, var1);
         } else {
            throw new IllegalArgumentException("x value invalid for Fp field element");
         }
      }

      @Override
      protected ECPoint createRawPoint(ECFieldElement var1, ECFieldElement var2) {
         return new ECPoint.Fp(this, var1, var2);
      }

      @Override
      protected ECPoint createRawPoint(ECFieldElement var1, ECFieldElement var2, ECFieldElement[] var3) {
         return new ECPoint.Fp(this, var1, var2, var3);
      }

      @Override
      public ECPoint importPoint(ECPoint var1) {
         if (this != var1.getCurve() && this.getCoordinateSystem() == 2 && !var1.isInfinity()) {
            switch (var1.getCurve().getCoordinateSystem()) {
               case 2:
               case 3:
               case 4:
                  return new ECPoint.Fp(
                     this,
                     this.fromBigInteger(var1.x.toBigInteger()),
                     this.fromBigInteger(var1.y.toBigInteger()),
                     new ECFieldElement[]{this.fromBigInteger(var1.zs[0].toBigInteger())}
                  );
            }
         }

         return super.importPoint(var1);
      }

      @Override
      public ECPoint getInfinity() {
         return this.infinity;
      }
   }
}
