package org.bouncycastle.pqc.crypto.lms;

import org.bouncycastle.crypto.Digest;

class LMS {
   static final short D_LEAF = -32126;
   static final short D_INTR = -31869;

   public static LMSPrivateKeyParameters generateKeys(LMSigParameters var0, LMOtsParameters var1, int var2, byte[] var3, byte[] var4) throws IllegalArgumentException {
      if (var4 != null && var4.length >= var0.getM()) {
         int var5 = 1 << var0.getH();
         return new LMSPrivateKeyParameters(var0, var1, var2, var3, var5, var4);
      } else {
         throw new IllegalArgumentException("root seed is less than " + var0.getM());
      }
   }

   public static LMSSignature generateSign(LMSPrivateKeyParameters var0, byte[] var1) {
      LMSContext var2 = var0.generateLMSContext();
      var2.update(var1, 0, var1.length);
      return generateSign(var2);
   }

   public static LMSSignature generateSign(LMSContext var0) {
      LMOtsSignature var1 = LM_OTS.lm_ots_generate_signature(var0.getPrivateKey(), var0.getQ(), var0.getC());
      return new LMSSignature(var0.getPrivateKey().getQ(), var1, var0.getSigParams(), var0.getPath());
   }

   public static boolean verifySignature(LMSPublicKeyParameters var0, LMSSignature var1, byte[] var2) {
      LMSContext var3 = var0.generateOtsContext(var1);
      LmsUtils.byteArray(var2, var3);
      return verifySignature(var0, var3);
   }

   public static boolean verifySignature(LMSPublicKeyParameters var0, byte[] var1, byte[] var2) {
      LMSContext var3 = var0.generateLMSContext(var1);
      LmsUtils.byteArray(var2, var3);
      return verifySignature(var0, var3);
   }

   public static boolean verifySignature(LMSPublicKeyParameters var0, LMSContext var1) {
      LMSSignature var2 = (LMSSignature)var1.getSignature();
      LMSigParameters var3 = var2.getParameter();
      int var4 = var3.getH();
      byte[][] var5 = var2.getY();
      byte[] var6 = LM_OTS.lm_ots_validate_signature_calculate(var1);
      int var7 = (1 << var4) + var2.getQ();
      byte[] var8 = var0.getI();
      Digest var9 = DigestUtil.getDigest(var3);
      byte[] var10 = new byte[var9.getDigestSize()];
      var9.update(var8, 0, var8.length);
      LmsUtils.u32str(var7, var9);
      LmsUtils.u16str((short)-32126, var9);
      var9.update(var6, 0, var6.length);
      var9.doFinal(var10, 0);
      int var11 = 0;

      while (var7 > 1) {
         if ((var7 & 1) == 1) {
            var9.update(var8, 0, var8.length);
            LmsUtils.u32str(var7 / 2, var9);
            LmsUtils.u16str((short)-31869, var9);
            var9.update(var5[var11], 0, var5[var11].length);
            var9.update(var10, 0, var10.length);
            var9.doFinal(var10, 0);
         } else {
            var9.update(var8, 0, var8.length);
            LmsUtils.u32str(var7 / 2, var9);
            LmsUtils.u16str((short)-31869, var9);
            var9.update(var10, 0, var10.length);
            var9.update(var5[var11], 0, var5[var11].length);
            var9.doFinal(var10, 0);
         }

         var7 /= 2;
         if (++var11 == var5.length && var7 > 1) {
            return false;
         }
      }

      return var0.matchesT1(var10);
   }
}
