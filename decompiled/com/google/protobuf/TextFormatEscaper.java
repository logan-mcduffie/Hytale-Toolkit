package com.google.protobuf;

final class TextFormatEscaper {
   private TextFormatEscaper() {
   }

   static String escapeBytes(TextFormatEscaper.ByteSequence input) {
      StringBuilder builder = new StringBuilder(input.size());

      for (int i = 0; i < input.size(); i++) {
         byte b = input.byteAt(i);
         switch (b) {
            case 7:
               builder.append("\\a");
               break;
            case 8:
               builder.append("\\b");
               break;
            case 9:
               builder.append("\\t");
               break;
            case 10:
               builder.append("\\n");
               break;
            case 11:
               builder.append("\\v");
               break;
            case 12:
               builder.append("\\f");
               break;
            case 13:
               builder.append("\\r");
               break;
            case 34:
               builder.append("\\\"");
               break;
            case 39:
               builder.append("\\'");
               break;
            case 92:
               builder.append("\\\\");
               break;
            default:
               if (b >= 32 && b <= 126) {
                  builder.append((char)b);
               } else {
                  builder.append('\\');
                  builder.append((char)(48 + (b >>> 6 & 3)));
                  builder.append((char)(48 + (b >>> 3 & 7)));
                  builder.append((char)(48 + (b & 7)));
               }
         }
      }

      return builder.toString();
   }

   static String escapeBytes(final ByteString input) {
      return escapeBytes(new TextFormatEscaper.ByteSequence() {
         @Override
         public int size() {
            return input.size();
         }

         @Override
         public byte byteAt(int offset) {
            return input.byteAt(offset);
         }
      });
   }

   static String escapeBytes(final byte[] input) {
      return escapeBytes(new TextFormatEscaper.ByteSequence() {
         @Override
         public int size() {
            return input.length;
         }

         @Override
         public byte byteAt(int offset) {
            return input[offset];
         }
      });
   }

   static String escapeText(String input) {
      return escapeBytes(ByteString.copyFromUtf8(input));
   }

   static String escapeDoubleQuotesAndBackslashes(String input) {
      return input.replace("\\", "\\\\").replace("\"", "\\\"");
   }

   private interface ByteSequence {
      int size();

      byte byteAt(int offset);
   }
}
