package org.bouncycastle.jcajce.provider.symmetric;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DefaultBufferedBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.AESWrapEngine;
import org.bouncycastle.crypto.engines.AESWrapPadEngine;
import org.bouncycastle.crypto.engines.RFC3211WrapEngine;
import org.bouncycastle.crypto.engines.RFC5649WrapEngine;
import org.bouncycastle.crypto.generators.Poly1305KeyGenerator;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CCMBlockCipher;
import org.bouncycastle.crypto.modes.CCMModeCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.internal.asn1.cms.CCMParameters;
import org.bouncycastle.internal.asn1.cms.GCMParameters;
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
import org.bouncycastle.jcajce.provider.symmetric.util.PBESecretKeyFactory;
import org.bouncycastle.jcajce.spec.AEADParameterSpec;

public final class AES {
   private static final Map<String, String> generalAesAttributes = new HashMap<>();

   private AES() {
   }

   static {
      generalAesAttributes.put("SupportedKeyClasses", "javax.crypto.SecretKey");
      generalAesAttributes.put("SupportedKeyFormats", "RAW");
   }

   public static class AESCCMMAC extends BaseMac {
      public AESCCMMAC() {
         super(new AES.CCMMac());
      }
   }

   public static class AESCCMMAC128 extends BaseMac {
      public AESCCMMAC128() {
         super(128, new AES.CCMMac());
      }
   }

   public static class AESCCMMAC192 extends BaseMac {
      public AESCCMMAC192() {
         super(192, new AES.CCMMac());
      }
   }

   public static class AESCCMMAC256 extends BaseMac {
      public AESCCMMAC256() {
         super(256, new AES.CCMMac());
      }
   }

   public static class AESCMAC extends BaseMac {
      public AESCMAC() {
         super(new CMac(AESEngine.newInstance()));
      }
   }

   public static class AESGMAC extends BaseMac {
      public AESGMAC() {
         super(new GMac(GCMBlockCipher.newInstance(AESEngine.newInstance())));
      }
   }

   public static class AlgParamGen extends BaseAlgorithmParameterGenerator {
      @Override
      protected void engineInit(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
         throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for AES parameter generation.");
      }

      @Override
      protected AlgorithmParameters engineGenerateParameters() {
         byte[] var1 = new byte[16];
         if (this.random == null) {
            this.random = CryptoServicesRegistrar.getSecureRandom();
         }

         this.random.nextBytes(var1);

         try {
            AlgorithmParameters var2 = this.createParametersInstance("AES");
            var2.init(new IvParameterSpec(var1));
            return var2;
         } catch (Exception var4) {
            throw new RuntimeException(var4.getMessage());
         }
      }
   }

   public static class AlgParamGenCCM extends BaseAlgorithmParameterGenerator {
      @Override
      protected void engineInit(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
         throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for AES parameter generation.");
      }

      @Override
      protected AlgorithmParameters engineGenerateParameters() {
         byte[] var1 = new byte[12];
         if (this.random == null) {
            this.random = new SecureRandom();
         }

         this.random.nextBytes(var1);

         try {
            AlgorithmParameters var2 = this.createParametersInstance("CCM");
            var2.init(new CCMParameters(var1, 12).getEncoded());
            return var2;
         } catch (Exception var4) {
            throw new RuntimeException(var4.getMessage());
         }
      }
   }

   public static class AlgParamGenGCM extends BaseAlgorithmParameterGenerator {
      @Override
      protected void engineInit(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
         throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for AES parameter generation.");
      }

      @Override
      protected AlgorithmParameters engineGenerateParameters() {
         byte[] var1 = new byte[12];
         if (this.random == null) {
            this.random = new SecureRandom();
         }

         this.random.nextBytes(var1);

         try {
            AlgorithmParameters var2 = this.createParametersInstance("GCM");
            var2.init(new GCMParameters(var1, 16).getEncoded());
            return var2;
         } catch (Exception var4) {
            throw new RuntimeException(var4.getMessage());
         }
      }
   }

   public static class AlgParams extends IvAlgorithmParameters {
      @Override
      protected String engineToString() {
         return "AES IV";
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
         super(CBCBlockCipher.newInstance(AESEngine.newInstance()), 128);
      }
   }

