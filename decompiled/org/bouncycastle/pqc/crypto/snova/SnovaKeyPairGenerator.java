package org.bouncycastle.pqc.crypto.snova;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.util.Arrays;

public class SnovaKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private SnovaEngine engine;
   private static final int seedLength = 48;
   static final int publicSeedLength = 16;
   static final int privateSeedLength = 32;
   private SnovaParameters params;
   private SecureRandom random;
   private boolean initialized;

   @Override
   public void init(KeyGenerationParameters var1) {
      SnovaKeyGenerationParameters var2 = (SnovaKeyGenerationParameters)var1;
      this.params = var2.getParameters();
      this.random = var2.getRandom();
      this.initialized = true;
      this.engine = new SnovaEngine(this.params);
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      if (!this.initialized) {
         throw new IllegalStateException("SNOVA key pair generator not initialized");
      } else {
         byte[] var1 = new byte[48];
         this.random.nextBytes(var1);
         byte[] var2 = new byte[this.params.getPublicKeyLength()];
         byte[] var3 = new byte[this.params.getPrivateKeyLength()];
         byte[] var4 = Arrays.copyOfRange(var1, 0, 16);
         byte[] var5 = Arrays.copyOfRange(var1, 16, var1.length);
         SnovaKeyElements var6 = new SnovaKeyElements(this.params);
         System.arraycopy(var4, 0, var2, 0, var4.length);
         this.engine.genMap1T12Map2(var6, var4, var5);
         this.engine.genP22(var2, var4.length, var6.T12, var6.map1.p21, var6.map2.f12);
         System.arraycopy(var4, 0, var2, 0, var4.length);
         if (this.params.isSkIsSeed()) {
            var3 = var1;
         } else {
            int var7 = this.params.getO();
            int var8 = this.params.getLsq();
            int var9 = this.params.getV();
            int var10 = var7 * this.params.getAlpha() * var8 * 4 + var9 * var7 * var8 + (var7 * var9 * var9 + var7 * var9 * var7 + var7 * var7 * var9) * var8;
            byte[] var11 = new byte[var10];
            int var12 = 0;
            var12 = SnovaKeyElements.copy3d(var6.map1.aAlpha, var11, var12);
            var12 = SnovaKeyElements.copy3d(var6.map1.bAlpha, var11, var12);
            var12 = SnovaKeyElements.copy3d(var6.map1.qAlpha1, var11, var12);
            var12 = SnovaKeyElements.copy3d(var6.map1.qAlpha2, var11, var12);
            var12 = SnovaKeyElements.copy3d(var6.T12, var11, var12);
            var12 = SnovaKeyElements.copy4d(var6.map2.f11, var11, var12);
            var12 = SnovaKeyElements.copy4d(var6.map2.f12, var11, var12);
            SnovaKeyElements.copy4d(var6.map2.f21, var11, var12);
            GF16Utils.encodeMergeInHalf(var11, var10, var3);
            System.arraycopy(var1, 0, var3, var3.length - 48, 48);
         }

         return new AsymmetricCipherKeyPair(
            (AsymmetricKeyParameter)(new SnovaPublicKeyParameters(this.params, var2)),
            (AsymmetricKeyParameter)(new SnovaPrivateKeyParameters(this.params, var3))
         );
      }
   }
}
