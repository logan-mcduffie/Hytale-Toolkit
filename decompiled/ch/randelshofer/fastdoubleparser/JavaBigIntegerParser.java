package ch.randelshofer.fastdoubleparser;

import java.math.BigInteger;

public final class JavaBigIntegerParser {
   private static final JavaBigIntegerFromByteArray BYTE_ARRAY_PARSER = new JavaBigIntegerFromByteArray();
   private static final JavaBigIntegerFromCharArray CHAR_ARRAY_PARSER = new JavaBigIntegerFromCharArray();
   private static final JavaBigIntegerFromCharSequence CHAR_SEQUENCE_PARSER = new JavaBigIntegerFromCharSequence();

   private JavaBigIntegerParser() {
   }

   public static BigInteger parseBigInteger(CharSequence str) {
      return CHAR_SEQUENCE_PARSER.parseBigIntegerString(str, 0, str.length(), 10);
   }

   public static BigInteger parseBigInteger(CharSequence str, int radix) {
      return CHAR_SEQUENCE_PARSER.parseBigIntegerString(str, 0, str.length(), radix);
   }

   public static BigInteger parseBigInteger(CharSequence str, int offset, int length) {
      return CHAR_SEQUENCE_PARSER.parseBigIntegerString(str, offset, length, 10);
   }

   public static BigInteger parseBigInteger(CharSequence str, int offset, int length, int radix) {
      return CHAR_SEQUENCE_PARSER.parseBigIntegerString(str, offset, length, radix);
   }

   public static BigInteger parseBigInteger(byte[] str) {
      return BYTE_ARRAY_PARSER.parseBigIntegerString(str, 0, str.length, 10);
   }

   public static BigInteger parseBigInteger(byte[] str, int radix) {
      return BYTE_ARRAY_PARSER.parseBigIntegerString(str, 0, str.length, radix);
   }

   public static BigInteger parseBigInteger(byte[] str, int offset, int length) {
      return BYTE_ARRAY_PARSER.parseBigIntegerString(str, offset, length, 10);
   }

   public static BigInteger parseBigInteger(byte[] str, int offset, int length, int radix) {
      return BYTE_ARRAY_PARSER.parseBigIntegerString(str, offset, length, radix);
   }

   public static BigInteger parseBigInteger(char[] str) {
      return CHAR_ARRAY_PARSER.parseBigIntegerString(str, 0, str.length, 10);
   }

   public static BigInteger parseBigInteger(char[] str, int radix) {
      return CHAR_ARRAY_PARSER.parseBigIntegerString(str, 0, str.length, radix);
   }

   public static BigInteger parseBigInteger(char[] str, int offset, int length) {
      return CHAR_ARRAY_PARSER.parseBigIntegerString(str, offset, length, 10);
   }

   public static BigInteger parseBigInteger(char[] str, int offset, int length, int radix) {
      return CHAR_ARRAY_PARSER.parseBigIntegerString(str, offset, length, radix);
   }
}
