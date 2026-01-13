package org.bouncycastle.pqc.crypto.crystals.dilithium;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class DilithiumKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private DilithiumParameters dilithiumParams;
   private SecureRandom random;

   private void initialize(KeyGenerationParameters var1) {
      this.dilithiumParams = ((DilithiumKeyGenerationParameters)var1).getParameters();
      this.random = var1.getRandom();
   }

   private AsymmetricCipherKeyPair genKeyPair() {
      DilithiumEngine var1 = this.dilithiumParams.getEngine(this.random);
      byte[][] var2 = var1.generateKeyPair();
      DilithiumPublicKeyParameters var3 = new DilithiumPublicKeyParameters(this.dilithiumParams, var2[0], var2[6]);
      DilithiumPrivateKeyParameters var4 = new DilithiumPrivateKeyParameters(
         this.dilithiumParams, var2[0], var2[1], var2[2], var2[3], var2[4], var2[5], var2[6]
      );
      return new AsymmetricCipherKeyPair((AsymmetricKeyParameter)var3, (AsymmetricKeyParameter)var4);
   }

   @Override
   public void init(KeyGenerationParameters var1) {
      this.initialize(var1);
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      return this.genKeyPair();
   }

   public AsymmetricCipherKeyPair internalGenerateKeyPair(byte[] var1) {
      DilithiumEngine var2 = this.dilithiumParams.getEngine(this.random);
      byte[][] var3 = var2.generateKeyPairInternal(var1);
      DilithiumPublicKeyParameters var4 = new DilithiumPublicKeyParameters(this.dilithiumParams, var3[0], var3[6]);
      DilithiumPrivateKeyParameters var5 = new DilithiumPrivateKeyParameters(
         this.dilithiumParams, var3[0], var3[1], var3[2], var3[3], var3[4], var3[5], var3[6]
      );
      return new AsymmetricCipherKeyPair((AsymmetricKeyParameter)var4, (AsymmetricKeyParameter)var5);
   }
}
