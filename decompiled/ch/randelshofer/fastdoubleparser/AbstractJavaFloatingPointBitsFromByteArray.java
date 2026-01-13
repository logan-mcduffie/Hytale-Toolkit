package ch.randelshofer.fastdoubleparser;

abstract class AbstractJavaFloatingPointBitsFromByteArray extends AbstractFloatValueParser {
   private static int skipWhitespace(byte[] str, int index, int endIndex) {
      while (index < endIndex && (str[index] & 255) <= 32) {
         index++;
      }

      return index;
   }

   abstract long nan();

   abstract long negativeInfinity();

   private long parseDecFloatLiteral(byte[] str, int index, int startIndex, int endIndex, boolean isNegative) {
      long significand = 0L;
      int significandStartIndex = index;
      int integerDigitCount = -1;
      boolean illegal = false;
      byte ch = 0;

      for (int swarLimit = Math.min(endIndex - 4, 1073741824); index < endIndex; index++) {
         ch = str[index];
         int digit = (char)(ch - 48);
         if (digit < 10) {
            significand = 10L * significand + digit;
         } else {
            if (ch != 46) {
               break;
            }

            illegal |= integerDigitCount >= 0;

            for (integerDigitCount = index - significandStartIndex; index < swarLimit; index += 4) {
               int digits = FastDoubleSwar.tryToParseFourDigits(str, index + 1);
               if (digits < 0) {
                  break;
               }

               significand = 10000L * significand + digits;
            }
         }
      }

      int significandEndIndex = index;
      int exponent;
      int digitCount;
      if (integerDigitCount < 0) {
         digitCount = index - significandStartIndex;
         integerDigitCount = digitCount;
         exponent = 0;
      } else {
         digitCount = index - significandStartIndex - 1;
         exponent = integerDigitCount - digitCount;
      }

      illegal |= digitCount == 0 && index > significandStartIndex;
      int expNumber = 0;
      if ((ch | 32) == 101) {
         ch = charAt(str, ++index, endIndex);
         boolean isExponentNegative = ch == 45;
         if (isExponentNegative || ch == 43) {
            ch = charAt(str, ++index, endIndex);
         }

         int digit = (char)(ch - 48);
         illegal |= digit >= 10;

         do {
            if (expNumber < 1024) {
               expNumber = 10 * expNumber + digit;
            }

            ch = charAt(str, ++index, endIndex);
            digit = (char)(ch - 48);
         } while (digit < 10);

         if (isExponentNegative) {
            expNumber = -expNumber;
         }

         exponent += expNumber;
      }

      if (!illegal && digitCount == 0) {
         return this.parseNaNOrInfinity(str, index, endIndex, isNegative);
      } else {
         if ((ch | 34) == 102) {
            index++;
         }

         index = skipWhitespace(str, index, endIndex);
         if (!illegal && index >= endIndex) {
            boolean isSignificandTruncated;
            int exponentOfTruncatedSignificand;
            if (digitCount > 19) {
               int truncatedDigitCount = 0;
               significand = 0L;

               for (index = significandStartIndex; index < significandEndIndex; index++) {
                  ch = str[index];
                  int digit = (char)(ch - 48);
                  if (digit < 10) {
                     if (Long.compareUnsigned(significand, 1000000000000000000L) >= 0) {
                        break;
                     }

                     significand = 10L * significand + digit;
                     truncatedDigitCount++;
                  }
               }

               isSignificandTruncated = index < significandEndIndex;
               exponentOfTruncatedSignificand = integerDigitCount - truncatedDigitCount + expNumber;
            } else {
               isSignificandTruncated = false;
               exponentOfTruncatedSignificand = 0;
            }

            return this.valueOfFloatLiteral(
               str, startIndex, endIndex, isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand
            );
         } else {
            return 9221120237041090561L;
         }
      }
   }

   public long parseFloatingPointLiteral(byte[] str, int offset, int length) {
      int endIndex = checkBounds(str.length, offset, length);
      int index = skipWhitespace(str, offset, endIndex);
      if (index == endIndex) {
         return 9221120237041090561L;
      } else {
         byte ch = str[index];
         boolean isNegative = ch == 45;
         if (isNegative || ch == 43) {
            ch = charAt(str, ++index, endIndex);
            if (ch == 0) {
               return 9221120237041090561L;
            }
         }

         boolean hasLeadingZero = ch == 48;
         if (hasLeadingZero) {
            ch = charAt(str, ++index, endIndex);
            if ((ch | 32) == 120) {
               return this.parseHexFloatingPointLiteral(str, index + 1, offset, endIndex, isNegative);
            }

            index--;
         }

         return this.parseDecFloatLiteral(str, index, offset, endIndex, isNegative);
      }
   }

