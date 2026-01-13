package ch.randelshofer.fastdoubleparser;

final class JavaFloatBitsFromCharSequence extends AbstractJavaFloatingPointBitsFromCharSequence {
   public JavaFloatBitsFromCharSequence() {
   }

   @Override
   long nan() {
      return Float.floatToRawIntBits(Float.NaN);
   }

   @Override
   long negativeInfinity() {
      return Float.floatToRawIntBits(Float.NEGATIVE_INFINITY);
   }

   @Override
   long positiveInfinity() {
      return Float.floatToRawIntBits(Float.POSITIVE_INFINITY);
   }

   @Override
   long valueOfFloatLiteral(
      CharSequence str,
      int startIndex,
      int endIndex,
      boolean isNegative,
      long significand,
      int exponent,
      boolean isSignificandTruncated,
      int exponentOfTruncatedSignificand
   ) {
      float d = FastFloatMath.tryDecFloatToFloatTruncated(isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
      return Float.floatToRawIntBits(Float.isNaN(d) ? Float.parseFloat(str.subSequence(startIndex, endIndex).toString()) : d);
   }

   @Override
   long valueOfHexLiteral(
      CharSequence str,
      int startIndex,
      int endIndex,
      boolean isNegative,
      long significand,
      int exponent,
      boolean isSignificandTruncated,
      int exponentOfTruncatedSignificand
   ) {
      float d = FastFloatMath.tryHexFloatToFloatTruncated(isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
      return Float.floatToRawIntBits(Float.isNaN(d) ? Float.parseFloat(str.subSequence(startIndex, endIndex).toString()) : d);
   }
}
