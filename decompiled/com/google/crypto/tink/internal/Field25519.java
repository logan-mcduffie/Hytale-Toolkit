package com.google.crypto.tink.internal;

import com.google.crypto.tink.annotations.Alpha;
import java.util.Arrays;

@Alpha
public final class Field25519 {
   public static final int FIELD_LEN = 32;
   public static final int LIMB_CNT = 10;
   private static final long TWO_TO_25 = 33554432L;
   private static final long TWO_TO_26 = 67108864L;
   private static final int[] expandStart = new int[]{0, 3, 6, 9, 12, 16, 19, 22, 25, 28};
   private static final int[] expandShift = new int[]{0, 2, 3, 5, 6, 0, 1, 3, 4, 6};
   private static final int[] mask = new int[]{67108863, 33554431};
   private static final int[] shift = new int[]{26, 25};

   static void sum(long[] output, long[] in1, long[] in2) {
      for (int i = 0; i < 10; i++) {
         output[i] = in1[i] + in2[i];
      }
   }

   static void sum(long[] output, long[] in) {
      sum(output, output, in);
   }

   static void sub(long[] output, long[] in1, long[] in2) {
      for (int i = 0; i < 10; i++) {
         output[i] = in1[i] - in2[i];
      }
   }

   static void sub(long[] output, long[] in) {
      sub(output, in, output);
   }

   static void scalarProduct(long[] output, long[] in, long scalar) {
      for (int i = 0; i < 10; i++) {
         output[i] = in[i] * scalar;
      }
   }

   static void product(long[] out, long[] in2, long[] in) {
      out[0] = in2[0] * in[0];
      out[1] = in2[0] * in[1] + in2[1] * in[0];
      out[2] = 2L * in2[1] * in[1] + in2[0] * in[2] + in2[2] * in[0];
      out[3] = in2[1] * in[2] + in2[2] * in[1] + in2[0] * in[3] + in2[3] * in[0];
      out[4] = in2[2] * in[2] + 2L * (in2[1] * in[3] + in2[3] * in[1]) + in2[0] * in[4] + in2[4] * in[0];
      out[5] = in2[2] * in[3] + in2[3] * in[2] + in2[1] * in[4] + in2[4] * in[1] + in2[0] * in[5] + in2[5] * in[0];
      out[6] = 2L * (in2[3] * in[3] + in2[1] * in[5] + in2[5] * in[1]) + in2[2] * in[4] + in2[4] * in[2] + in2[0] * in[6] + in2[6] * in[0];
      out[7] = in2[3] * in[4] + in2[4] * in[3] + in2[2] * in[5] + in2[5] * in[2] + in2[1] * in[6] + in2[6] * in[1] + in2[0] * in[7] + in2[7] * in[0];
      out[8] = in2[4] * in[4]
         + 2L * (in2[3] * in[5] + in2[5] * in[3] + in2[1] * in[7] + in2[7] * in[1])
         + in2[2] * in[6]
         + in2[6] * in[2]
         + in2[0] * in[8]
         + in2[8] * in[0];
      out[9] = in2[4] * in[5]
         + in2[5] * in[4]
         + in2[3] * in[6]
         + in2[6] * in[3]
         + in2[2] * in[7]
         + in2[7] * in[2]
         + in2[1] * in[8]
         + in2[8] * in[1]
         + in2[0] * in[9]
         + in2[9] * in[0];
      out[10] = 2L * (in2[5] * in[5] + in2[3] * in[7] + in2[7] * in[3] + in2[1] * in[9] + in2[9] * in[1])
         + in2[4] * in[6]
         + in2[6] * in[4]
         + in2[2] * in[8]
         + in2[8] * in[2];
      out[11] = in2[5] * in[6] + in2[6] * in[5] + in2[4] * in[7] + in2[7] * in[4] + in2[3] * in[8] + in2[8] * in[3] + in2[2] * in[9] + in2[9] * in[2];
      out[12] = in2[6] * in[6] + 2L * (in2[5] * in[7] + in2[7] * in[5] + in2[3] * in[9] + in2[9] * in[3]) + in2[4] * in[8] + in2[8] * in[4];
      out[13] = in2[6] * in[7] + in2[7] * in[6] + in2[5] * in[8] + in2[8] * in[5] + in2[4] * in[9] + in2[9] * in[4];
      out[14] = 2L * (in2[7] * in[7] + in2[5] * in[9] + in2[9] * in[5]) + in2[6] * in[8] + in2[8] * in[6];
      out[15] = in2[7] * in[8] + in2[8] * in[7] + in2[6] * in[9] + in2[9] * in[6];
      out[16] = in2[8] * in[8] + 2L * (in2[7] * in[9] + in2[9] * in[7]);
      out[17] = in2[8] * in[9] + in2[9] * in[8];
      out[18] = 2L * in2[9] * in[9];
   }

