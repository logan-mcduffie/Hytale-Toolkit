package com.google.protobuf;

import java.nio.ByteBuffer;
import java.util.Arrays;

final class Utf8 {
   private static final Utf8.Processor processor = (Utf8.Processor)(Utf8.UnsafeProcessor.isAvailable() && !Android.isOnAndroidDevice()
      ? new Utf8.UnsafeProcessor()
      : new Utf8.SafeProcessor());
   private static final long ASCII_MASK_LONG = -9187201950435737472L;
   static final int MAX_BYTES_PER_CHAR = 3;
   static final int COMPLETE = 0;
   static final int MALFORMED = -1;
   private static final int UNSAFE_COUNT_ASCII_THRESHOLD = 16;

   static boolean isValidUtf8(byte[] bytes) {
      return processor.isValidUtf8(bytes, 0, bytes.length);
   }

   static boolean isValidUtf8(byte[] bytes, int index, int limit) {
      return processor.isValidUtf8(bytes, index, limit);
   }

   static int partialIsValidUtf8(int state, byte[] bytes, int index, int limit) {
      return processor.partialIsValidUtf8(state, bytes, index, limit);
   }

   private static int incompleteStateFor(int byte1) {
      return byte1 > -12 ? -1 : byte1;
   }

   private static int incompleteStateFor(int byte1, int byte2) {
      return byte1 <= -12 && byte2 <= -65 ? byte1 ^ byte2 << 8 : -1;
   }

   private static int incompleteStateFor(int byte1, int byte2, int byte3) {
      return byte1 <= -12 && byte2 <= -65 && byte3 <= -65 ? byte1 ^ byte2 << 8 ^ byte3 << 16 : -1;
   }

   private static int incompleteStateFor(byte[] bytes, int index, int limit) {
      int byte1 = bytes[index - 1];
      switch (limit - index) {
         case 0:
            return incompleteStateFor(byte1);
         case 1:
            return incompleteStateFor(byte1, bytes[index]);
         case 2:
            return incompleteStateFor(byte1, bytes[index], bytes[index + 1]);
         default:
            throw new AssertionError();
      }
   }

   private static int incompleteStateFor(final ByteBuffer buffer, final int byte1, final int index, final int remaining) {
      switch (remaining) {
         case 0:
            return incompleteStateFor(byte1);
         case 1:
            return incompleteStateFor(byte1, buffer.get(index));
         case 2:
            return incompleteStateFor(byte1, buffer.get(index), buffer.get(index + 1));
         default:
            throw new AssertionError();
      }
   }

   static int encodedLength(String string) {
      int utf16Length = string.length();
      int utf8Length = utf16Length;
      int i = 0;

      while (i < utf16Length && string.charAt(i) < 128) {
         i++;
      }

      while (i < utf16Length) {
         char c = string.charAt(i);
         if (c >= 2048) {
            utf8Length += encodedLengthGeneral(string, i);
            break;
         }

         utf8Length += 127 - c >>> 31;
         i++;
      }

      if (utf8Length < utf16Length) {
         throw new IllegalArgumentException("UTF-8 length does not fit in int: " + (utf8Length + 4294967296L));
      } else {
         return utf8Length;
      }
   }

   private static int encodedLengthGeneral(String string, int start) {
      int utf16Length = string.length();
      int utf8Length = 0;

      for (int i = start; i < utf16Length; i++) {
         char c = string.charAt(i);
         if (c < 2048) {
            utf8Length += 127 - c >>> 31;
         } else {
            utf8Length += 2;
            if ('\ud800' <= c && c <= '\udfff') {
               int cp = Character.codePointAt(string, i);
               if (cp < 65536) {
                  throw new Utf8.UnpairedSurrogateException(i, utf16Length);
               }

               i++;
            }
         }
      }

      return utf8Length;
   }

   static int encode(String in, byte[] out, int offset, int length) {
      return processor.encodeUtf8(in, out, offset, length);
   }

   static boolean isValidUtf8(ByteBuffer buffer) {
      return processor.isValidUtf8(buffer, buffer.position(), buffer.remaining());
   }

   static int partialIsValidUtf8(int state, ByteBuffer buffer, int index, int limit) {
      return processor.partialIsValidUtf8(state, buffer, index, limit);
   }

   static String decodeUtf8(ByteBuffer buffer, int index, int size) throws InvalidProtocolBufferException {
      return processor.decodeUtf8(buffer, index, size);
   }

   static String decodeUtf8(byte[] bytes, int index, int size) throws InvalidProtocolBufferException {
      return processor.decodeUtf8(bytes, index, size);
   }

   static void encodeUtf8(String in, ByteBuffer out) {
      processor.encodeUtf8(in, out);
   }

   private static int estimateConsecutiveAscii(ByteBuffer buffer, int index, int limit) {
      int i = index;
      int lim = limit - 7;

      while (i < lim && (buffer.getLong(i) & -9187201950435737472L) == 0L) {
         i += 8;
      }

      return i - index;
   }

   private Utf8() {
   }

   private static class DecodeUtil {
      private static boolean isOneByte(byte b) {
         return b >= 0;
      }

      private static boolean isTwoBytes(byte b) {
         return b < -32;
      }

      private static boolean isThreeBytes(byte b) {
         return b < -16;
      }

      private static void handleOneByte(byte byte1, char[] resultArr, int resultPos) {
         resultArr[resultPos] = (char)byte1;
      }