   private long parseHexFloatingPointLiteral(byte[] str, int index, int startIndex, int endIndex, boolean isNegative) {
      long significand = 0L;
      int exponent = 0;
      int significandStartIndex = index;
      int virtualIndexOfPoint = -1;
      boolean illegal = false;

      byte ch;
      for (ch = 0; index < endIndex; index++) {
         ch = str[index];
         int hexValue = lookupHex(ch);
         if (hexValue >= 0) {
            significand = significand << 4 | hexValue;
         } else {
            if (hexValue != -4) {
               break;
            }

            illegal |= virtualIndexOfPoint >= 0;
            virtualIndexOfPoint = index;
         }
      }

      int significandEndIndex = index;
      int digitCount;
      if (virtualIndexOfPoint < 0) {
         digitCount = index - significandStartIndex;
         virtualIndexOfPoint = index;
      } else {
         digitCount = index - significandStartIndex - 1;
         exponent = Math.min(virtualIndexOfPoint - index + 1, 1024) * 4;
      }

      int expNumber = 0;
      boolean hasExponent = (ch | 32) == 112;
      if (hasExponent) {
         ch = charAt(str, ++index, endIndex);
         boolean isExponentNegative = ch == 45;
         if (isExponentNegative || ch == 43) {
            ch = charAt(str, ++index, endIndex);
         }

         int digit = (char)(ch - 48);
         illegal |= digit >= 10;

         do {
            if (expNumber < 1024) {
               expNumber = 10 * expNumber + digit;
            }

            ch = charAt(str, ++index, endIndex);
            digit = (char)(ch - 48);
         } while (digit < 10);

         if (isExponentNegative) {
            expNumber = -expNumber;
         }

         exponent += expNumber;
      }

      if ((ch | 34) == 102) {
         index++;
      }

      index = skipWhitespace(str, index, endIndex);
      if (!illegal && index >= endIndex && digitCount != 0 && hasExponent) {
         int skipCountInTruncatedDigits = 0;
         boolean isSignificandTruncated;
         if (digitCount > 16) {
            significand = 0L;

            for (index = significandStartIndex; index < significandEndIndex; index++) {
               ch = str[index];
               int hexValue = lookupHex(ch);
               if (hexValue >= 0) {
                  if (Long.compareUnsigned(significand, 1000000000000000000L) >= 0) {
                     break;
                  }

                  significand = significand << 4 | hexValue;
               } else {
                  skipCountInTruncatedDigits++;
               }
            }

            isSignificandTruncated = index < significandEndIndex;
         } else {
            isSignificandTruncated = false;
         }

         return this.valueOfHexLiteral(
            str,
            startIndex,
            endIndex,
            isNegative,
            significand,
            exponent,
            isSignificandTruncated,
            (virtualIndexOfPoint - index + skipCountInTruncatedDigits) * 4 + expNumber
         );
      } else {
         return 9221120237041090561L;
      }
   }

   private long parseNaNOrInfinity(byte[] str, int index, int endIndex, boolean isNegative) {
      if (index < endIndex) {
         if (str[index] == 78) {
            if (index + 2 < endIndex && str[index + 1] == 97 && str[index + 2] == 78) {
               index = skipWhitespace(str, index + 3, endIndex);
               if (index == endIndex) {
                  return this.nan();
               }
            }
         } else if (index + 7 < endIndex && FastDoubleSwar.readLongLE(str, index) == 8751735898823355977L) {
            index = skipWhitespace(str, index + 8, endIndex);
            if (index == endIndex) {
               return isNegative ? this.negativeInfinity() : this.positiveInfinity();
            }
         }
      }

      return 9221120237041090561L;
   }

   abstract long positiveInfinity();

   abstract long valueOfFloatLiteral(byte[] var1, int var2, int var3, boolean var4, long var5, int var7, boolean var8, int var9);

   abstract long valueOfHexLiteral(byte[] var1, int var2, int var3, boolean var4, long var5, int var7, boolean var8, int var9);
}
