package ch.randelshofer.fastdoubleparser;

import java.math.BigInteger;
import java.util.Map;

final class JavaBigIntegerFromCharArray extends AbstractBigIntegerParser {
   public BigInteger parseBigIntegerString(char[] str, int offset, int length, int radix) throws NumberFormatException {
      try {
         int endIndex = AbstractNumberParser.checkBounds(str.length, offset, length);
         int index = offset;
         char ch = str[offset];
         boolean isNegative = ch == '-';
         if (isNegative || ch == '+') {
            index = offset + 1;
            ch = charAt(str, index, endIndex);
            if (ch == 0) {
               throw new NumberFormatException("illegal syntax");
            }
         }

         switch (radix) {
            case 10:
               return this.parseDecDigits(str, index, endIndex, isNegative);
            case 16:
               return this.parseHexDigits(str, index, endIndex, isNegative);
            default:
               return new BigInteger(new String(str, offset, length), radix);
         }
      } catch (ArithmeticException var9) {
         NumberFormatException nfe = new NumberFormatException("value exceeds limits");
         nfe.initCause(var9);
         throw nfe;
      }
   }

   private BigInteger parseDecDigits(char[] str, int from, int to, boolean isNegative) {
      int numDigits = to - from;
      if (hasManyDigits(numDigits)) {
         return this.parseManyDecDigits(str, from, to, isNegative);
      } else {
         int preroll = from + (numDigits & 7);
         long significand = FastDoubleSwar.tryToParseUpTo7Digits(str, from, preroll);
         boolean success = significand >= 0L;

         for (int var11 = preroll; var11 < to; var11 += 8) {
            int addend = FastDoubleSwar.tryToParseEightDigits(str, var11);
            success &= addend >= 0;
            significand = significand * 100000000L + addend;
         }

         if (!success) {
            throw new NumberFormatException("illegal syntax");
         } else {
            return BigInteger.valueOf(isNegative ? -significand : significand);
         }
      }
   }

   private BigInteger parseHexDigits(char[] str, int from, int to, boolean isNegative) {
      from = this.skipZeroes(str, from, to);
      int numDigits = to - from;
      if (numDigits <= 0) {
         return BigInteger.ZERO;
      } else {
         checkHexBigIntegerBounds(numDigits);
         byte[] bytes = new byte[(numDigits + 1 >> 1) + 1];
         int index = 1;
         boolean illegalDigits = false;
         if ((numDigits & 1) != 0) {
            char chLow = str[from++];
            int valueLow = lookupHex(chLow);
            bytes[index++] = (byte)valueLow;
            illegalDigits = valueLow < 0;
         }

         for (int prerollLimit = from + (to - from & 7); from < prerollLimit; from += 2) {
            char chHigh = str[from];
            char chLow = str[from + 1];
            int valueHigh = lookupHex(chHigh);
            int valueLow = lookupHex(chLow);
            bytes[index++] = (byte)(valueHigh << 4 | valueLow);
            illegalDigits |= valueHigh < 0 || valueLow < 0;
         }

         while (from < to) {
            long value = FastDoubleSwar.tryToParseEightHexDigits(str, from);
            FastDoubleSwar.writeIntBE(bytes, index, (int)value);
            illegalDigits |= value < 0L;
            from += 8;
            index += 4;
         }

         if (illegalDigits) {
            throw new NumberFormatException("illegal syntax");
         } else {
            BigInteger result = new BigInteger(bytes);
            return isNegative ? result.negate() : result;
         }
      }
   }

   private BigInteger parseManyDecDigits(char[] str, int from, int to, boolean isNegative) {
      from = this.skipZeroes(str, from, to);
      int numDigits = to - from;
      checkDecBigIntegerBounds(numDigits);
      Map<Integer, BigInteger> powersOfTen = FastIntegerMath.fillPowersOf10Floor16(from, to);
      BigInteger result = ParseDigitsTaskCharArray.parseDigitsRecursive(str, from, to, powersOfTen, 400);
      return isNegative ? result.negate() : result;
   }

   private int skipZeroes(char[] str, int from, int to) {
      while (from < to - 8 && FastDoubleSwar.isEightZeroes(str, from)) {
         from += 8;
      }

      while (from < to && str[from] == '0') {
         from++;
      }

      return from;
   }
}