      private static void handleTwoBytes(byte byte1, byte byte2, char[] resultArr, int resultPos) throws InvalidProtocolBufferException {
         if (byte1 >= -62 && !isNotTrailingByte(byte2)) {
            resultArr[resultPos] = (char)((byte1 & 31) << 6 | trailingByteValue(byte2));
         } else {
            throw InvalidProtocolBufferException.invalidUtf8();
         }
      }

      private static void handleThreeBytes(byte byte1, byte byte2, byte byte3, char[] resultArr, int resultPos) throws InvalidProtocolBufferException {
         if (!isNotTrailingByte(byte2) && (byte1 != -32 || byte2 >= -96) && (byte1 != -19 || byte2 < -96) && !isNotTrailingByte(byte3)) {
            resultArr[resultPos] = (char)((byte1 & 15) << 12 | trailingByteValue(byte2) << 6 | trailingByteValue(byte3));
         } else {
            throw InvalidProtocolBufferException.invalidUtf8();
         }
      }

      private static void handleFourBytes(byte byte1, byte byte2, byte byte3, byte byte4, char[] resultArr, int resultPos) throws InvalidProtocolBufferException {
         if (!isNotTrailingByte(byte2) && (byte1 << 28) + (byte2 - -112) >> 30 == 0 && !isNotTrailingByte(byte3) && !isNotTrailingByte(byte4)) {
            int codepoint = (byte1 & 7) << 18 | trailingByteValue(byte2) << 12 | trailingByteValue(byte3) << 6 | trailingByteValue(byte4);
            resultArr[resultPos] = highSurrogate(codepoint);
            resultArr[resultPos + 1] = lowSurrogate(codepoint);
         } else {
            throw InvalidProtocolBufferException.invalidUtf8();
         }
      }

      private static boolean isNotTrailingByte(byte b) {
         return b > -65;
      }

      private static int trailingByteValue(byte b) {
         return b & 63;
      }

      private static char highSurrogate(int codePoint) {
         return (char)(55232 + (codePoint >>> 10));
      }

      private static char lowSurrogate(int codePoint) {
         return (char)(56320 + (codePoint & 1023));
      }
   }

   abstract static class Processor {
      final boolean isValidUtf8(byte[] bytes, int index, int limit) {
         return this.partialIsValidUtf8(0, bytes, index, limit) == 0;
      }

      abstract int partialIsValidUtf8(int state, byte[] bytes, int index, int limit);

      final boolean isValidUtf8(ByteBuffer buffer, int index, int limit) {
         return this.partialIsValidUtf8(0, buffer, index, limit) == 0;
      }

      final int partialIsValidUtf8(final int state, final ByteBuffer buffer, int index, final int limit) {
         if (buffer.hasArray()) {
            int offset = buffer.arrayOffset();
            return this.partialIsValidUtf8(state, buffer.array(), offset + index, offset + limit);
         } else {
            return buffer.isDirect() ? this.partialIsValidUtf8Direct(state, buffer, index, limit) : this.partialIsValidUtf8Default(state, buffer, index, limit);
         }
      }

      abstract int partialIsValidUtf8Direct(final int state, final ByteBuffer buffer, int index, final int limit);

      final int partialIsValidUtf8Default(final int state, final ByteBuffer buffer, int index, final int limit) {
         if (state != 0) {
            if (index >= limit) {
               return state;
            }

            byte byte1 = (byte)state;
            if (byte1 < -32) {
               if (byte1 < -62 || buffer.get(index++) > -65) {
                  return -1;
               }
            } else if (byte1 < -16) {
               byte byte2 = (byte)(~(state >> 8));
               if (byte2 == 0) {
                  byte2 = buffer.get(index++);
                  if (index >= limit) {
                     return Utf8.incompleteStateFor(byte1, byte2);
                  }
               }

               if (byte2 > -65 || byte1 == -32 && byte2 < -96 || byte1 == -19 && byte2 >= -96 || buffer.get(index++) > -65) {
                  return -1;
               }
            } else {
               byte byte2x = (byte)(~(state >> 8));
               byte byte3 = 0;
               if (byte2x == 0) {
                  byte2x = buffer.get(index++);
                  if (index >= limit) {
                     return Utf8.incompleteStateFor(byte1, byte2x);
                  }
               } else {
                  byte3 = (byte)(state >> 16);
               }

               if (byte3 == 0) {
                  byte3 = buffer.get(index++);
                  if (index >= limit) {
                     return Utf8.incompleteStateFor(byte1, byte2x, byte3);
                  }
               }

               if (byte2x > -65 || (byte1 << 28) + (byte2x - -112) >> 30 != 0 || byte3 > -65 || buffer.get(index++) > -65) {
                  return -1;
               }
            }
         }

         return partialIsValidUtf8(buffer, index, limit);
      }

      private static int partialIsValidUtf8(final ByteBuffer buffer, int index, final int limit) {
         index += Utf8.estimateConsecutiveAscii(buffer, index, limit);

         while (index < limit) {
            int byte1;
            if ((byte1 = buffer.get(index++)) < 0) {
               if (byte1 < -32) {
                  if (index >= limit) {
                     return byte1;
                  }

                  if (byte1 < -62 || buffer.get(index) > -65) {
                     return -1;
                  }

                  index++;
               } else if (byte1 < -16) {
                  if (index >= limit - 1) {
                     return Utf8.incompleteStateFor(buffer, byte1, index, limit - index);
                  }

                  byte byte2 = buffer.get(index++);
                  if (byte2 > -65 || byte1 == -32 && byte2 < -96 || byte1 == -19 && byte2 >= -96 || buffer.get(index) > -65) {
                     return -1;
                  }

                  index++;
               } else {
                  if (index >= limit - 2) {
                     return Utf8.incompleteStateFor(buffer, byte1, index, limit - index);
                  }

                  int byte2 = buffer.get(index++);
                  if (byte2 > -65 || (byte1 << 28) + (byte2 - -112) >> 30 != 0 || buffer.get(index++) > -65 || buffer.get(index++) > -65) {
                     return -1;
                  }
               }
            }
         }

         return 0;
      }

