package org.bson.codecs;

final class UuidCodecHelper {
   public static void reverseByteArray(byte[] data, int start, int length) {
      int left = start;

      for (int right = start + length - 1; left < right; right--) {
         byte temp = data[left];
         data[left] = data[right];
         data[right] = temp;
         left++;
      }
   }

   private UuidCodecHelper() {
   }
}
