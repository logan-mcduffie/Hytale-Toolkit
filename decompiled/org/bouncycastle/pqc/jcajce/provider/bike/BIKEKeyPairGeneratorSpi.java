package org.bouncycastle.pqc.jcajce.provider.bike;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.jcajce.util.SpecUtil;
import org.bouncycastle.pqc.crypto.bike.BIKEKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.bike.BIKEKeyPairGenerator;
import org.bouncycastle.pqc.crypto.bike.BIKEParameters;
import org.bouncycastle.pqc.crypto.bike.BIKEPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.bike.BIKEPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.spec.BIKEParameterSpec;
import org.bouncycastle.util.Strings;

public class BIKEKeyPairGeneratorSpi extends KeyPairGenerator {
   private static Map parameters = new HashMap();
   BIKEKeyGenerationParameters param;
   BIKEKeyPairGenerator engine = new BIKEKeyPairGenerator();
   SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
   boolean initialised = false;

   public BIKEKeyPairGeneratorSpi() {
      super("BIKE");
   }

   @Override
   public void initialize(int var1, SecureRandom var2) {
      throw new IllegalArgumentException("use AlgorithmParameterSpec");
   }

   @Override
   public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      String var3 = getNameFromParams(var1);
      if (var3 != null) {
         this.param = new BIKEKeyGenerationParameters(var2, (BIKEParameters)parameters.get(var3));
         this.engine.init(this.param);
         this.initialised = true;
      } else {
         throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + var1);
      }
   }

   private static String getNameFromParams(AlgorithmParameterSpec var0) {
      if (var0 instanceof BIKEParameterSpec) {
         BIKEParameterSpec var1 = (BIKEParameterSpec)var0;
         return var1.getName();
      } else {
         return Strings.toLowerCase(SpecUtil.getNameFrom(var0));
      }
   }

   @Override
   public KeyPair generateKeyPair() {
      if (!this.initialised) {
         this.param = new BIKEKeyGenerationParameters(this.random, BIKEParameters.bike128);
         this.engine.init(this.param);
         this.initialised = true;
      }

      AsymmetricCipherKeyPair var1 = this.engine.generateKeyPair();
      BIKEPublicKeyParameters var2 = (BIKEPublicKeyParameters)var1.getPublic();
      BIKEPrivateKeyParameters var3 = (BIKEPrivateKeyParameters)var1.getPrivate();
      return new KeyPair(new BCBIKEPublicKey(var2), new BCBIKEPrivateKey(var3));
   }

   static {
      parameters.put("bike128", BIKEParameters.bike128);
      parameters.put("bike192", BIKEParameters.bike192);
      parameters.put("bike256", BIKEParameters.bike256);
      parameters.put(BIKEParameterSpec.bike128.getName(), BIKEParameters.bike128);
      parameters.put(BIKEParameterSpec.bike192.getName(), BIKEParameters.bike192);
      parameters.put(BIKEParameterSpec.bike256.getName(), BIKEParameters.bike256);
   }
}