   public static class CBC128 extends BaseBlockCipher {
      public CBC128() {
         super(128, CBCBlockCipher.newInstance(AESEngine.newInstance()), 128);
      }
   }

   public static class CBC192 extends BaseBlockCipher {
      public CBC192() {
         super(192, CBCBlockCipher.newInstance(AESEngine.newInstance()), 128);
      }
   }

   public static class CBC256 extends BaseBlockCipher {
      public CBC256() {
         super(192, CBCBlockCipher.newInstance(AESEngine.newInstance()), 128);
      }
   }

   public static class CCM extends BaseBlockCipher {
      public CCM() {
         super(CCMBlockCipher.newInstance(AESEngine.newInstance()), false, 12);
      }
   }

   public static class CCM128 extends BaseBlockCipher {
      public CCM128() {
         super(128, CCMBlockCipher.newInstance(AESEngine.newInstance()), false, 12);
      }
   }

   public static class CCM192 extends BaseBlockCipher {
      public CCM192() {
         super(192, CCMBlockCipher.newInstance(AESEngine.newInstance()), false, 12);
      }
   }

   public static class CCM256 extends BaseBlockCipher {
      public CCM256() {
         super(256, CCMBlockCipher.newInstance(AESEngine.newInstance()), false, 12);
      }
   }

   static class CCMMac implements Mac {
      private final CCMModeCipher ccm = CCMBlockCipher.newInstance(AESEngine.newInstance());
      private int macLength = 8;

      @Override
      public void init(CipherParameters var1) throws IllegalArgumentException {
         this.ccm.init(true, var1);
         this.macLength = this.ccm.getMac().length;
      }

      @Override
      public String getAlgorithmName() {
         return this.ccm.getAlgorithmName() + "Mac";
      }

      @Override
      public int getMacSize() {
         return this.macLength;
      }

      @Override
      public void update(byte var1) throws IllegalStateException {
         this.ccm.processAADByte(var1);
      }

      @Override
      public void update(byte[] var1, int var2, int var3) throws DataLengthException, IllegalStateException {
         this.ccm.processAADBytes(var1, var2, var3);
      }

      @Override
      public int doFinal(byte[] var1, int var2) throws DataLengthException, IllegalStateException {
         try {
            return this.ccm.doFinal(var1, 0);
         } catch (InvalidCipherTextException var4) {
            throw new IllegalStateException("exception on doFinal(): " + var4.toString());
         }
      }

      @Override
      public void reset() {
         this.ccm.reset();
      }
   }

   public static class CFB extends BaseBlockCipher {
      public CFB() {
         super(new DefaultBufferedBlockCipher(CFBBlockCipher.newInstance(AESEngine.newInstance(), 128)), 128);
      }
   }

   public static class CFB128 extends BaseBlockCipher {
      public CFB128() {
         super(128, new DefaultBufferedBlockCipher(CFBBlockCipher.newInstance(AESEngine.newInstance(), 128)), 128);
      }
   }

   public static class CFB192 extends BaseBlockCipher {
      public CFB192() {
         super(192, new DefaultBufferedBlockCipher(CFBBlockCipher.newInstance(AESEngine.newInstance(), 128)), 128);
      }
   }

   public static class CFB256 extends BaseBlockCipher {
      public CFB256() {
         super(256, new DefaultBufferedBlockCipher(CFBBlockCipher.newInstance(AESEngine.newInstance(), 128)), 128);
      }
   }

   public static class ECB extends BaseBlockCipher {
      public ECB() {
         super(new BlockCipherProvider() {
            @Override
            public BlockCipher get() {
               return AESEngine.newInstance();
            }
         });
      }
   }

   public static class ECB128 extends BaseBlockCipher {
      public ECB128() {
         super(128, new BlockCipherProvider() {
            @Override
            public BlockCipher get() {
               return AESEngine.newInstance();
            }
         });
      }
   }

   public static class ECB192 extends BaseBlockCipher {
      public ECB192() {
         super(192, new BlockCipherProvider() {
            @Override
            public BlockCipher get() {
               return AESEngine.newInstance();
            }
         });
      }
   }

   public static class ECB256 extends BaseBlockCipher {
      public ECB256() {
         super(256, new BlockCipherProvider() {
            @Override
            public BlockCipher get() {
               return AESEngine.newInstance();
            }
         });
      }
   }

