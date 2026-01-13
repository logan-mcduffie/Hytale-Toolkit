package ch.randelshofer.fastdoubleparser;

import ch.randelshofer.fastdoubleparser.chr.CharDigitSet;
import ch.randelshofer.fastdoubleparser.chr.CharSet;
import ch.randelshofer.fastdoubleparser.chr.CharSetOfNone;
import ch.randelshofer.fastdoubleparser.chr.CharTrie;
import ch.randelshofer.fastdoubleparser.chr.FormatCharSet;

abstract class AbstractConfigurableFloatingPointBitsFromCharArray extends AbstractFloatValueParser {
   private final CharDigitSet digitSet;
   private final CharSet minusSign;
   private final CharSet plusSign;
   private final CharSet decimalSeparator;
   private final CharSet groupingSeparator;
   private final CharTrie nan;
   private final CharTrie infinity;
   private final CharTrie exponentSeparator;
   private final CharSet formatChar;

   public AbstractConfigurableFloatingPointBitsFromCharArray(NumberFormatSymbols symbols, boolean ignoreCase) {
      this.decimalSeparator = CharSet.copyOf(symbols.decimalSeparator(), ignoreCase);
      this.groupingSeparator = CharSet.copyOf(symbols.groupingSeparator(), ignoreCase);
      this.digitSet = CharDigitSet.copyOf(symbols.digits());
      this.minusSign = CharSet.copyOf(symbols.minusSign(), ignoreCase);
      this.exponentSeparator = CharTrie.copyOf(symbols.exponentSeparator(), ignoreCase);
      this.plusSign = CharSet.copyOf(symbols.plusSign(), ignoreCase);
      this.nan = CharTrie.copyOf(symbols.nan(), ignoreCase);
      this.infinity = CharTrie.copyOf(symbols.infinity(), ignoreCase);
      this.formatChar = (CharSet)(NumberFormatSymbolsInfo.containsFormatChars(symbols) ? new CharSetOfNone() : new FormatCharSet());
   }

   abstract long nan();

   abstract long negativeInfinity();

