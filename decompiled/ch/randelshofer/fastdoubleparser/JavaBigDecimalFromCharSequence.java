package ch.randelshofer.fastdoubleparser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.NavigableMap;

final class JavaBigDecimalFromCharSequence extends AbstractBigDecimalParser {
   public JavaBigDecimalFromCharSequence() {
   }

   public BigDecimal parseBigDecimalString(CharSequence str, int offset, int length) {
      try {
         int endIndex = checkBounds(str.length(), offset, length);
         if (hasManyDigits(length)) {
            return this.parseBigDecimalStringWithManyDigits(str, offset, length);
         } else {
            long significand = 0L;
            int decimalPointIndex = -1;
            int index = offset;
            char ch = charAt(str, offset, endIndex);
            boolean illegal = false;
            boolean isNegative = ch == '-';
            if (isNegative || ch == '+') {
               index = offset + 1;
               ch = charAt(str, index, endIndex);
               if (ch == 0) {
                  throw new NumberFormatException("illegal syntax");
               }
            }

            int integerPartIndex;
            for (integerPartIndex = index; index < endIndex; index++) {
               ch = str.charAt(index);
               int digit = (char)(ch - '0');
               if (digit < 10) {
                  significand = 10L * significand + digit;
               } else {
                  if (ch != '.') {
                     break;
                  }

                  illegal |= decimalPointIndex >= 0;

                  for (decimalPointIndex = index; index < endIndex - 4; index += 4) {
                     int digits = FastDoubleSwar.tryToParseFourDigits(str, index + 1);
                     if (digits < 0) {
                        break;
                     }

                     significand = 10000L * significand + digits;
                  }
               }
            }

            long exponent;
            int digitCount;
            if (decimalPointIndex < 0) {
               digitCount = index - integerPartIndex;
               decimalPointIndex = index;
               exponent = 0L;
            } else {
               digitCount = index - integerPartIndex - 1;
               exponent = decimalPointIndex - index + 1;
            }

            long expNumber = 0L;
            int exponentIndicatorIndex;
            if ((ch | ' ') == 101) {
               exponentIndicatorIndex = index++;
               ch = charAt(str, index, endIndex);
               boolean isExponentNegative = ch == '-';
               if (isExponentNegative || ch == '+') {
                  ch = charAt(str, ++index, endIndex);
               }

               int digit = (char)(ch - '0');
               illegal |= digit >= 10;

               do {
                  if (expNumber < 2147483647L) {
                     expNumber = 10L * expNumber + digit;
                  }

                  ch = charAt(str, ++index, endIndex);
                  digit = (char)(ch - '0');
               } while (digit < 10);

               if (isExponentNegative) {
                  expNumber = -expNumber;
               }

               exponent += expNumber;
            } else {
               exponentIndicatorIndex = endIndex;
            }

            illegal |= digitCount == 0;
            checkParsedBigDecimalBounds(illegal, index, endIndex, digitCount, exponent);
            return digitCount < 19
               ? new BigDecimal(isNegative ? -significand : significand).scaleByPowerOfTen((int)exponent)
               : this.valueOfBigDecimalString(
                  str, integerPartIndex, decimalPointIndex, decimalPointIndex + 1, exponentIndicatorIndex, isNegative, (int)exponent
               );
         }
      } catch (ArithmeticException var22) {
         NumberFormatException nfe = new NumberFormatException("value exceeds limits");
         nfe.initCause(var22);
         throw nfe;
      }
   }

