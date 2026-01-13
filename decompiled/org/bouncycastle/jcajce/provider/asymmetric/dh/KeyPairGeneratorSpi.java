package org.bouncycastle.jcajce.provider.asymmetric.dh;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Hashtable;
import javax.crypto.spec.DHParameterSpec;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.generators.DHBasicKeyPairGenerator;
import org.bouncycastle.crypto.generators.DHParametersGenerator;
import org.bouncycastle.crypto.params.DHKeyGenerationParameters;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.PrimeCertaintyCalculator;
import org.bouncycastle.jcajce.spec.DHDomainParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Integers;

public class KeyPairGeneratorSpi extends KeyPairGenerator {
   private static Hashtable params = new Hashtable();
   private static Object lock = new Object();
   DHKeyGenerationParameters param;
   DHBasicKeyPairGenerator engine = new DHBasicKeyPairGenerator();
   int strength = 2048;
   SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
   boolean initialised = false;

   public KeyPairGeneratorSpi() {
      super("DH");
   }

   @Override
   public void initialize(int var1, SecureRandom var2) {
      this.strength = var1;
      this.random = var2;
      this.initialised = false;
   }

   @Override
   public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      if (!(var1 instanceof DHParameterSpec)) {
         throw new InvalidAlgorithmParameterException("parameter object not a DHParameterSpec");
      } else {
         DHParameterSpec var3 = (DHParameterSpec)var1;

         try {
            this.param = this.convertParams(var2, var3);
         } catch (IllegalArgumentException var5) {
            throw new InvalidAlgorithmParameterException(var5.getMessage(), var5);
         }

         this.engine.init(this.param);
         this.initialised = true;
      }
   }

   private DHKeyGenerationParameters convertParams(SecureRandom var1, DHParameterSpec var2) {
      return var2 instanceof DHDomainParameterSpec
         ? new DHKeyGenerationParameters(var1, ((DHDomainParameterSpec)var2).getDomainParameters())
         : new DHKeyGenerationParameters(var1, new DHParameters(var2.getP(), var2.getG(), null, var2.getL()));
   }

   @Override
   public KeyPair generateKeyPair() {
      if (!this.initialised) {
         Integer var1 = Integers.valueOf(this.strength);
         if (params.containsKey(var1)) {
            this.param = (DHKeyGenerationParameters)params.get(var1);
         } else {
            DHParameterSpec var2 = BouncyCastleProvider.CONFIGURATION.getDHDefaultParameters(this.strength);
            if (var2 != null) {
               this.param = this.convertParams(this.random, var2);
            } else {
               synchronized (lock) {
                  if (params.containsKey(var1)) {
                     this.param = (DHKeyGenerationParameters)params.get(var1);
                  } else {
                     DHParametersGenerator var4 = new DHParametersGenerator();
                     var4.init(this.strength, PrimeCertaintyCalculator.getDefaultCertainty(this.strength), this.random);
                     this.param = new DHKeyGenerationParameters(this.random, var4.generateParameters());
                     params.put(var1, this.param);
                  }
               }
            }
         }

         this.engine.init(this.param);
         this.initialised = true;
      }

      AsymmetricCipherKeyPair var7 = this.engine.generateKeyPair();
      DHPublicKeyParameters var8 = (DHPublicKeyParameters)var7.getPublic();
      DHPrivateKeyParameters var9 = (DHPrivateKeyParameters)var7.getPrivate();
      return new KeyPair(new BCDHPublicKey(var8), new BCDHPrivateKey(var9));
   }
}
