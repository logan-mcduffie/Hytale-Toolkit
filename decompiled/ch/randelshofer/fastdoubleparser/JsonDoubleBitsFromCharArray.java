package ch.randelshofer.fastdoubleparser;

final class JsonDoubleBitsFromCharArray extends AbstractJsonFloatingPointBitsFromCharArray {
   public JsonDoubleBitsFromCharArray() {
   }

   @Override
   long valueOfFloatLiteral(
      char[] str,
      int startIndex,
      int endIndex,
      boolean isNegative,
      long significand,
      int exponent,
      boolean isSignificandTruncated,
      int exponentOfTruncatedSignificand
   ) {
      double d = FastDoubleMath.tryDecFloatToDoubleTruncated(isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
      return Double.doubleToRawLongBits(Double.isNaN(d) ? Double.parseDouble(new String(str, startIndex, endIndex - startIndex)) : d);
   }
}
