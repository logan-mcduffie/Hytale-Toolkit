package ch.randelshofer.fastdoubleparser;

import java.nio.charset.StandardCharsets;

final class JsonDoubleBitsFromByteArray extends AbstractJsonFloatingPointBitsFromByteArray {
   public JsonDoubleBitsFromByteArray() {
   }

   @Override
   long valueOfFloatLiteral(
      byte[] str,
      int startIndex,
      int endIndex,
      boolean isNegative,
      long significand,
      int exponent,
      boolean isSignificandTruncated,
      int exponentOfTruncatedSignificand
   ) {
      double d = FastDoubleMath.tryDecFloatToDoubleTruncated(isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
      return Double.doubleToRawLongBits(
         Double.isNaN(d) ? Double.parseDouble(new String(str, startIndex, endIndex - startIndex, StandardCharsets.ISO_8859_1)) : d
      );
   }
}