   public static class GCM extends BaseBlockCipher {
      public GCM() {
         super(GCMBlockCipher.newInstance(AESEngine.newInstance()));
      }
   }

   public static class GCM128 extends BaseBlockCipher {
      public GCM128() {
         super(128, GCMBlockCipher.newInstance(AESEngine.newInstance()));
      }
   }

   public static class GCM192 extends BaseBlockCipher {
      public GCM192() {
         super(192, GCMBlockCipher.newInstance(AESEngine.newInstance()));
      }
   }

   public static class GCM256 extends BaseBlockCipher {
      public GCM256() {
         super(256, GCMBlockCipher.newInstance(AESEngine.newInstance()));
      }
   }

   public static class KeyFactory extends BaseSecretKeyFactory {
      public KeyFactory() {
         super("AES", null);
      }
   }

   public static class KeyGen extends BaseKeyGenerator {
      public KeyGen() {
         this(192);
      }

      public KeyGen(int var1) {
         super("AES", var1, new CipherKeyGenerator());
      }
   }

   public static class KeyGen128 extends AES.KeyGen {
      public KeyGen128() {
         super(128);
      }
   }

   public static class KeyGen192 extends AES.KeyGen {
      public KeyGen192() {
         super(192);
      }
   }

   public static class KeyGen256 extends AES.KeyGen {
      public KeyGen256() {
         super(256);
      }
   }

