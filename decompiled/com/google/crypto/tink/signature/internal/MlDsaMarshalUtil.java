package com.google.crypto.tink.signature.internal;

import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.util.SecretBytes;
import java.security.GeneralSecurityException;

final class MlDsaMarshalUtil {
   static void simpleBitPack10(MlDsaArithmeticUtil.PolyRq w, byte[] z, int offset) throws GeneralSecurityException {
      if (offset + 320 > z.length) {
         throw new GeneralSecurityException("Provided buffer too short");
      } else {
         for (int i = 0; i < 64; i++) {
            int a = w.polynomial[4 * i].r;
            int b = w.polynomial[4 * i + 1].r;
            int c = w.polynomial[4 * i + 2].r;
            int d = w.polynomial[4 * i + 3].r;
            if (a >= 1024 || b >= 1024 || c >= 1024 || d >= 1024) {
               throw new GeneralSecurityException("Polynomial coefficient too large");
            }

            z[offset + 5 * i] = (byte)a;
            z[offset + 5 * i + 1] = (byte)(a >> 8 | b << 2);
            z[offset + 5 * i + 2] = (byte)(b >> 6 | c << 4);
            z[offset + 5 * i + 3] = (byte)(c >> 4 | d << 6);
            z[offset + 5 * i + 4] = (byte)(d >> 2);
         }
      }
   }

   static void bitPack3(MlDsaArithmeticUtil.PolyRq w, byte[] z, int offset) throws GeneralSecurityException {
      if (offset + 96 > z.length) {
         throw new GeneralSecurityException("Provided buffer too short");
      } else {
         MlDsaArithmeticUtil.RingZq two = new MlDsaArithmeticUtil.RingZq(2);

         for (int i = 0; i < 32; i++) {
            int a = two.minus(w.polynomial[8 * i]).r;
            int b = two.minus(w.polynomial[8 * i + 1]).r;
            int c = two.minus(w.polynomial[8 * i + 2]).r;
            int d = two.minus(w.polynomial[8 * i + 3]).r;
            int e = two.minus(w.polynomial[8 * i + 4]).r;
            int f = two.minus(w.polynomial[8 * i + 5]).r;
            int g = two.minus(w.polynomial[8 * i + 6]).r;
            int h = two.minus(w.polynomial[8 * i + 7]).r;
            if (a > 4 || b > 4 || c > 4 || d > 4 || e > 4 || f > 4 || g > 4 || h > 4) {
               throw new GeneralSecurityException("Polynomial coefficients out of bounds");
            }

            z[offset + 3 * i] = (byte)(a | b << 3 | c << 6);
            z[offset + 3 * i + 1] = (byte)(c >> 2 | d << 1 | e << 4 | f << 7);
            z[offset + 3 * i + 2] = (byte)(f >> 1 | g << 2 | h << 5);
         }
      }
   }

   static void bitPack4(MlDsaArithmeticUtil.PolyRq w, byte[] z, int offset) throws GeneralSecurityException {
      if (offset + 128 > z.length) {
         throw new GeneralSecurityException("Provided buffer too short");
      } else {
         MlDsaArithmeticUtil.RingZq four = new MlDsaArithmeticUtil.RingZq(4);

         for (int i = 0; i < 128; i++) {
            int a = four.minus(w.polynomial[2 * i]).r;
            int b = four.minus(w.polynomial[2 * i + 1]).r;
            if (a > 8 || b > 8) {
               throw new GeneralSecurityException("Polynomial coefficients out of bounds");
            }

            z[offset + i] = (byte)(a | b << 4);
         }
      }
   }

