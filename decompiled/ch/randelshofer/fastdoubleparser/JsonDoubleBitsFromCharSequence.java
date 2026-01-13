package ch.randelshofer.fastdoubleparser;

final class JsonDoubleBitsFromCharSequence extends AbstractJsonFloatingPointBitsFromCharSequence {
   public JsonDoubleBitsFromCharSequence() {
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
      double d = FastDoubleMath.tryDecFloatToDoubleTruncated(isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
      return Double.doubleToRawLongBits(Double.isNaN(d) ? Double.parseDouble(str.subSequence(startIndex, endIndex).toString()) : d);
   }
}