   BigDecimal parseBigDecimalStringWithManyDigits(CharSequence str, int offset, int length) {
      int nonZeroFractionalPartIndex = -1;
      int decimalPointIndex = -1;
      int endIndex = offset + length;
      int index = offset;
      char ch = charAt(str, offset, endIndex);
      boolean illegal = false;
      boolean isNegative = ch == '-';
      if (isNegative || ch == '+') {
         index = offset + 1;
         ch = charAt(str, index, endIndex);
         if (ch == 0) {
            throw new NumberFormatException("illegal syntax");
         }
      }

      int integerPartIndex = index;
      int swarLimit = Math.min(endIndex - 8, 1073741824);

      while (index < swarLimit && FastDoubleSwar.isEightZeroes(str, index)) {
         index += 8;
      }

      while (index < endIndex && str.charAt(index) == '0') {
         index++;
      }

      int nonZeroIntegerPartIndex = index;

      while (index < swarLimit && FastDoubleSwar.isEightDigits(str, index)) {
         index += 8;
      }

      while (index < endIndex && FastDoubleSwar.isDigit(ch = str.charAt(index))) {
         index++;
      }

      if (ch == '.') {
         decimalPointIndex = index++;

         while (index < swarLimit && FastDoubleSwar.isEightZeroes(str, index)) {
            index += 8;
         }

         while (index < endIndex && str.charAt(index) == '0') {
            index++;
         }

         nonZeroFractionalPartIndex = index;

         while (index < swarLimit && FastDoubleSwar.isEightDigits(str, index)) {
            index += 8;
         }

         while (index < endIndex && FastDoubleSwar.isDigit(ch = str.charAt(index))) {
            index++;
         }
      }

      int digitCountWithoutLeadingZeros;
      long exponent;
      if (decimalPointIndex < 0) {
         digitCountWithoutLeadingZeros = index - nonZeroIntegerPartIndex;
         decimalPointIndex = index;
         nonZeroFractionalPartIndex = index;
         exponent = 0L;
      } else {
         digitCountWithoutLeadingZeros = nonZeroIntegerPartIndex == decimalPointIndex
            ? index - nonZeroFractionalPartIndex
            : index - nonZeroIntegerPartIndex - 1;
         exponent = decimalPointIndex - index + 1;
      }

      long expNumber = 0L;
      int exponentIndicatorIndex;
      if ((ch | ' ') == 101) {
         exponentIndicatorIndex = index++;
         ch = charAt(str, index, endIndex);
         boolean isExponentNegative = ch == '-';
         if (isExponentNegative || ch == '+') {
            ch = charAt(str, ++index, endIndex);
         }

         int digit = (char)(ch - '0');
         illegal |= digit >= 10;

         do {
            if (expNumber < 2147483647L) {
               expNumber = 10L * expNumber + digit;
            }

            ch = charAt(str, ++index, endIndex);
            digit = (char)(ch - '0');
         } while (digit < 10);

         if (isExponentNegative) {
            expNumber = -expNumber;
         }

         exponent += expNumber;
      } else {
         exponentIndicatorIndex = endIndex;
      }

      illegal |= integerPartIndex == decimalPointIndex && decimalPointIndex == exponentIndicatorIndex;
      checkParsedBigDecimalBounds(illegal, index, endIndex, digitCountWithoutLeadingZeros, exponent);
      return this.valueOfBigDecimalString(
         str, nonZeroIntegerPartIndex, decimalPointIndex, nonZeroFractionalPartIndex, exponentIndicatorIndex, isNegative, (int)exponent
      );
   }

   BigDecimal valueOfBigDecimalString(
      CharSequence str,
      int integerPartIndex,
      int decimalPointIndex,
      int nonZeroFractionalPartIndex,
      int exponentIndicatorIndex,
      boolean isNegative,
      int exponent
   ) {
      int fractionDigitsCount = exponentIndicatorIndex - decimalPointIndex - 1;
      int nonZeroFractionDigitsCount = exponentIndicatorIndex - nonZeroFractionalPartIndex;
      int integerDigitsCount = decimalPointIndex - integerPartIndex;
      NavigableMap<Integer, BigInteger> powersOfTen = null;
      BigInteger integerPart;
      if (integerDigitsCount > 0) {
         if (integerDigitsCount > 400) {
            powersOfTen = FastIntegerMath.createPowersOfTenFloor16Map();
            FastIntegerMath.fillPowersOfNFloor16Recursive(powersOfTen, integerPartIndex, decimalPointIndex);
            integerPart = ParseDigitsTaskCharSequence.parseDigitsRecursive(str, integerPartIndex, decimalPointIndex, powersOfTen, 400);
         } else {
            integerPart = ParseDigitsTaskCharSequence.parseDigitsIterative(str, integerPartIndex, decimalPointIndex);
         }
      } else {
         integerPart = BigInteger.ZERO;
      }

      BigInteger significand;
      if (fractionDigitsCount > 0) {
         BigInteger fractionalPart;
         if (nonZeroFractionDigitsCount > 400) {
            if (powersOfTen == null) {
               powersOfTen = FastIntegerMath.createPowersOfTenFloor16Map();
            }

            FastIntegerMath.fillPowersOfNFloor16Recursive(powersOfTen, nonZeroFractionalPartIndex, exponentIndicatorIndex);
            fractionalPart = ParseDigitsTaskCharSequence.parseDigitsRecursive(str, nonZeroFractionalPartIndex, exponentIndicatorIndex, powersOfTen, 400);
         } else {
            fractionalPart = ParseDigitsTaskCharSequence.parseDigitsIterative(str, nonZeroFractionalPartIndex, exponentIndicatorIndex);
         }

         if (integerPart.signum() == 0) {
            significand = fractionalPart;
         } else {
            BigInteger integerFactor = FastIntegerMath.computePowerOfTen(powersOfTen, fractionDigitsCount);
            significand = FftMultiplier.multiply(integerPart, integerFactor).add(fractionalPart);
         }
      } else {
         significand = integerPart;
      }

      return new BigDecimal(isNegative ? significand.negate() : significand, -exponent);
   }
}