   public final long parseFloatingPointLiteral(char[] str, int offset, int length) {
      int endIndex = checkBounds(str.length, offset, length);
      int index = this.skipFormatCharacters(str, offset, endIndex);
      if (index == endIndex) {
         return 9221120237041090561L;
      } else {
         char ch = str[index];
         boolean isNegative = this.minusSign.containsKey(ch);
         boolean isSignificandSigned = false;
         if (isNegative || this.plusSign.containsKey(ch)) {
            isSignificandSigned = true;
            if (++index == endIndex) {
               return 9221120237041090561L;
            }
         }

         long significand = 0L;
         int significandStartIndex = index;
         int decimalSeparatorIndex = -1;
         int integerDigitCount = -1;
         int groupingCount = 0;

         boolean illegal;
         for (illegal = false; index < endIndex; index++) {
            ch = str[index];
            int digit = this.digitSet.toDigit(ch);
            if (digit < 10) {
               significand = 10L * significand + digit;
            } else if (this.decimalSeparator.containsKey(ch)) {
               illegal |= integerDigitCount >= 0;
               decimalSeparatorIndex = index;
               integerDigitCount = index - significandStartIndex - groupingCount;
            } else {
               if (!this.groupingSeparator.containsKey(ch)) {
                  break;
               }

               illegal |= decimalSeparatorIndex != -1;
               groupingCount++;
            }
         }

         int significandEndIndex = index;
         int exponent;
         int digitCount;
         if (integerDigitCount < 0) {
            integerDigitCount = digitCount = index - significandStartIndex - groupingCount;
            decimalSeparatorIndex = index;
            exponent = 0;
         } else {
            digitCount = index - significandStartIndex - 1 - groupingCount;
            exponent = integerDigitCount - digitCount;
         }

         illegal |= digitCount == 0 && index > significandStartIndex;
         if (!illegal && digitCount == 0) {
            return this.parseNaNOrInfinity(str, index, endIndex, isNegative, isSignificandSigned);
         } else {
            if (index < endIndex && !isSignificandSigned) {
               isNegative = this.minusSign.containsKey(ch);
               if (isNegative || this.plusSign.containsKey(ch)) {
                  index++;
               }
            }

            int expNumber = 0;
            boolean isExponentSigned = false;
            if (digitCount > 0) {
               int count = this.exponentSeparator.match(str, index, endIndex);
               if (count > 0) {
                  int var25 = index + count;
                  index = this.skipFormatCharacters(str, var25, endIndex);
                  ch = charAt(str, index, endIndex);
                  boolean isExponentNegative = this.minusSign.containsKey(ch);
                  if (isExponentNegative || this.plusSign.containsKey(ch)) {
                     ch = charAt(str, ++index, endIndex);
                     isExponentSigned = true;
                  }

                  int digit = this.digitSet.toDigit(ch);
                  illegal |= digit >= 10;

                  do {
                     if (expNumber < 1024) {
                        expNumber = 10 * expNumber + digit;
                     }

                     ch = charAt(str, ++index, endIndex);
                     digit = this.digitSet.toDigit(ch);
                  } while (digit < 10);

                  if (!isExponentSigned) {
                     isExponentNegative = this.minusSign.containsKey(ch);
                     if (isExponentNegative || this.plusSign.containsKey(ch)) {
                        index++;
                     }
                  }

                  if (isExponentNegative) {
                     expNumber = -expNumber;
                  }

                  exponent += expNumber;
               }
            }

            if (!illegal && index >= endIndex) {
               boolean isSignificandTruncated;
               int exponentOfTruncatedSignificand;
               if (digitCount > 19) {
                  int truncatedDigitCount = 0;
                  significand = 0L;

                  for (index = significandStartIndex; index < significandEndIndex; index++) {
                     ch = str[index];
                     int digit = this.digitSet.toDigit(ch);
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
                  str,
                  significandStartIndex,
                  decimalSeparatorIndex,
                  decimalSeparatorIndex + 1,
                  significandEndIndex,
                  isNegative,
                  significand,
                  exponent,
                  isSignificandTruncated,
                  exponentOfTruncatedSignificand,
                  expNumber,
                  offset,
                  endIndex
               );
            } else {
               return 9221120237041090561L;
            }
         }
      }
   }

   private int skipFormatCharacters(char[] str, int index, int endIndex) {
      while (index < endIndex && this.formatChar.containsKey(str[index])) {
         index++;
      }

      return index;
   }

   private long parseNaNOrInfinity(char[] str, int index, int endIndex, boolean isNegative, boolean isSignificandSigned) {
      int nanMatch = this.nan.match(str, index, endIndex);
      if (nanMatch > 0) {
         index += nanMatch;
         if (index < endIndex && !isSignificandSigned) {
            char ch = str[index];
            if (this.minusSign.containsKey(ch) || this.plusSign.containsKey(ch)) {
               index++;
            }
         }

         return index == endIndex ? this.nan() : 9221120237041090561L;
      } else {
         int infinityMatch = this.infinity.match(str, index, endIndex);
         if (infinityMatch > 0) {
            index += infinityMatch;
            if (index < endIndex && !isSignificandSigned) {
               char ch = str[index];
               isNegative = this.minusSign.containsKey(ch);
               if (isNegative || this.plusSign.containsKey(ch)) {
                  index++;
               }
            }

            if (index == endIndex) {
               return isNegative ? this.negativeInfinity() : this.positiveInfinity();
            }
         }

         return 9221120237041090561L;
      }
   }

   abstract long positiveInfinity();

   abstract long valueOfFloatLiteral(
      char[] var1, int var2, int var3, int var4, int var5, boolean var6, long var7, int var9, boolean var10, int var11, int var12, int var13, int var14
   );

   protected double slowPathToDouble(
      char[] str, int integerStartIndex, int integerEndIndex, int fractionStartIndex, int fractionEndIndex, boolean isSignificandNegative, int exponentValue
   ) {
      return SlowDoubleConversionPath.toDouble(
         str, this.digitSet, integerStartIndex, integerEndIndex, fractionStartIndex, fractionEndIndex, isSignificandNegative, (long)exponentValue
      );
   }
}
