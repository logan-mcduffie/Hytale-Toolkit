package org.bson.types;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class Decimal128 extends Number implements Comparable<Decimal128> {
   private static final long serialVersionUID = 4570973266503637887L;
   private static final long INFINITY_MASK = 8646911284551352320L;
   private static final long NaN_MASK = 8935141660703064064L;
   private static final long SIGN_BIT_MASK = Long.MIN_VALUE;
   private static final int MIN_EXPONENT = -6176;
   private static final int MAX_EXPONENT = 6111;
   private static final int EXPONENT_OFFSET = 6176;
   private static final int MAX_BIT_LENGTH = 113;
   private static final BigInteger BIG_INT_TEN = new BigInteger("10");
   private static final BigInteger BIG_INT_ONE = new BigInteger("1");
   private static final BigInteger BIG_INT_ZERO = new BigInteger("0");
   private static final Set<String> NaN_STRINGS = new HashSet<>(Collections.singletonList("nan"));
   private static final Set<String> NEGATIVE_NaN_STRINGS = new HashSet<>(Collections.singletonList("-nan"));
   private static final Set<String> POSITIVE_INFINITY_STRINGS = new HashSet<>(Arrays.asList("inf", "+inf", "infinity", "+infinity"));
   private static final Set<String> NEGATIVE_INFINITY_STRINGS = new HashSet<>(Arrays.asList("-inf", "-infinity"));
   public static final Decimal128 POSITIVE_INFINITY = fromIEEE754BIDEncoding(8646911284551352320L, 0L);
   public static final Decimal128 NEGATIVE_INFINITY = fromIEEE754BIDEncoding(-576460752303423488L, 0L);
   public static final Decimal128 NEGATIVE_NaN = fromIEEE754BIDEncoding(-288230376151711744L, 0L);
   public static final Decimal128 NaN = fromIEEE754BIDEncoding(8935141660703064064L, 0L);
   public static final Decimal128 POSITIVE_ZERO = fromIEEE754BIDEncoding(3476778912330022912L, 0L);
   public static final Decimal128 NEGATIVE_ZERO = fromIEEE754BIDEncoding(-5746593124524752896L, 0L);
   private final long high;
   private final long low;

   public static Decimal128 parse(String value) {
      String lowerCasedValue = value.toLowerCase();
      if (NaN_STRINGS.contains(lowerCasedValue)) {
         return NaN;
      } else if (NEGATIVE_NaN_STRINGS.contains(lowerCasedValue)) {
         return NEGATIVE_NaN;
      } else if (POSITIVE_INFINITY_STRINGS.contains(lowerCasedValue)) {
         return POSITIVE_INFINITY;
      } else {
         return NEGATIVE_INFINITY_STRINGS.contains(lowerCasedValue) ? NEGATIVE_INFINITY : new Decimal128(new BigDecimal(value), value.charAt(0) == '-');
      }
   }

   public static Decimal128 fromIEEE754BIDEncoding(long high, long low) {
      return new Decimal128(high, low);
   }

   public Decimal128(long value) {
      this(new BigDecimal(value, MathContext.DECIMAL128));
   }

   public Decimal128(BigDecimal value) {
      this(value, value.signum() == -1);
   }

   private Decimal128(long high, long low) {
      this.high = high;
      this.low = low;
   }

   private Decimal128(BigDecimal initialValue, boolean isNegative) {
      long localHigh = 0L;
      long localLow = 0L;
      BigDecimal value = this.clampAndRound(initialValue);
      long exponent = -value.scale();
      if (exponent >= -6176L && exponent <= 6111L) {
         if (value.unscaledValue().bitLength() > 113) {
            throw new AssertionError("Unscaled roundedValue is out of range for Decimal128 encoding:" + value.unscaledValue());
         } else {
            BigInteger significand = value.unscaledValue().abs();
            int bitLength = significand.bitLength();

            for (int i = 0; i < Math.min(64, bitLength); i++) {
               if (significand.testBit(i)) {
                  localLow |= 1L << i;
               }
            }

            for (int ix = 64; ix < bitLength; ix++) {
               if (significand.testBit(ix)) {
                  localHigh |= 1L << ix - 64;
               }
            }

            long biasedExponent = exponent + 6176L;
            localHigh |= biasedExponent << 49;
            if (value.signum() == -1 || isNegative) {
               localHigh |= Long.MIN_VALUE;
            }

            this.high = localHigh;
            this.low = localLow;
         }
      } else {
         throw new AssertionError("Exponent is out of range for Decimal128 encoding: " + exponent);
      }
   }

   private BigDecimal clampAndRound(BigDecimal initialValue) {
      BigDecimal value;
      if (-initialValue.scale() > 6111) {
         int diff = -initialValue.scale() - 6111;
         if (initialValue.unscaledValue().equals(BIG_INT_ZERO)) {
            value = new BigDecimal(initialValue.unscaledValue(), -6111);
         } else {
            if (diff + initialValue.precision() > 34) {
               throw new NumberFormatException("Exponent is out of range for Decimal128 encoding of " + initialValue);
            }

            BigInteger multiplier = BIG_INT_TEN.pow(diff);
            value = new BigDecimal(initialValue.unscaledValue().multiply(multiplier), initialValue.scale() + diff);
         }
      } else if (-initialValue.scale() < -6176) {
         int diff = initialValue.scale() + -6176;
         int undiscardedPrecision = this.ensureExactRounding(initialValue, diff);
         BigInteger divisor = undiscardedPrecision == 0 ? BIG_INT_ONE : BIG_INT_TEN.pow(diff);
         value = new BigDecimal(initialValue.unscaledValue().divide(divisor), initialValue.scale() - diff);
      } else {
         value = initialValue.round(MathContext.DECIMAL128);
         int extraPrecision = initialValue.precision() - value.precision();
         if (extraPrecision > 0) {
            this.ensureExactRounding(initialValue, extraPrecision);
         }
      }

      return value;
   }

   private int ensureExactRounding(BigDecimal initialValue, int extraPrecision) {
      String significand = initialValue.unscaledValue().abs().toString();
      int undiscardedPrecision = Math.max(0, significand.length() - extraPrecision);

      for (int i = undiscardedPrecision; i < significand.length(); i++) {
         if (significand.charAt(i) != '0') {
            throw new NumberFormatException("Conversion to Decimal128 would require inexact rounding of " + initialValue);
         }
      }

      return undiscardedPrecision;
   }

   public long getHigh() {
      return this.high;
   }

   public long getLow() {
      return this.low;
   }

   public BigDecimal bigDecimalValue() {
      if (this.isNaN()) {
         throw new ArithmeticException("NaN can not be converted to a BigDecimal");
      } else if (this.isInfinite()) {
         throw new ArithmeticException("Infinity can not be converted to a BigDecimal");
      } else {
         BigDecimal bigDecimal = this.bigDecimalValueNoNegativeZeroCheck();
         if (this.isNegative() && bigDecimal.signum() == 0) {
            throw new ArithmeticException("Negative zero can not be converted to a BigDecimal");
         } else {
            return bigDecimal;
         }
      }
   }

   private boolean hasDifferentSign(BigDecimal bigDecimal) {
      return this.isNegative() && bigDecimal.signum() == 0;
   }

   private boolean isZero(BigDecimal bigDecimal) {
      return !this.isNaN() && !this.isInfinite() && bigDecimal.compareTo(BigDecimal.ZERO) == 0;
   }

   private BigDecimal bigDecimalValueNoNegativeZeroCheck() {
      int scale = -this.getExponent();
      return this.twoHighestCombinationBitsAreSet()
         ? BigDecimal.valueOf(0L, scale)
         : new BigDecimal(new BigInteger(this.isNegative() ? -1 : 1, this.getBytes()), scale);
   }

   private byte[] getBytes() {
      byte[] bytes = new byte[15];
      long mask = 255L;

      for (int i = 14; i >= 7; i--) {
         bytes[i] = (byte)((this.low & mask) >>> (14 - i << 3));
         mask <<= 8;
      }

      mask = 255L;

      for (int i = 6; i >= 1; i--) {
         bytes[i] = (byte)((this.high & mask) >>> (6 - i << 3));
         mask <<= 8;
      }

      mask = 281474976710656L;
      bytes[0] = (byte)((this.high & mask) >>> 48);
      return bytes;
   }

   private int getExponent() {
      return this.twoHighestCombinationBitsAreSet()
         ? (int)((this.high & 2305807824841605120L) >>> 47) - 6176
         : (int)((this.high & 9223231299366420480L) >>> 49) - 6176;
   }

   private boolean twoHighestCombinationBitsAreSet() {
      return (this.high & 6917529027641081856L) == 6917529027641081856L;
   }

   public boolean isNegative() {
      return (this.high & Long.MIN_VALUE) == Long.MIN_VALUE;
   }

   public boolean isInfinite() {
      return (this.high & 8646911284551352320L) == 8646911284551352320L;
   }

   public boolean isFinite() {
      return !this.isInfinite();
   }

   public boolean isNaN() {
      return (this.high & 8935141660703064064L) == 8935141660703064064L;
   }

   public int compareTo(Decimal128 o) {
      if (this.isNaN()) {
         return o.isNaN() ? 0 : 1;
      } else if (this.isInfinite()) {
         if (this.isNegative()) {
            return o.isInfinite() && o.isNegative() ? 0 : -1;
         } else if (o.isNaN()) {
            return -1;
         } else {
            return o.isInfinite() && !o.isNegative() ? 0 : 1;
         }
      } else {
         BigDecimal bigDecimal = this.bigDecimalValueNoNegativeZeroCheck();
         BigDecimal otherBigDecimal = o.bigDecimalValueNoNegativeZeroCheck();
         if (this.isZero(bigDecimal) && o.isZero(otherBigDecimal)) {
            if (this.hasDifferentSign(bigDecimal)) {
               if (o.hasDifferentSign(otherBigDecimal)) {
                  return 0;
               }

               return -1;
            }

            if (o.hasDifferentSign(otherBigDecimal)) {
               return 1;
            }
         }

         if (o.isNaN()) {
            return -1;
         } else if (o.isInfinite()) {
            return o.isNegative() ? 1 : -1;
         } else {
            return bigDecimal.compareTo(otherBigDecimal);
         }
      }
   }

   @Override
   public int intValue() {
      return (int)this.doubleValue();
   }

   @Override
   public long longValue() {
      return (long)this.doubleValue();
   }

   @Override
   public float floatValue() {
      return (float)this.doubleValue();
   }

   @Override
   public double doubleValue() {
      if (this.isNaN()) {
         return Double.NaN;
      } else if (this.isInfinite()) {
         return this.isNegative() ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
      } else {
         BigDecimal bigDecimal = this.bigDecimalValueNoNegativeZeroCheck();
         return this.hasDifferentSign(bigDecimal) ? -0.0 : bigDecimal.doubleValue();
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Decimal128 that = (Decimal128)o;
         return this.high != that.high ? false : this.low == that.low;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int result = (int)(this.low ^ this.low >>> 32);
      return 31 * result + (int)(this.high ^ this.high >>> 32);
   }

   @Override
   public String toString() {
      if (this.isNaN()) {
         return "NaN";
      } else if (this.isInfinite()) {
         return this.isNegative() ? "-Infinity" : "Infinity";
      } else {
         return this.toStringWithBigDecimal();
      }
   }

   private String toStringWithBigDecimal() {
      StringBuilder buffer = new StringBuilder();
      BigDecimal bigDecimal = this.bigDecimalValueNoNegativeZeroCheck();
      String significand = bigDecimal.unscaledValue().abs().toString();
      if (this.isNegative()) {
         buffer.append('-');
      }

      int exponent = -bigDecimal.scale();
      int adjustedExponent = exponent + (significand.length() - 1);
      if (exponent > 0 || adjustedExponent < -6) {
         buffer.append(significand.charAt(0));
         if (significand.length() > 1) {
            buffer.append('.');
            buffer.append(significand, 1, significand.length());
         }

         buffer.append('E');
         if (adjustedExponent > 0) {
            buffer.append('+');
         }

         buffer.append(adjustedExponent);
      } else if (exponent == 0) {
         buffer.append(significand);
      } else {
         int pad = -exponent - significand.length();
         if (pad >= 0) {
            buffer.append('0');
            buffer.append('.');

            for (int i = 0; i < pad; i++) {
               buffer.append('0');
            }

            buffer.append(significand, 0, significand.length());
         } else {
            buffer.append(significand, 0, -pad);
            buffer.append('.');
            buffer.append(significand, -pad, -pad - exponent);
         }
      }

      return buffer.toString();
   }
}