   static void bitPack13(MlDsaArithmeticUtil.PolyRq w, byte[] z, int offset) throws GeneralSecurityException {
      if (offset + 416 > z.length) {
         throw new GeneralSecurityException("Provided buffer too short");
      } else {
         MlDsaArithmeticUtil.RingZq twoPowDMinusOne = new MlDsaArithmeticUtil.RingZq(4096);

         for (int i = 0; i < 32; i++) {
            int a = twoPowDMinusOne.minus(w.polynomial[8 * i]).r;
            int b = twoPowDMinusOne.minus(w.polynomial[8 * i + 1]).r;
            int c = twoPowDMinusOne.minus(w.polynomial[8 * i + 2]).r;
            int d = twoPowDMinusOne.minus(w.polynomial[8 * i + 3]).r;
            int e = twoPowDMinusOne.minus(w.polynomial[8 * i + 4]).r;
            int f = twoPowDMinusOne.minus(w.polynomial[8 * i + 5]).r;
            int g = twoPowDMinusOne.minus(w.polynomial[8 * i + 6]).r;
            int h = twoPowDMinusOne.minus(w.polynomial[8 * i + 7]).r;
            if (a >= 8192 || b >= 8192 || c >= 8192 || d >= 8192 || e >= 8192 || f >= 8192 || g >= 8192 || h >= 8192) {
               throw new GeneralSecurityException("Polynomial coefficient too large");
            }

            z[offset + 13 * i] = (byte)a;
            z[offset + 13 * i + 1] = (byte)(a >> 8 | b << 5);
            z[offset + 13 * i + 2] = (byte)(b >> 3);
            z[offset + 13 * i + 3] = (byte)(b >> 11 | c << 2);
            z[offset + 13 * i + 4] = (byte)(c >> 6 | d << 7);
            z[offset + 13 * i + 5] = (byte)(d >> 1);
            z[offset + 13 * i + 6] = (byte)(d >> 9 | e << 4);
            z[offset + 13 * i + 7] = (byte)(e >> 4);
            z[offset + 13 * i + 8] = (byte)(e >> 12 | f << 1);
            z[offset + 13 * i + 9] = (byte)(f >> 7 | g << 6);
            z[offset + 13 * i + 10] = (byte)(g >> 2);
            z[offset + 13 * i + 11] = (byte)(g >> 10 | h << 3);
            z[offset + 13 * i + 12] = (byte)(h >> 5);
         }
      }
   }

   static byte[] pkEncode(byte[] rho, MlDsaArithmeticUtil.VectorRq t1Bold, MlDsaConstants.Params params) throws GeneralSecurityException {
      if (rho.length == 32 && t1Bold.vector.length == params.k) {
         byte[] pk = new byte[params.pkLength];
         System.arraycopy(rho, 0, pk, 0, 32);

         for (int i = 0; i < params.k; i++) {
            simpleBitPack10(t1Bold.vector[i], pk, 32 + 32 * i * 10);
         }

         return pk;
      } else {
         throw new GeneralSecurityException("Invalid parameters length for pkEncode");
      }
   }

   static SecretBytes skEncode(
      byte[] rho,
      byte[] capK,
      byte[] tr,
      MlDsaArithmeticUtil.VectorRq s1Bold,
      MlDsaArithmeticUtil.VectorRq s2Bold,
      MlDsaArithmeticUtil.VectorRq t0Bold,
      MlDsaConstants.Params params
   ) throws GeneralSecurityException {
      if (rho.length == 32
         && capK.length == 32
         && tr.length == 64
         && s1Bold.vector.length == params.l
         && s2Bold.vector.length == params.k
         && t0Bold.vector.length == params.k) {
         byte[] sk = new byte[params.skLength];
         System.arraycopy(rho, 0, sk, 0, 32);
         System.arraycopy(capK, 0, sk, 32, 32);
         System.arraycopy(tr, 0, sk, 64, 64);
         int baseOffset = 128;
         if (params.eta == 2) {
            for (int i = 0; i < params.l; i++) {
               bitPack3(s1Bold.vector[i], sk, baseOffset + 32 * i * params.bitlen2Eta);
            }

            baseOffset += 32 * params.l * params.bitlen2Eta;

            for (int i = 0; i < params.k; i++) {
               bitPack3(s2Bold.vector[i], sk, baseOffset + 32 * i * params.bitlen2Eta);
            }
         } else if (params.eta == 4) {
            for (int i = 0; i < params.l; i++) {
               bitPack4(s1Bold.vector[i], sk, baseOffset + 32 * i * params.bitlen2Eta);
            }

            baseOffset += 32 * params.l * params.bitlen2Eta;

            for (int i = 0; i < params.k; i++) {
               bitPack4(s2Bold.vector[i], sk, baseOffset + 32 * i * params.bitlen2Eta);
            }
         }

         baseOffset += 32 * params.k * params.bitlen2Eta;

         for (int i = 0; i < params.k; i++) {
            bitPack13(t0Bold.vector[i], sk, baseOffset + 32 * i * 13);
         }

         return SecretBytes.copyFrom(sk, InsecureSecretKeyAccess.get());
      } else {
         throw new GeneralSecurityException("Invalid parameters length");
      }
   }

   private MlDsaMarshalUtil() {
   }
}