   static void reduce(long[] input, long[] output) {
      long[] tmp;
      if (input.length == 19) {
         tmp = input;
      } else {
         tmp = new long[19];
         System.arraycopy(input, 0, tmp, 0, input.length);
      }

      reduceSizeByModularReduction(tmp);
      reduceCoefficients(tmp);
      System.arraycopy(tmp, 0, output, 0, 10);
   }

   static void reduceSizeByModularReduction(long[] output) {
      output[8] += output[18] << 4;
      output[8] += output[18] << 1;
      output[8] += output[18];
      output[7] += output[17] << 4;
      output[7] += output[17] << 1;
      output[7] += output[17];
      output[6] += output[16] << 4;
      output[6] += output[16] << 1;
      output[6] += output[16];
      output[5] += output[15] << 4;
      output[5] += output[15] << 1;
      output[5] += output[15];
      output[4] += output[14] << 4;
      output[4] += output[14] << 1;
      output[4] += output[14];
      output[3] += output[13] << 4;
      output[3] += output[13] << 1;
      output[3] += output[13];
      output[2] += output[12] << 4;
      output[2] += output[12] << 1;
      output[2] += output[12];
      output[1] += output[11] << 4;
      output[1] += output[11] << 1;
      output[1] += output[11];
      output[0] += output[10] << 4;
      output[0] += output[10] << 1;
      output[0] += output[10];
   }

   static void reduceCoefficients(long[] output) {
      output[10] = 0L;

      for (int i = 0; i < 10; i += 2) {
         long over = output[i] / 67108864L;
         output[i] -= over << 26;
         output[i + 1] = output[i + 1] + over;
         over = output[i + 1] / 33554432L;
         output[i + 1] = output[i + 1] - (over << 25);
         output[i + 2] = output[i + 2] + over;
      }

      output[0] += output[10] << 4;
      output[0] += output[10] << 1;
      output[0] += output[10];
      output[10] = 0L;
      long over = output[0] / 67108864L;
      output[0] -= over << 26;
      output[1] += over;
   }

   static void mult(long[] output, long[] in, long[] in2) {
      long[] t = new long[19];
      product(t, in, in2);
      reduce(t, output);
   }

   private static void squareInner(long[] out, long[] in) {
      out[0] = in[0] * in[0];
      out[1] = 2L * in[0] * in[1];
      out[2] = 2L * (in[1] * in[1] + in[0] * in[2]);
      out[3] = 2L * (in[1] * in[2] + in[0] * in[3]);
      out[4] = in[2] * in[2] + 4L * in[1] * in[3] + 2L * in[0] * in[4];
      out[5] = 2L * (in[2] * in[3] + in[1] * in[4] + in[0] * in[5]);
      out[6] = 2L * (in[3] * in[3] + in[2] * in[4] + in[0] * in[6] + 2L * in[1] * in[5]);
      out[7] = 2L * (in[3] * in[4] + in[2] * in[5] + in[1] * in[6] + in[0] * in[7]);
      out[8] = in[4] * in[4] + 2L * (in[2] * in[6] + in[0] * in[8] + 2L * (in[1] * in[7] + in[3] * in[5]));
      out[9] = 2L * (in[4] * in[5] + in[3] * in[6] + in[2] * in[7] + in[1] * in[8] + in[0] * in[9]);
      out[10] = 2L * (in[5] * in[5] + in[4] * in[6] + in[2] * in[8] + 2L * (in[3] * in[7] + in[1] * in[9]));
      out[11] = 2L * (in[5] * in[6] + in[4] * in[7] + in[3] * in[8] + in[2] * in[9]);
      out[12] = in[6] * in[6] + 2L * (in[4] * in[8] + 2L * (in[5] * in[7] + in[3] * in[9]));
      out[13] = 2L * (in[6] * in[7] + in[5] * in[8] + in[4] * in[9]);
      out[14] = 2L * (in[7] * in[7] + in[6] * in[8] + 2L * in[5] * in[9]);
      out[15] = 2L * (in[7] * in[8] + in[6] * in[9]);
      out[16] = in[8] * in[8] + 4L * in[7] * in[9];
      out[17] = 2L * in[8] * in[9];
      out[18] = 2L * in[9] * in[9];
   }

   static void square(long[] output, long[] in) {
      long[] t = new long[19];
      squareInner(t, in);
      reduce(t, output);
   }

   static long[] expand(byte[] input) {
      long[] output = new long[10];

      for (int i = 0; i < 10; i++) {
         output[i] = (
                  input[expandStart[i]] & 255
                     | (long)(input[expandStart[i] + 1] & 255) << 8
                     | (long)(input[expandStart[i] + 2] & 255) << 16
                     | (long)(input[expandStart[i] + 3] & 255) << 24
               )
               >> expandShift[i]
            & mask[i & 1];
      }

      return output;
   }

