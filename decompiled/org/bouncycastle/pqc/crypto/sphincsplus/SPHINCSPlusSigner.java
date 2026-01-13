package org.bouncycastle.pqc.crypto.sphincsplus;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.MessageSigner;
import org.bouncycastle.util.Arrays;

public class SPHINCSPlusSigner implements MessageSigner {
   private SPHINCSPlusPrivateKeyParameters privKey;
   private SPHINCSPlusPublicKeyParameters pubKey;
   private SecureRandom random;

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (var1) {
         if (var2 instanceof ParametersWithRandom) {
            this.privKey = (SPHINCSPlusPrivateKeyParameters)((ParametersWithRandom)var2).getParameters();
            this.random = ((ParametersWithRandom)var2).getRandom();
         } else {
            this.privKey = (SPHINCSPlusPrivateKeyParameters)var2;
         }
      } else {
         this.pubKey = (SPHINCSPlusPublicKeyParameters)var2;
      }
   }

   @Override
   public byte[] generateSignature(byte[] var1) {
      SPHINCSPlusEngine var2 = this.privKey.getParameters().getEngine();
      var2.init(this.privKey.pk.seed);
      byte[] var3 = new byte[var2.N];
      if (this.random != null) {
         this.random.nextBytes(var3);
      } else {
         System.arraycopy(this.privKey.pk.seed, 0, var3, 0, var3.length);
      }

      Fors var4 = new Fors(var2);
      byte[] var5 = var2.PRF_msg(this.privKey.sk.prf, var3, var1);
      IndexedDigest var6 = var2.H_msg(var5, this.privKey.pk.seed, this.privKey.pk.root, var1);
      byte[] var7 = var6.digest;
      long var8 = var6.idx_tree;
      int var10 = var6.idx_leaf;
      ADRS var11 = new ADRS();
      var11.setTypeAndClear(3);
      var11.setTreeAddress(var8);
      var11.setKeyPairAddress(var10);
      SIG_FORS[] var12 = var4.sign(var7, this.privKey.sk.seed, this.privKey.pk.seed, var11);
      var11 = new ADRS();
      var11.setTypeAndClear(3);
      var11.setTreeAddress(var8);
      var11.setKeyPairAddress(var10);
      byte[] var13 = var4.pkFromSig(var12, var7, this.privKey.pk.seed, var11);
      ADRS var14 = new ADRS();
      var14.setTypeAndClear(2);
      HT var15 = new HT(var2, this.privKey.getSeed(), this.privKey.getPublicSeed());
      byte[] var16 = var15.sign(var13, var8, var10);
      byte[][] var17 = new byte[var12.length + 2][];
      var17[0] = var5;

      for (int var18 = 0; var18 != var12.length; var18++) {
         var17[1 + var18] = Arrays.concatenate(var12[var18].sk, Arrays.concatenate(var12[var18].authPath));
      }

      var17[var17.length - 1] = var16;
      return Arrays.concatenate(var17);
   }

   @Override
   public boolean verifySignature(byte[] var1, byte[] var2) {
      SPHINCSPlusEngine var3 = this.pubKey.getParameters().getEngine();
      var3.init(this.pubKey.getSeed());
      ADRS var4 = new ADRS();
      SIG var5 = new SIG(var3.N, var3.K, var3.A, var3.D, var3.H_PRIME, var3.WOTS_LEN, var2);
      byte[] var6 = var5.getR();
      SIG_FORS[] var7 = var5.getSIG_FORS();
      SIG_XMSS[] var8 = var5.getSIG_HT();
      IndexedDigest var9 = var3.H_msg(var6, this.pubKey.getSeed(), this.pubKey.getRoot(), var1);
      byte[] var10 = var9.digest;
      long var11 = var9.idx_tree;
      int var13 = var9.idx_leaf;
      var4.setTypeAndClear(3);
      var4.setLayerAddress(0);
      var4.setTreeAddress(var11);
      var4.setKeyPairAddress(var13);
      byte[] var14 = new Fors(var3).pkFromSig(var7, var10, this.pubKey.getSeed(), var4);
      var4.setTypeAndClear(2);
      var4.setLayerAddress(0);
      var4.setTreeAddress(var11);
      var4.setKeyPairAddress(var13);
      HT var15 = new HT(var3, null, this.pubKey.getSeed());
      return var15.verify(var14, var8, this.pubKey.getSeed(), var11, var13, this.pubKey.getRoot());
   }
}
