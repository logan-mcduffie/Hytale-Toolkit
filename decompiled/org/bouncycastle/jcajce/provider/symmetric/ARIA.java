package org.bouncycastle.jcajce.provider.symmetric;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.engines.ARIAEngine;
import org.bouncycastle.crypto.engines.ARIAWrapEngine;
import org.bouncycastle.crypto.engines.ARIAWrapPadEngine;
import org.bouncycastle.crypto.engines.RFC3211WrapEngine;
import org.bouncycastle.crypto.generators.Poly1305KeyGenerator;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CCMBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.internal.asn1.cms.CCMParameters;
import org.bouncycastle.internal.asn1.cms.GCMParameters;
import org.bouncycastle.internal.asn1.nsri.NSRIObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BlockCipherProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.GcmSpecUtil;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;
import org.bouncycastle.jcajce.spec.AEADParameterSpec;

public final class ARIA {
   private ARIA() {
   }

   public static class AlgParamGen extends BaseAlgorithmParameterGenerator {
      @Override
      protected void engineInit(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
         throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for ARIA parameter generation.");
      }

      @Override
      protected AlgorithmParameters engineGenerateParameters() {
         byte[] var1 = new byte[16];
         if (this.random == null) {
            this.random = CryptoServicesRegistrar.getSecureRandom();
         }

         this.random.nextBytes(var1);

         try {
            AlgorithmParameters var2 = this.createParametersInstance("ARIA");
            var2.init(new IvParameterSpec(var1));
            return var2;
         } catch (Exception var4) {
            throw new RuntimeException(var4.getMessage());
         }
      }
   }

   public static class AlgParams extends IvAlgorithmParameters {
      @Override
      protected String engineToString() {
         return "ARIA IV";
      }
   }

   public static class AlgParamsCCM extends BaseAlgorithmParameters {
      private CCMParameters ccmParams;

      @Override
      protected void engineInit(AlgorithmParameterSpec var1) throws InvalidParameterSpecException {
         if (GcmSpecUtil.isGcmSpec(var1)) {
            this.ccmParams = CCMParameters.getInstance(GcmSpecUtil.extractGcmParameters(var1));
         } else {
            if (!(var1 instanceof AEADParameterSpec)) {
               throw new InvalidParameterSpecException("AlgorithmParameterSpec class not recognized: " + var1.getClass().getName());
            }

            this.ccmParams = new CCMParameters(((AEADParameterSpec)var1).getNonce(), ((AEADParameterSpec)var1).getMacSizeInBits() / 8);
         }
      }

      @Override
      protected void engineInit(byte[] var1) throws IOException {
         this.ccmParams = CCMParameters.getInstance(var1);
      }

      @Override
      protected void engineInit(byte[] var1, String var2) throws IOException {
         if (!this.isASN1FormatString(var2)) {
            throw new IOException("unknown format specified");
         } else {
            this.ccmParams = CCMParameters.getInstance(var1);
         }
      }

      @Override
      protected byte[] engineGetEncoded() throws IOException {
         return this.ccmParams.getEncoded();
      }

      @Override
      protected byte[] engineGetEncoded(String var1) throws IOException {
         if (!this.isASN1FormatString(var1)) {
            throw new IOException("unknown format specified");
         } else {
            return this.ccmParams.getEncoded();
         }
      }

      @Override
      protected String engineToString() {
         return "CCM";
      }

      @Override
      protected AlgorithmParameterSpec localEngineGetParameterSpec(Class var1) throws InvalidParameterSpecException {
         if (var1 != AlgorithmParameterSpec.class && !GcmSpecUtil.isGcmSpec(var1)) {
            if (var1 == AEADParameterSpec.class) {
               return new AEADParameterSpec(this.ccmParams.getNonce(), this.ccmParams.getIcvLen() * 8);
            } else if (var1 == IvParameterSpec.class) {
               return new IvParameterSpec(this.ccmParams.getNonce());
            } else {
               throw new InvalidParameterSpecException("AlgorithmParameterSpec not recognized: " + var1.getName());
            }
         } else {
            return (AlgorithmParameterSpec)(GcmSpecUtil.gcmSpecExtractable()
               ? GcmSpecUtil.extractGcmSpec(this.ccmParams.toASN1Primitive())
               : new AEADParameterSpec(this.ccmParams.getNonce(), this.ccmParams.getIcvLen() * 8));
         }
      }
   }

