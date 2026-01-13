package io.netty.util;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SWARUtil;

final class AsciiStringUtil {
   static AsciiString toLowerCase(AsciiString string) {
      byte[] byteArray = string.array();
      int offset = string.arrayOffset();
      int length = string.length();
      if (!containsUpperCase(byteArray, offset, length)) {
         return string;
      } else {
         byte[] newByteArray = PlatformDependent.allocateUninitializedArray(length);
         toLowerCase(byteArray, offset, newByteArray);
         return new AsciiString(newByteArray, false);
      }
   }

   private static boolean containsUpperCase(byte[] byteArray, int offset, int length) {
      if (!PlatformDependent.isUnaligned()) {
         return linearContainsUpperCase(byteArray, offset, length);
      } else {
         int longCount = length >>> 3;

         for (int i = 0; i < longCount; i++) {
            long word = PlatformDependent.getLong(byteArray, offset);
            if (SWARUtil.containsUpperCase(word)) {
               return true;
            }

            offset += 8;
         }

         return unrolledContainsUpperCase(byteArray, offset, length & 7);
      }
   }

   private static boolean linearContainsUpperCase(byte[] byteArray, int offset, int length) {
      int end = offset + length;

      for (int idx = offset; idx < end; idx++) {
         if (isUpperCase(byteArray[idx])) {
            return true;
         }
      }

      return false;
   }

   private static boolean unrolledContainsUpperCase(byte[] byteArray, int offset, int byteCount) {
      assert byteCount >= 0 && byteCount < 8;

      if ((byteCount & 4) != 0) {
         int word = PlatformDependent.getInt(byteArray, offset);
         if (SWARUtil.containsUpperCase(word)) {
            return true;
         }

         offset += 4;
      }

      if ((byteCount & 2) != 0) {
         if (isUpperCase(PlatformDependent.getByte(byteArray, offset))) {
            return true;
         }

         if (isUpperCase(PlatformDependent.getByte(byteArray, offset + 1))) {
            return true;
         }

         offset += 2;
      }

      return (byteCount & 1) != 0 ? isUpperCase(PlatformDependent.getByte(byteArray, offset)) : false;
   }

   private static void toLowerCase(byte[] src, int srcOffset, byte[] dst) {
      if (!PlatformDependent.isUnaligned()) {
         linearToLowerCase(src, srcOffset, dst);
      } else {
         int length = dst.length;
         int longCount = length >>> 3;
         int offset = 0;

         for (int i = 0; i < longCount; i++) {
            long word = PlatformDependent.getLong(src, srcOffset + offset);
            PlatformDependent.putLong(dst, offset, SWARUtil.toLowerCase(word));
            offset += 8;
         }

         unrolledToLowerCase(src, srcOffset + offset, dst, offset, length & 7);
      }
   }

   private static void linearToLowerCase(byte[] src, int srcOffset, byte[] dst) {
      for (int i = 0; i < dst.length; i++) {
         dst[i] = toLowerCase(src[srcOffset + i]);
      }
   }

   private static void unrolledToLowerCase(byte[] src, int srcPos, byte[] dst, int dstOffset, int byteCount) {
      assert byteCount >= 0 && byteCount < 8;

      int offset = 0;
      if ((byteCount & 4) != 0) {
         int word = PlatformDependent.getInt(src, srcPos + offset);
         PlatformDependent.putInt(dst, dstOffset + offset, SWARUtil.toLowerCase(word));
         offset += 4;
      }

      if ((byteCount & 2) != 0) {
         short word = PlatformDependent.getShort(src, srcPos + offset);
         short result = (short)(toLowerCase((byte)(word >>> 8)) << 8 | toLowerCase((byte)word));
         PlatformDependent.putShort(dst, dstOffset + offset, result);
         offset += 2;
      }

      if ((byteCount & 1) != 0) {
         PlatformDependent.putByte(dst, dstOffset + offset, toLowerCase(PlatformDependent.getByte(src, srcPos + offset)));
      }
   }

