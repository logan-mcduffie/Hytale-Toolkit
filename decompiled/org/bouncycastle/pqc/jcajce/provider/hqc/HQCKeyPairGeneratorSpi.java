package org.bouncycastle.pqc.jcajce.provider.hqc;

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
import org.bouncycastle.pqc.crypto.hqc.HQCKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCKeyPairGenerator;
import org.bouncycastle.pqc.crypto.hqc.HQCParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.spec.HQCParameterSpec;
import org.bouncycastle.util.Strings;

public class HQCKeyPairGeneratorSpi extends KeyPairGenerator {
   private static Map parameters = new HashMap();
   HQCKeyGenerationParameters param;
   HQCKeyPairGenerator engine = new HQCKeyPairGenerator();
   SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
   boolean initialised = false;

   public HQCKeyPairGeneratorSpi() {
      super("HQC");
   }

   protected HQCKeyPairGeneratorSpi(HQCParameterSpec var1) {
      super(Strings.toUpperCase(var1.getName()));
   }

   @Override
   public void initialize(int var1, SecureRandom var2) {
      throw new IllegalArgumentException("use AlgorithmParameterSpec");
   }

   @Override
   public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      String var3 = getNameFromParams(var1);
      if (var3 != null) {
         this.param = new HQCKeyGenerationParameters(var2, (HQCParameters)parameters.get(var3));
         this.engine.init(this.param);
         this.initialised = true;
      } else {
         throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + var1);
      }
   }

   private static String getNameFromParams(AlgorithmParameterSpec var0) {
      if (var0 instanceof HQCParameterSpec) {
         HQCParameterSpec var1 = (HQCParameterSpec)var0;
         return var1.getName();
      } else {
         return Strings.toLowerCase(SpecUtil.getNameFrom(var0));
      }
   }

   @Override
   public KeyPair generateKeyPair() {
      if (!this.initialised) {
         this.param = new HQCKeyGenerationParameters(this.random, HQCParameters.hqc128);
         this.engine.init(this.param);
         this.initialised = true;
      }

      AsymmetricCipherKeyPair var1 = this.engine.generateKeyPair();
      HQCPublicKeyParameters var2 = (HQCPublicKeyParameters)var1.getPublic();
      HQCPrivateKeyParameters var3 = (HQCPrivateKeyParameters)var1.getPrivate();
      return new KeyPair(new BCHQCPublicKey(var2), new BCHQCPrivateKey(var3));
   }

   static {
      parameters.put("hqc-128", HQCParameters.hqc128);
      parameters.put("hqc-192", HQCParameters.hqc192);
      parameters.put("hqc-256", HQCParameters.hqc256);
      parameters.put(HQCParameterSpec.hqc128.getName(), HQCParameters.hqc128);
      parameters.put(HQCParameterSpec.hqc192.getName(), HQCParameters.hqc192);
      parameters.put(HQCParameterSpec.hqc256.getName(), HQCParameters.hqc256);
   }

   public static class HQC128 extends HQCKeyPairGeneratorSpi {
      public HQC128() throws NoSuchAlgorithmException {
         super(HQCParameterSpec.hqc128);
      }
   }

   public static class HQC192 extends HQCKeyPairGeneratorSpi {
      public HQC192() throws NoSuchAlgorithmException {
         super(HQCParameterSpec.hqc192);
      }
   }

   public static class HQC256 extends HQCKeyPairGeneratorSpi {
      public HQC256() throws NoSuchAlgorithmException {
         super(HQCParameterSpec.hqc256);
      }
   }
}
