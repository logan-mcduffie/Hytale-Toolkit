package org.bouncycastle.pqc.crypto.xmss;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public final class XMSSKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private XMSSParameters params;
   private SecureRandom prng;

   @Override
   public void init(KeyGenerationParameters var1) {
      XMSSKeyGenerationParameters var2 = (XMSSKeyGenerationParameters)var1;
      this.prng = var2.getRandom();
      this.params = var2.getParameters();
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      XMSSPrivateKeyParameters var1 = this.generatePrivateKey(this.params, this.prng);
      XMSSNode var2 = var1.getBDSState().getRoot();
      var1 = new XMSSPrivateKeyParameters.Builder(this.params)
         .withSecretKeySeed(var1.getSecretKeySeed())
         .withSecretKeyPRF(var1.getSecretKeyPRF())
         .withPublicSeed(var1.getPublicSeed())
         .withRoot(var2.getValue())
         .withBDSState(var1.getBDSState())
         .build();
      XMSSPublicKeyParameters var3 = new XMSSPublicKeyParameters.Builder(this.params).withRoot(var2.getValue()).withPublicSeed(var1.getPublicSeed()).build();
      return new AsymmetricCipherKeyPair((AsymmetricKeyParameter)var3, (AsymmetricKeyParameter)var1);
   }

   private XMSSPrivateKeyParameters generatePrivateKey(XMSSParameters var1, SecureRandom var2) {
      int var3 = var1.getTreeDigestSize();
      byte[] var4 = new byte[var3];
      var2.nextBytes(var4);
      byte[] var5 = new byte[var3];
      var2.nextBytes(var5);
      byte[] var6 = new byte[var3];
      var2.nextBytes(var6);
      return new XMSSPrivateKeyParameters.Builder(var1)
         .withSecretKeySeed(var4)
         .withSecretKeyPRF(var5)
         .withPublicSeed(var6)
         .withBDSState(new BDS(var1, var6, var4, (OTSHashAddress)new OTSHashAddress.Builder().build()))
         .build();
   }
}
