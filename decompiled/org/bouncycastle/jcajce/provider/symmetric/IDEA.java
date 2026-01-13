package org.bouncycastle.jcajce.provider.symmetric;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.engines.IDEAEngine;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.crypto.macs.CFBBlockCipherMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.internal.asn1.misc.IDEACBCPar;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.PBESecretKeyFactory;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public final class IDEA {
   private IDEA() {
   }

   public static class AlgParamGen extends BaseAlgorithmParameterGenerator {
      @Override
      protected void engineInit(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
         throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for IDEA parameter generation.");
      }

      @Override
      protected AlgorithmParameters engineGenerateParameters() {
         byte[] var1 = new byte[8];
         if (this.random == null) {
            this.random = CryptoServicesRegistrar.getSecureRandom();
         }

         this.random.nextBytes(var1);

         try {
            AlgorithmParameters var2 = this.createParametersInstance("IDEA");
            var2.init(new IvParameterSpec(var1));
            return var2;
         } catch (Exception var4) {
            throw new RuntimeException(var4.getMessage());
         }
      }
   }

   public static class AlgParams extends BaseAlgorithmParameters {
      private byte[] iv;

      @Override
      protected byte[] engineGetEncoded() throws IOException {
         return this.engineGetEncoded("ASN.1");
      }

      @Override
      protected byte[] engineGetEncoded(String var1) throws IOException {
         if (this.isASN1FormatString(var1)) {
            return new IDEACBCPar(this.engineGetEncoded("RAW")).getEncoded();
         } else if (var1.equals("RAW")) {
            byte[] var2 = new byte[this.iv.length];
            System.arraycopy(this.iv, 0, var2, 0, this.iv.length);
            return var2;
         } else {
            return null;
         }
      }

      @Override
      protected AlgorithmParameterSpec localEngineGetParameterSpec(Class var1) throws InvalidParameterSpecException {
         if (var1 != IvParameterSpec.class && var1 != AlgorithmParameterSpec.class) {
            throw new InvalidParameterSpecException("unknown parameter spec passed to IV parameters object.");
         } else {
            return new IvParameterSpec(this.iv);
         }
      }

      @Override
      protected void engineInit(AlgorithmParameterSpec var1) throws InvalidParameterSpecException {
         if (!(var1 instanceof IvParameterSpec)) {
            throw new InvalidParameterSpecException("IvParameterSpec required to initialise a IV parameters algorithm parameters object");
         } else {
            this.iv = ((IvParameterSpec)var1).getIV();
         }
      }

      @Override
      protected void engineInit(byte[] var1) throws IOException {
         this.iv = new byte[var1.length];
         System.arraycopy(var1, 0, this.iv, 0, this.iv.length);
      }

      @Override
      protected void engineInit(byte[] var1, String var2) throws IOException {
         if (var2.equals("RAW")) {
            this.engineInit(var1);
         } else if (var2.equals("ASN.1")) {
            IDEACBCPar var3 = IDEACBCPar.getInstance(var1);
            this.engineInit(var3.getIV());
         } else {
            throw new IOException("Unknown parameters format in IV parameters object");
         }
      }

      @Override
      protected String engineToString() {
         return "IDEA Parameters";
      }
   }

   public static class CBC extends BaseBlockCipher {
      public CBC() {
         super(CBCBlockCipher.newInstance(new IDEAEngine()), 64);
      }
   }

   public static class CFB8Mac extends BaseMac {
      public CFB8Mac() {
         super(new CFBBlockCipherMac(new IDEAEngine()));
      }
   }

   public static class ECB extends BaseBlockCipher {
      public ECB() {
         super(new IDEAEngine());
      }
   }

   public static class KeyGen extends BaseKeyGenerator {
      public KeyGen() {
         super("IDEA", 128, new CipherKeyGenerator());
      }
   }

   public static class Mac extends BaseMac {
      public Mac() {
         super(new CBCBlockCipherMac(new IDEAEngine()));
      }
   }

   public static class Mappings extends AlgorithmProvider {
      private static final String PREFIX = IDEA.class.getName();

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("AlgorithmParameterGenerator.IDEA", PREFIX + "$AlgParamGen");
         var1.addAlgorithm("AlgorithmParameterGenerator.1.3.6.1.4.1.188.7.1.1.2", PREFIX + "$AlgParamGen");
         var1.addAlgorithm("AlgorithmParameters.IDEA", PREFIX + "$AlgParams");
         var1.addAlgorithm("AlgorithmParameters.1.3.6.1.4.1.188.7.1.1.2", PREFIX + "$AlgParams");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAANDIDEA", "PKCS12PBE");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAANDIDEA-CBC", "PKCS12PBE");
         var1.addAlgorithm("Cipher.IDEA", PREFIX + "$ECB");
         var1.addAlgorithm("Cipher", MiscObjectIdentifiers.as_sys_sec_alg_ideaCBC, PREFIX + "$CBC");
         var1.addAlgorithm("Cipher.PBEWITHSHAANDIDEA-CBC", PREFIX + "$PBEWithSHAAndIDEA");
         var1.addAlgorithm("KeyGenerator.IDEA", PREFIX + "$KeyGen");
         var1.addAlgorithm("KeyGenerator", MiscObjectIdentifiers.as_sys_sec_alg_ideaCBC, PREFIX + "$KeyGen");
         var1.addAlgorithm("SecretKeyFactory.PBEWITHSHAANDIDEA-CBC", PREFIX + "$PBEWithSHAAndIDEAKeyGen");
         var1.addAlgorithm("Mac.IDEAMAC", PREFIX + "$Mac");
         var1.addAlgorithm("Alg.Alias.Mac.IDEA", "IDEAMAC");
         var1.addAlgorithm("Mac.IDEAMAC/CFB8", PREFIX + "$CFB8Mac");
         var1.addAlgorithm("Alg.Alias.Mac.IDEA/CFB8", "IDEAMAC/CFB8");
      }
   }

   public static class PBEWithSHAAndIDEA extends BaseBlockCipher {
      public PBEWithSHAAndIDEA() {
         super(CBCBlockCipher.newInstance(new IDEAEngine()), 2, 1, 128, 8);
      }
   }

   public static class PBEWithSHAAndIDEAKeyGen extends PBESecretKeyFactory {
      public PBEWithSHAAndIDEAKeyGen() {
         super("PBEwithSHAandIDEA-CBC", null, true, 2, 1, 128, 64);
      }
   }
}
