package org.bouncycastle.pqc.crypto.ntruprime;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.util.Arrays;

public class NTRULPRimeKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private NTRULPRimeKeyGenerationParameters params;

   public NTRULPRimeKeyGenerationParameters getParams() {
      return this.params;
   }

   @Override
   public void init(KeyGenerationParameters var1) {
      this.params = (NTRULPRimeKeyGenerationParameters)var1;
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      int var1 = this.params.getNtrulprParams().getP();
      int var2 = this.params.getNtrulprParams().getQ();
      int var3 = this.params.getNtrulprParams().getW();
      byte[] var4 = new byte[32];
      this.params.getRandom().nextBytes(var4);
      short[] var5 = new short[var1];
      Utils.generatePolynomialInRQFromSeed(var5, var4, var1, var2);
      byte[] var6 = new byte[var1];
      Utils.getRandomShortPolynomial(this.params.getRandom(), var6, var1, var3);
      short[] var7 = new short[var1];
      Utils.multiplicationInRQ(var7, var5, var6, var1, var2);
      short[] var8 = new short[var1];
      Utils.roundPolynomial(var8, var7);
      byte[] var9 = new byte[this.params.getNtrulprParams().getPublicKeyBytes() - 32];
      Utils.getRoundedEncodedPolynomial(var9, var8, var1, var2);
      NTRULPRimePublicKeyParameters var10 = new NTRULPRimePublicKeyParameters(this.params.getNtrulprParams(), var4, var9);
      byte[] var11 = new byte[(var1 + 3) / 4];
      Utils.getEncodedSmallPolynomial(var11, var6, var1);
      byte[] var12 = new byte[32];
      this.params.getRandom().nextBytes(var12);
      byte[] var13 = new byte[]{4};
      byte[] var14 = Utils.getHashWithPrefix(var13, var10.getEncoded());
      NTRULPRimePrivateKeyParameters var15 = new NTRULPRimePrivateKeyParameters(
         this.params.getNtrulprParams(), var11, var10.getEncoded(), var12, Arrays.copyOfRange(var14, 0, var14.length / 2)
      );
      return new AsymmetricCipherKeyPair((AsymmetricKeyParameter)var10, (AsymmetricKeyParameter)var15);
   }
}
