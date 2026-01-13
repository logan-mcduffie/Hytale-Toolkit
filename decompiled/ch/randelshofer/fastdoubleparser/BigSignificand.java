package ch.randelshofer.fastdoubleparser;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

final class BigSignificand {
   private static final long LONG_MASK = 4294967295L;
   private final int numInts;
   private final int[] x;
   private int firstNonZeroInt;

   public BigSignificand(long numBits) {
      if (numBits > 0L && numBits < 2147483647L) {
         int numLongs = (int)(numBits + 63L >>> 6) + 1;
         this.numInts = numLongs << 1;
         this.x = new int[this.numInts];
         this.firstNonZeroInt = this.numInts;
      } else {
         throw new IllegalArgumentException("numBits=" + numBits);
      }
   }

   public void add(int value) {
      if (value != 0) {
         long carry = value & 4294967295L;

         int i;
         for (i = this.numInts - 1; carry != 0L; i--) {
            long sum = (this.x(i) & 4294967295L) + carry;
            this.x(i, (int)sum);
            carry = sum >>> 32;
         }

         this.firstNonZeroInt = Math.min(this.firstNonZeroInt, i + 1);
      }
   }

   public void fma(int factor, int addend) {
      long factorL = factor & 4294967295L;
      long carry = addend;

      int i;
      for (i = this.numInts - 1; i >= this.firstNonZeroInt; i--) {
         long product = factorL * (this.x(i) & 4294967295L) + carry;
         this.x(i, (int)product);
         carry = product >>> 32;
      }

      if (carry != 0L) {
         this.x(i, (int)carry);
         this.firstNonZeroInt = i;
      }
   }

   public BigInteger toBigInteger() {
      byte[] bytes = new byte[this.x.length << 2];
      IntBuffer buf = ByteBuffer.wrap(bytes).asIntBuffer();

      for (int i = 0; i < this.x.length; i++) {
         buf.put(i, this.x[i]);
      }

      return new BigInteger(bytes);
   }

   private void x(int i, int value) {
      this.x[i] = value;
   }

   private int x(int i) {
      return this.x[i];
   }
}