   public static class AlgParamsGCM extends BaseAlgorithmParameters {
      private GCMParameters gcmParams;

      @Override
      protected void engineInit(AlgorithmParameterSpec var1) throws InvalidParameterSpecException {
         if (GcmSpecUtil.isGcmSpec(var1)) {
            this.gcmParams = GcmSpecUtil.extractGcmParameters(var1);
         } else {
            if (!(var1 instanceof AEADParameterSpec)) {
               throw new InvalidParameterSpecException("AlgorithmParameterSpec class not recognized: " + var1.getClass().getName());
            }

            this.gcmParams = new GCMParameters(((AEADParameterSpec)var1).getNonce(), ((AEADParameterSpec)var1).getMacSizeInBits() / 8);
         }
      }

      @Override
      protected void engineInit(byte[] var1) throws IOException {
         this.gcmParams = GCMParameters.getInstance(var1);
      }

      @Override
      protected void engineInit(byte[] var1, String var2) throws IOException {
         if (!this.isASN1FormatString(var2)) {
            throw new IOException("unknown format specified");
         } else {
            this.gcmParams = GCMParameters.getInstance(var1);
         }
      }

      @Override
      protected byte[] engineGetEncoded() throws IOException {
         return this.gcmParams.getEncoded();
      }

      @Override
      protected byte[] engineGetEncoded(String var1) throws IOException {
         if (!this.isASN1FormatString(var1)) {
            throw new IOException("unknown format specified");
         } else {
            return this.gcmParams.getEncoded();
         }
      }

      @Override
      protected String engineToString() {
         return "GCM";
      }

      @Override
      protected AlgorithmParameterSpec localEngineGetParameterSpec(Class var1) throws InvalidParameterSpecException {
         if (var1 != AlgorithmParameterSpec.class && !GcmSpecUtil.isGcmSpec(var1)) {
            if (var1 == AEADParameterSpec.class) {
               return new AEADParameterSpec(this.gcmParams.getNonce(), this.gcmParams.getIcvLen() * 8);
            } else if (var1 == IvParameterSpec.class) {
               return new IvParameterSpec(this.gcmParams.getNonce());
            } else {
               throw new InvalidParameterSpecException("AlgorithmParameterSpec not recognized: " + var1.getName());
            }
         } else {
            return (AlgorithmParameterSpec)(GcmSpecUtil.gcmSpecExtractable()
               ? GcmSpecUtil.extractGcmSpec(this.gcmParams.toASN1Primitive())
               : new AEADParameterSpec(this.gcmParams.getNonce(), this.gcmParams.getIcvLen() * 8));
         }
      }
   }

   public static class CBC extends BaseBlockCipher {
      public CBC() {
         super(new CBCBlockCipher(new ARIAEngine()), 128);
      }
   }

   public static class CCM extends BaseBlockCipher {
      public CCM() {
         super(CCMBlockCipher.newInstance(new ARIAEngine()), false, 12);
      }
   }

   public static class CFB extends BaseBlockCipher {
      public CFB() {
         super(new BufferedBlockCipher(new CFBBlockCipher(new ARIAEngine(), 128)), 128);
      }
   }

   public static class ECB extends BaseBlockCipher {
      public ECB() {
         super(new BlockCipherProvider() {
            @Override
            public BlockCipher get() {
               return new ARIAEngine();
            }
         });
      }
   }

   public static class GCM extends BaseBlockCipher {
      public GCM() {
         super(GCMBlockCipher.newInstance(new ARIAEngine()));
      }
   }

   public static class GMAC extends BaseMac {
      public GMAC() {
         super(new GMac(GCMBlockCipher.newInstance(new ARIAEngine())));
      }
   }

   public static class KeyFactory extends BaseSecretKeyFactory {
      public KeyFactory() {
         super("ARIA", null);
      }
   }

