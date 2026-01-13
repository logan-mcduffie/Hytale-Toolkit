package com.google.crypto.tink.aead.internal;

import com.google.crypto.tink.subtle.Bytes;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class Poly1305 {
   public static final int MAC_TAG_SIZE_IN_BYTES = 16;
   public static final int MAC_KEY_SIZE_IN_BYTES = 32;

   private Poly1305() {
   }

   private static long load32(byte[] in, int idx) {
      return (in[idx] & 255 | (in[idx + 1] & 255) << 8 | (in[idx + 2] & 255) << 16 | (in[idx + 3] & 255) << 24) & 4294967295L;
   }

   private static long load26(byte[] in, int idx, int shift) {
      return load32(in, idx) >> shift & 67108863L;
   }

   private static void toByteArray(byte[] output, long num, int idx) {
      int i = 0;

      while (i < 4) {
         output[idx + i] = (byte)(num & 255L);
         i++;
         num >>= 8;
      }
   }

   private static void copyBlockSize(byte[] output, byte[] in, int idx) {
      int copyCount = Math.min(16, in.length - idx);
      System.arraycopy(in, idx, output, 0, copyCount);
      output[copyCount] = 1;
      if (copyCount != 16) {
         Arrays.fill(output, copyCount + 1, output.length, (byte)0);
      }
   }

   public static byte[] computeMac(final byte[] key, byte[] data) {
      if (key.length != 32) {
         throw new IllegalArgumentException("The key length in bytes must be 32.");
      } else {
         long h0 = 0L;
         long h1 = 0L;
         long h2 = 0L;
         long h3 = 0L;
         long h4 = 0L;
         long r0 = load26(key, 0, 0) & 67108863L;
         long r1 = load26(key, 3, 2) & 67108611L;
         long r2 = load26(key, 6, 4) & 67092735L;
         long r3 = load26(key, 9, 6) & 66076671L;
         long r4 = load26(key, 12, 8) & 1048575L;
         long s1 = r1 * 5L;
         long s2 = r2 * 5L;
         long s3 = r3 * 5L;
         long s4 = r4 * 5L;
         byte[] buf = new byte[17];

         for (int i = 0; i < data.length; i += 16) {
            copyBlockSize(buf, data, i);
            h0 += load26(buf, 0, 0);
            h1 += load26(buf, 3, 2);
            h2 += load26(buf, 6, 4);
            h3 += load26(buf, 9, 6);
            h4 += load26(buf, 12, 8) | buf[16] << 24;
            long d0 = h0 * r0 + h1 * s4 + h2 * s3 + h3 * s2 + h4 * s1;
            long d1 = h0 * r1 + h1 * r0 + h2 * s4 + h3 * s3 + h4 * s2;
            long d2 = h0 * r2 + h1 * r1 + h2 * r0 + h3 * s4 + h4 * s3;
            long d3 = h0 * r3 + h1 * r2 + h2 * r1 + h3 * r0 + h4 * s4;
            long d4 = h0 * r4 + h1 * r3 + h2 * r2 + h3 * r1 + h4 * r0;
            long c = d0 >> 26;
            h0 = d0 & 67108863L;
            d1 += c;
            c = d1 >> 26;
            h1 = d1 & 67108863L;
            d2 += c;
            c = d2 >> 26;
            h2 = d2 & 67108863L;
            d3 += c;
            c = d3 >> 26;
            h3 = d3 & 67108863L;
            d4 += c;
            c = d4 >> 26;
            h4 = d4 & 67108863L;
            h0 += c * 5L;
            c = h0 >> 26;
            h0 &= 67108863L;
            h1 += c;
         }

         long c = h1 >> 26;
         h1 &= 67108863L;
         h2 += c;
         c = h2 >> 26;
         h2 &= 67108863L;
         h3 += c;
         c = h3 >> 26;
         h3 &= 67108863L;
         h4 += c;
         c = h4 >> 26;
         h4 &= 67108863L;
         h0 += c * 5L;
         c = h0 >> 26;
         h0 &= 67108863L;
         h1 += c;
         long g0 = h0 + 5L;
         c = g0 >> 26;
         g0 &= 67108863L;
         long g1 = h1 + c;
         c = g1 >> 26;
         g1 &= 67108863L;
         long g2 = h2 + c;
         c = g2 >> 26;
         g2 &= 67108863L;
         long g3 = h3 + c;
         c = g3 >> 26;
         g3 &= 67108863L;
         long g4 = h4 + c - 67108864L;
         long mask = g4 >> 63;
         h0 &= mask;
         h1 &= mask;
         h2 &= mask;
         h3 &= mask;
         h4 &= mask;
         mask = ~mask;
         h0 |= g0 & mask;
         h1 |= g1 & mask;
         h2 |= g2 & mask;
         h3 |= g3 & mask;
         h4 |= g4 & mask;
         h0 = (h0 | h1 << 26) & 4294967295L;
         h1 = (h1 >> 6 | h2 << 20) & 4294967295L;
         h2 = (h2 >> 12 | h3 << 14) & 4294967295L;
         h3 = (h3 >> 18 | h4 << 8) & 4294967295L;
         c = h0 + load32(key, 16);
         h0 = c & 4294967295L;
         c = h1 + load32(key, 20) + (c >> 32);
         h1 = c & 4294967295L;
         c = h2 + load32(key, 24) + (c >> 32);
         h2 = c & 4294967295L;
         c = h3 + load32(key, 28) + (c >> 32);
         h3 = c & 4294967295L;
         byte[] mac = new byte[16];
         toByteArray(mac, h0, 0);
         toByteArray(mac, h1, 4);
         toByteArray(mac, h2, 8);
         toByteArray(mac, h3, 12);
         return mac;
      }
   }

   public static void verifyMac(final byte[] key, byte[] data, byte[] mac) throws GeneralSecurityException {
      if (!Bytes.equal(computeMac(key, data), mac)) {
         throw new GeneralSecurityException("invalid MAC");
      }
   }
}
