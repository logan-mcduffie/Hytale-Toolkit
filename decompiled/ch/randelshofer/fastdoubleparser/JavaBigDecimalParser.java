package ch.randelshofer.fastdoubleparser;

import java.math.BigDecimal;

public final class JavaBigDecimalParser {
   private static final JavaBigDecimalFromByteArray BYTE_ARRAY_PARSER = new JavaBigDecimalFromByteArray();
   private static final JavaBigDecimalFromCharArray CHAR_ARRAY_PARSER = new JavaBigDecimalFromCharArray();
   private static final JavaBigDecimalFromCharSequence CHAR_SEQUENCE_PARSER = new JavaBigDecimalFromCharSequence();

   private JavaBigDecimalParser() {
   }

   public static BigDecimal parseBigDecimal(CharSequence str) throws NumberFormatException {
      return parseBigDecimal(str, 0, str.length());
   }

   public static BigDecimal parseBigDecimal(CharSequence str, int offset, int length) throws NumberFormatException {
      return CHAR_SEQUENCE_PARSER.parseBigDecimalString(str, offset, length);
   }

   public static BigDecimal parseBigDecimal(byte[] str) throws NumberFormatException {
      return parseBigDecimal(str, 0, str.length);
   }

   public static BigDecimal parseBigDecimal(byte[] str, int offset, int length) throws NumberFormatException {
      return BYTE_ARRAY_PARSER.parseBigDecimalString(str, offset, length);
   }

   public static BigDecimal parseBigDecimal(char[] str) throws NumberFormatException {
      return parseBigDecimal(str, 0, str.length);
   }

   public static BigDecimal parseBigDecimal(char[] str, int offset, int length) throws NumberFormatException {
      return CHAR_ARRAY_PARSER.parseBigDecimalString(str, offset, length);
   }
}