   static AsciiString toUpperCase(AsciiString string) {
      byte[] byteArray = string.array();
      int offset = string.arrayOffset();
      int length = string.length();
      if (!containsLowerCase(byteArray, offset, length)) {
         return string;
      } else {
         byte[] newByteArray = PlatformDependent.allocateUninitializedArray(length);
         toUpperCase(byteArray, offset, newByteArray);
         return new AsciiString(newByteArray, false);
      }
   }

   private static boolean containsLowerCase(byte[] byteArray, int offset, int length) {
      if (!PlatformDependent.isUnaligned()) {
         return linearContainsLowerCase(byteArray, offset, length);
      } else {
         int longCount = length >>> 3;

         for (int i = 0; i < longCount; i++) {
            long word = PlatformDependent.getLong(byteArray, offset);
            if (SWARUtil.containsLowerCase(word)) {
               return true;
            }

            offset += 8;
         }

         return unrolledContainsLowerCase(byteArray, offset, length & 7);
      }
   }

   private static boolean linearContainsLowerCase(byte[] byteArray, int offset, int length) {
      int end = offset + length;

      for (int idx = offset; idx < end; idx++) {
         if (isLowerCase(byteArray[idx])) {
            return true;
         }
      }

      return false;
   }

   private static boolean unrolledContainsLowerCase(byte[] byteArray, int offset, int byteCount) {
      assert byteCount >= 0 && byteCount < 8;

      if ((byteCount & 4) != 0) {
         int word = PlatformDependent.getInt(byteArray, offset);
         if (SWARUtil.containsLowerCase(word)) {
            return true;
         }

         offset += 4;
      }

      if ((byteCount & 2) != 0) {
         if (isLowerCase(PlatformDependent.getByte(byteArray, offset))) {
            return true;
         }

         if (isLowerCase(PlatformDependent.getByte(byteArray, offset + 1))) {
            return true;
         }

         offset += 2;
      }

      return (byteCount & 1) != 0 ? isLowerCase(PlatformDependent.getByte(byteArray, offset)) : false;
   }

   private static void toUpperCase(byte[] src, int srcOffset, byte[] dst) {
      if (!PlatformDependent.isUnaligned()) {
         linearToUpperCase(src, srcOffset, dst);
      } else {
         int length = dst.length;
         int longCount = length >>> 3;
         int offset = 0;

         for (int i = 0; i < longCount; i++) {
            long word = PlatformDependent.getLong(src, srcOffset + offset);
            PlatformDependent.putLong(dst, offset, SWARUtil.toUpperCase(word));
            offset += 8;
         }

         unrolledToUpperCase(src, srcOffset + offset, dst, offset, length & 7);
      }
   }

   private static void linearToUpperCase(byte[] src, int srcOffset, byte[] dst) {
      for (int i = 0; i < dst.length; i++) {
         dst[i] = toUpperCase(src[srcOffset + i]);
      }
   }

   private static void unrolledToUpperCase(byte[] src, int srcOffset, byte[] dst, int dstOffset, int byteCount) {
      assert byteCount >= 0 && byteCount < 8;

      int offset = 0;
      if ((byteCount & 4) != 0) {
         int word = PlatformDependent.getInt(src, srcOffset + offset);
         PlatformDependent.putInt(dst, dstOffset + offset, SWARUtil.toUpperCase(word));
         offset += 4;
      }

      if ((byteCount & 2) != 0) {
         short word = PlatformDependent.getShort(src, srcOffset + offset);
         short result = (short)(toUpperCase((byte)(word >>> 8)) << 8 | toUpperCase((byte)word));
         PlatformDependent.putShort(dst, dstOffset + offset, result);
         offset += 2;
      }

      if ((byteCount & 1) != 0) {
         PlatformDependent.putByte(dst, dstOffset + offset, toUpperCase(PlatformDependent.getByte(src, srcOffset + offset)));
      }
   }

   private static boolean isLowerCase(byte value) {
      return value >= 97 && value <= 122;
   }

   static boolean isUpperCase(byte value) {
      return value >= 65 && value <= 90;
   }

   static byte toLowerCase(byte value) {
      return isUpperCase(value) ? (byte)(value + 32) : value;
   }

   static byte toUpperCase(byte value) {
      return isLowerCase(value) ? (byte)(value - 32) : value;
   }

   private AsciiStringUtil() {
   }
}