      abstract String decodeUtf8(byte[] bytes, int index, int size) throws InvalidProtocolBufferException;

      final String decodeUtf8(ByteBuffer buffer, int index, int size) throws InvalidProtocolBufferException {
         if (buffer.hasArray()) {
            int offset = buffer.arrayOffset();
            return this.decodeUtf8(buffer.array(), offset + index, size);
         } else {
            return buffer.isDirect() ? this.decodeUtf8Direct(buffer, index, size) : this.decodeUtf8Default(buffer, index, size);
         }
      }

      abstract String decodeUtf8Direct(ByteBuffer buffer, int index, int size) throws InvalidProtocolBufferException;

      final String decodeUtf8Default(ByteBuffer buffer, int index, int size) throws InvalidProtocolBufferException {
         if ((index | size | buffer.limit() - index - size) < 0) {
            throw new ArrayIndexOutOfBoundsException(String.format("buffer limit=%d, index=%d, limit=%d", buffer.limit(), index, size));
         } else {
            int offset = index;
            int limit = index + size;
            char[] resultArr = new char[size];
            int resultPos = 0;

            while (offset < limit) {
               byte b = buffer.get(offset);
               if (!Utf8.DecodeUtil.isOneByte(b)) {
                  break;
               }

               offset++;
               Utf8.DecodeUtil.handleOneByte(b, resultArr, resultPos++);
            }

            while (offset < limit) {
               byte byte1 = buffer.get(offset++);
               if (Utf8.DecodeUtil.isOneByte(byte1)) {
                  Utf8.DecodeUtil.handleOneByte(byte1, resultArr, resultPos++);

                  while (offset < limit) {
                     byte b = buffer.get(offset);
                     if (!Utf8.DecodeUtil.isOneByte(b)) {
                        break;
                     }

                     offset++;
                     Utf8.DecodeUtil.handleOneByte(b, resultArr, resultPos++);
                  }
               } else if (Utf8.DecodeUtil.isTwoBytes(byte1)) {
                  if (offset >= limit) {
                     throw InvalidProtocolBufferException.invalidUtf8();
                  }

                  Utf8.DecodeUtil.handleTwoBytes(byte1, buffer.get(offset++), resultArr, resultPos++);
               } else if (Utf8.DecodeUtil.isThreeBytes(byte1)) {
                  if (offset >= limit - 1) {
                     throw InvalidProtocolBufferException.invalidUtf8();
                  }

                  Utf8.DecodeUtil.handleThreeBytes(byte1, buffer.get(offset++), buffer.get(offset++), resultArr, resultPos++);
               } else {
                  if (offset >= limit - 2) {
                     throw InvalidProtocolBufferException.invalidUtf8();
                  }

                  Utf8.DecodeUtil.handleFourBytes(byte1, buffer.get(offset++), buffer.get(offset++), buffer.get(offset++), resultArr, resultPos++);
                  resultPos++;
               }
            }

            return new String(resultArr, 0, resultPos);
         }
      }

      abstract int encodeUtf8(String in, byte[] out, int offset, int length);

      final void encodeUtf8(String in, ByteBuffer out) {
         if (out.hasArray()) {
            int offset = out.arrayOffset();
            int endIndex = Utf8.encode(in, out.array(), offset + out.position(), out.remaining());
            Java8Compatibility.position(out, endIndex - offset);
         } else if (out.isDirect()) {
            this.encodeUtf8Direct(in, out);
         } else {
            this.encodeUtf8Default(in, out);
         }
      }

      abstract void encodeUtf8Direct(String in, ByteBuffer out);

      final void encodeUtf8Default(String in, ByteBuffer out) {
         int inLength = in.length();
         int outIx = out.position();
         int inIx = 0;

         try {
            char c;
            while (inIx < inLength && (c = in.charAt(inIx)) < 128) {
               out.put(outIx + inIx, (byte)c);
               inIx++;
            }

            if (inIx == inLength) {
               Java8Compatibility.position(out, outIx + inIx);
            } else {
               for (outIx += inIx; inIx < inLength; outIx++) {
                  c = in.charAt(inIx);
                  if (c < 128) {
                     out.put(outIx, (byte)c);
                  } else if (c < 2048) {
                     out.put(outIx++, (byte)(192 | c >>> 6));
                     out.put(outIx, (byte)(128 | '?' & c));
                  } else if (c >= '\ud800' && '\udfff' >= c) {
                     char low;
                     if (inIx + 1 == inLength || !Character.isSurrogatePair(c, low = in.charAt(++inIx))) {
                        throw new Utf8.UnpairedSurrogateException(inIx, inLength);
                     }

                     int codePoint = Character.toCodePoint(c, low);
                     out.put(outIx++, (byte)(240 | codePoint >>> 18));
                     out.put(outIx++, (byte)(128 | 63 & codePoint >>> 12));
                     out.put(outIx++, (byte)(128 | 63 & codePoint >>> 6));
                     out.put(outIx, (byte)(128 | 63 & codePoint));
                  } else {
                     out.put(outIx++, (byte)(224 | c >>> '\f'));
                     out.put(outIx++, (byte)(128 | 63 & c >>> 6));
                     out.put(outIx, (byte)(128 | '?' & c));
                  }

                  inIx++;
               }

               Java8Compatibility.position(out, outIx);
            }
         } catch (IndexOutOfBoundsException var9) {
            throw new ArrayIndexOutOfBoundsException("Not enough space in output buffer to encode UTF-8 string");
         }
      }
   }

