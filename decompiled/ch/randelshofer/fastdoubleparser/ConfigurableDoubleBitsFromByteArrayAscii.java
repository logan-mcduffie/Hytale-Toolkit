package ch.randelshofer.fastdoubleparser;

final class ConfigurableDoubleBitsFromByteArrayAscii extends AbstractConfigurableFloatingPointBitsFromByteArrayAscii {
   public ConfigurableDoubleBitsFromByteArrayAscii(NumberFormatSymbols symbols, boolean ignoreCase) {
      super(symbols, ignoreCase);
   }

   @Override
   long nan() {
      return Double.doubleToRawLongBits(Double.NaN);
   }

   @Override
   long negativeInfinity() {
      return Double.doubleToRawLongBits(Double.NEGATIVE_INFINITY);
   }

   @Override
   long positiveInfinity() {
      return Double.doubleToRawLongBits(Double.POSITIVE_INFINITY);
   }

   @Override
   long valueOfFloatLiteral(
      byte[] str,
      int integerStartIndex,
      int integerEndIndex,
      int fractionStartIndex,
      int fractionEndIndex,
      boolean isSignificandNegative,
      long significand,
      int exponent,
      boolean isSignificandTruncated,
      int exponentOfTruncatedSignificand,
      int exponentValue,
      int startIndex,
      int endIndex
   ) {
      double d = FastDoubleMath.tryDecFloatToDoubleTruncated(
         isSignificandNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand
      );
      return Double.doubleToRawLongBits(
         Double.isNaN(d)
            ? this.slowPathToDouble(str, integerStartIndex, integerEndIndex, fractionStartIndex, fractionEndIndex, isSignificandNegative, exponentValue)
            : d
      );
   }
}
