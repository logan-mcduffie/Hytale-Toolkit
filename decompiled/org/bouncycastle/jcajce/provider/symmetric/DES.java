package org.bouncycastle.jcajce.provider.symmetric;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.PasswordConverter;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.engines.RFC3211WrapEngine;
import org.bouncycastle.crypto.generators.DESKeyGenerator;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.crypto.macs.CFBBlockCipherMac;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.macs.ISO9797Alg3Mac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.ISO7816d4Padding;
import org.bouncycastle.crypto.params.DESParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.jcajce.PBKDF1Key;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.PBE;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public final class DES {
   private DES() {
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
         super(new CBCBlockCipher(new DESEngine()), 64);
      }
   }

   public static class CBCMAC extends BaseMac {
      public CBCMAC() {
         super(new CBCBlockCipherMac(new DESEngine()));
      }
   }

   public static class CMAC extends BaseMac {
      public CMAC() {
         super(new CMac(new DESEngine()));
      }
   }

   public static class DES64 extends BaseMac {
      public DES64() {
         super(new CBCBlockCipherMac(new DESEngine(), 64));
      }
   }

   public static class DES64with7816d4 extends BaseMac {
      public DES64with7816d4() {
         super(new CBCBlockCipherMac(new DESEngine(), 64, new ISO7816d4Padding()));
      }
   }

   public static class DES9797Alg3 extends BaseMac {
      public DES9797Alg3() {
         super(new ISO9797Alg3Mac(new DESEngine()));
      }
   }

   public static class DES9797Alg3with7816d4 extends BaseMac {
      public DES9797Alg3with7816d4() {
         super(new ISO9797Alg3Mac(new DESEngine(), new ISO7816d4Padding()));
      }
   }

   public static class DESCFB8 extends BaseMac {
      public DESCFB8() {
         super(new CFBBlockCipherMac(new DESEngine()));
      }
   }

   public static class DESPBEKeyFactory extends BaseSecretKeyFactory {
      private boolean forCipher;
      private int scheme;
      private int digest;
      private int keySize;
      private int ivSize;

      public DESPBEKeyFactory(String var1, ASN1ObjectIdentifier var2, boolean var3, int var4, int var5, int var6, int var7) {
         super(var1, var2);
         this.forCipher = var3;
         this.scheme = var4;
         this.digest = var5;
         this.keySize = var6;
         this.ivSize = var7;
      }

      @Override
      protected SecretKey engineGenerateSecret(KeySpec var1) throws InvalidKeySpecException {
         if (var1 instanceof PBEKeySpec) {
            PBEKeySpec var2 = (PBEKeySpec)var1;
            if (var2.getSalt() == null) {
               return (SecretKey)(this.scheme != 0 && this.scheme != 4
                  ? new BCPBEKey(this.algName, this.algOid, this.scheme, this.digest, this.keySize, this.ivSize, var2, null)
                  : new PBKDF1Key(var2.getPassword(), this.scheme == 0 ? PasswordConverter.ASCII : PasswordConverter.UTF8));
            } else {
               CipherParameters var3;
               if (this.forCipher) {
                  var3 = PBE.Util.makePBEParameters(var2, this.scheme, this.digest, this.keySize, this.ivSize);
               } else {
                  var3 = PBE.Util.makePBEMacParameters(var2, this.scheme, this.digest, this.keySize);
               }

               KeyParameter var4;
               if (var3 instanceof ParametersWithIV) {
                  var4 = (KeyParameter)((ParametersWithIV)var3).getParameters();
               } else {
                  var4 = (KeyParameter)var3;
               }

               DESParameters.setOddParity(var4.getKey());
               return new BCPBEKey(this.algName, this.algOid, this.scheme, this.digest, this.keySize, this.ivSize, var2, var3);
            }
         } else {
            throw new InvalidKeySpecException("Invalid KeySpec");
         }
      }
   }

   public static class ECB extends BaseBlockCipher {
      public ECB() {
         super(new DESEngine());
      }
   }

   public static class KeyFactory extends BaseSecretKeyFactory {
      public KeyFactory() {
         super("DES", null);
      }

      @Override
      protected KeySpec engineGetKeySpec(SecretKey var1, Class var2) throws InvalidKeySpecException {
         if (var2 == null) {
            throw new InvalidKeySpecException("keySpec parameter is null");
         } else if (var1 == null) {
            throw new InvalidKeySpecException("key parameter is null");
         } else if (SecretKeySpec.class.isAssignableFrom(var2)) {
            return new SecretKeySpec(var1.getEncoded(), this.algName);
         } else if (DESKeySpec.class.isAssignableFrom(var2)) {
            byte[] var3 = var1.getEncoded();

            try {
               return new DESKeySpec(var3);
            } catch (Exception var5) {
               throw new InvalidKeySpecException(var5.toString());
            }
         } else {
            throw new InvalidKeySpecException("Invalid KeySpec");
         }
      }

      @Override
      protected SecretKey engineGenerateSecret(KeySpec var1) throws InvalidKeySpecException {
         if (var1 instanceof DESKeySpec) {
            DESKeySpec var2 = (DESKeySpec)var1;
            return new SecretKeySpec(var2.getKey(), "DES");
         } else {
            return super.engineGenerateSecret(var1);
         }
      }
   }

   public static class KeyGenerator extends BaseKeyGenerator {
      public KeyGenerator() {
         super("DES", 64, new DESKeyGenerator());
      }

      @Override
      protected void engineInit(int var1, SecureRandom var2) {
         super.engineInit(var1, var2);
      }

      @Override
      protected SecretKey engineGenerateKey() {
         if (this.uninitialised) {
            this.engine.init(new KeyGenerationParameters(CryptoServicesRegistrar.getSecureRandom(), this.defaultKeySize));
            this.uninitialised = false;
         }

         return new SecretKeySpec(this.engine.generateKey(), this.algName);
      }
   }

   public static class Mappings extends AlgorithmProvider {
      private static final String PREFIX = DES.class.getName();
      private static final String PACKAGE = "org.bouncycastle.jcajce.provider.symmetric";

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("Cipher.DES", PREFIX + "$ECB");
         var1.addAlgorithm("Cipher", OIWObjectIdentifiers.desCBC, PREFIX + "$CBC");
         this.addAlias(var1, OIWObjectIdentifiers.desCBC, "DES");
         var1.addAlgorithm("Cipher.DESRFC3211WRAP", PREFIX + "$RFC3211");
         var1.addAlgorithm("KeyGenerator.DES", PREFIX + "$KeyGenerator");
         var1.addAlgorithm("SecretKeyFactory.DES", PREFIX + "$KeyFactory");
         var1.addAlgorithm("Mac.DESCMAC", PREFIX + "$CMAC");
         var1.addAlgorithm("Mac.DESMAC", PREFIX + "$CBCMAC");
         var1.addAlgorithm("Alg.Alias.Mac.DES", "DESMAC");
         var1.addAlgorithm("Mac.DESMAC/CFB8", PREFIX + "$DESCFB8");
         var1.addAlgorithm("Alg.Alias.Mac.DES/CFB8", "DESMAC/CFB8");
         var1.addAlgorithm("Mac.DESMAC64", PREFIX + "$DES64");
         var1.addAlgorithm("Alg.Alias.Mac.DES64", "DESMAC64");
         var1.addAlgorithm("Mac.DESMAC64WITHISO7816-4PADDING", PREFIX + "$DES64with7816d4");
         var1.addAlgorithm("Alg.Alias.Mac.DES64WITHISO7816-4PADDING", "DESMAC64WITHISO7816-4PADDING");
         var1.addAlgorithm("Alg.Alias.Mac.DESISO9797ALG1MACWITHISO7816-4PADDING", "DESMAC64WITHISO7816-4PADDING");
         var1.addAlgorithm("Alg.Alias.Mac.DESISO9797ALG1WITHISO7816-4PADDING", "DESMAC64WITHISO7816-4PADDING");
         var1.addAlgorithm("Mac.DESWITHISO9797", PREFIX + "$DES9797Alg3");
         var1.addAlgorithm("Alg.Alias.Mac.DESISO9797MAC", "DESWITHISO9797");
         var1.addAlgorithm("Mac.ISO9797ALG3MAC", PREFIX + "$DES9797Alg3");
         var1.addAlgorithm("Alg.Alias.Mac.ISO9797ALG3", "ISO9797ALG3MAC");
         var1.addAlgorithm("Mac.ISO9797ALG3WITHISO7816-4PADDING", PREFIX + "$DES9797Alg3with7816d4");
         var1.addAlgorithm("Alg.Alias.Mac.ISO9797ALG3MACWITHISO7816-4PADDING", "ISO9797ALG3WITHISO7816-4PADDING");
         var1.addAlgorithm("AlgorithmParameters.DES", "org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters", OIWObjectIdentifiers.desCBC, "DES");
         var1.addAlgorithm("AlgorithmParameterGenerator.DES", PREFIX + "$AlgParamGen");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + OIWObjectIdentifiers.desCBC, "DES");
         var1.addAlgorithm("Cipher.PBEWITHMD2ANDDES", PREFIX + "$PBEWithMD2");
         var1.addAlgorithm("Cipher.PBEWITHMD5ANDDES", PREFIX + "$PBEWithMD5");
         var1.addAlgorithm("Cipher.PBEWITHSHA1ANDDES", PREFIX + "$PBEWithSHA1");
         var1.addAlgorithm("Alg.Alias.Cipher", PKCSObjectIdentifiers.pbeWithMD2AndDES_CBC, "PBEWITHMD2ANDDES");
         var1.addAlgorithm("Alg.Alias.Cipher", PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC, "PBEWITHMD5ANDDES");
         var1.addAlgorithm("Alg.Alias.Cipher", PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC, "PBEWITHSHA1ANDDES");
         var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHMD2ANDDES-CBC", "PBEWITHMD2ANDDES");
         var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHMD5ANDDES-CBC", "PBEWITHMD5ANDDES");
         var1.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1ANDDES-CBC", "PBEWITHSHA1ANDDES");
         var1.addAlgorithm("SecretKeyFactory.PBEWITHMD2ANDDES", PREFIX + "$PBEWithMD2KeyFactory");
         var1.addAlgorithm("SecretKeyFactory.PBEWITHMD5ANDDES", PREFIX + "$PBEWithMD5KeyFactory");
         var1.addAlgorithm("SecretKeyFactory.PBEWITHSHA1ANDDES", PREFIX + "$PBEWithSHA1KeyFactory");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHMD2ANDDES-CBC", "PBEWITHMD2ANDDES");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHMD5ANDDES-CBC", "PBEWITHMD5ANDDES");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA1ANDDES-CBC", "PBEWITHSHA1ANDDES");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory." + PKCSObjectIdentifiers.pbeWithMD2AndDES_CBC, "PBEWITHMD2ANDDES");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory." + PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC, "PBEWITHMD5ANDDES");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory." + PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC, "PBEWITHSHA1ANDDES");
      }

      private void addAlias(ConfigurableProvider var1, ASN1ObjectIdentifier var2, String var3) {
         var1.addAlgorithm("Alg.Alias.KeyGenerator." + var2.getId(), var3);
         var1.addAlgorithm("Alg.Alias.KeyFactory." + var2.getId(), var3);
      }
   }

   public static class PBEWithMD2 extends BaseBlockCipher {
      public PBEWithMD2() {
         super(new CBCBlockCipher(new DESEngine()), 0, 5, 64, 8);
      }
   }

   public static class PBEWithMD2KeyFactory extends DES.DESPBEKeyFactory {
      public PBEWithMD2KeyFactory() {
         super("PBEwithMD2andDES", PKCSObjectIdentifiers.pbeWithMD2AndDES_CBC, true, 0, 5, 64, 64);
      }
   }

   public static class PBEWithMD5 extends BaseBlockCipher {
      public PBEWithMD5() {
         super(new CBCBlockCipher(new DESEngine()), 0, 0, 64, 8);
      }
   }

   public static class PBEWithMD5KeyFactory extends DES.DESPBEKeyFactory {
      public PBEWithMD5KeyFactory() {
         super("PBEwithMD5andDES", PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC, true, 0, 0, 64, 64);
      }
   }

   public static class PBEWithSHA1 extends BaseBlockCipher {
      public PBEWithSHA1() {
         super(new CBCBlockCipher(new DESEngine()), 0, 1, 64, 8);
      }
   }

   public static class PBEWithSHA1KeyFactory extends DES.DESPBEKeyFactory {
      public PBEWithSHA1KeyFactory() {
         super("PBEwithSHA1andDES", PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC, true, 0, 1, 64, 64);
      }
   }

   public static class RFC3211 extends BaseWrapCipher {
      public RFC3211() {
         super(new RFC3211WrapEngine(new DESEngine()), 8);
      }
   }
}
