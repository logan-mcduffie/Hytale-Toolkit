package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECCSIKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECCSIPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECCSIPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.BigIntegers;

public class ECCSIKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private BigInteger q;
   private ECPoint G;
   private Digest digest;
   private ECCSIKeyGenerationParameters parameters;

   @Override
   public void init(KeyGenerationParameters var1) {
      this.parameters = (ECCSIKeyGenerationParameters)var1;
      this.q = this.parameters.getQ();
      this.G = this.parameters.getG();
      this.digest = this.parameters.getDigest();
      CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("ECCSI", this.parameters.getN(), null, CryptoServicePurpose.KEYGEN));
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      SecureRandom var1 = this.parameters.getRandom();
      this.digest.reset();
      byte[] var2 = this.parameters.getId();
      ECPoint var3 = this.parameters.getKPAK();
      BigInteger var4 = BigIntegers.createRandomBigInteger(256, var1).mod(this.q);
      ECPoint var5 = this.G.multiply(var4).normalize();
      byte[] var6 = this.G.getEncoded(false);
      this.digest.update(var6, 0, var6.length);
      var6 = var3.getEncoded(false);
      this.digest.update(var6, 0, var6.length);
      this.digest.update(var2, 0, var2.length);
      var6 = var5.getEncoded(false);
      this.digest.update(var6, 0, var6.length);
      var6 = new byte[this.digest.getDigestSize()];
      this.digest.doFinal(var6, 0);
      BigInteger var7 = new BigInteger(1, var6).mod(this.q);
      BigInteger var8 = this.parameters.computeSSK(var7.multiply(var4));
      ECCSIPublicKeyParameters var9 = new ECCSIPublicKeyParameters(var5);
      return new AsymmetricCipherKeyPair(
         (AsymmetricKeyParameter)(new ECCSIPublicKeyParameters(var5)), (AsymmetricKeyParameter)(new ECCSIPrivateKeyParameters(var8, var9))
      );
   }
}
