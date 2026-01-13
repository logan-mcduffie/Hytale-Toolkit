package ch.randelshofer.fastdoubleparser;

import ch.randelshofer.fastdoubleparser.bte.ByteDigitSet;
import ch.randelshofer.fastdoubleparser.chr.CharDigitSet;
import java.math.BigDecimal;
import java.math.BigInteger;

final class SlowDoubleConversionPath {
   private static final int[] powersOfTen = new int[]{0, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000};

   private SlowDoubleConversionPath() {
   }

   static double toDouble(
      CharSequence str,
      CharDigitSet digitSet,
      int integerStartIndex,
      int integerEndIndex,
      int fractionStartIndex,
      int fractionEndIndex,
      boolean isSignificandNegative,
      long exponentValue
   ) {
      double v = toBigDecimal(str, digitSet, integerStartIndex, integerEndIndex, fractionStartIndex, fractionEndIndex, 768, exponentValue).doubleValue();
      return isSignificandNegative ? -v : v;
   }

   static BigDecimal toBigDecimal(
      CharSequence str,
      CharDigitSet digitSet,
      int integerStartIndex,
      int integerEndIndex,
      int fractionStartIndex,
      int fractionEndIndex,
      int maxRequiredDigits,
      long exponentValue
   ) {
      while (integerStartIndex < integerEndIndex) {
         char ch = str.charAt(integerStartIndex);
         int digit = digitSet.toDigit(ch);
         boolean isDigit = digit < 10;
         if (!isDigit || digit <= 0) {
            integerStartIndex++;
            continue;
         }
         break;
      }

      int skippedFractionDigits = 0;
      if (integerStartIndex == integerEndIndex) {
         while (fractionStartIndex < fractionEndIndex) {
            char ch = str.charAt(fractionStartIndex);
            int digit = digitSet.toDigit(ch);
            if (digit > 0 && digit < 10) {
               break;
            }

            skippedFractionDigits++;
            fractionStartIndex++;
         }
      }

      int estimatedNumDigits = integerEndIndex - integerStartIndex + fractionEndIndex - fractionStartIndex;
      BigSignificand b = new BigSignificand(FastIntegerMath.estimateNumBits(Math.min(estimatedNumDigits, maxRequiredDigits)));
      int numIntegerDigits = 0;
      int acc = 0;

      int i;
      for (i = integerStartIndex; i < integerEndIndex && numIntegerDigits < maxRequiredDigits; i++) {
         char ch = str.charAt(i);
         int digit = digitSet.toDigit(ch);
         if (digit < 10) {
            acc = acc * 10 + digit;
            if (++numIntegerDigits % 8 == 0) {
               b.fma(100000000, acc);
               acc = 0;
            }
         }
      }

      int mul = powersOfTen[numIntegerDigits % 8];
      if (mul != 0) {
         b.fma(mul, acc);
      }

      int skippedIntegerDigits;
      for (skippedIntegerDigits = 0; i < integerEndIndex; i++) {
         char ch = str.charAt(i);
         int digit = digitSet.toDigit(ch);
         if (digit < 10) {
            skippedIntegerDigits++;
         }
      }

      fractionEndIndex = Math.min(fractionEndIndex, fractionStartIndex + Math.max(maxRequiredDigits - numIntegerDigits, 0));
      int numFractionDigits = 0;
      acc = 0;

      for (int var27 = fractionStartIndex; var27 < fractionEndIndex; var27++) {
         char ch = str.charAt(var27);
         acc = acc * 10 + digitSet.toDigit(ch);
         if (++numFractionDigits % 8 == 0) {
            b.fma(100000000, acc);
            acc = 0;
         }
      }

      mul = powersOfTen[numFractionDigits % 8];
      if (mul != 0) {
         b.fma(mul, acc);
      }

      int exponent = (int)(exponentValue + skippedIntegerDigits - numFractionDigits - skippedFractionDigits);
      BigInteger bigInteger = b.toBigInteger();
      return new BigDecimal(bigInteger, -exponent);
   }

   static double toDouble(
      char[] str,
      CharDigitSet digitSet,
      int integerStartIndex,
      int integerEndIndex,
      int fractionStartIndex,
      int fractionEndIndex,
      boolean isSignificandNegative,
      long exponentValue
   ) {
      double v = toBigDecimal(str, digitSet, integerStartIndex, integerEndIndex, fractionStartIndex, fractionEndIndex, 768, exponentValue).doubleValue();
      return isSignificandNegative ? -v : v;
   }

   static double toDouble(
      byte[] str,
      ByteDigitSet digitSet,
      int integerStartIndex,
      int integerEndIndex,
      int fractionStartIndex,
      int fractionEndIndex,
      boolean isSignificandNegative,
      long exponentValue
   ) {
      double v = toBigDecimal(str, digitSet, integerStartIndex, integerEndIndex, fractionStartIndex, fractionEndIndex, 768, exponentValue).doubleValue();
      return isSignificandNegative ? -v : v;
   }

