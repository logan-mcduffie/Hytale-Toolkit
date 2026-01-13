package org.bouncycastle.pqc.crypto.lms;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

class LM_OTS {
   private static final short D_PBLC = -32640;
   private static final int ITER_K = 20;
   private static final int ITER_PREV = 23;
   private static final int ITER_J = 22;
   static final int SEED_RANDOMISER_INDEX = -3;
   static final int MAX_HASH = 32;
   static final short D_MESG = -32383;

   public static int coef(byte[] var0, int var1, int var2) {
      int var3 = var1 * var2 / 8;
      int var4 = 8 / var2;
      int var5 = var2 * (~var1 & var4 - 1);
      int var6 = (1 << var2) - 1;
      return var0[var3] >>> var5 & var6;
   }

   public static int cksm(byte[] var0, int var1, LMOtsParameters var2) {
      int var3 = 0;
      int var4 = var2.getW();
      int var5 = (1 << var4) - 1;

      for (int var6 = 0; var6 < var1 * 8 / var2.getW(); var6++) {
         var3 = var3 + var5 - coef(var0, var6, var2.getW());
      }

      return var3 << var2.getLs();
   }

   public static LMOtsPublicKey lms_ots_generatePublicKey(LMOtsPrivateKey var0) {
      byte[] var1 = lms_ots_generatePublicKey(var0.getParameter(), var0.getI(), var0.getQ(), var0.getMasterSecret());
      return new LMOtsPublicKey(var0.getParameter(), var0.getI(), var0.getQ(), var1);
   }

   static byte[] lms_ots_generatePublicKey(LMOtsParameters var0, byte[] var1, int var2, byte[] var3) {
      Digest var4 = DigestUtil.getDigest(var0);
      byte[] var5 = Composer.compose().bytes(var1).u32str(var2).u16str(-32640).padUntil(0, 22).build();
      var4.update(var5, 0, var5.length);
      Digest var6 = DigestUtil.getDigest(var0);
      byte[] var7 = Composer.compose().bytes(var1).u32str(var2).padUntil(0, 23 + var6.getDigestSize()).build();
      SeedDerive var8 = new SeedDerive(var1, var3, DigestUtil.getDigest(var0));
      var8.setQ(var2);
      var8.setJ(0);
      int var9 = var0.getP();
      int var10 = var0.getN();
      int var11 = (1 << var0.getW()) - 1;

      for (int var12 = 0; var12 < var9; var12++) {
         var8.deriveSeed(var7, var12 < var9 - 1, 23);
         Pack.shortToBigEndian((short)var12, var7, 20);

         for (int var13 = 0; var13 < var11; var13++) {
            var7[22] = (byte)var13;
            var6.update(var7, 0, var7.length);
            var6.doFinal(var7, 23);
         }

         var4.update(var7, 23, var10);
      }

      byte[] var14 = new byte[var4.getDigestSize()];
      var4.doFinal(var14, 0);
      return var14;
   }

   public static LMOtsSignature lm_ots_generate_signature(LMSigParameters var0, LMOtsPrivateKey var1, byte[][] var2, byte[] var3, boolean var4) {
      byte[] var6 = new byte[34];
      byte[] var5;
      if (!var4) {
         LMSContext var7 = var1.getSignatureContext(var0, var2);
         LmsUtils.byteArray(var3, 0, var3.length, var7);
         var5 = var7.getC();
         var6 = var7.getQ();
      } else {
         int var8 = var1.getParameter().getN();
         var5 = new byte[var8];
         System.arraycopy(var3, 0, var6, 0, var8);
      }

      return lm_ots_generate_signature(var1, var6, var5);
   }

   public static LMOtsSignature lm_ots_generate_signature(LMOtsPrivateKey var0, byte[] var1, byte[] var2) {
      LMOtsParameters var3 = var0.getParameter();
      int var4 = var3.getN();
      int var5 = var3.getP();
      int var6 = var3.getW();
      byte[] var7 = new byte[var5 * var4];
      Digest var8 = DigestUtil.getDigest(var3);
      SeedDerive var9 = var0.getDerivationFunction();
      int var10 = cksm(var1, var4, var3);
      var1[var4] = (byte)(var10 >>> 8 & 0xFF);
      var1[var4 + 1] = (byte)var10;
      byte[] var11 = Composer.compose().bytes(var0.getI()).u32str(var0.getQ()).padUntil(0, 23 + var4).build();
      var9.setJ(0);

      for (int var12 = 0; var12 < var5; var12++) {
         Pack.shortToBigEndian((short)var12, var11, 20);
         var9.deriveSeed(var11, var12 < var5 - 1, 23);
         int var13 = coef(var1, var12, var6);

         for (int var14 = 0; var14 < var13; var14++) {
            var11[22] = (byte)var14;
            var8.update(var11, 0, 23 + var4);
            var8.doFinal(var11, 23);
         }

         System.arraycopy(var11, 23, var7, var4 * var12, var4);
      }

      return new LMOtsSignature(var3, var2, var7);
   }

   public static boolean lm_ots_validate_signature(LMOtsPublicKey var0, LMOtsSignature var1, byte[] var2, boolean var3) throws LMSException {
      if (!var1.getType().equals(var0.getParameter())) {
         throw new LMSException("public key and signature ots types do not match");
      } else {
         return Arrays.areEqual(lm_ots_validate_signature_calculate(var0, var1, var2), var0.getK());
      }
   }

   public static byte[] lm_ots_validate_signature_calculate(LMOtsPublicKey var0, LMOtsSignature var1, byte[] var2) {
      LMSContext var3 = var0.createOtsContext(var1);
      LmsUtils.byteArray(var2, var3);
      return lm_ots_validate_signature_calculate(var3);
   }

   public static byte[] lm_ots_validate_signature_calculate(LMSContext var0) {
      LMOtsPublicKey var1 = var0.getPublicKey();
      LMOtsParameters var2 = var1.getParameter();
      Object var3 = var0.getSignature();
      LMOtsSignature var4;
      if (var3 instanceof LMSSignature) {
         var4 = ((LMSSignature)var3).getOtsSignature();
      } else {
         var4 = (LMOtsSignature)var3;
      }

      int var5 = var2.getN();
      int var6 = var2.getW();
      int var7 = var2.getP();
      byte[] var8 = var0.getQ();
      int var9 = cksm(var8, var5, var2);
      var8[var5] = (byte)(var9 >>> 8 & 0xFF);
      var8[var5 + 1] = (byte)var9;
      byte[] var10 = var1.getI();
      int var11 = var1.getQ();
      Digest var12 = DigestUtil.getDigest(var2);
      LmsUtils.byteArray(var10, var12);
      LmsUtils.u32str(var11, var12);
      LmsUtils.u16str((short)-32640, var12);
      byte[] var13 = Composer.compose().bytes(var10).u32str(var11).padUntil(0, 23 + var5).build();
      int var14 = (1 << var6) - 1;
      byte[] var15 = var4.getY();
      Digest var16 = DigestUtil.getDigest(var2);

      for (int var17 = 0; var17 < var7; var17++) {
         Pack.shortToBigEndian((short)var17, var13, 20);
         System.arraycopy(var15, var17 * var5, var13, 23, var5);
         int var18 = coef(var8, var17, var6);

         for (int var19 = var18; var19 < var14; var19++) {
            var13[22] = (byte)var19;
            var16.update(var13, 0, 23 + var5);
            var16.doFinal(var13, 23);
         }

         var12.update(var13, 23, var5);
      }

      byte[] var20 = new byte[var5];
      var12.doFinal(var20, 0);
      return var20;
   }
}
