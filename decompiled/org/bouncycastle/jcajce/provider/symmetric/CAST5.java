package org.bouncycastle.jcajce.provider.symmetric;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.engines.CAST5Engine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.internal.asn1.misc.CAST5CBCParameters;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public final class CAST5 {
   private CAST5() {
   }

   public static class AlgParamGen extends BaseAlgorithmParameterGenerator {
      @Override
      protected void engineInit(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
         throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for CAST5 parameter generation.");
      }

      @Override
      protected AlgorithmParameters engineGenerateParameters() {
         byte[] var1 = new byte[8];
         if (this.random == null) {
            this.random = CryptoServicesRegistrar.getSecureRandom();
         }

         this.random.nextBytes(var1);

         try {
            AlgorithmParameters var2 = this.createParametersInstance("CAST5");
            var2.init(new IvParameterSpec(var1));
            return var2;
         } catch (Exception var4) {
            throw new RuntimeException(var4.getMessage());
         }
      }
   }

   public static class AlgParams extends BaseAlgorithmParameters {
      private byte[] iv;
      private int keyLength = 128;

      @Override
      protected byte[] engineGetEncoded() {
         byte[] var1 = new byte[this.iv.length];
         System.arraycopy(this.iv, 0, var1, 0, this.iv.length);
         return var1;
      }

      @Override
      protected byte[] engineGetEncoded(String var1) throws IOException {
         if (this.isASN1FormatString(var1)) {
            return new CAST5CBCParameters(this.engineGetEncoded(), this.keyLength).getEncoded();
         } else {
            return var1.equals("RAW") ? this.engineGetEncoded() : null;
         }
      }

      @Override
      protected AlgorithmParameterSpec localEngineGetParameterSpec(Class var1) throws InvalidParameterSpecException {
         if (var1 != IvParameterSpec.class && var1 != AlgorithmParameterSpec.class) {
            throw new InvalidParameterSpecException("unknown parameter spec passed to CAST5 parameters object.");
         } else {
            return new IvParameterSpec(this.iv);
         }
      }

      @Override
      protected void engineInit(AlgorithmParameterSpec var1) throws InvalidParameterSpecException {
         if (var1 instanceof IvParameterSpec) {
            this.iv = ((IvParameterSpec)var1).getIV();
         } else {
            throw new InvalidParameterSpecException("IvParameterSpec required to initialise a CAST5 parameters algorithm parameters object");
         }
      }

      @Override
      protected void engineInit(byte[] var1) throws IOException {
         this.iv = new byte[var1.length];
         System.arraycopy(var1, 0, this.iv, 0, this.iv.length);
      }

      @Override
      protected void engineInit(byte[] var1, String var2) throws IOException {
         if (this.isASN1FormatString(var2)) {
            ASN1InputStream var3 = new ASN1InputStream(var1);
            CAST5CBCParameters var4 = CAST5CBCParameters.getInstance(var3.readObject());
            this.keyLength = var4.getKeyLength();
            this.iv = var4.getIV();
         } else if (var2.equals("RAW")) {
            this.engineInit(var1);
         } else {
            throw new IOException("Unknown parameters format in IV parameters object");
         }
      }

      @Override
      protected String engineToString() {
         return "CAST5 Parameters";
      }
   }

   public static class CBC extends BaseBlockCipher {
      public CBC() {
         super(new CBCBlockCipher(new CAST5Engine()), 64);
      }
   }

   public static class ECB extends BaseBlockCipher {
      public ECB() {
         super(new CAST5Engine());
      }
   }

   public static class KeyGen extends BaseKeyGenerator {
      public KeyGen() {
         super("CAST5", 128, new CipherKeyGenerator());
      }
   }

   public static class Mappings extends AlgorithmProvider {
      private static final String PREFIX = CAST5.class.getName();

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("AlgorithmParameters.CAST5", PREFIX + "$AlgParams");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.1.2.840.113533.7.66.10", "CAST5");
         var1.addAlgorithm("AlgorithmParameterGenerator.CAST5", PREFIX + "$AlgParamGen");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator.1.2.840.113533.7.66.10", "CAST5");
         var1.addAlgorithm("Cipher.CAST5", PREFIX + "$ECB");
         var1.addAlgorithm("Cipher", MiscObjectIdentifiers.cast5CBC, PREFIX + "$CBC");
         var1.addAlgorithm("KeyGenerator.CAST5", PREFIX + "$KeyGen");
         var1.addAlgorithm("Alg.Alias.KeyGenerator", MiscObjectIdentifiers.cast5CBC, "CAST5");
      }
   }
}