   static final class SafeProcessor extends Utf8.Processor {
      @Override
      int partialIsValidUtf8(int state, byte[] bytes, int index, int limit) {
         if (state != 0) {
            if (index >= limit) {
               return state;
            }

            int byte1 = (byte)state;
            if (byte1 < -32) {
               if (byte1 < -62 || bytes[index++] > -65) {
                  return -1;
               }
            } else if (byte1 < -16) {
               int byte2 = (byte)(~(state >> 8));
               if (byte2 == 0) {
                  byte2 = bytes[index++];
                  if (index >= limit) {
                     return Utf8.incompleteStateFor(byte1, byte2);
                  }
               }

               if (byte2 > -65 || byte1 == -32 && byte2 < -96 || byte1 == -19 && byte2 >= -96 || bytes[index++] > -65) {
                  return -1;
               }
            } else {
               int byte2x = (byte)(~(state >> 8));
               int byte3 = 0;
               if (byte2x == 0) {
                  byte2x = bytes[index++];
                  if (index >= limit) {
                     return Utf8.incompleteStateFor(byte1, byte2x);
                  }
               } else {
                  byte3 = (byte)(state >> 16);
               }

               if (byte3 == 0) {
                  byte3 = bytes[index++];
                  if (index >= limit) {
                     return Utf8.incompleteStateFor(byte1, byte2x, byte3);
                  }
               }

               if (byte2x > -65 || (byte1 << 28) + (byte2x - -112) >> 30 != 0 || byte3 > -65 || bytes[index++] > -65) {
                  return -1;
               }
            }
         }

         return partialIsValidUtf8(bytes, index, limit);
      }

      @Override
      int partialIsValidUtf8Direct(int state, ByteBuffer buffer, int index, int limit) {
         return this.partialIsValidUtf8Default(state, buffer, index, limit);
      }

      @Override
      String decodeUtf8(byte[] bytes, int index, int size) throws InvalidProtocolBufferException {
         if ((index | size | bytes.length - index - size) < 0) {
            throw new ArrayIndexOutOfBoundsException(String.format("buffer length=%d, index=%d, size=%d", bytes.length, index, size));
         } else {
            int offset = index;
            int limit = index + size;
            char[] resultArr = new char[size];
            int resultPos = 0;

            while (offset < limit) {
               byte b = bytes[offset];
               if (!Utf8.DecodeUtil.isOneByte(b)) {
                  break;
               }

               offset++;
               Utf8.DecodeUtil.handleOneByte(b, resultArr, resultPos++);
            }

            while (offset < limit) {
               byte byte1 = bytes[offset++];
               if (Utf8.DecodeUtil.isOneByte(byte1)) {
                  Utf8.DecodeUtil.handleOneByte(byte1, resultArr, resultPos++);

                  while (offset < limit) {
                     byte b = bytes[offset];
                     if (!Utf8.DecodeUtil.isOneByte(b)) {
                        break;
                     }

                     offset++;
                     Utf8.DecodeUtil.handleOneByte(b, resultArr, resultPos++);
                  }
               } else if (Utf8.DecodeUtil.isTwoBytes(byte1)) {
                  if (offset >= limit) {
                     throw InvalidProtocolBufferException.invalidUtf8();
                  }

                  Utf8.DecodeUtil.handleTwoBytes(byte1, bytes[offset++], resultArr, resultPos++);
               } else if (Utf8.DecodeUtil.isThreeBytes(byte1)) {
                  if (offset >= limit - 1) {
                     throw InvalidProtocolBufferException.invalidUtf8();
                  }

                  Utf8.DecodeUtil.handleThreeBytes(byte1, bytes[offset++], bytes[offset++], resultArr, resultPos++);
               } else {
                  if (offset >= limit - 2) {
                     throw InvalidProtocolBufferException.invalidUtf8();
                  }

                  Utf8.DecodeUtil.handleFourBytes(byte1, bytes[offset++], bytes[offset++], bytes[offset++], resultArr, resultPos++);
                  resultPos++;
               }
            }

            return new String(resultArr, 0, resultPos);
         }
      }

      @Override
      String decodeUtf8Direct(ByteBuffer buffer, int index, int size) throws InvalidProtocolBufferException {
         return this.decodeUtf8Default(buffer, index, size);
      }