   static BigDecimal toBigDecimal(
      char[] str,
      CharDigitSet digitSet,
      int integerStartIndex,
      int integerEndIndex,
      int fractionStartIndex,
      int fractionEndIndex,
      int maxRequiredDigits,
      long exponentValue
   ) {
      while (integerStartIndex < integerEndIndex) {
         char ch = str[integerStartIndex];
         int digit = digitSet.toDigit(ch);
         boolean isDigit = digit < 10;
         if (!isDigit || digit <= 0) {
            integerStartIndex++;
            continue;
         }
         break;
      }

      int skippedFractionDigits = 0;
      if (integerStartIndex == integerEndIndex) {
         while (fractionStartIndex < fractionEndIndex) {
            char ch = str[fractionStartIndex];
            int digit = digitSet.toDigit(ch);
            if (digit > 0 && digit < 10) {
               break;
            }

            skippedFractionDigits++;
            fractionStartIndex++;
         }
      }

      int estimatedNumDigits = integerEndIndex - integerStartIndex + fractionEndIndex - fractionStartIndex;
      BigSignificand b = new BigSignificand(FastIntegerMath.estimateNumBits(Math.min(estimatedNumDigits, maxRequiredDigits)));
      int numIntegerDigits = 0;
      int acc = 0;

      int i;
      for (i = integerStartIndex; i < integerEndIndex && numIntegerDigits < maxRequiredDigits; i++) {
         char ch = str[i];
         int digit = digitSet.toDigit(ch);
         if (digit < 10) {
            acc = acc * 10 + digit;
            if (++numIntegerDigits % 8 == 0) {
               b.fma(100000000, acc);
               acc = 0;
            }
         }
      }

      int mul = powersOfTen[numIntegerDigits % 8];
      if (mul != 0) {
         b.fma(mul, acc);
      }

      int skippedIntegerDigits;
      for (skippedIntegerDigits = 0; i < integerEndIndex; i++) {
         char ch = str[i];
         int digit = digitSet.toDigit(ch);
         if (digit < 10) {
            skippedIntegerDigits++;
         }
      }

      fractionEndIndex = Math.min(fractionEndIndex, fractionStartIndex + Math.max(maxRequiredDigits - numIntegerDigits, 0));
      int numFractionDigits = 0;
      acc = 0;

      for (int var27 = fractionStartIndex; var27 < fractionEndIndex; var27++) {
         char ch = str[var27];
         acc = acc * 10 + digitSet.toDigit(ch);
         if (++numFractionDigits % 8 == 0) {
            b.fma(100000000, acc);
            acc = 0;
         }
      }

      mul = powersOfTen[numFractionDigits % 8];
      if (mul != 0) {
         b.fma(mul, acc);
      }

      int exponent = (int)(exponentValue + skippedIntegerDigits - numFractionDigits - skippedFractionDigits);
      BigInteger bigInteger = b.toBigInteger();
      return new BigDecimal(bigInteger, -exponent);
   }

   static BigDecimal toBigDecimal(
      byte[] str,
      ByteDigitSet digitSet,
      int integerStartIndex,
      int integerEndIndex,
      int fractionStartIndex,
      int fractionEndIndex,
      int maxRequiredDigits,
      long exponentValue
   ) {
      while (integerStartIndex < integerEndIndex) {
         byte ch = str[integerStartIndex];
         int digit = digitSet.toDigit(ch);
         boolean isDigit = digit < 10;
         if (!isDigit || digit <= 0) {
            integerStartIndex++;
            continue;
         }
         break;
      }

      int skippedFractionDigits = 0;
      if (integerStartIndex == integerEndIndex) {
         while (fractionStartIndex < fractionEndIndex) {
            byte ch = str[fractionStartIndex];
            int digit = digitSet.toDigit(ch);
            if (digit > 0 && digit < 10) {
               break;
            }

            skippedFractionDigits++;
            fractionStartIndex++;
         }
      }

      int estimatedNumDigits = integerEndIndex - integerStartIndex + fractionEndIndex - fractionStartIndex;
      BigSignificand b = new BigSignificand(FastIntegerMath.estimateNumBits(Math.min(estimatedNumDigits, maxRequiredDigits)));
      int numIntegerDigits = 0;
      int acc = 0;

      int i;
      for (i = integerStartIndex; i < integerEndIndex && numIntegerDigits < maxRequiredDigits; i++) {
         byte ch = str[i];
         int digit = digitSet.toDigit(ch);
         if (digit < 10) {
            acc = acc * 10 + digit;
            if (++numIntegerDigits % 8 == 0) {
               b.fma(100000000, acc);
               acc = 0;
            }
         }
      }

      int mul = powersOfTen[numIntegerDigits % 8];
      if (mul != 0) {
         b.fma(mul, acc);
      }

      int skippedIntegerDigits;
      for (skippedIntegerDigits = 0; i < integerEndIndex; i++) {
         byte ch = str[i];
         int digit = digitSet.toDigit(ch);
         if (digit < 10) {
            skippedIntegerDigits++;
         }
      }

      fractionEndIndex = Math.min(fractionEndIndex, fractionStartIndex + Math.max(maxRequiredDigits - numIntegerDigits, 0));
      int numFractionDigits = 0;
      acc = 0;

      for (int var27 = fractionStartIndex; var27 < fractionEndIndex; var27++) {
         byte ch = str[var27];
         acc = acc * 10 + digitSet.toDigit(ch);
         if (++numFractionDigits % 8 == 0) {
            b.fma(100000000, acc);
            acc = 0;
         }
      }

      mul = powersOfTen[numFractionDigits % 8];
      if (mul != 0) {
         b.fma(mul, acc);
      }

      int exponent = (int)(exponentValue + skippedIntegerDigits - numFractionDigits - skippedFractionDigits);
      BigInteger bigInteger = b.toBigInteger();
      return new BigDecimal(bigInteger, -exponent);
   }
}
