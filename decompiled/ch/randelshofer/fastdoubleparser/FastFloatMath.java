package ch.randelshofer.fastdoubleparser;

final class FastFloatMath {
   private static final int FLOAT_EXPONENT_BIAS = 127;
   private static final int FLOAT_SIGNIFICAND_WIDTH = 24;
   private static final int FLOAT_MIN_EXPONENT_POWER_OF_TEN = -45;
   private static final int FLOAT_MAX_EXPONENT_POWER_OF_TEN = 38;
   private static final int FLOAT_MIN_EXPONENT_POWER_OF_TWO = -126;
   private static final int FLOAT_MAX_EXPONENT_POWER_OF_TWO = 127;
   private static final float[] FLOAT_POWER_OF_TEN = new float[]{
      1.0F, 10.0F, 100.0F, 1000.0F, 10000.0F, 100000.0F, 1000000.0F, 1.0E7F, 1.0E8F, 1.0E9F, 1.0E10F
   };

   private FastFloatMath() {
   }

   static float tryDecFloatToFloatTruncated(
      boolean isNegative, long significand, int exponent, boolean isSignificandTruncated, int exponentOfTruncatedSignificand
   ) {
      if (significand == 0L) {
         return isNegative ? -0.0F : 0.0F;
      } else {
         float result;
         if (isSignificandTruncated) {
            if (-45 <= exponentOfTruncatedSignificand && exponentOfTruncatedSignificand <= 38) {
               float withoutRounding = tryDecToFloatWithFastAlgorithm(isNegative, significand, exponentOfTruncatedSignificand);
               float roundedUp = tryDecToFloatWithFastAlgorithm(isNegative, significand + 1L, exponentOfTruncatedSignificand);
               if (roundedUp == withoutRounding) {
                  return withoutRounding;
               }
            }

            result = Float.NaN;
         } else if (-45 <= exponent && exponent <= 38) {
            result = tryDecToFloatWithFastAlgorithm(isNegative, significand, exponent);
         } else {
            result = Float.NaN;
         }

         return result;
      }
   }

   static float tryHexFloatToFloatTruncated(
      boolean isNegative, long significand, int exponent, boolean isSignificandTruncated, int exponentOfTruncatedSignificand
   ) {
      int power = isSignificandTruncated ? exponentOfTruncatedSignificand : exponent;
      if (-126 <= power && power <= 127) {
         float d = (float)significand + (significand < 0L ? 1.8446744E19F : 0.0F);
         d = fastScalb(d, power);
         return isNegative ? -d : d;
      } else {
         return Float.NaN;
      }
   }

   static float tryDecToFloatWithFastAlgorithm(boolean isNegative, long significand, int power) {
      if (-10 <= power && power <= 10 && Long.compareUnsigned(significand, 16777215L) <= 0) {
         float d = (float)significand;
         if (power < 0) {
            d /= FLOAT_POWER_OF_TEN[-power];
         } else {
            d *= FLOAT_POWER_OF_TEN[power];
         }

         return isNegative ? -d : d;
      } else {
         long factorMantissa = FastDoubleMath.MANTISSA_64[power - -325];
         long exponent = (217706L * power >> 16) + 127L + 64L;
         int lz = Long.numberOfLeadingZeros(significand);
         long shiftedSignificand = significand << lz;
         long upper = FastIntegerMath.unsignedMultiplyHigh(shiftedSignificand, factorMantissa);
         long upperbit = upper >>> 63;
         long mantissa = upper >>> (int)(upperbit + 38L);
         lz += (int)(1L ^ upperbit);
         if ((upper & 274877906943L) != 274877906943L && ((upper & 274877906943L) != 0L || (mantissa & 3L) != 1L)) {
            mantissa = ++mantissa >>> 1;
            if (mantissa >= 16777216L) {
               mantissa = 8388608L;
               lz--;
            }

            mantissa &= -8388609L;
            long real_exponent = exponent - lz;
            if (real_exponent >= 1L && real_exponent <= 254L) {
               int bits = (int)(mantissa | real_exponent << 23 | (isNegative ? 2147483648L : 0L));
               return Float.intBitsToFloat(bits);
            } else {
               return Float.NaN;
            }
         } else {
            return Float.NaN;
         }
      }
   }

   static float fastScalb(float number, int scaleFactor) {
      return number * Float.intBitsToFloat(scaleFactor + 127 << 23);
   }
}
