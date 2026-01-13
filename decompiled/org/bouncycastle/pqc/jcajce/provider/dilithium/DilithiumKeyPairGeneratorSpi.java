package org.bouncycastle.pqc.jcajce.provider.dilithium;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.jcajce.util.SpecUtil;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumKeyPairGenerator;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.spec.DilithiumParameterSpec;
import org.bouncycastle.util.Strings;

public class DilithiumKeyPairGeneratorSpi extends KeyPairGenerator {
   private static Map parameters = new HashMap();
   private final DilithiumParameters dilithiumParameters;
   DilithiumKeyGenerationParameters param;
   DilithiumKeyPairGenerator engine = new DilithiumKeyPairGenerator();
   SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
   boolean initialised = false;

   public DilithiumKeyPairGeneratorSpi() {
      super("DILITHIUM");
      this.dilithiumParameters = null;
   }

   protected DilithiumKeyPairGeneratorSpi(DilithiumParameters var1) {
      super(Strings.toUpperCase(var1.getName()));
      this.dilithiumParameters = var1;
   }

   @Override
   public void initialize(int var1, SecureRandom var2) {
      throw new IllegalArgumentException("use AlgorithmParameterSpec");
   }

   @Override
   public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      String var3 = getNameFromParams(var1);
      if (var3 != null && parameters.containsKey(var3)) {
         DilithiumParameters var4 = (DilithiumParameters)parameters.get(var3);
         this.param = new DilithiumKeyGenerationParameters(var2, var4);
         if (this.dilithiumParameters != null && !var4.getName().equals(this.dilithiumParameters.getName())) {
            throw new InvalidAlgorithmParameterException("key pair generator locked to " + Strings.toUpperCase(this.dilithiumParameters.getName()));
         } else {
            this.engine.init(this.param);
            this.initialised = true;
         }
      } else {
         throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + var1);
      }
   }

   private static String getNameFromParams(AlgorithmParameterSpec var0) {
      if (var0 instanceof DilithiumParameterSpec) {
         DilithiumParameterSpec var1 = (DilithiumParameterSpec)var0;
         return var1.getName();
      } else {
         return Strings.toLowerCase(SpecUtil.getNameFrom(var0));
      }
   }

   @Override
   public KeyPair generateKeyPair() {
      if (!this.initialised) {
         if (this.dilithiumParameters != null) {
            this.param = new DilithiumKeyGenerationParameters(this.random, this.dilithiumParameters);
         } else {
            this.param = new DilithiumKeyGenerationParameters(this.random, DilithiumParameters.dilithium3);
         }

         this.engine.init(this.param);
         this.initialised = true;
      }

      AsymmetricCipherKeyPair var1 = this.engine.generateKeyPair();
      DilithiumPublicKeyParameters var2 = (DilithiumPublicKeyParameters)var1.getPublic();
      DilithiumPrivateKeyParameters var3 = (DilithiumPrivateKeyParameters)var1.getPrivate();
      return new KeyPair(new BCDilithiumPublicKey(var2), new BCDilithiumPrivateKey(var3));
   }

   static {
      parameters.put(DilithiumParameterSpec.dilithium2.getName(), DilithiumParameters.dilithium2);
      parameters.put(DilithiumParameterSpec.dilithium3.getName(), DilithiumParameters.dilithium3);
      parameters.put(DilithiumParameterSpec.dilithium5.getName(), DilithiumParameters.dilithium5);
   }

   public static class Base2 extends DilithiumKeyPairGeneratorSpi {
      public Base2() throws NoSuchAlgorithmException {
         super(DilithiumParameters.dilithium2);
      }
   }

   public static class Base3 extends DilithiumKeyPairGeneratorSpi {
      public Base3() throws NoSuchAlgorithmException {
         super(DilithiumParameters.dilithium3);
      }
   }

   public static class Base5 extends DilithiumKeyPairGeneratorSpi {
      public Base5() throws NoSuchAlgorithmException {
         super(DilithiumParameters.dilithium5);
      }
   }
}
