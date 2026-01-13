package com.google.crypto.tink.signature.internal;

import java.security.GeneralSecurityException;
import java.util.Arrays;

final class MlDsaArithmeticUtil {
   private MlDsaArithmeticUtil() {
   }

   static final class MatrixTq {
      final MlDsaArithmeticUtil.RingTq[][] matrix;

      MatrixTq(int k, int l) throws GeneralSecurityException {
         if (k == 6 && l == 5 || k == 8 && l == 7) {
            this.matrix = new MlDsaArithmeticUtil.RingTq[k][l];

            for (int i = 0; i < k; i++) {
               for (int j = 0; j < l; j++) {
                  this.matrix[i][j] = new MlDsaArithmeticUtil.RingTq();
               }
            }
         } else {
            throw new GeneralSecurityException("Wrong size of the ML-DSA matrix: k=" + k + ", l=" + l);
         }
      }

      MlDsaArithmeticUtil.VectorTq multiplyVector(MlDsaArithmeticUtil.VectorTq other) throws GeneralSecurityException {
         if (this.matrix[0].length != other.vector.length) {
            throw new GeneralSecurityException(
               "Invalid parameters for matrix multiplication: matrix size ("
                  + this.matrix.length
                  + ", "
                  + this.matrix[0].length
                  + "), vector size "
                  + other.vector.length
            );
         } else {
            MlDsaArithmeticUtil.VectorTq result = new MlDsaArithmeticUtil.VectorTq(this.matrix.length);

            for (int i = 0; i < this.matrix.length; i++) {
               for (int j = 0; j < other.vector.length; j++) {
                  result.vector[i] = result.vector[i].plus(this.matrix[i][j].multiply(other.vector[j]));
               }
            }

            return result;
         }
      }
   }

   static final class PolyRq {
      final MlDsaArithmeticUtil.RingZq[] polynomial = new MlDsaArithmeticUtil.RingZq[256];

      static MlDsaArithmeticUtil.PolyRq copyFromVector(MlDsaArithmeticUtil.RingTq vector) {
         MlDsaArithmeticUtil.PolyRq result = new MlDsaArithmeticUtil.PolyRq();
         System.arraycopy(vector.vector, 0, result.polynomial, 0, 256);
         return result;
      }

      PolyRq() {
         for (int i = 0; i < 256; i++) {
            this.polynomial[i] = new MlDsaArithmeticUtil.RingZq(0);
         }
      }

      MlDsaArithmeticUtil.PolyRq plus(MlDsaArithmeticUtil.PolyRq other) {
         MlDsaArithmeticUtil.PolyRq result = new MlDsaArithmeticUtil.PolyRq();

         for (int i = 0; i < 256; i++) {
            result.polynomial[i] = this.polynomial[i].plus(other.polynomial[i]);
         }

         return result;
      }

      MlDsaArithmeticUtil.PolyRqPair power2Round() {
         MlDsaArithmeticUtil.PolyRq t1Bold = new MlDsaArithmeticUtil.PolyRq();
         MlDsaArithmeticUtil.PolyRq t0Bold = new MlDsaArithmeticUtil.PolyRq();

         for (int i = 0; i < 256; i++) {
            MlDsaArithmeticUtil.RingZqPair result = this.polynomial[i].power2Round();
            t1Bold.polynomial[i] = result.r1;
            t0Bold.polynomial[i] = result.r0;
         }

         return new MlDsaArithmeticUtil.PolyRqPair(t1Bold, t0Bold);
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (!(o instanceof MlDsaArithmeticUtil.PolyRq)) {
            return false;
         } else {
            MlDsaArithmeticUtil.PolyRq other = (MlDsaArithmeticUtil.PolyRq)o;
            return Arrays.equals((Object[])this.polynomial, (Object[])other.polynomial);
         }
      }

      @Override
      public int hashCode() {
         return Arrays.hashCode((Object[])this.polynomial);
      }
   }

   static final class PolyRqPair {
      final MlDsaArithmeticUtil.PolyRq t1Bold;
      final MlDsaArithmeticUtil.PolyRq t0Bold;

      PolyRqPair(MlDsaArithmeticUtil.PolyRq t1Bold, MlDsaArithmeticUtil.PolyRq t0Bold) {
         this.t1Bold = t1Bold;
         this.t0Bold = t0Bold;
      }
   }

   static final class RingTq {
      final MlDsaArithmeticUtil.RingZq[] vector = new MlDsaArithmeticUtil.RingZq[256];

      RingTq() {
         for (int i = 0; i < 256; i++) {
            this.vector[i] = new MlDsaArithmeticUtil.RingZq(0);
         }
      }

