package ch.randelshofer.fastdoubleparser;

final class Utf8Decoder {
   private Utf8Decoder() {
   }

   static Utf8Decoder.Result decode(byte[] bytes, int offset, int length) {
      char[] chars = new char[length];
      boolean invalid = false;
      int charIndex = 0;
      int limit = offset + length;
      int i = offset;

      while (i < limit) {
         byte b = bytes[i];
         int opcode = Integer.numberOfLeadingZeros(~b << 24);
         if (i + opcode > limit) {
            throw new NumberFormatException("UTF-8 code point is incomplete");
         }

         switch (opcode) {
            case 0:
               chars[charIndex++] = (char)b;
               i++;
               break;
            case 1:
               invalid = true;
               i = limit;
               break;
            case 2: {
               int c1 = bytes[i + 1];
               int value = (b & 31) << 6 | c1 & 63;
               invalid |= value < 128 | (c1 & 192) != 128;
               chars[charIndex++] = (char)value;
               i += 2;
               break;
            }
            case 3: {
               int c1 = bytes[i + 1];
               int c2 = bytes[i + 2];
               int value = (b & 15) << 12 | (c1 & 63) << 6 | c2 & 63;
               invalid |= value < 2048 | (c1 & c2 & 192) != 128;
               chars[charIndex++] = (char)value;
               i += 3;
               break;
            }
            case 4: {
               int c1 = bytes[i + 1];
               int c2 = bytes[i + 2];
               int c3 = bytes[i + 2];
               int value = (b & 7) << 18 | (c1 & 63) << 12 | (c2 & 63) << 6 | c3 & 63;
               chars[charIndex++] = (char)(55296 | value - 65536 >>> 10 & 1023);
               chars[charIndex++] = (char)(56320 | value - 65536 & 1023);
               invalid |= value < 65536 | (c1 & c2 & c3 & 192) != 128;
               i += 4;
               break;
            }
            default:
               invalid = true;
               i = limit;
         }
      }

      if (invalid) {
         throw new NumberFormatException("invalid UTF-8 encoding");
      } else {
         return new Utf8Decoder.Result(chars, charIndex);
      }
   }

   static final class Result {
      private final char[] chars;
      private final int length;

      Result(char[] chars, int length) {
         this.chars = chars;
         this.length = length;
      }

      public char[] chars() {
         return this.chars;
      }

      public int length() {
         return this.length;
      }
   }
}