   public static byte[] contract(long[] inputLimbs) {
      long[] input = Arrays.copyOf(inputLimbs, 10);

      for (int j = 0; j < 2; j++) {
         for (int i = 0; i < 9; i++) {
            int carry = -((int)((input[i] & input[i] >> 31) >> shift[i & 1]));
            input[i] += carry << shift[i & 1];
            input[i + 1] = input[i + 1] - carry;
         }

         int carry = -((int)((input[9] & input[9] >> 31) >> 25));
         input[9] += carry << 25;
         input[0] -= carry * 19L;
      }

      int carry = -((int)((input[0] & input[0] >> 31) >> 26));
      input[0] += carry << 26;
      input[1] -= carry;

      for (int j = 0; j < 2; j++) {
         for (int i = 0; i < 9; i++) {
            int carryx = (int)(input[i] >> shift[i & 1]);
            input[i] &= Field25519.mask[i & 1];
            input[i + 1] = input[i + 1] + carryx;
         }
      }

      carry = (int)(input[9] >> 25);
      input[9] &= 33554431L;
      input[0] += 19L * carry;
      carry = gte((int)input[0], 67108845);

      for (int i = 1; i < 10; i++) {
         carry &= eq((int)input[i], Field25519.mask[i & 1]);
      }

      input[0] -= carry & 67108845;
      input[1] -= carry & 33554431;

      for (int i = 2; i < 10; i += 2) {
         input[i] -= carry & 67108863;
         input[i + 1] = input[i + 1] - (carry & 33554431);
      }

      for (int i = 0; i < 10; i++) {
         input[i] <<= expandShift[i];
      }

      byte[] output = new byte[32];

      for (int i = 0; i < 10; i++) {
         output[expandStart[i]] = (byte)(output[expandStart[i]] | input[i] & 255L);
         output[expandStart[i] + 1] = (byte)(output[expandStart[i] + 1] | input[i] >> 8 & 255L);
         output[expandStart[i] + 2] = (byte)(output[expandStart[i] + 2] | input[i] >> 16 & 255L);
         output[expandStart[i] + 3] = (byte)(output[expandStart[i] + 3] | input[i] >> 24 & 255L);
      }

      return output;
   }

   static void inverse(long[] out, long[] z) {
      long[] z2 = new long[10];
      long[] z9 = new long[10];
      long[] z11 = new long[10];
      long[] z2To5Minus1 = new long[10];
      long[] z2To10Minus1 = new long[10];
      long[] z2To20Minus1 = new long[10];
      long[] z2To50Minus1 = new long[10];
      long[] z2To100Minus1 = new long[10];
      long[] t0 = new long[10];
      long[] t1 = new long[10];
      square(z2, z);
      square(t1, z2);
      square(t0, t1);
      mult(z9, t0, z);
      mult(z11, z9, z2);
      square(t0, z11);
      mult(z2To5Minus1, t0, z9);
      square(t0, z2To5Minus1);
      square(t1, t0);
      square(t0, t1);
      square(t1, t0);
      square(t0, t1);
      mult(z2To10Minus1, t0, z2To5Minus1);
      square(t0, z2To10Minus1);
      square(t1, t0);

      for (int i = 2; i < 10; i += 2) {
         square(t0, t1);
         square(t1, t0);
      }

      mult(z2To20Minus1, t1, z2To10Minus1);
      square(t0, z2To20Minus1);
      square(t1, t0);

      for (int i = 2; i < 20; i += 2) {
         square(t0, t1);
         square(t1, t0);
      }

      mult(t0, t1, z2To20Minus1);
      square(t1, t0);
      square(t0, t1);

      for (int i = 2; i < 10; i += 2) {
         square(t1, t0);
         square(t0, t1);
      }

      mult(z2To50Minus1, t0, z2To10Minus1);
      square(t0, z2To50Minus1);
      square(t1, t0);

      for (int i = 2; i < 50; i += 2) {
         square(t0, t1);
         square(t1, t0);
      }

      mult(z2To100Minus1, t1, z2To50Minus1);
      square(t1, z2To100Minus1);
      square(t0, t1);

      for (int i = 2; i < 100; i += 2) {
         square(t1, t0);
         square(t0, t1);
      }

      mult(t1, t0, z2To100Minus1);
      square(t0, t1);
      square(t1, t0);

      for (int i = 2; i < 50; i += 2) {
         square(t0, t1);
         square(t1, t0);
      }

      mult(t0, t1, z2To50Minus1);
      square(t1, t0);
      square(t0, t1);
      square(t1, t0);
      square(t0, t1);
      square(t1, t0);
      mult(out, t1, z11);
   }

   private static int eq(int a, int b) {
      a = ~(a ^ b);
      a &= a << 16;
      a &= a << 8;
      a &= a << 4;
      a &= a << 2;
      a &= a << 1;
      return a >> 31;
   }

   private static int gte(int a, int b) {
      a -= b;
      return ~(a >> 31);
   }

   private Field25519() {
   }
}
