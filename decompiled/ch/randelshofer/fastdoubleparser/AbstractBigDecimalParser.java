package ch.randelshofer.fastdoubleparser;

abstract class AbstractBigDecimalParser extends AbstractNumberParser {
   public static final int MANY_DIGITS_THRESHOLD = 32;
   static final int RECURSION_THRESHOLD = 400;
   protected static final long MAX_EXPONENT_NUMBER = 2147483647L;
   protected static final int MAX_DIGITS_WITHOUT_LEADING_ZEROS = 646456993;

   protected static boolean hasManyDigits(int length) {
      return length >= 32;
   }

   protected static void checkParsedBigDecimalBounds(boolean illegal, int index, int endIndex, int digitCount, long exponent) {
      if (illegal || index < endIndex) {
         throw new NumberFormatException("illegal syntax");
      } else if (exponent <= -2147483648L || exponent > 2147483647L || digitCount > 646456993) {
         throw new NumberFormatException("value exceeds limits");
      }
   }
}
