package org.bouncycastle.jcajce.provider.asymmetric.dsa;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAParameterSpec;
import java.util.Hashtable;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.DSAKeyPairGenerator;
import org.bouncycastle.crypto.generators.DSAParametersGenerator;
import org.bouncycastle.crypto.params.DSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.DSAParameterGenerationParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.PrimeCertaintyCalculator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Properties;

public class KeyPairGeneratorSpi extends KeyPairGenerator {
   private static Hashtable params = new Hashtable();
   private static Object lock = new Object();
   DSAKeyGenerationParameters param;
   DSAKeyPairGenerator engine = new DSAKeyPairGenerator();
   int strength = 2048;
   SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
   boolean initialised = false;

   public KeyPairGeneratorSpi() {
      super("DSA");
   }

   @Override
   public void initialize(int var1, SecureRandom var2) {
      if (var1 >= 512 && var1 <= 4096 && (var1 >= 1024 || var1 % 64 == 0) && (var1 < 1024 || var1 % 1024 == 0)) {
         DSAParameterSpec var3 = BouncyCastleProvider.CONFIGURATION.getDSADefaultParameters(var1);
         if (var3 != null) {
            this.param = new DSAKeyGenerationParameters(var2, new DSAParameters(var3.getP(), var3.getQ(), var3.getG()));
            this.engine.init(this.param);
            this.initialised = true;
         } else {
            this.strength = var1;
            this.random = var2;
            this.initialised = false;
         }
      } else {
         throw new InvalidParameterException("strength must be from 512 - 4096 and a multiple of 1024 above 1024");
      }
   }

   @Override
   public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      if (!(var1 instanceof DSAParameterSpec)) {
         throw new InvalidAlgorithmParameterException("parameter object not a DSAParameterSpec");
      } else {
         DSAParameterSpec var3 = (DSAParameterSpec)var1;
         this.param = new DSAKeyGenerationParameters(var2, new DSAParameters(var3.getP(), var3.getQ(), var3.getG()));
         this.engine.init(this.param);
         this.initialised = true;
      }
   }

   @Override
   public KeyPair generateKeyPair() {
      if (!this.initialised) {
         Integer var1 = Integers.valueOf(this.strength);
         if (params.containsKey(var1)) {
            this.param = (DSAKeyGenerationParameters)params.get(var1);
         } else {
            synchronized (lock) {
               if (params.containsKey(var1)) {
                  this.param = (DSAKeyGenerationParameters)params.get(var1);
               } else {
                  int var5 = PrimeCertaintyCalculator.getDefaultCertainty(this.strength);
                  DSAParametersGenerator var3;
                  if (this.strength == 1024) {
                     var3 = new DSAParametersGenerator();
                     if (Properties.isOverrideSet("org.bouncycastle.dsa.FIPS186-2for1024bits")) {
                        var3.init(this.strength, var5, this.random);
                     } else {
                        DSAParameterGenerationParameters var4 = new DSAParameterGenerationParameters(1024, 160, var5, this.random);
                        var3.init(var4);
                     }
                  } else if (this.strength > 1024) {
                     DSAParameterGenerationParameters var11 = new DSAParameterGenerationParameters(this.strength, 256, var5, this.random);
                     var3 = new DSAParametersGenerator(SHA256Digest.newInstance());
                     var3.init(var11);
                  } else {
                     var3 = new DSAParametersGenerator();
                     var3.init(this.strength, var5, this.random);
                  }

                  this.param = new DSAKeyGenerationParameters(this.random, var3.generateParameters());
                  params.put(var1, this.param);
               }
            }
         }

         this.engine.init(this.param);
         this.initialised = true;
      }

      AsymmetricCipherKeyPair var8 = this.engine.generateKeyPair();
      DSAPublicKeyParameters var9 = (DSAPublicKeyParameters)var8.getPublic();
      DSAPrivateKeyParameters var10 = (DSAPrivateKeyParameters)var8.getPrivate();
      return new KeyPair(new BCDSAPublicKey(var9), new BCDSAPrivateKey(var10));
   }
}