      static MlDsaArithmeticUtil.RingTq copyFromPolynomial(MlDsaArithmeticUtil.PolyRq polynomial) {
         MlDsaArithmeticUtil.RingTq result = new MlDsaArithmeticUtil.RingTq();
         System.arraycopy(polynomial.polynomial, 0, result.vector, 0, 256);
         return result;
      }

      MlDsaArithmeticUtil.RingTq plus(MlDsaArithmeticUtil.RingTq other) {
         MlDsaArithmeticUtil.RingTq result = new MlDsaArithmeticUtil.RingTq();

         for (int i = 0; i < 256; i++) {
            result.vector[i] = this.vector[i].plus(other.vector[i]);
         }

         return result;
      }

      MlDsaArithmeticUtil.RingTq multiply(MlDsaArithmeticUtil.RingTq other) {
         MlDsaArithmeticUtil.RingTq result = new MlDsaArithmeticUtil.RingTq();

         for (int i = 0; i < 256; i++) {
            result.vector[i] = this.vector[i].multiply(other.vector[i]);
         }

         return result;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (!(o instanceof MlDsaArithmeticUtil.RingTq)) {
            return false;
         } else {
            MlDsaArithmeticUtil.RingTq other = (MlDsaArithmeticUtil.RingTq)o;
            return Arrays.equals((Object[])this.vector, (Object[])other.vector);
         }
      }

      @Override
      public int hashCode() {
         return Arrays.hashCode((Object[])this.vector);
      }
   }

   static final class RingZq {
      static final MlDsaArithmeticUtil.RingZq INVALID = new MlDsaArithmeticUtil.RingZq(-1);
      static final int Q = 8380417;
      final int r;

      RingZq(int r) {
         if ((r < 0 || r >= 8380417) && INVALID != null) {
            this.r = INVALID.r;
         } else {
            this.r = r;
         }
      }

      MlDsaArithmeticUtil.RingZq plus(MlDsaArithmeticUtil.RingZq other) {
         return new MlDsaArithmeticUtil.RingZq((this.r + other.r) % 8380417);
      }

      MlDsaArithmeticUtil.RingZq minus(MlDsaArithmeticUtil.RingZq other) {
         return new MlDsaArithmeticUtil.RingZq((this.r - other.r + 8380417) % 8380417);
      }

      MlDsaArithmeticUtil.RingZq multiply(MlDsaArithmeticUtil.RingZq other) {
         return new MlDsaArithmeticUtil.RingZq((int)((long)this.r * other.r % 8380417L));
      }

      MlDsaArithmeticUtil.RingZq negative() {
         return new MlDsaArithmeticUtil.RingZq((8380417 - this.r) % 8380417);
      }

      MlDsaArithmeticUtil.RingZqPair power2Round() {
         int rPlus = this.r % 8380417;
         int rZero = ((rPlus + 4096 - 1 & 8191) - 4095 + 8380417) % 8380417;
         int rOne = (rPlus - rZero + 8380417) % 8380417 >> 13;
         return new MlDsaArithmeticUtil.RingZqPair(rOne, rZero);
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (!(o instanceof MlDsaArithmeticUtil.RingZq)) {
            return false;
         } else {
            MlDsaArithmeticUtil.RingZq other = (MlDsaArithmeticUtil.RingZq)o;
            return this.r == other.r;
         }
      }

      @Override
      public int hashCode() {
         return Integer.hashCode(this.r);
      }
   }

   static final class RingZqPair {
      final MlDsaArithmeticUtil.RingZq r1;
      final MlDsaArithmeticUtil.RingZq r0;

      RingZqPair(int r1, int r0) {
         this.r1 = new MlDsaArithmeticUtil.RingZq(r1);
         this.r0 = new MlDsaArithmeticUtil.RingZq(r0);
      }
   }

   static final class VectorRq {
      final MlDsaArithmeticUtil.PolyRq[] vector;

      VectorRq(int l) {
         this.vector = new MlDsaArithmeticUtil.PolyRq[l];

         for (int i = 0; i < l; i++) {
            this.vector[i] = new MlDsaArithmeticUtil.PolyRq();
         }
      }
   }

   static final class VectorRqPair {
      MlDsaArithmeticUtil.VectorRq s1;
      MlDsaArithmeticUtil.VectorRq s2;

      VectorRqPair(int l1, int l2) {
         this.s1 = new MlDsaArithmeticUtil.VectorRq(l1);
         this.s2 = new MlDsaArithmeticUtil.VectorRq(l2);
      }
   }

   static final class VectorTq {
      final MlDsaArithmeticUtil.RingTq[] vector;

      VectorTq(int l) {
         this.vector = new MlDsaArithmeticUtil.RingTq[l];

         for (int i = 0; i < l; i++) {
            this.vector[i] = new MlDsaArithmeticUtil.RingTq();
         }
      }
   }
}
