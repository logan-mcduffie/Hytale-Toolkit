package org.bouncycastle.pqc.crypto.xwing;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.generators.X25519KeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.X25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.prng.FixedSecureRandom;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMKeyPairGenerator;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters;
import org.bouncycastle.util.Arrays;

public class XWingKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private SecureRandom random;

   private void initialize(KeyGenerationParameters var1) {
      this.random = var1.getRandom();
   }

   static AsymmetricCipherKeyPair genKeyPair(byte[] var0) {
      SHAKEDigest var1 = new SHAKEDigest(256);
      var1.update(var0, 0, var0.length);
      byte[] var2 = new byte[96];
      var1.doOutput(var2, 0, var2.length);
      byte[] var3 = Arrays.copyOfRange(var2, 0, 64);
      byte[] var4 = Arrays.copyOfRange(var2, 64, 96);
      FixedSecureRandom var5 = new FixedSecureRandom(var3);
      MLKEMKeyPairGenerator var6 = new MLKEMKeyPairGenerator();
      var6.init(new MLKEMKeyGenerationParameters(var5, MLKEMParameters.ml_kem_768));
      AsymmetricCipherKeyPair var7 = var6.generateKeyPair();
      MLKEMPublicKeyParameters var8 = (MLKEMPublicKeyParameters)var7.getPublic();
      MLKEMPrivateKeyParameters var9 = (MLKEMPrivateKeyParameters)var7.getPrivate();
      FixedSecureRandom var10 = new FixedSecureRandom(var4);
      X25519KeyPairGenerator var11 = new X25519KeyPairGenerator();
      var11.init(new X25519KeyGenerationParameters(var10));
      AsymmetricCipherKeyPair var12 = var11.generateKeyPair();
      X25519PublicKeyParameters var13 = (X25519PublicKeyParameters)var12.getPublic();
      X25519PrivateKeyParameters var14 = (X25519PrivateKeyParameters)var12.getPrivate();
      return new AsymmetricCipherKeyPair(
         (AsymmetricKeyParameter)(new XWingPublicKeyParameters(var8, var13)),
         (AsymmetricKeyParameter)(new XWingPrivateKeyParameters(var0, var9, var14, var8, var13))
      );
   }

   @Override
   public void init(KeyGenerationParameters var1) {
      this.initialize(var1);
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      byte[] var1 = new byte[32];
      this.random.nextBytes(var1);
      return genKeyPair(var1);
   }
}
