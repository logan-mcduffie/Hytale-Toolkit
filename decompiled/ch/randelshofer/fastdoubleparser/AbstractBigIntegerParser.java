package ch.randelshofer.fastdoubleparser;

abstract class AbstractBigIntegerParser extends AbstractNumberParser {
   private static final int MAX_DECIMAL_DIGITS = 646456993;
   private static final int MAX_HEX_DIGITS = 536870912;
   static final int RECURSION_THRESHOLD = 400;

   protected static boolean hasManyDigits(int length) {
      return length > 18;
   }

   protected static void checkHexBigIntegerBounds(int numDigits) {
      if (numDigits > 536870912) {
         throw new NumberFormatException("value exceeds limits");
      }
   }

   protected static void checkDecBigIntegerBounds(int numDigits) {
      if (numDigits > 646456993) {
         throw new NumberFormatException("value exceeds limits");
      }
   }
}