      @Override
      int encodeUtf8(String in, byte[] out, int offset, int length) {
         int utf16Length = in.length();
         int j = offset;
         int i = 0;

         int limit;
         char c;
         for (limit = offset + length; i < utf16Length && i + j < limit && (c = in.charAt(i)) < 128; i++) {
            out[j + i] = (byte)c;
         }

         if (i == utf16Length) {
            return j + utf16Length;
         } else {
            for (j += i; i < utf16Length; i++) {
               c = in.charAt(i);
               if (c < 128 && j < limit) {
                  out[j++] = (byte)c;
               } else if (c < 2048 && j <= limit - 2) {
                  out[j++] = (byte)(960 | c >>> 6);
                  out[j++] = (byte)(128 | '?' & c);
               } else if ((c < '\ud800' || '\udfff' < c) && j <= limit - 3) {
                  out[j++] = (byte)(480 | c >>> '\f');
                  out[j++] = (byte)(128 | 63 & c >>> 6);
                  out[j++] = (byte)(128 | '?' & c);
               } else {
                  if (j > limit - 4) {
                     if ('\ud800' > c || c > '\udfff' || i + 1 != in.length() && Character.isSurrogatePair(c, in.charAt(i + 1))) {
                        throw new ArrayIndexOutOfBoundsException("Not enough space in output buffer to encode UTF-8 string");
                     }

                     throw new Utf8.UnpairedSurrogateException(i, utf16Length);
                  }

                  char low;
                  if (i + 1 == in.length() || !Character.isSurrogatePair(c, low = in.charAt(++i))) {
                     throw new Utf8.UnpairedSurrogateException(i - 1, utf16Length);
                  }

                  int codePoint = Character.toCodePoint(c, low);
                  out[j++] = (byte)(240 | codePoint >>> 18);
                  out[j++] = (byte)(128 | 63 & codePoint >>> 12);
                  out[j++] = (byte)(128 | 63 & codePoint >>> 6);
                  out[j++] = (byte)(128 | 63 & codePoint);
               }
            }

            return j;
         }
      }

      @Override
      void encodeUtf8Direct(String in, ByteBuffer out) {
         this.encodeUtf8Default(in, out);
      }

      private static int partialIsValidUtf8(byte[] bytes, int index, int limit) {
         while (index < limit && bytes[index] >= 0) {
            index++;
         }

         return index >= limit ? 0 : partialIsValidUtf8NonAscii(bytes, index, limit);
      }

      private static int partialIsValidUtf8NonAscii(byte[] bytes, int index, int limit) {
         while (index < limit) {
            int byte1;
            if ((byte1 = bytes[index++]) < 0) {
               if (byte1 < -32) {
                  if (index >= limit) {
                     return byte1;
                  }

                  if (byte1 < -62 || bytes[index++] > -65) {
                     return -1;
                  }
               } else if (byte1 < -16) {
                  if (index >= limit - 1) {
                     return Utf8.incompleteStateFor(bytes, index, limit);
                  }

                  int byte2;
                  if ((byte2 = bytes[index++]) > -65 || byte1 == -32 && byte2 < -96 || byte1 == -19 && byte2 >= -96 || bytes[index++] > -65) {
                     return -1;
                  }
               } else {
                  if (index >= limit - 2) {
                     return Utf8.incompleteStateFor(bytes, index, limit);
                  }

                  int byte2;
                  if ((byte2 = bytes[index++]) > -65 || (byte1 << 28) + (byte2 - -112) >> 30 != 0 || bytes[index++] > -65 || bytes[index++] > -65) {
                     return -1;
                  }
               }
            }
         }

         return 0;
      }
   }

   static class UnpairedSurrogateException extends IllegalArgumentException {
      UnpairedSurrogateException(int index, int length) {
         super("Unpaired surrogate at index " + index + " of " + length);
      }
   }

   static final class UnsafeProcessor extends Utf8.Processor {
      static boolean isAvailable() {
         return UnsafeUtil.hasUnsafeArrayOperations() && UnsafeUtil.hasUnsafeByteBufferOperations();
      }

      @Override
      int partialIsValidUtf8(int state, byte[] bytes, final int index, final int limit) {
         if ((index | limit | bytes.length - limit) < 0) {
            throw new ArrayIndexOutOfBoundsException(String.format("Array length=%d, index=%d, limit=%d", bytes.length, index, limit));
         } else {
            long offset = index;
            long offsetLimit = limit;
            if (state != 0) {
               if (offset >= offsetLimit) {
                  return state;
               }

               int byte1 = (byte)state;
               if (byte1 < -32) {
                  if (byte1 < -62 || UnsafeUtil.getByte(bytes, offset++) > -65) {
                     return -1;
                  }
               } else if (byte1 < -16) {
                  int byte2 = (byte)(~(state >> 8));
                  if (byte2 == 0) {
                     byte2 = UnsafeUtil.getByte(bytes, offset++);
                     if (offset >= offsetLimit) {
                        return Utf8.incompleteStateFor(byte1, byte2);
                     }
                  }

                  if (byte2 > -65 || byte1 == -32 && byte2 < -96 || byte1 == -19 && byte2 >= -96 || UnsafeUtil.getByte(bytes, offset++) > -65) {
                     return -1;
                  }
               } else {
                  int byte2x = (byte)(~(state >> 8));
                  int byte3 = 0;
                  if (byte2x == 0) {
                     byte2x = UnsafeUtil.getByte(bytes, offset++);
                     if (offset >= offsetLimit) {
                        return Utf8.incompleteStateFor(byte1, byte2x);
                     }
                  } else {
                     byte3 = (byte)(state >> 16);
                  }

                  if (byte3 == 0) {
                     byte3 = UnsafeUtil.getByte(bytes, offset++);
                     if (offset >= offsetLimit) {
                        return Utf8.incompleteStateFor(byte1, byte2x, byte3);
                     }
                  }

                  if (byte2x > -65 || (byte1 << 28) + (byte2x - -112) >> 30 != 0 || byte3 > -65 || UnsafeUtil.getByte(bytes, offset++) > -65) {
                     return -1;
                  }
               }
            }

            return partialIsValidUtf8(bytes, offset, (int)(offsetLimit - offset));
         }
      }

