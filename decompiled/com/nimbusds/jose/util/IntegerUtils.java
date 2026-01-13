package com.nimbusds.jose.util;

public class IntegerUtils {
   public static byte[] toBytes(int intValue) {
      return new byte[]{(byte)(intValue >>> 24), (byte)(intValue >>> 16 & 0xFF), (byte)(intValue >>> 8 & 0xFF), (byte)(intValue & 0xFF)};
   }

   private IntegerUtils() {
   }
}
