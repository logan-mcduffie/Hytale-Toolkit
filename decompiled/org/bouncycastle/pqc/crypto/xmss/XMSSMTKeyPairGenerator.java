package org.bouncycastle.pqc.crypto.xmss;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public final class XMSSMTKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private XMSSMTParameters params;
   private XMSSParameters xmssParams;
   private SecureRandom prng;

   @Override
   public void init(KeyGenerationParameters var1) {
      XMSSMTKeyGenerationParameters var2 = (XMSSMTKeyGenerationParameters)var1;
      this.prng = var2.getRandom();
      this.params = var2.getParameters();
      this.xmssParams = this.params.getXMSSParameters();
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      XMSSMTPrivateKeyParameters var1 = this.generatePrivateKey(new XMSSMTPrivateKeyParameters.Builder(this.params).build().getBDSState());
      this.xmssParams.getWOTSPlus().importKeys(new byte[this.params.getTreeDigestSize()], var1.getPublicSeed());
      int var3 = this.params.getLayers() - 1;
      OTSHashAddress var4 = (OTSHashAddress)new OTSHashAddress.Builder().withLayerAddress(var3).build();
      BDS var5 = new BDS(this.xmssParams, var1.getPublicSeed(), var1.getSecretKeySeed(), var4);
      XMSSNode var6 = var5.getRoot();
      var1.getBDSState().put(var3, var5);
      var1 = new XMSSMTPrivateKeyParameters.Builder(this.params)
         .withSecretKeySeed(var1.getSecretKeySeed())
         .withSecretKeyPRF(var1.getSecretKeyPRF())
         .withPublicSeed(var1.getPublicSeed())
         .withRoot(var6.getValue())
         .withBDSState(var1.getBDSState())
         .build();
      XMSSMTPublicKeyParameters var2 = new XMSSMTPublicKeyParameters.Builder(this.params)
         .withRoot(var6.getValue())
         .withPublicSeed(var1.getPublicSeed())
         .build();
      return new AsymmetricCipherKeyPair((AsymmetricKeyParameter)var2, (AsymmetricKeyParameter)var1);
   }

   private XMSSMTPrivateKeyParameters generatePrivateKey(BDSStateMap var1) {
      int var2 = this.params.getTreeDigestSize();
      byte[] var3 = new byte[var2];
      this.prng.nextBytes(var3);
      byte[] var4 = new byte[var2];
      this.prng.nextBytes(var4);
      byte[] var5 = new byte[var2];
      this.prng.nextBytes(var5);
      Object var6 = null;
      return new XMSSMTPrivateKeyParameters.Builder(this.params).withSecretKeySeed(var3).withSecretKeyPRF(var4).withPublicSeed(var5).withBDSState(var1).build();
   }
}