      @Override
      int partialIsValidUtf8Direct(final int state, ByteBuffer buffer, final int index, final int limit) {
         if ((index | limit | buffer.limit() - limit) < 0) {
            throw new ArrayIndexOutOfBoundsException(String.format("buffer limit=%d, index=%d, limit=%d", buffer.limit(), index, limit));
         } else {
            long address = UnsafeUtil.addressOffset(buffer) + index;
            long addressLimit = address + (limit - index);
            if (state != 0) {
               if (address >= addressLimit) {
                  return state;
               }

               int byte1 = (byte)state;
               if (byte1 < -32) {
                  if (byte1 < -62 || UnsafeUtil.getByte(address++) > -65) {
                     return -1;
                  }
               } else if (byte1 < -16) {
                  int byte2 = (byte)(~(state >> 8));
                  if (byte2 == 0) {
                     byte2 = UnsafeUtil.getByte(address++);
                     if (address >= addressLimit) {
                        return Utf8.incompleteStateFor(byte1, byte2);
                     }
                  }

                  if (byte2 > -65 || byte1 == -32 && byte2 < -96 || byte1 == -19 && byte2 >= -96 || UnsafeUtil.getByte(address++) > -65) {
                     return -1;
                  }
               } else {
                  int byte2x = (byte)(~(state >> 8));
                  int byte3 = 0;
                  if (byte2x == 0) {
                     byte2x = UnsafeUtil.getByte(address++);
                     if (address >= addressLimit) {
                        return Utf8.incompleteStateFor(byte1, byte2x);
                     }
                  } else {
                     byte3 = (byte)(state >> 16);
                  }

                  if (byte3 == 0) {
                     byte3 = UnsafeUtil.getByte(address++);
                     if (address >= addressLimit) {
                        return Utf8.incompleteStateFor(byte1, byte2x, byte3);
                     }
                  }

                  if (byte2x > -65 || (byte1 << 28) + (byte2x - -112) >> 30 != 0 || byte3 > -65 || UnsafeUtil.getByte(address++) > -65) {
                     return -1;
                  }
               }
            }

            return partialIsValidUtf8(address, (int)(addressLimit - address));
         }
      }

      @Override
      String decodeUtf8(byte[] bytes, int index, int size) throws InvalidProtocolBufferException {
         String s = new String(bytes, index, size, Internal.UTF_8);
         if (s.indexOf(65533) < 0) {
            return s;
         } else if (Arrays.equals(s.getBytes(Internal.UTF_8), Arrays.copyOfRange(bytes, index, index + size))) {
            return s;
         } else {
            throw InvalidProtocolBufferException.invalidUtf8();
         }
      }

      @Override
      String decodeUtf8Direct(ByteBuffer buffer, int index, int size) throws InvalidProtocolBufferException {
         if ((index | size | buffer.limit() - index - size) < 0) {
            throw new ArrayIndexOutOfBoundsException(String.format("buffer limit=%d, index=%d, limit=%d", buffer.limit(), index, size));
         } else {
            long address = UnsafeUtil.addressOffset(buffer) + index;
            long addressLimit = address + size;
            char[] resultArr = new char[size];
            int resultPos = 0;

            while (address < addressLimit) {
               byte b = UnsafeUtil.getByte(address);
               if (!Utf8.DecodeUtil.isOneByte(b)) {
                  break;
               }

               address++;
               Utf8.DecodeUtil.handleOneByte(b, resultArr, resultPos++);
            }

            while (address < addressLimit) {
               byte byte1 = UnsafeUtil.getByte(address++);
               if (Utf8.DecodeUtil.isOneByte(byte1)) {
                  Utf8.DecodeUtil.handleOneByte(byte1, resultArr, resultPos++);

                  while (address < addressLimit) {
                     byte b = UnsafeUtil.getByte(address);
                     if (!Utf8.DecodeUtil.isOneByte(b)) {
                        break;
                     }

                     address++;
                     Utf8.DecodeUtil.handleOneByte(b, resultArr, resultPos++);
                  }
               } else if (Utf8.DecodeUtil.isTwoBytes(byte1)) {
                  if (address >= addressLimit) {
                     throw InvalidProtocolBufferException.invalidUtf8();
                  }

                  Utf8.DecodeUtil.handleTwoBytes(byte1, UnsafeUtil.getByte(address++), resultArr, resultPos++);
               } else if (Utf8.DecodeUtil.isThreeBytes(byte1)) {
                  if (address >= addressLimit - 1L) {
                     throw InvalidProtocolBufferException.invalidUtf8();
                  }

                  Utf8.DecodeUtil.handleThreeBytes(byte1, UnsafeUtil.getByte(address++), UnsafeUtil.getByte(address++), resultArr, resultPos++);
               } else {
                  if (address >= addressLimit - 2L) {
                     throw InvalidProtocolBufferException.invalidUtf8();
                  }

                  Utf8.DecodeUtil.handleFourBytes(
                     byte1, UnsafeUtil.getByte(address++), UnsafeUtil.getByte(address++), UnsafeUtil.getByte(address++), resultArr, resultPos++
                  );
                  resultPos++;
               }
            }

            return new String(resultArr, 0, resultPos);
         }
      }

