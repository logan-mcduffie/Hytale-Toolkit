package org.bouncycastle.crypto.agreement;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.generators.DHKeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DHKeyGenerationParameters;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;

public class DHAgreement {
   private static final BigInteger ONE = BigInteger.valueOf(1L);
   private DHPrivateKeyParameters key;
   private DHParameters dhParams;
   private BigInteger privateValue;
   private SecureRandom random;

   public void init(CipherParameters var1) {
      AsymmetricKeyParameter var2;
      if (var1 instanceof ParametersWithRandom) {
         ParametersWithRandom var3 = (ParametersWithRandom)var1;
         this.random = var3.getRandom();
         var2 = (AsymmetricKeyParameter)var3.getParameters();
      } else {
         this.random = CryptoServicesRegistrar.getSecureRandom();
         var2 = (AsymmetricKeyParameter)var1;
      }

      if (!(var2 instanceof DHPrivateKeyParameters)) {
         throw new IllegalArgumentException("DHEngine expects DHPrivateKeyParameters");
      } else {
         this.key = (DHPrivateKeyParameters)var2;
         this.dhParams = this.key.getParameters();
         CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("DH", this.key));
      }
   }

   public BigInteger calculateMessage() {
      DHKeyPairGenerator var1 = new DHKeyPairGenerator();
      var1.init(new DHKeyGenerationParameters(this.random, this.dhParams));
      AsymmetricCipherKeyPair var2 = var1.generateKeyPair();
      this.privateValue = ((DHPrivateKeyParameters)var2.getPrivate()).getX();
      return ((DHPublicKeyParameters)var2.getPublic()).getY();
   }

   public BigInteger calculateAgreement(DHPublicKeyParameters var1, BigInteger var2) {
      if (!var1.getParameters().equals(this.dhParams)) {
         throw new IllegalArgumentException("Diffie-Hellman public key has wrong parameters.");
      } else {
         BigInteger var3 = this.dhParams.getP();
         BigInteger var4 = var1.getY();
         if (var4 != null && var4.compareTo(ONE) > 0 && var4.compareTo(var3.subtract(ONE)) < 0) {
            BigInteger var5 = var4.modPow(this.privateValue, var3);
            if (var5.equals(ONE)) {
               throw new IllegalStateException("Shared key can't be 1");
            } else {
               return var2.modPow(this.key.getX(), var3).multiply(var5).mod(var3);
            }
         } else {
            throw new IllegalArgumentException("Diffie-Hellman public key is weak");
         }
      }
   }
}
