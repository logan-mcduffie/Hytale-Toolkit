package org.bouncycastle.pqc.crypto.ntruprime;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.util.Arrays;

public class SNTRUPrimeKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private SNTRUPrimeKeyGenerationParameters params;

   public SNTRUPrimeKeyGenerationParameters getParams() {
      return this.params;
   }

   @Override
   public void init(KeyGenerationParameters var1) {
      this.params = (SNTRUPrimeKeyGenerationParameters)var1;
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      int var1 = this.params.getSntrupParams().getP();
      int var2 = this.params.getSntrupParams().getQ();
      int var3 = this.params.getSntrupParams().getW();
      byte[] var4 = new byte[var1];
      byte[] var5 = new byte[var1];

      do {
         Utils.getRandomSmallPolynomial(this.params.getRandom(), var4);
      } while (!Utils.isInvertiblePolynomialInR3(var4, var5, var1));

      byte[] var6 = new byte[var1];
      Utils.getRandomShortPolynomial(this.params.getRandom(), var6, var1, var3);
      short[] var7 = new short[var1];
      Utils.getOneThirdInverseInRQ(var7, var6, var1, var2);
      short[] var8 = new short[var1];
      Utils.multiplicationInRQ(var8, var7, var4, var1, var2);
      byte[] var9 = new byte[this.params.getSntrupParams().getPublicKeyBytes()];
      Utils.getEncodedPolynomial(var9, var8, var1, var2);
      SNTRUPrimePublicKeyParameters var10 = new SNTRUPrimePublicKeyParameters(this.params.getSntrupParams(), var9);
      byte[] var11 = new byte[(var1 + 3) / 4];
      Utils.getEncodedSmallPolynomial(var11, var6, var1);
      byte[] var12 = new byte[(var1 + 3) / 4];
      Utils.getEncodedSmallPolynomial(var12, var5, var1);
      byte[] var13 = new byte[(var1 + 3) / 4];
      this.params.getRandom().nextBytes(var13);
      byte[] var14 = new byte[]{4};
      byte[] var15 = Utils.getHashWithPrefix(var14, var9);
      SNTRUPrimePrivateKeyParameters var16 = new SNTRUPrimePrivateKeyParameters(
         this.params.getSntrupParams(), var11, var12, var9, var13, Arrays.copyOfRange(var15, 0, var15.length / 2)
      );
      return new AsymmetricCipherKeyPair((AsymmetricKeyParameter)var10, (AsymmetricKeyParameter)var16);
   }
}
