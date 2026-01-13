package org.bouncycastle.pqc.crypto.lms;

import java.util.Arrays;
import java.util.List;
import org.bouncycastle.pqc.crypto.ExhaustedPrivateKeyException;

class HSS {
   public static HSSPrivateKeyParameters generateHSSKeyPair(HSSKeyGenerationParameters var0) {
      LMSPrivateKeyParameters[] var1 = new LMSPrivateKeyParameters[var0.getDepth()];
      LMSSignature[] var2 = new LMSSignature[var0.getDepth() - 1];
      byte[] var3 = new byte[var0.getLmsParameters()[0].getLMSigParam().getM()];
      var0.getRandom().nextBytes(var3);
      byte[] var4 = new byte[16];
      var0.getRandom().nextBytes(var4);
      byte[] var5 = new byte[0];
      long var6 = 1L;

      for (int var8 = 0; var8 < var1.length; var8++) {
         if (var8 == 0) {
            var1[var8] = new LMSPrivateKeyParameters(
               var0.getLmsParameters()[var8].getLMSigParam(),
               var0.getLmsParameters()[var8].getLMOTSParam(),
               0,
               var4,
               1 << var0.getLmsParameters()[var8].getLMSigParam().getH(),
               var3
            );
         } else {
            var1[var8] = new HSS.PlaceholderLMSPrivateKey(
               var0.getLmsParameters()[var8].getLMSigParam(),
               var0.getLmsParameters()[var8].getLMOTSParam(),
               -1,
               var5,
               1 << var0.getLmsParameters()[var8].getLMSigParam().getH(),
               var5
            );
         }

         var6 *= 1 << var0.getLmsParameters()[var8].getLMSigParam().getH();
      }

      if (var6 == 0L) {
         var6 = Long.MAX_VALUE;
      }

      return new HSSPrivateKeyParameters(var0.getDepth(), Arrays.asList(var1), Arrays.asList(var2), 0L, var6);
   }

   public static void incrementIndex(HSSPrivateKeyParameters var0) {
      synchronized (var0) {
         rangeTestKeys(var0);
         var0.incIndex();
         var0.getKeys().get(var0.getL() - 1).incIndex();
      }
   }

   static void rangeTestKeys(HSSPrivateKeyParameters var0) {
      synchronized (var0) {
         if (var0.getIndex() >= var0.getIndexLimit()) {
            throw new ExhaustedPrivateKeyException("hss private key" + (var0.isShard() ? " shard" : "") + " is exhausted");
         } else {
            int var2 = var0.getL();
            int var3 = var2;
            List var4 = var0.getKeys();

            while (((LMSPrivateKeyParameters)var4.get(var3 - 1)).getIndex() == 1 << ((LMSPrivateKeyParameters)var4.get(var3 - 1)).getSigParameters().getH()) {
               if (--var3 == 0) {
                  throw new ExhaustedPrivateKeyException(
                     "hss private key" + (var0.isShard() ? " shard" : "") + " is exhausted the maximum limit for this HSS private key"
                  );
               }
            }

            while (var3 < var2) {
               var0.replaceConsumedKey(var3);
               var3++;
            }
         }
      }
   }

   public static HSSSignature generateSignature(HSSPrivateKeyParameters var0, byte[] var1) {
      int var4 = var0.getL();
      LMSSignedPubKey[] var2;
      LMSPrivateKeyParameters var3;
      synchronized (var0) {
         rangeTestKeys(var0);
         List var6 = var0.getKeys();
         List var7 = var0.getSig();
         var3 = var0.getKeys().get(var4 - 1);
         int var8 = 0;

         for (var2 = new LMSSignedPubKey[var4 - 1]; var8 < var4 - 1; var8++) {
            var2[var8] = new LMSSignedPubKey((LMSSignature)var7.get(var8), ((LMSPrivateKeyParameters)var6.get(var8 + 1)).getPublicKey());
         }

         var0.incIndex();
      }

      LMSContext var5 = var3.generateLMSContext().withSignedPublicKeys(var2);
      var5.update(var1, 0, var1.length);
      return generateSignature(var4, var5);
   }

   public static HSSSignature generateSignature(int var0, LMSContext var1) {
      return new HSSSignature(var0 - 1, var1.getSignedPubKeys(), LMS.generateSign(var1));
   }

   public static boolean verifySignature(HSSPublicKeyParameters var0, HSSSignature var1, byte[] var2) {
      int var3 = var1.getlMinus1();
      if (var3 + 1 != var0.getL()) {
         return false;
      } else {
         LMSSignature[] var4 = new LMSSignature[var3 + 1];
         LMSPublicKeyParameters[] var5 = new LMSPublicKeyParameters[var3];

         for (int var6 = 0; var6 < var3; var6++) {
            var4[var6] = var1.getSignedPubKey()[var6].getSignature();
            var5[var6] = var1.getSignedPubKey()[var6].getPublicKey();
         }

         var4[var3] = var1.getSignature();
         LMSPublicKeyParameters var12 = var0.getLMSPublicKey();

         for (int var7 = 0; var7 < var3; var7++) {
            LMSSignature var8 = var4[var7];
            byte[] var9 = var5[var7].toByteArray();
            if (!LMS.verifySignature(var12, var8, var9)) {
               return false;
            }

            try {
               var12 = var5[var7];
            } catch (Exception var11) {
               throw new IllegalStateException(var11.getMessage(), var11);
            }
         }

         return LMS.verifySignature(var12, var4[var3], var2);
      }
   }

   static class PlaceholderLMSPrivateKey extends LMSPrivateKeyParameters {
      public PlaceholderLMSPrivateKey(LMSigParameters var1, LMOtsParameters var2, int var3, byte[] var4, int var5, byte[] var6) {
         super(var1, var2, var3, var4, var5, var6);
      }

      @Override
      LMOtsPrivateKey getNextOtsPrivateKey() {
         throw new RuntimeException("placeholder only");
      }

      @Override
      public LMSPublicKeyParameters getPublicKey() {
         throw new RuntimeException("placeholder only");
      }
   }
}
