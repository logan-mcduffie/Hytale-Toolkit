package org.bouncycastle.pqc.jcajce.provider.sphincsplus;

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
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusKeyPairGenerator;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusParameters;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.spec.SPHINCSPlusParameterSpec;
import org.bouncycastle.util.Strings;

public class SPHINCSPlusKeyPairGeneratorSpi extends KeyPairGenerator {
   private static Map parameters = new HashMap();
   SPHINCSPlusKeyGenerationParameters param;
   SPHINCSPlusKeyPairGenerator engine = new SPHINCSPlusKeyPairGenerator();
   SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
   boolean initialised = false;

   public SPHINCSPlusKeyPairGeneratorSpi() {
      super("SPHINCS+");
   }

   protected SPHINCSPlusKeyPairGeneratorSpi(SPHINCSPlusParameterSpec var1) {
      super("SPHINCS+-" + Strings.toUpperCase(var1.getName()));
      this.param = new SPHINCSPlusKeyGenerationParameters(this.random, (SPHINCSPlusParameters)parameters.get(var1.getName()));
      this.engine.init(this.param);
      this.initialised = true;
   }

   @Override
   public void initialize(int var1, SecureRandom var2) {
      throw new IllegalArgumentException("use AlgorithmParameterSpec");
   }

   @Override
   public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      String var3 = getNameFromParams(var1);
      if (var3 != null) {
         this.param = new SPHINCSPlusKeyGenerationParameters(var2, (SPHINCSPlusParameters)parameters.get(var3));
         this.engine.init(this.param);
         this.initialised = true;
      } else {
         throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + var1);
      }
   }

   @Override
   public KeyPair generateKeyPair() {
      if (!this.initialised) {
         this.param = new SPHINCSPlusKeyGenerationParameters(this.random, SPHINCSPlusParameters.sha2_256s);
         this.engine.init(this.param);
         this.initialised = true;
      }

      AsymmetricCipherKeyPair var1 = this.engine.generateKeyPair();
      SPHINCSPlusPublicKeyParameters var2 = (SPHINCSPlusPublicKeyParameters)var1.getPublic();
      SPHINCSPlusPrivateKeyParameters var3 = (SPHINCSPlusPrivateKeyParameters)var1.getPrivate();
      return new KeyPair(new BCSPHINCSPlusPublicKey(var2), new BCSPHINCSPlusPrivateKey(var3));
   }

   private static String getNameFromParams(AlgorithmParameterSpec var0) {
      if (var0 instanceof SPHINCSPlusParameterSpec) {
         SPHINCSPlusParameterSpec var1 = (SPHINCSPlusParameterSpec)var0;
         return var1.getName();
      } else {
         return Strings.toLowerCase(SpecUtil.getNameFrom(var0));
      }
   }

   static {
      parameters.put(SPHINCSPlusParameterSpec.sha2_128f_robust.getName(), SPHINCSPlusParameters.sha2_128f_robust);
      parameters.put(SPHINCSPlusParameterSpec.sha2_128s_robust.getName(), SPHINCSPlusParameters.sha2_128s_robust);
      parameters.put(SPHINCSPlusParameterSpec.sha2_192f_robust.getName(), SPHINCSPlusParameters.sha2_192f_robust);
      parameters.put(SPHINCSPlusParameterSpec.sha2_192s_robust.getName(), SPHINCSPlusParameters.sha2_192s_robust);
      parameters.put(SPHINCSPlusParameterSpec.sha2_256f_robust.getName(), SPHINCSPlusParameters.sha2_256f_robust);
      parameters.put(SPHINCSPlusParameterSpec.sha2_256s_robust.getName(), SPHINCSPlusParameters.sha2_256s_robust);
      parameters.put(SPHINCSPlusParameterSpec.sha2_128f.getName(), SPHINCSPlusParameters.sha2_128f);
      parameters.put(SPHINCSPlusParameterSpec.sha2_128s.getName(), SPHINCSPlusParameters.sha2_128s);
      parameters.put(SPHINCSPlusParameterSpec.sha2_192f.getName(), SPHINCSPlusParameters.sha2_192f);
      parameters.put(SPHINCSPlusParameterSpec.sha2_192s.getName(), SPHINCSPlusParameters.sha2_192s);
      parameters.put(SPHINCSPlusParameterSpec.sha2_256f.getName(), SPHINCSPlusParameters.sha2_256f);
      parameters.put(SPHINCSPlusParameterSpec.sha2_256s.getName(), SPHINCSPlusParameters.sha2_256s);
      parameters.put(SPHINCSPlusParameterSpec.shake_128f_robust.getName(), SPHINCSPlusParameters.shake_128f_robust);
      parameters.put(SPHINCSPlusParameterSpec.shake_128s_robust.getName(), SPHINCSPlusParameters.shake_128s_robust);
      parameters.put(SPHINCSPlusParameterSpec.shake_192f_robust.getName(), SPHINCSPlusParameters.shake_192f_robust);
      parameters.put(SPHINCSPlusParameterSpec.shake_192s_robust.getName(), SPHINCSPlusParameters.shake_192s_robust);
      parameters.put(SPHINCSPlusParameterSpec.shake_256f_robust.getName(), SPHINCSPlusParameters.shake_256f_robust);
      parameters.put(SPHINCSPlusParameterSpec.shake_256s_robust.getName(), SPHINCSPlusParameters.shake_256s_robust);
      parameters.put(SPHINCSPlusParameterSpec.shake_128f.getName(), SPHINCSPlusParameters.shake_128f);
      parameters.put(SPHINCSPlusParameterSpec.shake_128s.getName(), SPHINCSPlusParameters.shake_128s);
      parameters.put(SPHINCSPlusParameterSpec.shake_192f.getName(), SPHINCSPlusParameters.shake_192f);
      parameters.put(SPHINCSPlusParameterSpec.shake_192s.getName(), SPHINCSPlusParameters.shake_192s);
      parameters.put(SPHINCSPlusParameterSpec.shake_256f.getName(), SPHINCSPlusParameters.shake_256f);
      parameters.put(SPHINCSPlusParameterSpec.shake_256s.getName(), SPHINCSPlusParameters.shake_256s);
      parameters.put(SPHINCSPlusParameterSpec.haraka_128f.getName(), SPHINCSPlusParameters.haraka_128f);
      parameters.put(SPHINCSPlusParameterSpec.haraka_128s.getName(), SPHINCSPlusParameters.haraka_128s);
      parameters.put(SPHINCSPlusParameterSpec.haraka_192f.getName(), SPHINCSPlusParameters.haraka_192f);
      parameters.put(SPHINCSPlusParameterSpec.haraka_192s.getName(), SPHINCSPlusParameters.haraka_192s);
      parameters.put(SPHINCSPlusParameterSpec.haraka_256f.getName(), SPHINCSPlusParameters.haraka_256f);
      parameters.put(SPHINCSPlusParameterSpec.haraka_256s.getName(), SPHINCSPlusParameters.haraka_256s);
      parameters.put(SPHINCSPlusParameterSpec.haraka_128f_simple.getName(), SPHINCSPlusParameters.haraka_128f_simple);
      parameters.put(SPHINCSPlusParameterSpec.haraka_128s_simple.getName(), SPHINCSPlusParameters.haraka_128s_simple);
      parameters.put(SPHINCSPlusParameterSpec.haraka_192f_simple.getName(), SPHINCSPlusParameters.haraka_192f_simple);
      parameters.put(SPHINCSPlusParameterSpec.haraka_192s_simple.getName(), SPHINCSPlusParameters.haraka_192s_simple);
      parameters.put(SPHINCSPlusParameterSpec.haraka_256f_simple.getName(), SPHINCSPlusParameters.haraka_256f_simple);
      parameters.put(SPHINCSPlusParameterSpec.haraka_256s_simple.getName(), SPHINCSPlusParameters.haraka_256s_simple);
   }

   public static class Sha2_128f extends SPHINCSPlusKeyPairGeneratorSpi {
      public Sha2_128f() {
         super(SPHINCSPlusParameterSpec.sha2_128f);
      }
   }

   public static class Sha2_128s extends SPHINCSPlusKeyPairGeneratorSpi {
      public Sha2_128s() {
         super(SPHINCSPlusParameterSpec.sha2_128s);
      }
   }

   public static class Sha2_192f extends SPHINCSPlusKeyPairGeneratorSpi {
      public Sha2_192f() {
         super(SPHINCSPlusParameterSpec.sha2_192f);
      }
   }

   public static class Sha2_192s extends SPHINCSPlusKeyPairGeneratorSpi {
      public Sha2_192s() {
         super(SPHINCSPlusParameterSpec.sha2_192s);
      }
   }

   public static class Sha2_256f extends SPHINCSPlusKeyPairGeneratorSpi {
      public Sha2_256f() {
         super(SPHINCSPlusParameterSpec.sha2_256f);
      }
   }

   public static class Sha2_256s extends SPHINCSPlusKeyPairGeneratorSpi {
      public Sha2_256s() {
         super(SPHINCSPlusParameterSpec.sha2_256s);
      }
   }

   public static class Shake_128f extends SPHINCSPlusKeyPairGeneratorSpi {
      public Shake_128f() {
         super(SPHINCSPlusParameterSpec.shake_128f);
      }
   }

   public static class Shake_128s extends SPHINCSPlusKeyPairGeneratorSpi {
      public Shake_128s() {
         super(SPHINCSPlusParameterSpec.shake_128s);
      }
   }

   public static class Shake_192f extends SPHINCSPlusKeyPairGeneratorSpi {
      public Shake_192f() {
         super(SPHINCSPlusParameterSpec.shake_192f);
      }
   }

   public static class Shake_192s extends SPHINCSPlusKeyPairGeneratorSpi {
      public Shake_192s() {
         super(SPHINCSPlusParameterSpec.shake_192s);
      }
   }

   public static class Shake_256f extends SPHINCSPlusKeyPairGeneratorSpi {
      public Shake_256f() {
         super(SPHINCSPlusParameterSpec.shake_256f);
      }
   }

   public static class Shake_256s extends SPHINCSPlusKeyPairGeneratorSpi {
      public Shake_256s() {
         super(SPHINCSPlusParameterSpec.shake_256s);
      }
   }
}