   public static class Mappings extends SymmetricAlgorithmProvider {
      private static final String PREFIX = AES.class.getName();
      private static final String wrongAES128 = "2.16.840.1.101.3.4.2";
      private static final String wrongAES192 = "2.16.840.1.101.3.4.22";
      private static final String wrongAES256 = "2.16.840.1.101.3.4.42";

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("AlgorithmParameters.AES", PREFIX + "$AlgParams");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.2.16.840.1.101.3.4.2", "AES");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.2.16.840.1.101.3.4.22", "AES");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.2.16.840.1.101.3.4.42", "AES");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes128_CBC, "AES");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes192_CBC, "AES");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes256_CBC, "AES");
         var1.addAlgorithm("AlgorithmParameters.GCM", PREFIX + "$AlgParamsGCM");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes128_GCM, "GCM");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes192_GCM, "GCM");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes256_GCM, "GCM");
         var1.addAlgorithm("AlgorithmParameters.CCM", PREFIX + "$AlgParamsCCM");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes128_CCM, "CCM");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes192_CCM, "CCM");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes256_CCM, "CCM");
         var1.addAlgorithm("AlgorithmParameterGenerator.AES", PREFIX + "$AlgParamGen");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator.2.16.840.1.101.3.4.2", "AES");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator.2.16.840.1.101.3.4.22", "AES");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator.2.16.840.1.101.3.4.42", "AES");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes128_CBC, "AES");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes192_CBC, "AES");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes256_CBC, "AES");
         var1.addAttributes("Cipher.AES", AES.generalAesAttributes);
         var1.addAlgorithm("Cipher.AES", PREFIX + "$ECB");
         var1.addAlgorithm("Alg.Alias.Cipher.2.16.840.1.101.3.4.2", "AES");
         var1.addAlgorithm("Alg.Alias.Cipher.2.16.840.1.101.3.4.22", "AES");
         var1.addAlgorithm("Alg.Alias.Cipher.2.16.840.1.101.3.4.42", "AES");
         var1.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes128_ECB, PREFIX + "$ECB128");
         var1.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes192_ECB, PREFIX + "$ECB192");
         var1.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes256_ECB, PREFIX + "$ECB256");
         var1.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes128_CBC, PREFIX + "$CBC128");
         var1.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes192_CBC, PREFIX + "$CBC192");
         var1.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes256_CBC, PREFIX + "$CBC256");
         var1.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes128_OFB, PREFIX + "$OFB128");
         var1.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes192_OFB, PREFIX + "$OFB192");
         var1.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes256_OFB, PREFIX + "$OFB256");
         var1.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes128_CFB, PREFIX + "$CFB128");
         var1.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes192_CFB, PREFIX + "$CFB192");
         var1.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes256_CFB, PREFIX + "$CFB256");
         var1.addAttributes("Cipher.AESWRAP", AES.generalAesAttributes);
         var1.addAlgorithm("Cipher.AESWRAP", PREFIX + "$Wrap");
         var1.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes128_wrap, PREFIX + "$Wrap128");
         var1.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes192_wrap, PREFIX + "$Wrap192");
         var1.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes256_wrap, PREFIX + "$Wrap256");
         var1.addAlgorithm("Alg.Alias.Cipher.AESKW", "AESWRAP");
         var1.addAttributes("Cipher.AESWRAPPAD", AES.generalAesAttributes);
         var1.addAlgorithm("Cipher.AESWRAPPAD", PREFIX + "$WrapPad");
         var1.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes128_wrap_pad, PREFIX + "$WrapPad128");
         var1.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes192_wrap_pad, PREFIX + "$WrapPad192");
         var1.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes256_wrap_pad, PREFIX + "$WrapPad256");
         var1.addAlgorithm("Alg.Alias.Cipher.AESKWP", "AESWRAPPAD");
         var1.addAlgorithm("Cipher.AESRFC3211WRAP", PREFIX + "$RFC3211Wrap");
         var1.addAlgorithm("Cipher.AESRFC5649WRAP", PREFIX + "$RFC5649Wrap");
         var1.addAlgorithm("AlgorithmParameterGenerator.CCM", PREFIX + "$AlgParamGenCCM");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes128_CCM, "CCM");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes192_CCM, "CCM");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes256_CCM, "CCM");
         var1.addAttributes("Cipher.CCM", AES.generalAesAttributes);
         var1.addAlgorithm("Cipher.CCM", PREFIX + "$CCM");
         var1.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes128_CCM, PREFIX + "$CCM128");
         var1.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes192_CCM, PREFIX + "$CCM192");
         var1.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes256_CCM, PREFIX + "$CCM256");
         var1.addAlgorithm("AlgorithmParameterGenerator.GCM", PREFIX + "$AlgParamGenGCM");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes128_GCM, "GCM");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes192_GCM, "GCM");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes256_GCM, "GCM");
         var1.addAttributes("Cipher.GCM", AES.generalAesAttributes);
         var1.addAlgorithm("Cipher.GCM", PREFIX + "$GCM");
         var1.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes128_GCM, PREFIX + "$GCM128");
         var1.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes192_GCM, PREFIX + "$GCM192");
         var1.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes256_GCM, PREFIX + "$GCM256");
         var1.addAlgorithm("KeyGenerator.AES", PREFIX + "$KeyGen");
         var1.addAlgorithm("KeyGenerator.2.16.840.1.101.3.4.2", PREFIX + "$KeyGen128");
         var1.addAlgorithm("KeyGenerator.2.16.840.1.101.3.4.22", PREFIX + "$KeyGen192");
         var1.addAlgorithm("KeyGenerator.2.16.840.1.101.3.4.42", PREFIX + "$KeyGen256");
         var1.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_ECB, PREFIX + "$KeyGen128");
         var1.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_CBC, PREFIX + "$KeyGen128");
         var1.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_OFB, PREFIX + "$KeyGen128");
         var1.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_CFB, PREFIX + "$KeyGen128");
         var1.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_ECB, PREFIX + "$KeyGen192");
         var1.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_CBC, PREFIX + "$KeyGen192");
         var1.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_OFB, PREFIX + "$KeyGen192");
         var1.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_CFB, PREFIX + "$KeyGen192");
         var1.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_ECB, PREFIX + "$KeyGen256");
         var1.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_CBC, PREFIX + "$KeyGen256");
         var1.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_OFB, PREFIX + "$KeyGen256");
         var1.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_CFB, PREFIX + "$KeyGen256");
         var1.addAlgorithm("KeyGenerator.AESWRAP", PREFIX + "$KeyGen");
         var1.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_wrap, PREFIX + "$KeyGen128");
         var1.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_wrap, PREFIX + "$KeyGen192");
         var1.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_wrap, PREFIX + "$KeyGen256");
         var1.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_GCM, PREFIX + "$KeyGen128");
         var1.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_GCM, PREFIX + "$KeyGen192");
         var1.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_GCM, PREFIX + "$KeyGen256");
         var1.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_CCM, PREFIX + "$KeyGen128");
         var1.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_CCM, PREFIX + "$KeyGen192");
         var1.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_CCM, PREFIX + "$KeyGen256");
         var1.addAlgorithm("KeyGenerator.AESWRAPPAD", PREFIX + "$KeyGen");
         var1.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_wrap_pad, PREFIX + "$KeyGen128");
         var1.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_wrap_pad, PREFIX + "$KeyGen192");
         var1.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_wrap_pad, PREFIX + "$KeyGen256");
         var1.addAlgorithm("Mac.AESCMAC", PREFIX + "$AESCMAC");
         var1.addAlgorithm("Mac.AESCCMMAC", PREFIX + "$AESCCMMAC");
         var1.addAlgorithm("Mac." + NISTObjectIdentifiers.id_aes128_CCM.getId(), PREFIX + "$AESCCMMAC128");
         var1.addAlgorithm("Mac." + NISTObjectIdentifiers.id_aes192_CCM.getId(), PREFIX + "$AESCCMMAC192");
         var1.addAlgorithm("Mac." + NISTObjectIdentifiers.id_aes256_CCM.getId(), PREFIX + "$AESCCMMAC256");
         var1.addAlgorithm("Alg.Alias.Cipher", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes128_cbc, "PBEWITHSHAAND128BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.Cipher", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes192_cbc, "PBEWITHSHAAND192BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.Cipher", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes256_cbc, "PBEWITHSHAAND256BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.Cipher", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes128_cbc, "PBEWITHSHA256AND128BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.Cipher", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes192_cbc, "PBEWITHSHA256AND192BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.Cipher", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes256_cbc, "PBEWITHSHA256AND256BITAES-CBC-BC");
         var1.addAlgorithm("Cipher.PBEWITHSHAAND128BITAES-CBC-BC", PREFIX + "$PBEWithSHA1AESCBC128");
         var1.addAlgorithm("Cipher.PBEWITHSHAAND192BITAES-CBC-BC", PREFIX + "$PBEWithSHA1AESCBC192");
         var1.addAlgorithm("Cipher.PBEWITHSHAAND256BITAES-CBC-BC", PREFIX + "$PBEWithSHA1AESCBC256");
         var1.addAlgorithm("Cipher.PBEWITHSHA256AND128BITAES-CBC-BC", PREFIX + "$PBEWithSHA256AESCBC128");
         var1.addAlgorithm("Cipher.PBEWITHSHA256AND192BITAES-CBC-BC", PREFIX + "$PBEWithSHA256AESCBC192");
         var1.addAlgorithm("Cipher.PBEWITHSHA256AND256BITAES-CBC-BC", PREFIX + "$PBEWithSHA256AESCBC256");
         var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND128BITAES-CBC-BC", "PBEWITHSHAAND128BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND192BITAES-CBC-BC", "PBEWITHSHAAND192BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND256BITAES-CBC-BC", "PBEWITHSHAAND256BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND128BITAES-CBC-BC", "PBEWITHSHAAND128BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND192BITAES-CBC-BC", "PBEWITHSHAAND192BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND256BITAES-CBC-BC", "PBEWITHSHAAND256BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHAAND128BITAES-BC", "PBEWITHSHAAND128BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHAAND192BITAES-BC", "PBEWITHSHAAND192BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHAAND256BITAES-BC", "PBEWITHSHAAND256BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND128BITAES-BC", "PBEWITHSHAAND128BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND192BITAES-BC", "PBEWITHSHAAND192BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND256BITAES-BC", "PBEWITHSHAAND256BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND128BITAES-BC", "PBEWITHSHAAND128BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND192BITAES-BC", "PBEWITHSHAAND192BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND256BITAES-BC", "PBEWITHSHAAND256BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND128BITAES-CBC-BC", "PBEWITHSHA256AND128BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND192BITAES-CBC-BC", "PBEWITHSHA256AND192BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND256BITAES-CBC-BC", "PBEWITHSHA256AND256BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA256AND128BITAES-BC", "PBEWITHSHA256AND128BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA256AND192BITAES-BC", "PBEWITHSHA256AND192BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA256AND256BITAES-BC", "PBEWITHSHA256AND256BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND128BITAES-BC", "PBEWITHSHA256AND128BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND192BITAES-BC", "PBEWITHSHA256AND192BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND256BITAES-BC", "PBEWITHSHA256AND256BITAES-CBC-BC");
         var1.addAlgorithm("Cipher.PBEWITHMD5AND128BITAES-CBC-OPENSSL", PREFIX + "$PBEWithAESCBC");
         var1.addAlgorithm("Cipher.PBEWITHMD5AND192BITAES-CBC-OPENSSL", PREFIX + "$PBEWithAESCBC");
         var1.addAlgorithm("Cipher.PBEWITHMD5AND256BITAES-CBC-OPENSSL", PREFIX + "$PBEWithAESCBC");
         var1.addAlgorithm("SecretKeyFactory.AES", PREFIX + "$KeyFactory");
         var1.addAlgorithm("SecretKeyFactory", NISTObjectIdentifiers.aes, PREFIX + "$KeyFactory");
         var1.addAlgorithm("SecretKeyFactory.PBEWITHMD5AND128BITAES-CBC-OPENSSL", PREFIX + "$PBEWithMD5And128BitAESCBCOpenSSL");
         var1.addAlgorithm("SecretKeyFactory.PBEWITHMD5AND192BITAES-CBC-OPENSSL", PREFIX + "$PBEWithMD5And192BitAESCBCOpenSSL");
         var1.addAlgorithm("SecretKeyFactory.PBEWITHMD5AND256BITAES-CBC-OPENSSL", PREFIX + "$PBEWithMD5And256BitAESCBCOpenSSL");
         var1.addAlgorithm("SecretKeyFactory.PBEWITHSHAAND128BITAES-CBC-BC", PREFIX + "$PBEWithSHAAnd128BitAESBC");
         var1.addAlgorithm("SecretKeyFactory.PBEWITHSHAAND192BITAES-CBC-BC", PREFIX + "$PBEWithSHAAnd192BitAESBC");
         var1.addAlgorithm("SecretKeyFactory.PBEWITHSHAAND256BITAES-CBC-BC", PREFIX + "$PBEWithSHAAnd256BitAESBC");
         var1.addAlgorithm("SecretKeyFactory.PBEWITHSHA256AND128BITAES-CBC-BC", PREFIX + "$PBEWithSHA256And128BitAESBC");
         var1.addAlgorithm("SecretKeyFactory.PBEWITHSHA256AND192BITAES-CBC-BC", PREFIX + "$PBEWithSHA256And192BitAESBC");
         var1.addAlgorithm("SecretKeyFactory.PBEWITHSHA256AND256BITAES-CBC-BC", PREFIX + "$PBEWithSHA256And256BitAESBC");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA1AND128BITAES-CBC-BC", "PBEWITHSHAAND128BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA1AND192BITAES-CBC-BC", "PBEWITHSHAAND192BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA1AND256BITAES-CBC-BC", "PBEWITHSHAAND256BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-1AND128BITAES-CBC-BC", "PBEWITHSHAAND128BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-1AND192BITAES-CBC-BC", "PBEWITHSHAAND192BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-1AND256BITAES-CBC-BC", "PBEWITHSHAAND256BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND128BITAES-CBC-BC", "PBEWITHSHA256AND128BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND192BITAES-CBC-BC", "PBEWITHSHA256AND192BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND256BITAES-CBC-BC", "PBEWITHSHA256AND256BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND128BITAES-BC", "PBEWITHSHA256AND128BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND192BITAES-BC", "PBEWITHSHA256AND192BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND256BITAES-BC", "PBEWITHSHA256AND256BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes128_cbc, "PBEWITHSHAAND128BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes192_cbc, "PBEWITHSHAAND192BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes256_cbc, "PBEWITHSHAAND256BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes128_cbc, "PBEWITHSHA256AND128BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes192_cbc, "PBEWITHSHA256AND192BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes256_cbc, "PBEWITHSHA256AND256BITAES-CBC-BC");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND128BITAES-CBC-BC", "PKCS12PBE");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND192BITAES-CBC-BC", "PKCS12PBE");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND256BITAES-CBC-BC", "PKCS12PBE");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA256AND128BITAES-CBC-BC", "PKCS12PBE");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA256AND192BITAES-CBC-BC", "PKCS12PBE");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA256AND256BITAES-CBC-BC", "PKCS12PBE");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA1AND128BITAES-CBC-BC", "PKCS12PBE");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA1AND192BITAES-CBC-BC", "PKCS12PBE");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA1AND256BITAES-CBC-BC", "PKCS12PBE");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-1AND128BITAES-CBC-BC", "PKCS12PBE");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-1AND192BITAES-CBC-BC", "PKCS12PBE");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-1AND256BITAES-CBC-BC", "PKCS12PBE");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-256AND128BITAES-CBC-BC", "PKCS12PBE");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-256AND192BITAES-CBC-BC", "PKCS12PBE");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-256AND256BITAES-CBC-BC", "PKCS12PBE");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters." + BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes128_cbc.getId(), "PKCS12PBE");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters." + BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes192_cbc.getId(), "PKCS12PBE");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters." + BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes256_cbc.getId(), "PKCS12PBE");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters." + BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes128_cbc.getId(), "PKCS12PBE");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters." + BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes192_cbc.getId(), "PKCS12PBE");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters." + BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes256_cbc.getId(), "PKCS12PBE");
         this.addGMacAlgorithm(var1, "AES", PREFIX + "$AESGMAC", PREFIX + "$KeyGen128");
         this.addPoly1305Algorithm(var1, "AES", PREFIX + "$Poly1305", PREFIX + "$Poly1305KeyGen");
      }
   }

   public static class OFB extends BaseBlockCipher {
      public OFB() {
         super(new DefaultBufferedBlockCipher(new OFBBlockCipher(AESEngine.newInstance(), 128)), 128);
      }
   }

   public static class OFB128 extends BaseBlockCipher {
      public OFB128() {
         super(128, new DefaultBufferedBlockCipher(new OFBBlockCipher(AESEngine.newInstance(), 128)), 128);
      }
   }

   public static class OFB192 extends BaseBlockCipher {
      public OFB192() {
         super(192, new DefaultBufferedBlockCipher(new OFBBlockCipher(AESEngine.newInstance(), 128)), 128);
      }
   }

   public static class OFB256 extends BaseBlockCipher {
      public OFB256() {
         super(256, new DefaultBufferedBlockCipher(new OFBBlockCipher(AESEngine.newInstance(), 128)), 128);
      }
   }

   public static class PBEWithAESCBC extends BaseBlockCipher {
      public PBEWithAESCBC() {
         super(CBCBlockCipher.newInstance(AESEngine.newInstance()));
      }
   }

   public static class PBEWithMD5And128BitAESCBCOpenSSL extends PBESecretKeyFactory {
      public PBEWithMD5And128BitAESCBCOpenSSL() {
         super("PBEWithMD5And128BitAES-CBC-OpenSSL", null, true, 3, 0, 128, 128);
      }
   }

   public static class PBEWithMD5And192BitAESCBCOpenSSL extends PBESecretKeyFactory {
      public PBEWithMD5And192BitAESCBCOpenSSL() {
         super("PBEWithMD5And192BitAES-CBC-OpenSSL", null, true, 3, 0, 192, 128);
      }
   }

   public static class PBEWithMD5And256BitAESCBCOpenSSL extends PBESecretKeyFactory {
      public PBEWithMD5And256BitAESCBCOpenSSL() {
         super("PBEWithMD5And256BitAES-CBC-OpenSSL", null, true, 3, 0, 256, 128);
      }
   }

   public static class PBEWithSHA1AESCBC128 extends BaseBlockCipher {
      public PBEWithSHA1AESCBC128() {
         super(CBCBlockCipher.newInstance(AESEngine.newInstance()), 2, 1, 128, 16);
      }
   }

   public static class PBEWithSHA1AESCBC192 extends BaseBlockCipher {
      public PBEWithSHA1AESCBC192() {
         super(CBCBlockCipher.newInstance(AESEngine.newInstance()), 2, 1, 192, 16);
      }
   }

   public static class PBEWithSHA1AESCBC256 extends BaseBlockCipher {
      public PBEWithSHA1AESCBC256() {
         super(CBCBlockCipher.newInstance(AESEngine.newInstance()), 2, 1, 256, 16);
      }
   }

   public static class PBEWithSHA256AESCBC128 extends BaseBlockCipher {
      public PBEWithSHA256AESCBC128() {
         super(CBCBlockCipher.newInstance(AESEngine.newInstance()), 2, 4, 128, 16);
      }
   }

   public static class PBEWithSHA256AESCBC192 extends BaseBlockCipher {
      public PBEWithSHA256AESCBC192() {
         super(CBCBlockCipher.newInstance(AESEngine.newInstance()), 2, 4, 192, 16);
      }
   }

   public static class PBEWithSHA256AESCBC256 extends BaseBlockCipher {
      public PBEWithSHA256AESCBC256() {
         super(CBCBlockCipher.newInstance(AESEngine.newInstance()), 2, 4, 256, 16);
      }
   }

   public static class PBEWithSHA256And128BitAESBC extends PBESecretKeyFactory {
      public PBEWithSHA256And128BitAESBC() {
         super("PBEWithSHA256And128BitAES-CBC-BC", null, true, 2, 4, 128, 128);
      }
   }

   public static class PBEWithSHA256And192BitAESBC extends PBESecretKeyFactory {
      public PBEWithSHA256And192BitAESBC() {
         super("PBEWithSHA256And192BitAES-CBC-BC", null, true, 2, 4, 192, 128);
      }
   }

   public static class PBEWithSHA256And256BitAESBC extends PBESecretKeyFactory {
      public PBEWithSHA256And256BitAESBC() {
         super("PBEWithSHA256And256BitAES-CBC-BC", null, true, 2, 4, 256, 128);
      }
   }

   public static class PBEWithSHAAnd128BitAESBC extends PBESecretKeyFactory {
      public PBEWithSHAAnd128BitAESBC() {
         super("PBEWithSHA1And128BitAES-CBC-BC", null, true, 2, 1, 128, 128);
      }
   }

   public static class PBEWithSHAAnd192BitAESBC extends PBESecretKeyFactory {
      public PBEWithSHAAnd192BitAESBC() {
         super("PBEWithSHA1And192BitAES-CBC-BC", null, true, 2, 1, 192, 128);
      }
   }

   public static class PBEWithSHAAnd256BitAESBC extends PBESecretKeyFactory {
      public PBEWithSHAAnd256BitAESBC() {
         super("PBEWithSHA1And256BitAES-CBC-BC", null, true, 2, 1, 256, 128);
      }
   }

   public static class Poly1305 extends BaseMac {
      public Poly1305() {
         super(new org.bouncycastle.crypto.macs.Poly1305(AESEngine.newInstance()));
      }
   }

   public static class Poly1305KeyGen extends BaseKeyGenerator {
      public Poly1305KeyGen() {
         super("Poly1305-AES", 256, new Poly1305KeyGenerator());
      }
   }

   public static class RFC3211Wrap extends BaseWrapCipher {
      public RFC3211Wrap() {
         super(new RFC3211WrapEngine(AESEngine.newInstance()), 16);
      }
   }

   public static class RFC5649Wrap extends BaseWrapCipher {
      public RFC5649Wrap() {
         super(new RFC5649WrapEngine(AESEngine.newInstance()));
      }
   }

   public static class Wrap extends BaseWrapCipher {
      public Wrap() {
         super(new AESWrapEngine());
      }
   }

   public static class Wrap128 extends BaseWrapCipher {
      public Wrap128() {
         super(new AESWrapEngine());
      }
   }

   public static class Wrap192 extends BaseWrapCipher {
      public Wrap192() {
         super(new AESWrapEngine());
      }
   }

   public static class Wrap256 extends BaseWrapCipher {
      public Wrap256() {
         super(new AESWrapEngine());
      }
   }

   public static class WrapPad extends BaseWrapCipher {
      public WrapPad() {
         super(new AESWrapPadEngine());
      }
   }

   public static class WrapPad128 extends BaseWrapCipher {
      public WrapPad128() {
         super(new AESWrapPadEngine());
      }
   }

   public static class WrapPad192 extends BaseWrapCipher {
      public WrapPad192() {
         super(new AESWrapPadEngine());
      }
   }

   public static class WrapPad256 extends BaseWrapCipher {
      public WrapPad256() {
         super(new AESWrapPadEngine());
      }
   }
}