      @Override
      int encodeUtf8(final String in, final byte[] out, final int offset, final int length) {
         long outIx = offset;
         long outLimit = outIx + length;
         int inLimit = in.length();
         if (inLimit <= length && out.length - length >= offset) {
            int inIx;
            char c;
            for (inIx = 0; inIx < inLimit && (c = in.charAt(inIx)) < 128; inIx++) {
               UnsafeUtil.putByte(out, outIx++, (byte)c);
            }

            if (inIx == inLimit) {
               return (int)outIx;
            } else {
               for (; inIx < inLimit; inIx++) {
                  c = in.charAt(inIx);
                  if (c < 128 && outIx < outLimit) {
                     UnsafeUtil.putByte(out, outIx++, (byte)c);
                  } else if (c < 2048 && outIx <= outLimit - 2L) {
                     UnsafeUtil.putByte(out, outIx++, (byte)(960 | c >>> 6));
                     UnsafeUtil.putByte(out, outIx++, (byte)(128 | '?' & c));
                  } else if ((c < '\ud800' || '\udfff' < c) && outIx <= outLimit - 3L) {
                     UnsafeUtil.putByte(out, outIx++, (byte)(480 | c >>> '\f'));
                     UnsafeUtil.putByte(out, outIx++, (byte)(128 | 63 & c >>> 6));
                     UnsafeUtil.putByte(out, outIx++, (byte)(128 | '?' & c));
                  } else {
                     if (outIx > outLimit - 4L) {
                        if ('\ud800' > c || c > '\udfff' || inIx + 1 != inLimit && Character.isSurrogatePair(c, in.charAt(inIx + 1))) {
                           throw new ArrayIndexOutOfBoundsException("Not enough space in output buffer to encode UTF-8 string");
                        }

                        throw new Utf8.UnpairedSurrogateException(inIx, inLimit);
                     }

                     char low;
                     if (inIx + 1 == inLimit || !Character.isSurrogatePair(c, low = in.charAt(++inIx))) {
                        throw new Utf8.UnpairedSurrogateException(inIx - 1, inLimit);
                     }

                     int codePoint = Character.toCodePoint(c, low);
                     UnsafeUtil.putByte(out, outIx++, (byte)(240 | codePoint >>> 18));
                     UnsafeUtil.putByte(out, outIx++, (byte)(128 | 63 & codePoint >>> 12));
                     UnsafeUtil.putByte(out, outIx++, (byte)(128 | 63 & codePoint >>> 6));
                     UnsafeUtil.putByte(out, outIx++, (byte)(128 | 63 & codePoint));
                  }
               }

               return (int)outIx;
            }
         } else {
            throw new ArrayIndexOutOfBoundsException("Not enough space in output buffer to encode UTF-8 string");
         }
      }

      @Override
      void encodeUtf8Direct(String in, ByteBuffer out) {
         long address = UnsafeUtil.addressOffset(out);
         long outIx = address + out.position();
         long outLimit = address + out.limit();
         int inLimit = in.length();
         if (inLimit > outLimit - outIx) {
            throw new ArrayIndexOutOfBoundsException("Not enough space in output buffer to encode UTF-8 string");
         } else {
            int inIx;
            char c;
            for (inIx = 0; inIx < inLimit && (c = in.charAt(inIx)) < 128; inIx++) {
               UnsafeUtil.putByte(outIx++, (byte)c);
            }

            if (inIx == inLimit) {
               Java8Compatibility.position(out, (int)(outIx - address));
            } else {
               for (; inIx < inLimit; inIx++) {
                  c = in.charAt(inIx);
                  if (c < 128 && outIx < outLimit) {
                     UnsafeUtil.putByte(outIx++, (byte)c);
                  } else if (c < 2048 && outIx <= outLimit - 2L) {
                     UnsafeUtil.putByte(outIx++, (byte)(960 | c >>> 6));
                     UnsafeUtil.putByte(outIx++, (byte)(128 | '?' & c));
                  } else if ((c < '\ud800' || '\udfff' < c) && outIx <= outLimit - 3L) {
                     UnsafeUtil.putByte(outIx++, (byte)(480 | c >>> '\f'));
                     UnsafeUtil.putByte(outIx++, (byte)(128 | 63 & c >>> 6));
                     UnsafeUtil.putByte(outIx++, (byte)(128 | '?' & c));
                  } else {
                     if (outIx > outLimit - 4L) {
                        if ('\ud800' > c || c > '\udfff' || inIx + 1 != inLimit && Character.isSurrogatePair(c, in.charAt(inIx + 1))) {
                           throw new ArrayIndexOutOfBoundsException("Not enough space in output buffer to encode UTF-8 string");
                        }

                        throw new Utf8.UnpairedSurrogateException(inIx, inLimit);
                     }

                     char low;
                     if (inIx + 1 == inLimit || !Character.isSurrogatePair(c, low = in.charAt(++inIx))) {
                        throw new Utf8.UnpairedSurrogateException(inIx - 1, inLimit);
                     }

                     int codePoint = Character.toCodePoint(c, low);
                     UnsafeUtil.putByte(outIx++, (byte)(240 | codePoint >>> 18));
                     UnsafeUtil.putByte(outIx++, (byte)(128 | 63 & codePoint >>> 12));
                     UnsafeUtil.putByte(outIx++, (byte)(128 | 63 & codePoint >>> 6));
                     UnsafeUtil.putByte(outIx++, (byte)(128 | 63 & codePoint));
                  }
               }

               Java8Compatibility.position(out, (int)(outIx - address));
            }
         }
      }

