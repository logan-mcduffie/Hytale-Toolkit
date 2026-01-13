package com.nimbusds.jose.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ByteUtils {
   public static byte[] concat(byte[]... byteArrays) {
      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();

         for (byte[] bytes : byteArrays) {
            if (bytes != null) {
               baos.write(bytes);
            }
         }

         return baos.toByteArray();
      } catch (IOException var6) {
         throw new IllegalStateException(var6.getMessage(), var6);
      }
   }

   public static byte[] subArray(byte[] byteArray, int beginIndex, int length) {
      byte[] subArray = new byte[length];
      System.arraycopy(byteArray, beginIndex, subArray, 0, subArray.length);
      return subArray;
   }

   public static int bitLength(int byteLength) {
      return byteLength * 8;
   }

   public static int safeBitLength(int byteLength) throws IntegerOverflowException {
      long longResult = byteLength * 8L;
      if ((int)longResult != longResult) {
         throw new IntegerOverflowException();
      } else {
         return (int)longResult;
      }
   }

   public static int bitLength(byte[] byteArray) {
      return byteArray == null ? 0 : bitLength(byteArray.length);
   }

   public static int safeBitLength(byte[] byteArray) throws IntegerOverflowException {
      return byteArray == null ? 0 : safeBitLength(byteArray.length);
   }

   public static int byteLength(int bitLength) {
      return bitLength / 8;
   }

   public static boolean isZeroFilled(byte[] byteArray) {
      for (byte b : byteArray) {
         if (b != 0) {
            return false;
         }
      }

      return true;
   }
}
