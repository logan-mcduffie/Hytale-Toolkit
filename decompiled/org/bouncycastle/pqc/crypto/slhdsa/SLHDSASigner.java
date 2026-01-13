package org.bouncycastle.pqc.crypto.slhdsa;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithContext;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.MessageSigner;
import org.bouncycastle.util.Arrays;

public class SLHDSASigner implements MessageSigner {
   private static final byte[] DEFAULT_PREFIX = new byte[]{0, 0};
   private byte[] msgPrefix;
   private SLHDSAPublicKeyParameters pubKey;
   private SLHDSAPrivateKeyParameters privKey;
   private SecureRandom random;

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (var2 instanceof ParametersWithContext) {
         ParametersWithContext var3 = (ParametersWithContext)var2;
         var2 = var3.getParameters();
         int var4 = var3.getContextLength();
         if (var4 > 255) {
            throw new IllegalArgumentException("context too long");
         }

         this.msgPrefix = new byte[2 + var4];
         this.msgPrefix[0] = 0;
         this.msgPrefix[1] = (byte)var4;
         var3.copyContextTo(this.msgPrefix, 2, var4);
      } else {
         this.msgPrefix = DEFAULT_PREFIX;
      }

      SLHDSAParameters var5;
      if (var1) {
         this.pubKey = null;
         if (var2 instanceof ParametersWithRandom) {
            ParametersWithRandom var6 = (ParametersWithRandom)var2;
            this.privKey = (SLHDSAPrivateKeyParameters)var6.getParameters();
            this.random = var6.getRandom();
         } else {
            this.privKey = (SLHDSAPrivateKeyParameters)var2;
            this.random = null;
         }

         var5 = this.privKey.getParameters();
      } else {
         this.pubKey = (SLHDSAPublicKeyParameters)var2;
         this.privKey = null;
         this.random = null;
         var5 = this.pubKey.getParameters();
      }

      if (var5.isPreHash()) {
         throw new IllegalArgumentException("\"pure\" slh-dsa must use non pre-hash parameters");
      }
   }

   @Override
   public byte[] generateSignature(byte[] var1) {
      SLHDSAEngine var2 = this.privKey.getParameters().getEngine();
      var2.init(this.privKey.pk.seed);
      byte[] var3 = new byte[var2.N];
      if (this.random != null) {
         this.random.nextBytes(var3);
      } else {
         System.arraycopy(this.privKey.pk.seed, 0, var3, 0, var3.length);
      }

      return internalGenerateSignature(this.privKey, this.msgPrefix, var1, var3);
   }

   @Override
   public boolean verifySignature(byte[] var1, byte[] var2) {
      return internalVerifySignature(this.pubKey, this.msgPrefix, var1, var2);
   }

   protected boolean internalVerifySignature(byte[] var1, byte[] var2) {
      return internalVerifySignature(this.pubKey, null, var1, var2);
   }

   private static boolean internalVerifySignature(SLHDSAPublicKeyParameters var0, byte[] var1, byte[] var2, byte[] var3) {
      SLHDSAEngine var4 = var0.getParameters().getEngine();
      var4.init(var0.getSeed());
      ADRS var5 = new ADRS();
      if ((1 + var4.K * (1 + var4.A) + var4.H + var4.D * var4.WOTS_LEN) * var4.N != var3.length) {
         return false;
      } else {
         SIG var6 = new SIG(var4.N, var4.K, var4.A, var4.D, var4.H_PRIME, var4.WOTS_LEN, var3);
         byte[] var7 = var6.getR();
         SIG_FORS[] var8 = var6.getSIG_FORS();
         SIG_XMSS[] var9 = var6.getSIG_HT();
         IndexedDigest var10 = var4.H_msg(var7, var0.getSeed(), var0.getRoot(), var1, var2);
         byte[] var11 = var10.digest;
         long var12 = var10.idx_tree;
         int var14 = var10.idx_leaf;
         var5.setTypeAndClear(3);
         var5.setLayerAddress(0);
         var5.setTreeAddress(var12);
         var5.setKeyPairAddress(var14);
         byte[] var15 = new Fors(var4).pkFromSig(var8, var11, var0.getSeed(), var5);
         var5.setTypeAndClear(2);
         var5.setLayerAddress(0);
         var5.setTreeAddress(var12);
         var5.setKeyPairAddress(var14);
         HT var16 = new HT(var4, null, var0.getSeed());
         return var16.verify(var15, var9, var0.getSeed(), var12, var14, var0.getRoot());
      }
   }

   protected byte[] internalGenerateSignature(byte[] var1, byte[] var2) {
      return internalGenerateSignature(this.privKey, null, var1, var2);
   }

   private static byte[] internalGenerateSignature(SLHDSAPrivateKeyParameters var0, byte[] var1, byte[] var2, byte[] var3) {
      SLHDSAEngine var4 = var0.getParameters().getEngine();
      var4.init(var0.pk.seed);
      Fors var5 = new Fors(var4);
      byte[] var6 = var4.PRF_msg(var0.sk.prf, var3, var1, var2);
      IndexedDigest var7 = var4.H_msg(var6, var0.pk.seed, var0.pk.root, var1, var2);
      byte[] var8 = var7.digest;
      long var9 = var7.idx_tree;
      int var11 = var7.idx_leaf;
      ADRS var12 = new ADRS();
      var12.setTypeAndClear(3);
      var12.setTreeAddress(var9);
      var12.setKeyPairAddress(var11);
      SIG_FORS[] var13 = var5.sign(var8, var0.sk.seed, var0.pk.seed, var12);
      var12 = new ADRS();
      var12.setTypeAndClear(3);
      var12.setTreeAddress(var9);
      var12.setKeyPairAddress(var11);
      byte[] var14 = var5.pkFromSig(var13, var8, var0.pk.seed, var12);
      ADRS var15 = new ADRS();
      var15.setTypeAndClear(2);
      HT var16 = new HT(var4, var0.getSeed(), var0.getPublicSeed());
      byte[] var17 = var16.sign(var14, var9, var11);
      byte[][] var18 = new byte[var13.length + 2][];
      var18[0] = var6;

      for (int var19 = 0; var19 != var13.length; var19++) {
         var18[1 + var19] = Arrays.concatenate(var13[var19].sk, Arrays.concatenate(var13[var19].authPath));
      }

      var18[var18.length - 1] = var17;
      return Arrays.concatenate(var18);
   }
}