   public static class KeyGen extends BaseKeyGenerator {
      public KeyGen() {
         this(256);
      }

      public KeyGen(int var1) {
         super("ARIA", var1, new CipherKeyGenerator());
      }
   }

   public static class KeyGen128 extends ARIA.KeyGen {
      public KeyGen128() {
         super(128);
      }
   }

   public static class KeyGen192 extends ARIA.KeyGen {
      public KeyGen192() {
         super(192);
      }
   }

   public static class KeyGen256 extends ARIA.KeyGen {
      public KeyGen256() {
         super(256);
      }
   }

   public static class Mappings extends SymmetricAlgorithmProvider {
      private static final String PREFIX = ARIA.class.getName();

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("AlgorithmParameters.ARIA", PREFIX + "$AlgParams");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters", NSRIObjectIdentifiers.id_aria128_cbc, "ARIA");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters", NSRIObjectIdentifiers.id_aria192_cbc, "ARIA");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters", NSRIObjectIdentifiers.id_aria256_cbc, "ARIA");
         var1.addAlgorithm("AlgorithmParameterGenerator.ARIA", PREFIX + "$AlgParamGen");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria128_cbc, "ARIA");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria192_cbc, "ARIA");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria256_cbc, "ARIA");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria128_ofb, "ARIA");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria192_ofb, "ARIA");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria256_ofb, "ARIA");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria128_cfb, "ARIA");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria192_cfb, "ARIA");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator", NSRIObjectIdentifiers.id_aria256_cfb, "ARIA");
         var1.addAlgorithm("Cipher.ARIA", PREFIX + "$ECB");
         var1.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria128_ecb, PREFIX + "$ECB");
         var1.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria192_ecb, PREFIX + "$ECB");
         var1.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria256_ecb, PREFIX + "$ECB");
         var1.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria128_cbc, PREFIX + "$CBC");
         var1.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria192_cbc, PREFIX + "$CBC");
         var1.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria256_cbc, PREFIX + "$CBC");
         var1.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria128_cfb, PREFIX + "$CFB");
         var1.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria192_cfb, PREFIX + "$CFB");
         var1.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria256_cfb, PREFIX + "$CFB");
         var1.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria128_ofb, PREFIX + "$OFB");
         var1.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria192_ofb, PREFIX + "$OFB");
         var1.addAlgorithm("Cipher", NSRIObjectIdentifiers.id_aria256_ofb, PREFIX + "$OFB");
         var1.addAlgorithm("Cipher.ARIARFC3211WRAP", PREFIX + "$RFC3211Wrap");
         var1.addAlgorithm("Cipher.ARIAWRAP", PREFIX + "$Wrap");
         var1.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria128_kw, "ARIAWRAP");
         var1.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria192_kw, "ARIAWRAP");
         var1.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria256_kw, "ARIAWRAP");
         var1.addAlgorithm("Alg.Alias.Cipher.ARIAKW", "ARIAWRAP");
         var1.addAlgorithm("Cipher.ARIAWRAPPAD", PREFIX + "$WrapPad");
         var1.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria128_kwp, "ARIAWRAPPAD");
         var1.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria192_kwp, "ARIAWRAPPAD");
         var1.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria256_kwp, "ARIAWRAPPAD");
         var1.addAlgorithm("Alg.Alias.Cipher.ARIAKWP", "ARIAWRAPPAD");
         var1.addAlgorithm("KeyGenerator.ARIA", PREFIX + "$KeyGen");
         var1.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_kw, PREFIX + "$KeyGen128");
         var1.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_kw, PREFIX + "$KeyGen192");
         var1.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_kw, PREFIX + "$KeyGen256");
         var1.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_kwp, PREFIX + "$KeyGen128");
         var1.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_kwp, PREFIX + "$KeyGen192");
         var1.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_kwp, PREFIX + "$KeyGen256");
         var1.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_ecb, PREFIX + "$KeyGen128");
         var1.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_ecb, PREFIX + "$KeyGen192");
         var1.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_ecb, PREFIX + "$KeyGen256");
         var1.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_cbc, PREFIX + "$KeyGen128");
         var1.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_cbc, PREFIX + "$KeyGen192");
         var1.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_cbc, PREFIX + "$KeyGen256");
         var1.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_cfb, PREFIX + "$KeyGen128");
         var1.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_cfb, PREFIX + "$KeyGen192");
         var1.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_cfb, PREFIX + "$KeyGen256");
         var1.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_ofb, PREFIX + "$KeyGen128");
         var1.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_ofb, PREFIX + "$KeyGen192");
         var1.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_ofb, PREFIX + "$KeyGen256");
         var1.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_ccm, PREFIX + "$KeyGen128");
         var1.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_ccm, PREFIX + "$KeyGen192");
         var1.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_ccm, PREFIX + "$KeyGen256");
         var1.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria128_gcm, PREFIX + "$KeyGen128");
         var1.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria192_gcm, PREFIX + "$KeyGen192");
         var1.addAlgorithm("KeyGenerator", NSRIObjectIdentifiers.id_aria256_gcm, PREFIX + "$KeyGen256");
         var1.addAlgorithm("SecretKeyFactory.ARIA", PREFIX + "$KeyFactory");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory", NSRIObjectIdentifiers.id_aria128_cbc, "ARIA");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory", NSRIObjectIdentifiers.id_aria192_cbc, "ARIA");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory", NSRIObjectIdentifiers.id_aria256_cbc, "ARIA");
         var1.addAlgorithm("AlgorithmParameterGenerator.ARIACCM", PREFIX + "$AlgParamGen");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NSRIObjectIdentifiers.id_aria128_ccm, "ARIACCM");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NSRIObjectIdentifiers.id_aria192_ccm, "ARIACCM");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NSRIObjectIdentifiers.id_aria256_ccm, "ARIACCM");
         var1.addAlgorithm("Cipher.ARIACCM", PREFIX + "$CCM");
         var1.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria128_ccm, "CCM");
         var1.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria192_ccm, "CCM");
         var1.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria256_ccm, "CCM");
         var1.addAlgorithm("AlgorithmParameterGenerator.ARIAGCM", PREFIX + "$AlgParamGen");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NSRIObjectIdentifiers.id_aria128_gcm, "ARIAGCM");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NSRIObjectIdentifiers.id_aria192_gcm, "ARIAGCM");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NSRIObjectIdentifiers.id_aria256_gcm, "ARIAGCM");
         var1.addAlgorithm("Cipher.ARIAGCM", PREFIX + "$GCM");
         var1.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria128_gcm, "ARIAGCM");
         var1.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria192_gcm, "ARIAGCM");
         var1.addAlgorithm("Alg.Alias.Cipher", NSRIObjectIdentifiers.id_aria256_gcm, "ARIAGCM");
         this.addGMacAlgorithm(var1, "ARIA", PREFIX + "$GMAC", PREFIX + "$KeyGen");
         this.addPoly1305Algorithm(var1, "ARIA", PREFIX + "$Poly1305", PREFIX + "$Poly1305KeyGen");
      }
   }

   public static class OFB extends BaseBlockCipher {
      public OFB() {
         super(new BufferedBlockCipher(new OFBBlockCipher(new ARIAEngine(), 128)), 128);
      }
   }

   public static class Poly1305 extends BaseMac {
      public Poly1305() {
         super(new org.bouncycastle.crypto.macs.Poly1305(new ARIAEngine()));
      }
   }

   public static class Poly1305KeyGen extends BaseKeyGenerator {
      public Poly1305KeyGen() {
         super("Poly1305-ARIA", 256, new Poly1305KeyGenerator());
      }
   }

   public static class RFC3211Wrap extends BaseWrapCipher {
      public RFC3211Wrap() {
         super(new RFC3211WrapEngine(new ARIAEngine()), 16);
      }
   }

   public static class Wrap extends BaseWrapCipher {
      public Wrap() {
         super(new ARIAWrapEngine());
      }
   }

   public static class WrapPad extends BaseWrapCipher {
      public WrapPad() {
         super(new ARIAWrapPadEngine());
      }
   }
}