      private static int unsafeEstimateConsecutiveAscii(byte[] bytes, long offset, final int maxChars) {
         if (maxChars < 16) {
            return 0;
         } else {
            int unaligned = 8 - ((int)offset & 7);

            int i;
            for (i = 0; i < unaligned; i++) {
               if (UnsafeUtil.getByte(bytes, offset++) < 0) {
                  return i;
               }
            }

            while (i + 8 <= maxChars && (UnsafeUtil.getLong(bytes, UnsafeUtil.BYTE_ARRAY_BASE_OFFSET + offset) & -9187201950435737472L) == 0L) {
               offset += 8L;
               i += 8;
            }

            while (i < maxChars) {
               if (UnsafeUtil.getByte(bytes, offset++) < 0) {
                  return i;
               }

               i++;
            }

            return maxChars;
         }
      }

      private static int unsafeEstimateConsecutiveAscii(long address, final int maxChars) {
         if (maxChars < 16) {
            return 0;
         } else {
            int unaligned = (int)(-address & 7L);

            for (int j = unaligned; j > 0; j--) {
               if (UnsafeUtil.getByte(address++) < 0) {
                  return unaligned - j;
               }
            }

            int remaining;
            for (remaining = maxChars - unaligned; remaining >= 8 && (UnsafeUtil.getLong(address) & -9187201950435737472L) == 0L; remaining -= 8) {
               address += 8L;
            }

            return maxChars - remaining;
         }
      }

      private static int partialIsValidUtf8(final byte[] bytes, long offset, int remaining) {
         int skipped = unsafeEstimateConsecutiveAscii(bytes, offset, remaining);
         remaining -= skipped;
         offset += skipped;

         while (true) {
            int byte1 = 0;

            while (remaining > 0 && (byte1 = UnsafeUtil.getByte(bytes, offset++)) >= 0) {
               remaining--;
            }

            if (remaining == 0) {
               return 0;
            }

            remaining--;
            if (byte1 < -32) {
               if (remaining == 0) {
                  return byte1;
               }

               remaining--;
               if (byte1 < -62 || UnsafeUtil.getByte(bytes, offset++) > -65) {
                  return -1;
               }
            } else if (byte1 < -16) {
               if (remaining < 2) {
                  return unsafeIncompleteStateFor(bytes, byte1, offset, remaining);
               }

               remaining -= 2;
               int byte2;
               if ((byte2 = UnsafeUtil.getByte(bytes, offset++)) > -65
                  || byte1 == -32 && byte2 < -96
                  || byte1 == -19 && byte2 >= -96
                  || UnsafeUtil.getByte(bytes, offset++) > -65) {
                  return -1;
               }
            } else {
               if (remaining < 3) {
                  return unsafeIncompleteStateFor(bytes, byte1, offset, remaining);
               }

               remaining -= 3;
               int byte2;
               if ((byte2 = UnsafeUtil.getByte(bytes, offset++)) > -65
                  || (byte1 << 28) + (byte2 - -112) >> 30 != 0
                  || UnsafeUtil.getByte(bytes, offset++) > -65
                  || UnsafeUtil.getByte(bytes, offset++) > -65) {
                  return -1;
               }
            }
         }
      }

      private static int partialIsValidUtf8(long address, int remaining) {
         int skipped = unsafeEstimateConsecutiveAscii(address, remaining);
         address += skipped;
         remaining -= skipped;

         while (true) {
            int byte1 = 0;

            while (remaining > 0 && (byte1 = UnsafeUtil.getByte(address++)) >= 0) {
               remaining--;
            }

            if (remaining == 0) {
               return 0;
            }

            remaining--;
            if (byte1 < -32) {
               if (remaining == 0) {
                  return byte1;
               }

               remaining--;
               if (byte1 < -62 || UnsafeUtil.getByte(address++) > -65) {
                  return -1;
               }
            } else if (byte1 < -16) {
               if (remaining < 2) {
                  return unsafeIncompleteStateFor(address, byte1, remaining);
               }

               remaining -= 2;
               byte byte2 = UnsafeUtil.getByte(address++);
               if (byte2 > -65 || byte1 == -32 && byte2 < -96 || byte1 == -19 && byte2 >= -96 || UnsafeUtil.getByte(address++) > -65) {
                  return -1;
               }
            } else {
               if (remaining < 3) {
                  return unsafeIncompleteStateFor(address, byte1, remaining);
               }

               remaining -= 3;
               byte byte2 = UnsafeUtil.getByte(address++);
               if (byte2 > -65 || (byte1 << 28) + (byte2 - -112) >> 30 != 0 || UnsafeUtil.getByte(address++) > -65 || UnsafeUtil.getByte(address++) > -65) {
                  return -1;
               }
            }
         }
      }

      private static int unsafeIncompleteStateFor(byte[] bytes, int byte1, long offset, int remaining) {
         switch (remaining) {
            case 0:
               return Utf8.incompleteStateFor(byte1);
            case 1:
               return Utf8.incompleteStateFor(byte1, UnsafeUtil.getByte(bytes, offset));
            case 2:
               return Utf8.incompleteStateFor(byte1, UnsafeUtil.getByte(bytes, offset), UnsafeUtil.getByte(bytes, offset + 1L));
            default:
               throw new AssertionError();
         }
      }

      private static int unsafeIncompleteStateFor(long address, final int byte1, int remaining) {
         switch (remaining) {
            case 0:
               return Utf8.incompleteStateFor(byte1);
            case 1:
               return Utf8.incompleteStateFor(byte1, UnsafeUtil.getByte(address));
            case 2:
               return Utf8.incompleteStateFor(byte1, UnsafeUtil.getByte(address), UnsafeUtil.getByte(address + 1L));
            default:
               throw new AssertionError();
         }
      }
   }
}
