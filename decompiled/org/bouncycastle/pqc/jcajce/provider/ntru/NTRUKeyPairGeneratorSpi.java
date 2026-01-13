package org.bouncycastle.pqc.jcajce.provider.ntru;

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
import org.bouncycastle.pqc.crypto.ntru.NTRUKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUKeyPairGenerator;
import org.bouncycastle.pqc.crypto.ntru.NTRUParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.spec.NTRUParameterSpec;
import org.bouncycastle.util.Strings;

public class NTRUKeyPairGeneratorSpi extends KeyPairGenerator {
   private static Map parameters = new HashMap();
   NTRUKeyGenerationParameters param;
   NTRUKeyPairGenerator engine = new NTRUKeyPairGenerator();
   SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
   boolean initialised = false;

   public NTRUKeyPairGeneratorSpi() {
      super("NTRU");
   }

   @Override
   public void initialize(int var1, SecureRandom var2) {
      throw new IllegalArgumentException("use AlgorithmParameterSpec");
   }

   @Override
   public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      String var3 = getNameFromParams(var1);
      if (var3 != null) {
         this.param = new NTRUKeyGenerationParameters(var2, (NTRUParameters)parameters.get(var3));
         this.engine.init(this.param);
         this.initialised = true;
      } else {
         throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + var1);
      }
   }

   private static String getNameFromParams(AlgorithmParameterSpec var0) {
      if (var0 instanceof NTRUParameterSpec) {
         NTRUParameterSpec var1 = (NTRUParameterSpec)var0;
         return var1.getName();
      } else {
         return Strings.toLowerCase(SpecUtil.getNameFrom(var0));
      }
   }

   @Override
   public KeyPair generateKeyPair() {
      if (!this.initialised) {
         this.param = new NTRUKeyGenerationParameters(this.random, NTRUParameters.ntruhps2048509);
         this.engine.init(this.param);
         this.initialised = true;
      }

      AsymmetricCipherKeyPair var1 = this.engine.generateKeyPair();
      NTRUPublicKeyParameters var2 = (NTRUPublicKeyParameters)var1.getPublic();
      NTRUPrivateKeyParameters var3 = (NTRUPrivateKeyParameters)var1.getPrivate();
      return new KeyPair(new BCNTRUPublicKey(var2), new BCNTRUPrivateKey(var3));
   }

   static {
      parameters.put(NTRUParameterSpec.ntruhps2048509.getName(), NTRUParameters.ntruhps2048509);
      parameters.put(NTRUParameterSpec.ntruhps2048677.getName(), NTRUParameters.ntruhps2048677);
      parameters.put(NTRUParameterSpec.ntruhps4096821.getName(), NTRUParameters.ntruhps4096821);
      parameters.put(NTRUParameterSpec.ntruhps40961229.getName(), NTRUParameters.ntruhps40961229);
      parameters.put(NTRUParameterSpec.ntruhrss701.getName(), NTRUParameters.ntruhrss701);
      parameters.put(NTRUParameterSpec.ntruhrss1373.getName(), NTRUParameters.ntruhrss1373);
   }
}
