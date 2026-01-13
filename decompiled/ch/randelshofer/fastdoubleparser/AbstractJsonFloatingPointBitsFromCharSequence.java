package ch.randelshofer.fastdoubleparser;

abstract class AbstractJsonFloatingPointBitsFromCharSequence extends AbstractFloatValueParser {
   public final long parseNumber(CharSequence str, int offset, int length) {
      int endIndex = checkBounds(str.length(), offset, length);
      int index = offset;
      char ch = charAt(str, offset, endIndex);
      boolean isNegative = ch == '-';
      if (isNegative) {
         index = offset + 1;
         ch = charAt(str, index, endIndex);
         if (ch == 0) {
            return 9221120237041090561L;
         }
      }

      boolean hasLeadingZero = ch == '0';
      if (hasLeadingZero) {
         ch = charAt(str, ++index, endIndex);
         if (ch == '0') {
            return 9221120237041090561L;
         }
      }

      long significand = 0L;
      int significandStartIndex = index;
      int integerDigitCount = -1;

      boolean illegal;
      for (illegal = false; index < endIndex; index++) {
         ch = str.charAt(index);
         int digit = (char)(ch - '0');
         if (digit < 10) {
            significand = 10L * significand + digit;
         } else {
            if (ch != '.') {
               break;
            }

            illegal |= integerDigitCount >= 0;
            integerDigitCount = index - significandStartIndex;
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

      int expNumber = 0;
      if ((ch | ' ') == 101) {
         ch = charAt(str, ++index, endIndex);
         boolean isExponentNegative = ch == '-';
         if (isExponentNegative || ch == '+') {
            ch = charAt(str, ++index, endIndex);
         }

         int digit = (char)(ch - '0');
         illegal |= digit >= 10;

         do {
            if (expNumber < 1024) {
               expNumber = 10 * expNumber + digit;
            }

            ch = charAt(str, ++index, endIndex);
            digit = (char)(ch - '0');
         } while (digit < 10);

         if (isExponentNegative) {
            expNumber = -expNumber;
         }

         exponent += expNumber;
      }

      if (!illegal && index >= endIndex && (hasLeadingZero || digitCount != 0)) {
         boolean isSignificandTruncated;
         int exponentOfTruncatedSignificand;
         if (digitCount > 19) {
            int truncatedDigitCount = 0;
            significand = 0L;

            for (index = significandStartIndex; index < significandEndIndex; index++) {
               ch = str.charAt(index);
               int digit = (char)(ch - '0');
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

         return this.valueOfFloatLiteral(str, offset, endIndex, isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
      } else {
         return 9221120237041090561L;
      }
   }

   abstract long valueOfFloatLiteral(CharSequence var1, int var2, int var3, boolean var4, long var5, int var7, boolean var8, int var9);
}
