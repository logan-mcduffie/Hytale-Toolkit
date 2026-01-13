package org.bouncycastle.jcajce.provider.symmetric;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.DESedeWrapEngine;
import org.bouncycastle.crypto.engines.RFC3211WrapEngine;
import org.bouncycastle.crypto.generators.DESedeKeyGenerator;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.crypto.macs.CFBBlockCipherMac;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.ISO7816d4Padding;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public final class DESede {
   private DESede() {
   }

   public static class AlgParamGen extends BaseAlgorithmParameterGenerator {
      @Override
      protected void engineInit(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
         throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for DES parameter generation.");
      }

      @Override
      protected AlgorithmParameters engineGenerateParameters() {
         byte[] var1 = new byte[8];
         if (this.random == null) {
            this.random = CryptoServicesRegistrar.getSecureRandom();
         }

         this.random.nextBytes(var1);

         try {
            AlgorithmParameters var2 = this.createParametersInstance("DES");
            var2.init(new IvParameterSpec(var1));
            return var2;
         } catch (Exception var4) {
            throw new RuntimeException(var4.getMessage());
         }
      }
   }

   public static class CBC extends BaseBlockCipher {
      public CBC() {
         super(CBCBlockCipher.newInstance(new DESedeEngine()), 64);
      }
   }

   public static class CBCMAC extends BaseMac {
      public CBCMAC() {
         super(new CBCBlockCipherMac(new DESedeEngine()));
      }
   }

   public static class CMAC extends BaseMac {
      public CMAC() {
         super(new CMac(new DESedeEngine()));
      }
   }

   public static class DESede64 extends BaseMac {
      public DESede64() {
         super(new CBCBlockCipherMac(new DESedeEngine(), 64));
      }
   }

   public static class DESede64with7816d4 extends BaseMac {
      public DESede64with7816d4() {
         super(new CBCBlockCipherMac(new DESedeEngine(), 64, new ISO7816d4Padding()));
      }
   }

   public static class DESedeCFB8 extends BaseMac {
      public DESedeCFB8() {
         super(new CFBBlockCipherMac(new DESedeEngine()));
      }
   }

   public static class ECB extends BaseBlockCipher {
      public ECB() {
         super(new DESedeEngine());
      }
   }

   public static class KeyFactory extends BaseSecretKeyFactory {
      public KeyFactory() {
         super("DESede", null);
      }

      @Override
      protected KeySpec engineGetKeySpec(SecretKey var1, Class var2) throws InvalidKeySpecException {
         if (var2 == null) {
            throw new InvalidKeySpecException("keySpec parameter is null");
         } else if (var1 == null) {
            throw new InvalidKeySpecException("key parameter is null");
         } else if (SecretKeySpec.class.isAssignableFrom(var2)) {
            return new SecretKeySpec(var1.getEncoded(), this.algName);
         } else if (DESedeKeySpec.class.isAssignableFrom(var2)) {
            byte[] var3 = var1.getEncoded();

            try {
               if (var3.length == 16) {
                  byte[] var4 = new byte[24];
                  System.arraycopy(var3, 0, var4, 0, 16);
                  System.arraycopy(var3, 0, var4, 16, 8);
                  return new DESedeKeySpec(var4);
               } else {
                  return new DESedeKeySpec(var3);
               }
            } catch (Exception var5) {
               throw new InvalidKeySpecException(var5.toString());
            }
         } else {
            throw new InvalidKeySpecException("Invalid KeySpec");
         }
      }

      @Override
      protected SecretKey engineGenerateSecret(KeySpec var1) throws InvalidKeySpecException {
         if (var1 instanceof DESedeKeySpec) {
            DESedeKeySpec var2 = (DESedeKeySpec)var1;
            return new SecretKeySpec(var2.getKey(), "DESede");
         } else {
            return super.engineGenerateSecret(var1);
         }
      }
   }

   public static class KeyGenerator extends BaseKeyGenerator {
      private boolean keySizeSet = false;

      public KeyGenerator() {
         super("DESede", 192, new DESedeKeyGenerator());
      }

      @Override
      protected void engineInit(int var1, SecureRandom var2) {
         super.engineInit(var1, var2);
         this.keySizeSet = true;
      }

      @Override
      protected SecretKey engineGenerateKey() {
         if (this.uninitialised) {
            this.engine.init(new KeyGenerationParameters(CryptoServicesRegistrar.getSecureRandom(), this.defaultKeySize));
            this.uninitialised = false;
         }

         if (!this.keySizeSet) {
            byte[] var1 = this.engine.generateKey();
            System.arraycopy(var1, 0, var1, 16, 8);
            return new SecretKeySpec(var1, this.algName);
         } else {
            return new SecretKeySpec(this.engine.generateKey(), this.algName);
         }
      }
   }

   public static class KeyGenerator3 extends BaseKeyGenerator {
      public KeyGenerator3() {
         super("DESede3", 192, new DESedeKeyGenerator());
      }
   }

   public static class Mappings extends AlgorithmProvider {
      private static final String PREFIX = DESede.class.getName();
      private static final String PACKAGE = "org.bouncycastle.jcajce.provider.symmetric";

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("Cipher.DESEDE", PREFIX + "$ECB");
         var1.addAlgorithm("Cipher", PKCSObjectIdentifiers.des_EDE3_CBC, PREFIX + "$CBC");
         var1.addAlgorithm("Cipher.DESEDEWRAP", PREFIX + "$Wrap");
         var1.addAlgorithm("Cipher", PKCSObjectIdentifiers.id_alg_CMS3DESwrap, PREFIX + "$Wrap");
         var1.addAlgorithm("Cipher.DESEDERFC3211WRAP", PREFIX + "$RFC3211");
         var1.addAlgorithm("Alg.Alias.Cipher.DESEDERFC3217WRAP", "DESEDEWRAP");
         var1.addAlgorithm("Alg.Alias.Cipher.TDEA", "DESEDE");
         var1.addAlgorithm("Alg.Alias.Cipher.TDEAWRAP", "DESEDEWRAP");
         var1.addAlgorithm("Alg.Alias.KeyGenerator.TDEA", "DESEDE");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.TDEA", "DESEDE");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator.TDEA", "DESEDE");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory.TDEA", "DESEDE");
         if (var1.hasAlgorithm("MessageDigest", "SHA-1")) {
            var1.addAlgorithm("Cipher.PBEWITHSHAAND3-KEYTRIPLEDES-CBC", PREFIX + "$PBEWithSHAAndDES3Key");
            var1.addAlgorithm("Cipher.PBEWITHSHAAND2-KEYTRIPLEDES-CBC", PREFIX + "$PBEWithSHAAndDES2Key");
            var1.addAlgorithm("Alg.Alias.Cipher", PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, "PBEWITHSHAAND3-KEYTRIPLEDES-CBC");
            var1.addAlgorithm("Alg.Alias.Cipher", PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC, "PBEWITHSHAAND2-KEYTRIPLEDES-CBC");
            var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1ANDDESEDE", "PBEWITHSHAAND3-KEYTRIPLEDES-CBC");
            var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND3-KEYTRIPLEDES-CBC", "PBEWITHSHAAND3-KEYTRIPLEDES-CBC");
            var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND2-KEYTRIPLEDES-CBC", "PBEWITHSHAAND2-KEYTRIPLEDES-CBC");
            var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHAAND3-KEYDESEDE-CBC", "PBEWITHSHAAND3-KEYTRIPLEDES-CBC");
            var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHAAND2-KEYDESEDE-CBC", "PBEWITHSHAAND2-KEYTRIPLEDES-CBC");
            var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND3-KEYDESEDE-CBC", "PBEWITHSHAAND3-KEYTRIPLEDES-CBC");
            var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND2-KEYDESEDE-CBC", "PBEWITHSHAAND2-KEYTRIPLEDES-CBC");
            var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1ANDDESEDE-CBC", "PBEWITHSHAAND3-KEYTRIPLEDES-CBC");
         }

         var1.addAlgorithm("KeyGenerator.DESEDE", PREFIX + "$KeyGenerator");
         var1.addAlgorithm("KeyGenerator." + PKCSObjectIdentifiers.des_EDE3_CBC, PREFIX + "$KeyGenerator3");
         var1.addAlgorithm("KeyGenerator.DESEDEWRAP", PREFIX + "$KeyGenerator");
         var1.addAlgorithm("SecretKeyFactory.DESEDE", PREFIX + "$KeyFactory");
         var1.addAlgorithm("SecretKeyFactory", OIWObjectIdentifiers.desEDE, PREFIX + "$KeyFactory");
         var1.addAlgorithm("Mac.DESEDECMAC", PREFIX + "$CMAC");
         var1.addAlgorithm("Mac.DESEDEMAC", PREFIX + "$CBCMAC");
         var1.addAlgorithm("Alg.Alias.Mac.DESEDE", "DESEDEMAC");
         var1.addAlgorithm("Mac.DESEDEMAC/CFB8", PREFIX + "$DESedeCFB8");
         var1.addAlgorithm("Alg.Alias.Mac.DESEDE/CFB8", "DESEDEMAC/CFB8");
         var1.addAlgorithm("Mac.DESEDEMAC64", PREFIX + "$DESede64");
         var1.addAlgorithm("Alg.Alias.Mac.DESEDE64", "DESEDEMAC64");
         var1.addAlgorithm("Mac.DESEDEMAC64WITHISO7816-4PADDING", PREFIX + "$DESede64with7816d4");
         var1.addAlgorithm("Alg.Alias.Mac.DESEDE64WITHISO7816-4PADDING", "DESEDEMAC64WITHISO7816-4PADDING");
         var1.addAlgorithm("Alg.Alias.Mac.DESEDEISO9797ALG1MACWITHISO7816-4PADDING", "DESEDEMAC64WITHISO7816-4PADDING");
         var1.addAlgorithm("Alg.Alias.Mac.DESEDEISO9797ALG1WITHISO7816-4PADDING", "DESEDEMAC64WITHISO7816-4PADDING");
         var1.addAlgorithm("AlgorithmParameters.DESEDE", "org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters." + PKCSObjectIdentifiers.des_EDE3_CBC, "DESEDE");
         var1.addAlgorithm("AlgorithmParameterGenerator.DESEDE", PREFIX + "$AlgParamGen");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + PKCSObjectIdentifiers.des_EDE3_CBC, "DESEDE");
         var1.addAlgorithm("SecretKeyFactory.PBEWITHSHAAND3-KEYTRIPLEDES-CBC", PREFIX + "$PBEWithSHAAndDES3KeyFactory");
         var1.addAlgorithm("SecretKeyFactory.PBEWITHSHAAND2-KEYTRIPLEDES-CBC", PREFIX + "$PBEWithSHAAndDES2KeyFactory");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA1ANDDESEDE", "PBEWITHSHAAND3-KEYTRIPLEDES-CBC");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND3-KEYTRIPLEDES", "PKCS12PBE");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND2-KEYTRIPLEDES", "PKCS12PBE");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND3-KEYTRIPLEDES-CBC", "PKCS12PBE");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND2-KEYTRIPLEDES-CBC", "PKCS12PBE");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAANDDES3KEY-CBC", "PKCS12PBE");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAANDDES2KEY-CBC", "PKCS12PBE");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory.PBE", "PBEWITHSHAAND3-KEYTRIPLEDES-CBC");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory.1.2.840.113549.1.12.1.3", "PBEWITHSHAAND3-KEYTRIPLEDES-CBC");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory.1.2.840.113549.1.12.1.4", "PBEWITHSHAAND2-KEYTRIPLEDES-CBC");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWithSHAAnd3KeyTripleDES", "PBEWITHSHAAND3-KEYTRIPLEDES-CBC");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.1.2.840.113549.1.12.1.3", "PKCS12PBE");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.1.2.840.113549.1.12.1.4", "PKCS12PBE");
         var1.addAlgorithm("Alg.Alias.Cipher.PBEWithSHAAnd3KeyTripleDES", "PBEWITHSHAAND3-KEYTRIPLEDES-CBC");
      }
   }

   public static class PBEWithSHAAndDES2Key extends BaseBlockCipher {
      public PBEWithSHAAndDES2Key() {
         super(new CBCBlockCipher(new DESedeEngine()), 2, 1, 128, 8);
      }
   }

   public static class PBEWithSHAAndDES2KeyFactory extends DES.DESPBEKeyFactory {
      public PBEWithSHAAndDES2KeyFactory() {
         super("PBEwithSHAandDES2Key-CBC", PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC, true, 2, 1, 128, 64);
      }
   }

   public static class PBEWithSHAAndDES3Key extends BaseBlockCipher {
      public PBEWithSHAAndDES3Key() {
         super(new CBCBlockCipher(new DESedeEngine()), 2, 1, 192, 8);
      }
   }

   public static class PBEWithSHAAndDES3KeyFactory extends DES.DESPBEKeyFactory {
      public PBEWithSHAAndDES3KeyFactory() {
         super("PBEwithSHAandDES3Key-CBC", PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, true, 2, 1, 192, 64);
      }
   }

   public static class RFC3211 extends BaseWrapCipher {
      public RFC3211() {
         super(new RFC3211WrapEngine(new DESedeEngine()), 8);
      }
   }

   public static class Wrap extends BaseWrapCipher {
      public Wrap() {
         super(new DESedeWrapEngine());
      }
   }
}
