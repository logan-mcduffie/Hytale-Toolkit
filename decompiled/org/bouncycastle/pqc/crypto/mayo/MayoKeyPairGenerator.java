package org.bouncycastle.pqc.crypto.mayo;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.GF16;
import org.bouncycastle.util.Longs;

public class MayoKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private MayoParameters p;
   private SecureRandom random;

   @Override
   public void init(KeyGenerationParameters var1) {
      this.p = ((MayoKeyGenerationParameters)var1).getParameters();
      this.random = var1.getRandom();
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      int var1 = this.p.getMVecLimbs();
      int var2 = this.p.getM();
      int var3 = this.p.getV();
      int var4 = this.p.getO();
      int var5 = this.p.getOBytes();
      int var6 = this.p.getP1Limbs();
      int var7 = this.p.getP3Limbs();
      int var8 = this.p.getPkSeedBytes();
      int var9 = this.p.getSkSeedBytes();
      byte[] var10 = new byte[this.p.getCpkBytes()];
      byte[] var11 = new byte[this.p.getCskBytes()];
      byte[] var12 = new byte[var8 + var5];
      long[] var13 = new long[var6 + this.p.getP2Limbs()];
      long[] var14 = new long[var4 * var4 * var1];
      byte[] var15 = new byte[var3 * var4];
      this.random.nextBytes(var11);
      SHAKEDigest var16 = new SHAKEDigest(256);
      var16.update(var11, 0, var9);
      var16.doFinal(var12, 0, var8 + var5);
      GF16.decode(var12, var8, var15, 0, var15.length);
      Utils.expandP1P2(this.p, var13, var12);
      GF16Utils.mulAddMUpperTriangularMatXMat(var1, var13, var15, var13, var6, var3, var4);
      GF16Utils.mulAddMatTransXMMat(var1, var15, var13, var6, var14, var3, var4);
      System.arraycopy(var12, 0, var10, 0, var8);
      long[] var17 = new long[var7];
      int var18 = 0;
      int var19 = var4 * var1;
      int var20 = 0;
      int var21 = 0;

      for (int var22 = 0; var20 < var4; var21 += var1) {
         int var23 = var20;
         int var24 = var21;

         for (int var25 = var22; var23 < var4; var25 += var19) {
            System.arraycopy(var14, var22 + var24, var17, var18, var1);
            if (var20 != var23) {
               Longs.xorTo(var1, var14, var25 + var21, var17, var18);
            }

            var18 += var1;
            var23++;
            var24 += var1;
         }

         var20++;
         var22 += var19;
      }

      Utils.packMVecs(var17, var10, var8, var7 / var1, var2);
      Arrays.clear(var15);
      Arrays.clear(var14);
      return new AsymmetricCipherKeyPair(
         (AsymmetricKeyParameter)(new MayoPublicKeyParameters(this.p, var10)), (AsymmetricKeyParameter)(new MayoPrivateKeyParameters(this.p, var11))
      );
   }
}
