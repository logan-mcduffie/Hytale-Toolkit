package org.bouncycastle.pqc.crypto.bike;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class BIKEKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private SecureRandom random;
   private int r;
   private int l;
   private int L_BYTE;
   private int R_BYTE;
   private BIKEKeyGenerationParameters bikeKeyGenerationParameters;

   @Override
   public void init(KeyGenerationParameters var1) {
      this.bikeKeyGenerationParameters = (BIKEKeyGenerationParameters)var1;
      this.random = var1.getRandom();
      this.r = this.bikeKeyGenerationParameters.getParameters().getR();
      this.l = this.bikeKeyGenerationParameters.getParameters().getL();
      this.L_BYTE = this.l / 8;
      this.R_BYTE = (this.r + 7) / 8;
   }

   private AsymmetricCipherKeyPair genKeyPair() {
      BIKEEngine var1 = this.bikeKeyGenerationParameters.getParameters().getEngine();
      byte[] var2 = new byte[this.R_BYTE];
      byte[] var3 = new byte[this.R_BYTE];
      byte[] var4 = new byte[this.R_BYTE];
      byte[] var5 = new byte[this.L_BYTE];
      var1.genKeyPair(var2, var3, var5, var4, this.random);
      BIKEPublicKeyParameters var6 = new BIKEPublicKeyParameters(this.bikeKeyGenerationParameters.getParameters(), var4);
      BIKEPrivateKeyParameters var7 = new BIKEPrivateKeyParameters(this.bikeKeyGenerationParameters.getParameters(), var2, var3, var5);
      return new AsymmetricCipherKeyPair((AsymmetricKeyParameter)var6, (AsymmetricKeyParameter)var7);
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      return this.genKeyPair();
   }
}
